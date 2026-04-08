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
    
    public int trampoLineJumpCount = 0;
    public final int maxTrampoLineJumps = 3;
    
    public boolean onLadder = false;
    
    public boolean inWater = false;
    public int airTimer = 0; // Contador para o sistema de dano futuro
    public final int maxAir = 300; // Exemplo: 5 segundos a 60 FPS

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
        // 1. LÓGICA DE AGACHAR
        if (keyH.down && !onLadder && !inWater) { // Não agacha na escada/água
            if (!isCrouching) {
                worldY += (defaultHeight - crouchHeight);
                isCrouching = true;
            }
            currentHeight = crouchHeight;
            speed = 2;
        } else {
            if (isCrouching) {
                worldY -= (defaultHeight - crouchHeight);
                isCrouching = false;
            }
            currentHeight = defaultHeight;
            speed = keyH.ctrl ? 10 : 5;
        }

        // 2. MOVIMENTO HORIZONTAL
        int horizontalSpeed = inWater ? speed / 2 : speed; // Reduz velocidade na água
        if (keyH.left && !checkWallCollision(worldX - horizontalSpeed, worldY)) worldX -= horizontalSpeed;
        if (keyH.right && !checkWallCollision(worldX + horizontalSpeed, worldY)) worldX += horizontalSpeed;

        // 3. VERIFICAÇÃO DE COLISÃO
        gp.cChecker.checkTile(this); 

        // 4. LÓGICA VERTICAL
        if (onLadder) {
            airTimer = 0;
            velocityY = 0; 
            jumping = false; 
            
            if (keyH.up) worldY -= speed;
            if (keyH.down) worldY += speed;
            // Na escada o movimento é direto no worldY, não precisa de velocityY
        } 
        else if (inWater) {
        	airTimer++;
            velocityY = 1.2; // Gravidade da água
            jumping = false; 

            if (keyH.up) {
            	velocityY = -7;
            } else if(keyH.down) {
            	velocityY = 7;
            }else {
            	velocityY = 1.2;
            }
            worldY += velocityY;
        } else {
            // FÍSICA NORMAL
            airTimer = 0;
            velocityY += gravity;

            if (keyH.up && !jumping) {
                velocityY = isCrouching ? -5 : -10;
                jumping = true;
            }

            // Permite subir se estiver pulando (vel < 0) ou cair se não houver colisão
            if (!collisionOn || velocityY < 0) {
                worldY += velocityY;
            } else {
                velocityY = 0;
                trampoLineJumpCount = 0;
            }
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

        return isSolid(left, top) || isSolid(right, top) || 
                isSolid(left, bottom) || isSolid(right, bottom);
    }
    private boolean isSolid(int col, int row) {
    	int tileID = gp.tileM.mapTileNum[col][row];
    	return gp.tileM.tile[tileID].collision;
    }

    public void draw(Graphics g) {
        // IMPORTANTE: Desenhar no screenX/Y para ele ficar fixo no centro enquanto o mundo corre
        g.setColor(Color.blue);
        g.fillRect(screenX, screenY, 40, currentHeight);
    }
}