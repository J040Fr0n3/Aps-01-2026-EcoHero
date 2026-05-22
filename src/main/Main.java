package main;

import javax.swing.JFrame;

import engine.GamePanel;



public class Main {
	
	public static JFrame window;

    public static void main(String[] args) {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("EcoHero");
        GamePanel gp = new GamePanel();
        window.add(gp);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        gp.startGameThread();
    }
}