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
    public double playTime;
    
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
            
            if (itemM != null) {
                itemM.update();
            }
            
            // Verifica se o player pediu para pular de nível (atalho 'N')
            if (keyH.nextLevelRequested) {
                levelM.nextLevel();
                keyH.nextLevelRequested = false;
            }
            
            if (cloudM != null) {
                cloudM.update();
            }
            
            // AQUI: Incremente seu cronômetro de tempo e pontuação
            // Ex: scoreManager.update();
        }
        
        // Se o gameState for pauseState ou quitConfirmationState, 
        // este método não fará nada, "congelando" o jogo visualmente.
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // ESTADOS QUE SÃO APENAS INTERFACE (Fundo Preto)
        if (gameState == titleState || gameState == dataInputState || gameState == scoreState || gameState == levelSelectState || gameState == finishState) {
            ui.draw(g2); 
        } 
        
        // ESTADOS QUE ENVOLVEM O MUNDO DO JOGO (Mapa visível)
        else if (gameState == playState || gameState == pauseState || gameState == quitConfirmationState) {
            
            // 1. Desenha o mundo (Mapa, Itens, Player)
            if(tileM != null) tileM.draw(g2);
            if(itemM != null) itemM.draw(g2);
            if(player != null) player.draw(g2);
            
            // 2. Desenha a HUD básica (Vida, Ar)
            desenharSlotDeItem(g2);
            desenharVida(g2);
            desenharAr(g2);
            desenharRelogio(g2);
            
            // 3. Desenha a UI (Janela de Pausa ou Aviso de Saída vai por cima de tudo)
            ui.draw(g2);
        }
        
        g2.dispose();
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