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

public class GamePanel extends JPanel implements Runnable {

    // Configurações de Tela
    public final int tileSize = 48;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    
    public final int maxWorldCol = 50; 
    public final int maxWorldRow = 14; 
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;
    
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
    public CollisionChecker cChecker;
    Thread gameThread;

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
        
        g.dispose();
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

        // Preenchimento da barra baseado nos itens coletados
        if (totalItemsNoNivel > 0) {
            double progresso = (double) itensColetadosTotal / totalItemsNoNivel;
            int barFillWidth = (int) (barMaxWidth * progresso);

            g.setColor(new Color(0, 255, 100, 200)); // Verde
            g.fillRoundRect(barX + 2, barY + 2, Math.max(0, barFillWidth - 4), barHeight - 4, 8, 8);
        }

        // Texto do Score no meio da barra
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        String scoreText = "SCORE: " + score;
        int scoreTextX = barX + (barMaxWidth / 2) - (g.getFontMetrics().stringWidth(scoreText) / 2);
        g.drawString(scoreText, scoreTextX, barY + 25);

        // --- 2. DESENHO DO SLOT DO INVENTÁRIO ---
        // Fundo do slot
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(inventoryX, inventoryY, boxSize, boxSize);
        g.setColor(Color.WHITE);
        g.drawRect(inventoryX, inventoryY, boxSize, boxSize);

        // Item no slot
        if (player.inventory.size() > 0) {
            String proximoItem = player.inventory.get(0);
            g.setColor(getCorDoItem(proximoItem)); 
            g.fillRect(inventoryX + 10, inventoryY + 10, boxSize - 20, boxSize - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(proximoItem, inventoryX + (boxSize/2) - (g.getFontMetrics().stringWidth(proximoItem)/2), inventoryY + boxSize + 15);
        }

        // --- 3. STATUS E COMBO ---
        String status = player.inventory.size() >= player.maxInventorySize ? "INVENTÁRIO CHEIO!" : "Espaço: " + player.inventory.size() + "/" + player.maxInventorySize;
        g.setColor(player.inventory.size() >= player.maxInventorySize ? Color.RED : Color.YELLOW);
        g.drawString(status, inventoryX + (boxSize/2) - (g.getFontMetrics().stringWidth(status)/2), inventoryY + boxSize + 30);

        if (comboMultiplier > 1) {
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("COMBO X" + comboMultiplier, barX, barY + barHeight + 20);
        }
    }

    // Método auxiliar para organizar as cores
    private Color getCorDoItem(String tipo) {
        switch (tipo.toLowerCase()) {
            case "Plastico": return Color.RED;
            case "Papel": return Color.BLUE;
            case "Vidro": return Color.GREEN;
            case "Metal": return Color.YELLOW;
            default: return Color.GRAY;
        }
    }
    
}