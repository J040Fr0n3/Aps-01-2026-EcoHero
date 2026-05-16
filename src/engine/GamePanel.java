package engine;



import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;


import entities.Player;
import tile.CloudManager;
import tile.TileManager;

import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class GamePanel extends JPanel implements Runnable {

    // Configurações de Tela
    public final int tileSize = 48;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    
    BufferedImage heartImage;
    
    public int maxWorldCol; 
    public int maxWorldRow; 
    public int worldWidth;
    public int worldHeight;
    
    public int score = 0;
    public int comboMultiplier = 1;
    public int correctSequence = 0;
    public int totalItemsNoNivel = 0;
    public int itensColetadosTotal = 0;
    public Map<String, Integer> itensEntreguesFase = new HashMap<>();
    
    
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int scoreState = 2;
    public final int dataInputState = 3;
    public final int pauseState = 5;
    public final int quitConfirmationState = 6;
    public final int levelSelectState = 7;
    public final int finishState = 8;
    public final int gameOverState = 9;
    public final int creditsState = 10;
    public final int adminDeleteState = 11;
    public double playTime;
    private long musicTimePosition = 0;
    
    public int screenWidth2 = screenWidth;  
    public int screenHeight2 = screenHeight; 
    public boolean fullScreenOn = false;
    
    public int tutorialPasso = 1; 
    public boolean tutorialConcluido = false;
    
    public boolean apertouA = false;
    public boolean apertouD = false;
    public boolean apertouPulo = false;
    public boolean apertouCorrida = false;
    
    public int tutorialTimer = 0;
    
    private java.awt.image.BufferedImage fullScreenImage;
    private Graphics2D g2;
    
    public UI ui = new UI(this);
    
    public ItemManager itemM = new ItemManager(this);
    
    // teste mapas
    public int currentLevelIndex = 1;
    


    int FPS = 60;


    public TileManager tileM;
    public KeyHandler keyH = new KeyHandler(this);
    public Sound music = new Sound();
    public Sound fundo = new Sound();
    public Sound se = new Sound();
    public CollisionChecker cChecker;
    public Thread gameThread;

    // Entidades - APENAS DECLARE AQUI
    public Player player;
    
    public LevelManager levelM;
    
    public CloudManager cloudM;
    public java.util.HashMap<java.awt.Point, Integer> spawnerData = new java.util.HashMap<>();
    


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        try {
            heartImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/textures/coracao.png"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        
        this.levelM = new LevelManager(this);
        this.tileM = new TileManager(this);
        this.cChecker = new CollisionChecker(this);
        this.player = new Player(this, keyH);
        this.cloudM = new CloudManager(this);
        
        gameState = titleState;
        
    }

    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        
        // O jogo só processa movimentos e lógica se estiver no estado PLAY
        if (gameState == playState) {
        	playTime += (double)1/60;
            player.update();
            if (itemM != null) itemM.update();
            if(cloudM != null) cloudM.update();
            
           if(player.life <= 0) gameState = gameOverState;
            
            // Verifica se o player pediu para pular de nível (atalho 'N')
            if (keyH.nextLevelRequested) {
                levelM.nextLevel();
                keyH.nextLevelRequested = false;
            }
           
         // --- LÓGICA DO TUTORIAL ATIVO APENAS NA FASE 1 ---
            if (levelM != null && levelM.currentLevelIndex == 0 && !tutorialConcluido) {
                
                // PASSO 1: ANDAR (Precisa apertar A e D)
                if (tutorialPasso == 1) {
                    if (keyH.left) apertouA = true;
                    if (keyH.right) apertouD = true;
                    
                    if (apertouA && apertouD) {
                        tutorialTimer++;
                        if (tutorialTimer >= 60) { // Espera 1 segundo com o texto verde antes de mudar
                            tutorialPasso = 2;
                            tutorialTimer = 0;
                        }
                    }
                }
                // PASSO 2: PULAR (Precisa apertar W ou ESPAÇO)
                else if (tutorialPasso == 2) {
                    if (keyH.up) apertouPulo = true; // Ajuste se a sua tecla de pulo for keyH.space
                    
                    if (apertouPulo) {
                        tutorialTimer++;
                        if (tutorialTimer >= 60) {
                            tutorialPasso = 3;
                            tutorialTimer = 0;
                        }
                    }
                }
                // PASSO 3: CORRER (Precisa segurar CTRL + A ou D)
                else if (tutorialPasso == 3) {
                    if (keyH.ctrl && (keyH.left || keyH.right)) apertouCorrida = true;
                    
                    if (apertouCorrida) {
                        tutorialTimer++;
                        if (tutorialTimer >= 90) { // Deixa um pouco mais de tempo no último
                            tutorialPasso = 4;
                            tutorialConcluido = true;
                            tutorialTimer = 0;
                        }
                    }
                }
            }
            
        }
        
        // Se o gameState for pauseState ou quitConfirmationState, 
        // este método não fará nada, "congelando" o jogo visualmente.
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
     // --- ADAPTATIVO: Corrigido para não recriar o buffer infinitamente ---
        if (fullScreenImage == null || fullScreenImage.getWidth() != this.getWidth() || fullScreenImage.getHeight() != this.getHeight()) {
            
            // Pega o tamanho REAL atual da janela/painel
            screenWidth2 = this.getWidth();
            screenHeight2 = this.getHeight();
            
            // Instancia o buffer com o tamanho REAL da janela para estabilizar a condicional
            fullScreenImage = new java.awt.image.BufferedImage(screenWidth2, screenHeight2, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            g2 = (Graphics2D) fullScreenImage.getGraphics();
            
            // Aplica um Scale no g2 para que tudo que você desenhar em (screenWidth x screenHeight)
            // seja automaticamente escalado de forma interna para o tamanho real da janela.
            double scaleX = (double) screenWidth2 / screenWidth;
            double scaleY = (double) screenHeight2 / screenHeight;
            g2.scale(scaleX, scaleY);
        }

        // ----------------------------------------------------------------------
        // 2. TODO O DESENHO DO JOGO ACONTECE AQUI DENTRO DO BUFFER (USANDO g2)
        // ----------------------------------------------------------------------
        
        // ESTADOS QUE SÃO APENAS INTERFACE (Fundo Preto)
        if (gameState == titleState ||
            gameState == dataInputState ||
            gameState == scoreState ||
            gameState == levelSelectState ||
            gameState == finishState ||
            gameState == gameOverState ||
            gameState == creditsState ||
            gameState == adminDeleteState) {
            
            ui.draw(g2); 
        } 
        
        // ESTADOS QUE ENVOLVEM O MUNDO DO JOGO (Mapa visível)
        else if (gameState == playState || gameState == pauseState || gameState == quitConfirmationState) {
            
            // Desenha o mundo (Mapa, Itens, Player)
            if(tileM != null) tileM.draw(g2);
            if(itemM != null) itemM.draw(g2);
            if(player != null) player.draw(g2);
            
            // Desenha a HUD básica (Vida, Ar)
            desenharSlotDeItem(g2);
            desenharVida(g2);
            desenharAr(g2);
            desenharRelogio(g2);
            
            
            // Desenha a UI (Janela de Pausa ou Aviso de Saída vai por cima de tudo)
            ui.draw(g2);
            
            desenharTutorialFase1(g2);
        }
        
        // ----------------------------------------------------------------------
        // 3. PROJEÇÃO: JOGA O BUFFER PRONTO NA TELA REAL DO COMPUTADOR
        // ----------------------------------------------------------------------
        Graphics2D gScreen = (Graphics2D) g;
        
        // Projeta o buffer esticando-o dinamicamente para preencher 100% do painel visível
        gScreen.drawImage(fullScreenImage, 0, 0, screenWidth2, screenHeight2, null);
        
        gScreen.dispose();
    }
    
    public void resetarTutorial() {
        tutorialPasso = 1;
        tutorialConcluido = false;
        apertouA = false;
        apertouD = false;
        apertouPulo = false;
        apertouCorrida = false;
        tutorialTimer = 0;
    }
    
    private void desenharTutorialFase1(Graphics2D g2) {
        if (levelM != null && levelM.getCurrentLevel() != null && levelM.currentLevelIndex == 0 && !tutorialConcluido) {
            
            g2.setFont(Fonts.getPixelFont(20f)); 
            
            int boxSize = 60;
            int inventoryY = 20;
            // Mantém a posição fixa abaixo do inventário
            int textoY = inventoryY + boxSize + 75; 
            
            // --- PASSO 1: ANDAR ---
            if (tutorialPasso == 1) {
                String txtAndar = "APERTE [A] E [D] PARA ANDAR NA HORIZONTAL";
                int andarX = (screenWidth / 2) - (g2.getFontMetrics().stringWidth(txtAndar) / 2);
                
                // Sombra
                g2.setColor(Color.BLACK);
                g2.drawString(txtAndar, andarX + 1, textoY + 1);
                
                // Muda para verde no pequeno delay antes de sumir
                g2.setColor(apertouA && apertouD ? Color.GREEN : Color.RED);
                g2.drawString(txtAndar, andarX, textoY);
            }

            // --- PASSO 2: PULAR ---
            else if (tutorialPasso == 2) {
                String txtPular = "[W] OU [ESPAÇO] PARA PULAR";
                int pularX = (screenWidth / 2) - (g2.getFontMetrics().stringWidth(txtPular) / 2);
                
                g2.setColor(Color.BLACK);
                g2.drawString(txtPular, pularX + 1, textoY + 1);
                
                g2.setColor(apertouPulo ? Color.GREEN : Color.RED);
                g2.drawString(txtPular, pularX, textoY);
            }

            // --- PASSO 3: CORRER ---
            else if (tutorialPasso == 3) {
                String txtCorrer = "[CTRL + A ou D] PARA CORRER REPETIDAMENTE";
                int correrX = (screenWidth / 2) - (g2.getFontMetrics().stringWidth(txtCorrer) / 2);
                int correrY = textoY + 44;
                
                g2.setColor(Color.BLACK);
                g2.drawString(txtCorrer, correrX + 1, correrY + 1);
                
                g2.setColor(apertouCorrida ? Color.GREEN : Color.RED);
                g2.drawString(txtCorrer, correrX, textoY);
            }
        }
    }
    
    public String getProgressoLixeira(int tileNum) {
        // 1. Descobre qual é a string correspondente ao ID da lixeira
        String tipoLixo = "";
        switch (tileNum) {
            case 10: tipoLixo = "papel"; break;
            case 11: tipoLixo = "vidro"; break;
            case 12: tipoLixo = "metal"; break;
            case 13: tipoLixo = "plastico"; break;
            case 14: tipoLixo = "organico"; break;
            default: return ""; // Se não for lixeira, não desenha nada
        }

        // 2. Pega a configuração da fase atual através do seu LevelManager
        if (levelM != null && levelM.getCurrentLevel() != null) {
            LevelConfig faseAtual = levelM.getCurrentLevel();

            // Verifica se ESSA fase específica exige esse tipo de lixo
            if (faseAtual.itemsRequired.containsKey(tipoLixo)) {
                int meta = faseAtual.itemsRequired.get(tipoLixo); // Ex: 5
                
                // Pega o quanto já foi entregue guardado no GamePanel
                int atual = itensEntreguesFase.getOrDefault(tipoLixo, 0); // Ex: 2
                
                return atual + "/" + meta; // Retorna "2/5"
            } else {
                // Se a fase não pede esse lixo (ex: Vidro na Fase 1), mostra que a meta é 0
                int atual = itensEntreguesFase.getOrDefault(tipoLixo, 0);
                return atual + "/0";
            }
        }
        return "";
    }
    
    public void desenharTile(Graphics g, int tileNum, int screenX, int screenY) {
        if (screenX + tileSize > 0 && screenX < screenWidth && 
            screenY + tileSize > 0 && screenY < screenHeight) {
            
            int drawX = screenX;
            int drawY = screenY;
            int width = tileSize;
            int height = tileSize;

            // --- LÓGICA DE ALTURA ESPECIAL ---
            if (tileNum == 3) {
                // Plataforma (Metade da altura)
                height = tileSize / 2;
            } 
            else if (tileNum >= 10 && tileNum <= 14) {
                // Lixeiras (1.5 de altura)
                height = (int)(tileSize * 1.5);
                // Subtraímos a diferença da altura do Y para ela crescer para CIMA
                drawY -= (tileSize * 0.5); 
            }

            // Desenha o gráfico do bloco
            if (tileM.tile[tileNum].image != null) {
                g.drawImage(tileM.tile[tileNum].image, drawX, drawY, width, height, null);
            } else {
                // FALLBACK PARA CORES (Caso não tenha imagem)
                g.setColor(tileM.tile[tileNum].color);
                g.fillRect(screenX, drawY, tileSize, height);
            }

            // --- ADICIONADO: CONTADOR DE TASKS EM CIMA DAS LIXEIRAS ---
            if (tileNum >= 10 && tileNum <= 14) {
                Graphics2D g2 = (Graphics2D) g;
                
                // Configura a fonte pixel do jogo
                g2.setFont(Fonts.getPixelFont(20f)); // Fonte um pouco menor para ficar delicado
                
                // Pega a String de progresso (ex: "0/5") vinda do GamePanel
                String textoTask = getProgressoLixeira(tileNum);
                
                if (!textoTask.equals("")) {
                    int textoLargura = g2.getFontMetrics().stringWidth(textoTask);
                    
                    // Centraliza o texto horizontalmente em relação à lixeira
                    int textoX = drawX + (tileSize / 2) - (textoLargura / 2);
                    
                    // Posiciona um pouco acima do topo da lixeira (drawY é o topo dela)
                    int textoY = drawY - 8; 
                    
                    // 1. Desenha a sombra preta (essencial para leitura em qualquer mapa)
                    g2.setColor(Color.BLACK);
                    g2.drawString(textoTask, textoX + 1, textoY + 1);
                    
                    // 2. Desenha o texto principal branco ou colorido
                    // Dica: Se quiser dar um feedback visual, pode colocar VERDE se a task já foi concluída!
                    if (textoTask.startsWith(textoTask.substring(textoTask.indexOf("/") + 1))) {
                        g2.setColor(Color.GREEN); // Task feita!
                    } else {
                        g2.setColor(Color.WHITE); // Pendente
                    }
                    
                    g2.drawString(textoTask, textoX, textoY);
                }
            }
        }
    }
    
    public void playFundo(int i) {
    	fundo.setFile(i);
    	fundo.play();
    	fundo.loop();
    }
    
    public void stopFundo() {
    	fundo.stop();
    }
    
    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        se.playSE(i);
        se.play();
    }
    
    public void stopSE(int i) {
    	se.stop();
    }
    
    public void pausarSomDoJogo() {
        if (fundo != null && fundo.clip != null && fundo.clip.isRunning()) {
            musicTimePosition = fundo.clip.getMicrosecondPosition(); 
            fundo.clip.stop();
            System.out.println("Música de fundo pausada.");
        }
    }

    public void retomarSomDoJogo() {
        if (fundo != null && fundo.clip != null && !fundo.clip.isRunning()) {
        	fundo.clip.setMicrosecondPosition(musicTimePosition); // Define de onde continuar
        	fundo.clip.start(); // Dá o play
        	fundo.clip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY); // Mantém o loop da fase
            System.out.println("Música de fundo retomada.");
        }
    }
    
    public void resetarFaseAtual() {
    	this.score = 0;
    	player.life = player.maxLife;
    	player.airRecoveryCounter = 0;
    	levelM.loadCurrentLevel();
    	gameState = playState;
    	System.out.println("Jogaodr morreu. Score resetado e fase recarregada!");
    }
    
    public void setFullScreen() {
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        screenWidth2 = gd.getDisplayMode().getWidth();
        screenHeight2 = gd.getDisplayMode().getHeight();
        
        fullScreenOn = true;
    }
    
    private void desenharSlotDeItem(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int boxSize = 60;
        int inventoryX = (screenWidth / 2) - (boxSize / 2);
        int inventoryY = 20;

        // --- 1. BARRA DE PROGRESSO (SCORE) ---
        int barX = 20;
        int barY = inventoryY + 10; 
        int barHeight = 40;
        int barMaxWidth = inventoryX - barX - 20; 

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(barX, barY, barMaxWidth, barHeight, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(barX, barY, barMaxWidth, barHeight, 10, 10);

        if (totalItemsNoNivel > 0) {
            double progresso = (double) itensColetadosTotal / totalItemsNoNivel;
            int barFillWidth = (int) (barMaxWidth * progresso);
            g.setColor(new Color(0, 255, 100, 200)); 
            g.fillRoundRect(barX + 2, barY + 2, Math.max(0, barFillWidth - 4), barHeight - 4, 8, 8);
        }

        g.setFont(Fonts.getPixelFont(16f));
        g.setColor(Color.WHITE);
        String scoreText = "SCORE: " + score;
        int scoreTextX = barX + (barMaxWidth / 2) - (g.getFontMetrics().stringWidth(scoreText) / 2);
        g.drawString(scoreText, scoreTextX, barY + 25);

        // --- 2. SLOT DO INVENTÁRIO (QUADRADO CINZA) ---
        // Fundo do slot
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(inventoryX, inventoryY, boxSize, boxSize);

        // Borda Cinza Fixa
        g2.setStroke(new BasicStroke(3)); // Borda um pouco mais fina para ser elegante
        g2.setColor(new Color(150, 150, 150)); // Cinza médio
        g2.drawRect(inventoryX, inventoryY, boxSize, boxSize);
        g2.setStroke(new BasicStroke(1)); 

        // Item dentro do slot (o próximo a ser jogado)
        if (player.inventory.size() > 0) {
            String proximoItem = player.inventory.get(0);
            BufferedImage img = itemM.getImageByType(proximoItem);

            if (img != null) {
                // Desenha a textura centralizada no slot
                g2.drawImage(img, inventoryX + 10, inventoryY + 10, boxSize - 20, boxSize - 20, null);
            } else {
                // Fallback: Se a imagem falhar, usamos a cor antiga para o jogo não ficar "vazio"
                g2.setColor(getCorDoItem(proximoItem)); 
                g2.fillRect(inventoryX + 10, inventoryY + 10, boxSize - 20, boxSize - 20);
            }

            // Texto com nome do item abaixo das bolinhas (ajustado Y)
            g2.setColor(Color.WHITE);
            g2.setFont(Fonts.getPixelFont(12f));
            String nomeItem = proximoItem.toUpperCase();
            int nomeX = inventoryX + (boxSize/2) - (g2.getFontMetrics().stringWidth(nomeItem)/2);
            g2.drawString(nomeItem, nomeX, inventoryY + boxSize + 35);
        }

        // --- 3. INDICADORES DE ESPAÇO (BOLINHAS) ---
        int dotSpacing = 15; // Espaço entre as bolinhas
        int dotY = inventoryY + boxSize + 12; // Posição logo abaixo do quadrado
        
        for (int i = 0; i < player.maxInventorySize; i++) {
            // Centraliza as 3 bolinhas em relação ao quadrado
            int totalWidth = (player.maxInventorySize - 1) * dotSpacing;
            int startX = inventoryX + (boxSize / 2) - (totalWidth / 2);
            int dotX = startX + (i * dotSpacing);

            if (i < player.inventory.size()) {
                // Bolinha CHEIA (Item presente)
                g2.setColor(Color.WHITE);
                int size = 10; // Maior
                g2.fillOval(dotX - size/2, dotY - size/2, size, size);
            } else {
                // Bolinha VAZIA (Espaço disponível)
                g2.setColor(new Color(255, 255, 255, 100)); // Transparente
                int size = 6; // Menor
                g2.fillOval(dotX - size/2, dotY - size/2, size, size);
            }
        }

        // --- 4. MULTIPLICADOR DE COMBO ---
        if (comboMultiplier > 1) {
            g.setColor(Color.ORANGE);
            g.setFont(Fonts.getPixelFont(20f)); // Aumentei um pouco para dar destaque
            
            String comboText = "X" + comboMultiplier;
            // Pega a posição final da barra (barX + barMaxWidth) e subtrai a largura do texto
            int comboX = (barX + barMaxWidth) - g.getFontMetrics().stringWidth(comboText);
            
            g.drawString(comboText, comboX, barY + barHeight + 25);
        }
        
        if (player != null && player.pertoDaLixeira) {
            g2.setFont(Fonts.getPixelFont(20f)); 
            String aviso = "APERTE [E] PARA DESCARTAR";
            
            int textoLargura = g2.getFontMetrics().stringWidth(aviso);
            int avisoX = inventoryX + (boxSize / 2) - (textoLargura / 2);
            
            // Posiciona logo abaixo das bolinhas (dotY + 25 pixels para dar respiro)
            int avisoY = (inventoryY + boxSize + 12) + 100; 
            
            // Sombra preta para dar contraste
            g2.setColor(Color.BLACK);
            g2.drawString(aviso, avisoX + 1, avisoY + 1);
            
            // Texto principal amarelo vibrante
            g2.setColor(Color.YELLOW);
            g2.drawString(aviso, avisoX, avisoY);
        }
        
    }
    
    private void desenharRelogio(Graphics2D g2) {
        g2.setFont(Fonts.getPixelFont(18f));
        g2.setColor(Color.WHITE);

        // Converte os segundos para Minutos:Segundos
        int minutos = (int)(playTime / 60);
        int segundos = (int)(playTime % 60);
        
        String timeText = String.format("TEMPO: %02d:%02d", minutos, segundos);
        
        // Desenha no canto superior esquerdo ou próximo ao score
        g2.drawString(timeText, 20, 90); 
    }

    private Color getCorDoItem(String tipo) {
        switch (tipo.toLowerCase()) {
            case "Plastico": return Color.RED;
            case "Papel": return Color.BLUE;
            case "Vidro": return Color.GREEN;
            case "Metal": return Color.YELLOW;
            default: return Color.GRAY;
        }
    }
    
    private void desenharVida(Graphics g) {
        int x = screenWidth - 250; // Ajustado para caber os 6 corações na linha
        int y = 25;
        int size = 30;             // Tamanho da imagem do coração
        int spacing = 5;           // Espaço entre eles

        for (int i = 0; i < player.maxLife; i++) { // Loop de 0 a 5 (6 total)
            
            int currentHeartX = x + (i * (size + spacing));

            // 1. Desenha o fundo do "slot" (opcional, dá um efeito de Zelda/Minecraft)
            g.setColor(new Color(0, 0, 0, 0)); // Preto transparente
            g.fillRect(currentHeartX, y, size, size);
            
            // 2. Desenha a textura do coração
            // Se o índice i for menor que a vida atual, o player ainda tem esse coração
            if (i < player.life) {
                if (heartImage != null) {
                    g.drawImage(heartImage, currentHeartX, y, size, size, null);
                } else {
                    // Caso a imagem falhe em carregar, desenha um quadrado vermelho de segurança
                    g.setColor(Color.RED);
                    g.fillRect(currentHeartX + 2, y + 2, size - 4, size - 4);
                }
            } else {
                // 3. Opcional: Desenha uma borda cinza para os corações perdidos
                g.setColor(new Color(255, 255, 255, 50));
                g.drawRect(currentHeartX, y, size, size);
            }
        }
    }
    
    private void desenharAr(Graphics g) {
        // A condição chave é esta: player.airTimer > 0
        // Isso garante que mesmo fora da água, se houver ar para recuperar, a HUD desenha.
        if (player.inWater || player.airTimer > 0) {
            
            int x = screenWidth - 200; 
            int y = 65;               
            int bubbleSize = 10;
            int spacing = 5;

            // Calcula quantas bolhas estão "cheias"
            int bolhasCheias = 10 - (player.airTimer / 60);
            
            // Limita entre 0 e 10 para não bugar o desenho
            if (bolhasCheias < 0) bolhasCheias = 0;
            if (bolhasCheias > 10) bolhasCheias = 10;

            for (int i = 0; i < 10; i++) {
                int bx = x + (i * (bubbleSize + spacing));
                
                if (i < bolhasCheias) {
                    // Bolha recuperada (Azul)
                    g.setColor(new Color(0, 200, 255, 200));
                    g.fillOval(bx, y, bubbleSize, bubbleSize);
                    g.setColor(Color.WHITE);
                    g.fillOval(bx + 2, y + 2, 3, 3);
                } else {
                    // Bolha gasta (Contorno cinza)
                    g.setColor(new Color(255, 255, 255, 50));
                    g.drawOval(bx, y, bubbleSize, bubbleSize);
                }
            }
        }
    }
    
    
    
}