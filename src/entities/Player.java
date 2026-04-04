package entities;

import java.awt.Graphics;
import java.awt.Color;
import engine.GamePanel;
import engine.KeyHandler;

public class Player {

    GamePanel gp;
    KeyHandler keyH;
    //agachamento player
    public boolean isCrouching = false;
    public int currentHeight;
    public final int defaultHeight = 40;
    public final int crouchHeight = 20;
    
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
        this.currentHeight = defaultHeight;

        // Agora que o gp existe, calculamos o centro da tela
        this.screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        this.screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        // Define a posição inicial no mundo (ex: coluna 10, linha 10)
        this.worldX = gp.tileSize * 5; 
        this.worldY = gp.tileSize * 10;
    }

    public void update() {
        // 1. lógica de agachar (apenas muda a variavel de altura)
    	if (keyH.down) { // Supondo que você mapeou 'S' ou 'Seta Baixo' no KeyHandler
            isCrouching = true;
            currentHeight = crouchHeight;
            speed = 2;
        } else {
            isCrouching = false;
            currentHeight = defaultHeight;
            speed = keyH.ctrl ? 10: 5;
        }

        // 2. Movimento Horizontal
        if (keyH.left && !checkWallCollision(worldX - speed, worldY)) worldX -= speed;
        if (keyH.right && !checkWallCollision(worldX + speed, worldY)) worldX += speed;

        // 3. Pulo (Impedido se estiver agachado)
        if (keyH.up && !jumping && !isCrouching) {
            velocityY = -10;
            jumping = true;
        }

        // 4. Gravidade e Colisão
        velocityY += gravity;
        gp.cChecker.checkTile(this);
        
        if (!collisionOn) {
            worldY += velocityY;
        }
    }

    private boolean checkWallCollision(int targetX, int targetY) {
        int left = targetX / gp.tileSize;
        int right = (targetX + 39) / gp.tileSize;
        int top = targetY / gp.tileSize;
        int bottom = (targetY + currentHeight - 1) / gp.tileSize;

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
        g.fillRect(screenX, screenY, 40, currentHeight);
    }
}