package main;

import javax.swing.JFrame;
import engine.GamePanel;

public class Main {

    public static void main(String[] args) {

        // Criação da janela do jogo
        JFrame window = new JFrame();

        // Configurações da janela
        window.setTitle("EcoHero");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Criação do painel onde o jogo será executado
        GamePanel gamePanel = new GamePanel();

        // Adiciona o painel do jogo dentro da janela
        window.add(gamePanel);

        // Ajusta o tamanho da janela ao tamanho do GamePanel
        window.pack();

        // Centraliza a janela na tela
        window.setLocationRelativeTo(null);

        // Torna a janela visível
        window.setVisible(true);

        // Inicia o loop principal do jogo (thread do jogo)
        gamePanel.startGameThrend();
    }
}