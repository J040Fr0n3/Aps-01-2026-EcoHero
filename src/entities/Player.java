package entities;

import java.awt.Graphics;
import java.awt.Color;
import engine.GamePanel;
import engine.KeyHandler;

public class Player {

    GamePanel gp;
    KeyHandler keyH;

    // Posição e Atributos
    public int worldX; // 
    public int worldY;
    public int speed = 5;

    // Física
    public double velocityY = 0;
    public double gravity = 0.5;
    public boolean jumping = false;
    
    // Câmera
    public final int screenX;
    public final int screenY;
    
    public boolean collisionOn = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp; 
        this.keyH = keyH;

        // Agora que o gp existe, calculamos o centro da tela
        this.screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        this.screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        // Define a posição inicial no mundo (ex: coluna 10, linha 10)
        this.worldX = gp.tileSize * 5; 
        this.worldY = gp.tileSize * 10;
    }

    public void update() {
        
        // 1. Velocidade (Sprint com CTRL)
        if(keyH.ctrl) { speed = 10; } 
        else { speed = 5; }

        // 2. Movimento Horizontal
        if(keyH.left) {
            if(!checkWallCollision(worldX - speed, worldY)) {
                worldX -= speed;
            }
        }
        if(keyH.right) {
            if(!checkWallCollision(worldX + speed, worldY)) {
                worldX += speed;
            }
        }

        // 3. Pulo
        if(keyH.up && !jumping) {
            velocityY = -12;
            jumping = true;
        }

        // 4. Gravidade e Predição de Movimento
        velocityY += gravity;
        int nextWorldY = (int) (worldY + velocityY);

        // 5. Colisão Vertical ÚNICA (Resolve o treme-treme)
        if (checkWallCollision(worldX, nextWorldY)) {
            if (velocityY > 0) { // Caindo
                // SNAP: Alinha o pé do player exatamente no topo do tile
                // Usamos o nextWorldY para saber em qual linha ele bateria
                worldY = (nextWorldY / gp.tileSize) * gp.tileSize;
                jumping = false;
            } else if (velocityY < 0) { // Batendo a cabeça (opcional)
                worldY = ((nextWorldY / gp.tileSize) + 1) * gp.tileSize;
            }
            velocityY = 0; // Para a força ao colidir
        } else {
            worldY = nextWorldY; // Só move se o caminho estiver livre
        }
    }

    private boolean checkWallCollision(int targetX, int targetY) {
        int left = targetX / gp.tileSize;
        int right = (targetX + 39) / gp.tileSize;
        int top = targetY / gp.tileSize;
        int bottom = (targetY + 39) / gp.tileSize;

        // IMPORTANTE: Agora checamos contra o tamanho do MUNDO, não da tela
        if (left < 0 || right >= gp.maxWorldCol || top < 0 || bottom >= gp.maxWorldRow) {
            return true; 
        }

        return gp.tileM.mapTileNum[left][top] == 1 ||
               gp.tileM.mapTileNum[right][top] == 1 ||
               gp.tileM.mapTileNum[left][bottom] == 1 ||
               gp.tileM.mapTileNum[right][bottom] == 1;
    }

    public void draw(Graphics g) {
        // IMPORTANTE: Desenhar no screenX/Y para ele ficar fixo no centro enquanto o mundo corre
        g.setColor(Color.blue);
        g.fillRect(screenX, screenY, 40, 40);
    }
}