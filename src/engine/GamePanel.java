package engine;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import entities.Player;
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

    // FPS
    int FPS = 60;

    // Sistema - APENAS DECLARE AQUI (Sem o "new")
    public TileManager tileM;
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker;
    Thread gameThread;

    // Entidades - APENAS DECLARE AQUI
    public Player player;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // AGORA SIM, INSTANCIE NA ORDEM CORRETA:
        this.tileM = new TileManager(this);         // 1º Carrega o mapa
        this.cChecker = new CollisionChecker(this);     // 2º Prepara o checador
        this.player = new Player(this, keyH);       // 3º Cria o player (agora o gp não será nulo)
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
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Importante: Verifique se o mapa e player não são nulos antes de desenhar
        if(tileM != null) tileM.draw(g);
        if(player != null) player.draw(g);
        
        g.dispose();
    }
}