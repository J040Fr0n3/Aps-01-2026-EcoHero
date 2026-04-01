package entities;

import java.awt.Graphics;
import java.awt.Color;
import engine.GamePanel;
import engine.KeyHandler;

public class Player {

    GamePanel gp;
    KeyHandler keyH;

    // Posição na tela
    public int x;
    public int y;
    public int speed = 5;

    // Física
    public double velocityY = 0;
    public double gravity = 0.5;
    public boolean jumping = false;
    
    // Nova variável para controlar a colisão
    public boolean collisionOn = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        // Posição inicial
        x = 100;
        y = 100;
    }

    public void update() {
        
        // Ajuste de velocidade (Sprint)
        if(keyH.ctrl) {
            speed = 10;
        } else {
            speed = 5;
        }

        // --- MOVIMENTAÇÃO LATERAL COM CHECAGEM ---
        if(keyH.left) {
            // Verifica colisão antes de mover
            if(!checkWallCollision(x - speed, y)) {
                x -= speed;
            }
        }
        if(keyH.right) {
            if(!checkWallCollision(x + speed, y)) {
                x += speed;
            }
        }

        // --- LÓGICA DE PULO E GRAVIDADE ---
        if(keyH.up && !jumping) {
            velocityY = -12; // Força do pulo
            jumping = true;
        }

        velocityY += gravity;
        int nextY = (int) (y + velocityY);

        // Checagem de colisão Vertical (Chão e Teto)
        if (checkWallCollision(x, nextY)) {
            if (velocityY > 0) { // Caindo
                // Ajusta o player exatamente no topo do tile
                y = ((y + 40) / gp.tileSize) * gp.tileSize - 40;
                jumping = false;
            }
            velocityY = 0;
        } else {
            y = nextY;
        }
    }

    // Método auxiliar para facilitar a lógica (pode ser movido para o CollisionChecker depois)
    private boolean checkWallCollision(int targetX, int targetY) {
        // Define os 4 cantos do Player para checagem
        int left = targetX / gp.tileSize;
        int right = (targetX + 39) / gp.tileSize; // 39 pois o player tem 40px
        int top = targetY / gp.tileSize;
        int bottom = (targetY + 39) / gp.tileSize;

        // Evita erro de índice fora da matriz
        if (left < 0 || right >= gp.maxScreenCol || top < 0 || bottom >= gp.maxScreenRow) {
            return true; 
        }

        // Se qualquer um dos cantos tocar em um tile tipo 1 (chão)
        return gp.tileM.mapTileNum[left][top] == 1 ||
               gp.tileM.mapTileNum[right][top] == 1 ||
               gp.tileM.mapTileNum[left][bottom] == 1 ||
               gp.tileM.mapTileNum[right][bottom] == 1;
    }

    public void draw(Graphics g) {
        // Desenha o jogador
        g.setColor(Color.blue);
        g.fillRect(x, y, 40, 40);
        
        // O chão fixo (cinza) foi removido daqui, 
        // pois agora o TileManager desenha o mapa.
    }
}