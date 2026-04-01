package main;

import javax.swing.JFrame;
import engine.GamePanel;

public class Main {

    public static void main(String[] args) {
        
        // 1. Criar a janela principal
        JFrame window = new JFrame();
        
        // 2. Configurações básicas da janela
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Meu Jogo 2D"); // Nome do seu projeto

        // 3. Instanciar o GamePanel (onde a mágica acontece)
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        // 4. Ajustar o tamanho da janela ao tamanho do GamePanel
        window.pack();

        // 5. Centralizar e exibir
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // 6. Iniciar o Loop do Jogo
        gamePanel.startGameThread();
    }
}