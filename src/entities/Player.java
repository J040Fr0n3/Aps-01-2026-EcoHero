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
        if (keyH.down && !onLadder) {
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
        if (keyH.left && !checkWallCollision(worldX - speed, worldY)) worldX -= speed;
        if (keyH.right && !checkWallCollision(worldX + speed, worldY)) worldX += speed;

        // 3. VERIFICAÇÃO DE COLISÃO E ESCADA
        gp.cChecker.checkTile(this); // Isso define p.collisionOn e p.onLadder

        // 4. LÓGICA DE MOVIMENTAÇÃO VERTICAL (ESCADA VS GRAVIDADE)
        if (onLadder) {
            velocityY = 0; // Anula gravidade na escada
            jumping = false; 
            
            if (keyH.up) worldY -= speed;
            if (keyH.down) worldY += speed;
            
            // Opcional: Permitir pular para FORA da escada
            /*if (keyH.up) { // Use uma tecla diferente ou verifique Shift
                 velocityY = -4;
                 onLadder = false;
            }*/
        } else {
            // GRAVIDADE NORMAL
            velocityY += gravity;

            // PULO (Só funciona se NÃO estiver no ar/jumping)
            if (keyH.up && !jumping) {
                trampoLineJumpCount = 0;
                if (isCrouching) {
                    velocityY = -5;
                } else {
                    velocityY = -10;
                }
                jumping = true;
                collisionOn = false; // Força a saída do chão
            }
        }

        // 5. APLICAÇÃO FINAL DO MOVIMENTO Y
        if (!collisionOn || velocityY < 0) { // Se não há colisão embaixo OU se está subindo (pulo)
            worldY += velocityY;
        } else {
            velocityY = 0;
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