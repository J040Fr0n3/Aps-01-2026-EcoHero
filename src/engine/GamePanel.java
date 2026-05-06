package engine;



import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.Point;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import entities.Item;
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
    
    public int maxWorldCol; 
    public int maxWorldRow; 
    public int worldWidth;
    public int worldHeight;
    
    public int score = 0;
    public int comboMultiplier = 1;
    public int correctSequence = 0;
    public int totalItemsNoNivel = 0;
    public int itensColetadosTotal = 0;
    
    public ItemManager itemM = new ItemManager(this);
    
    // teste mapas
    public int currentLevelIndex = 1;
    


    int FPS = 60;


    public TileManager tileM;
    public KeyHandler keyH = new KeyHandler();
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
        
        this.levelM = new LevelManager(this);
        this.tileM = new TileManager(this);
        this.cChecker = new CollisionChecker(this);
        this.player = new Player(this, keyH);
        this.levelM.loadCurrentLevel();
        this.cloudM = new CloudManager(this);
        
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
        player.update();
        
        if(itemM != null) {
        	itemM.update();
        }
        
        if (keyH.nextLevelRequested) {
            levelM.nextLevel();
            keyH.nextLevelRequested = false;
        }
        cloudM.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Importante: Verifique se o mapa e player não são nulos antes de desenhar
        if(tileM != null) tileM.draw(g);
        if(itemM != null) itemM.draw(g);
        if(player != null) player.draw(g);
        desenharSlotDeItem(g);
        desenharVida(g);
        desenharAr(g);
        
        g.dispose();
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
        se.setFile(i);
        se.play();
    }
    
    public void stopSE(int i) {
    	se.stop();
    }
    
    private void desenharSlotDeItem(Graphics g) {
        int boxSize = 60;
        int inventoryX = (screenWidth / 2) - (boxSize / 2);
        int inventoryY = 20;

        // --- 1. DESENHO DA BARRA DE PROGRESSO (SCORE) ---
        int barX = 20;
        int barY = inventoryY + 10; 
        int barHeight = 40;
        // A barra vai da esquerda até 20px antes do slot de inventário
        int barMaxWidth = inventoryX - barX - 20; 

        // Fundo da barra (Vazio)
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(barX, barY, barMaxWidth, barHeight, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(barX, barY, barMaxWidth, barHeight, 10, 10);


        if (totalItemsNoNivel > 0) {
            double progresso = (double) itensColetadosTotal / totalItemsNoNivel;
            int barFillWidth = (int) (barMaxWidth * progresso);

            g.setColor(new Color(0, 255, 100, 200)); // Verde
            g.fillRoundRect(barX + 2, barY + 2, Math.max(0, barFillWidth - 4), barHeight - 4, 8, 8);
        }


        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        String scoreText = "SCORE: " + score;
        int scoreTextX = barX + (barMaxWidth / 2) - (g.getFontMetrics().stringWidth(scoreText) / 2);
        g.drawString(scoreText, scoreTextX, barY + 25);

        Graphics2D g2 = (Graphics2D) g;

        // Lógica de Cor da Borda: Vermelho se cheio, Verde se houver espaço
        boolean estaCheio = player.inventory.size() >= player.maxInventorySize;
        Color corBorda = estaCheio ? new Color(255, 0, 0) : new Color(0, 255, 0);


        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(inventoryX, inventoryY, boxSize, boxSize);


        g2.setStroke(new BasicStroke(10)); 
        g2.setColor(corBorda);
        g2.drawRect(inventoryX, inventoryY, boxSize, boxSize);
        g2.setStroke(new BasicStroke(1)); 

        if (player.inventory.size() > 0) {
            String proximoItem = player.inventory.get(0);
            
            g2.setColor(getCorDoItem(proximoItem)); 
            g2.fillRect(inventoryX + 10, inventoryY + 10, boxSize - 20, boxSize - 20);


            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            String nomeItem = proximoItem.toUpperCase();
            int nomeX = inventoryX + (boxSize/2) - (g2.getFontMetrics().stringWidth(nomeItem)/2);
            g2.drawString(nomeItem, nomeX, inventoryY + boxSize + 22);
        }

        if (comboMultiplier > 1) {
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("X" + comboMultiplier, barX, barY + barHeight + 20);
        }
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
        int x = screenWidth - 160; // Posição inicial X (canto direito)
        int y = 25;                // Posição inicial Y
        int size = 30;             // Tamanho do quadrado do coração
        int spacing = 10;          // Espaço entre os corações

        for (int i = 0; i < 3; i++) {
            // Determinamos quanto de vida este coração específico representa
            // Coração 0: vida 1 e 2 | Coração 1: vida 3 e 4 | Coração 2: vida 5 e 6
            int limiteVidaCoração = (i + 1) * 2;

            // Desenha o fundo (Preto translúcido para o espaço vazio)
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(x + (i * (size + spacing)), y, size, size);

            // Lógica de preenchimento
            if (player.life >= limiteVidaCoração) {
                // CORAÇÃO CHEIO (Tem os 2 pontos de vida)
                g.setColor(Color.RED);
                g.fillRect(x + (i * (size + spacing)) + 2, y + 2, size - 4, size - 4);
            } 
            else if (player.life == limiteVidaCoração - 1) {
                // MEIO CORAÇÃO (Tem apenas 1 ponto de vida)
                g.setColor(Color.RED);
                // Desenha apenas a metade esquerda do quadrado
                g.fillRect(x + (i * (size + spacing)) + 2, y + 2, (size - 4) / 2, size - 4);
            }

            // Borda do quadrado (Sempre branca)
            g.setColor(Color.WHITE);
            g.drawRect(x + (i * (size + spacing)), y, size, size);
        }
    }
    
    private void desenharAr(Graphics g) {
        // A condição chave é esta: player.airTimer > 0
        // Isso garante que mesmo fora da água, se houver ar para recuperar, a HUD desenha.
        if (player.inWater || player.airTimer > 0) {
            
            int x = screenWidth - 160; 
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