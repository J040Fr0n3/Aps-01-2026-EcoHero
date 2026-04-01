package engine;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import entities.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // Configurações de Tela
    public final int tileSize = 48; // Cada tile terá 48x48 pixels
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768px
    public final int screenHeight = tileSize * maxScreenRow; // 576px

    // FPS
    int FPS = 60;

    // Sistema
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    Thread gameThread; // A variável da Thread aqui!

    // Entidades
    public Player player = new Player(this, keyH);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(java.awt.Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    // O método que a sua classe MAIN vai chamar
    public void startGameThread() {
        gameThread = new Thread(this); // Cria a thread passando este painel (Runnable)
        gameThread.start(); // Isso chama automaticamente o método run() abaixo
    }

    @Override
    public void run() {
        // Intervalo de tempo entre cada frame (em nanosegundos)
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update(); // 1: Atualiza informações (posição do player, colisão)
                repaint(); // 2: Desenha na tela (chama o paintComponent)
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
        
        tileM.draw(g);   // Desenha o mapa primeiro
        player.draw(g);  // Desenha o player por cima
        
        g.dispose();
    }
}