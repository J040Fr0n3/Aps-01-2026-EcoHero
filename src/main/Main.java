package main;

import javax.swing.JFrame;

import engine.GamePanel;



public class Main {
	
	public static JFrame window;

    public static void main(String[] args) {
        
        window = new JFrame();
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("EcoHero");
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        gamePanel.startGameThread();
    }
}