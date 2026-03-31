package entities;

import java.awt.Graphics;
import java.awt.Color;

import engine.GamePanel;
import engine.KeyHandler;

public class Player {

    GamePanel gp;
    KeyHandler keyH;

    int x;
    int y;

    int speed = 5;

    double velocityY = 0;
    double gravity = 0.5;

    boolean jumping = false;

    int groundLevel;
    
    public int worldX;
    public int worldY;

    // Construtor que recebe o GamePanel e o KeyHandler e define a posição inicial do jogador e o nível do chão
    public Player(GamePanel gp, KeyHandler keyH) {

        this.gp = gp;
        this.keyH = keyH;

        x = 50;
        y = 400;

        groundLevel = gp.screenHeight - 100;
    }

    // Atualiza o estado do jogador (movimento lateral, pulo e aplicação da gravidade)
    public void update() {
    	
    	if(keyH.ctrl) {
    		speed = 10;
    	} else {
    		speed = 5;
    	}

        if(keyH.left) {
            x -= speed;
        }

        if(keyH.right) {
            x += speed;
        }

        if(keyH.up && !jumping) {

            velocityY = -10;
            jumping = true;

        }

        velocityY += gravity;
        y += velocityY;

        if(y >= groundLevel) {

            y = groundLevel;
            velocityY = 0;
            jumping = false;

        }
    }

    // Desenha o jogador na tela como um retângulo azul
    public void draw(Graphics g) {

        g.setColor(Color.blue);

        g.fillRect(x, y, 40, 40);
        
        g.setColor(Color.GRAY);
        g.fillRect(0, 516, 800, 50);
    }
}