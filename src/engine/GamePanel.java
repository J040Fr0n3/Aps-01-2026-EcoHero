package engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import entities.Player;

public class GamePanel extends JPanel implements Runnable{
	
	public final int tileSize = 48;
	public final int screenCol = 16;
	public final int screenRow = 12;
	
	public final int screenWidth = tileSize * screenCol;
	public final int screenHeight = tileSize * screenRow;
	
	
	Thread gameThrend;
	
	KeyHandler KeyH = new KeyHandler();
	Player player = new Player(this, KeyH);
	
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(KeyH);
		this.setFocusable(true);
	}
	
	public void startGameThrend() {
		
		gameThrend = new Thread(this);
		gameThrend.start();
	}
	
	@Override
	public void run() {
		double drawInterval = 1000000000 / 60;
        double delta = 0;

        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThrend != null) {

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
	public void paintComponent(Graphics g) {

        super.paintComponent(g);

        player.draw(g);

        g.dispose();
    }
	

        
    
	
}
