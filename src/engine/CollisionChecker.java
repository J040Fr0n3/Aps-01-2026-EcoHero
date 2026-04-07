package engine;

import entities.Player;
import tile.Tile;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Player p) {
        p.collisionOn = false;

        // Bordas do player (usando 39 para evitar colisão com tiles do lado)
        int pLeftCol = p.worldX / gp.tileSize;
        int pRightCol = (p.worldX + 39) / gp.tileSize;
        
        // -- Checagem de Teto
        if(p.velocityY < 0) {
        	int nextTopWordY = (int) (p.worldY + p.velocityY);
        	int nextTopRow = nextTopWordY / gp.tileSize;
        	if (nextTopRow >= 0) {
        		int t1 = gp.tileM.mapTileNum[pLeftCol][nextTopRow];
                int t2 = gp.tileM.mapTileNum[pRightCol][nextTopRow];

                // Se o bloco de cima for sólido e NÃO for um elevador (G/16)
                if ((gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision) && t1 != 16 && t2 != 16) {
                    p.velocityY = 0; // Para o pulo imediatamente
                    p.worldY = (nextTopRow + 1) * gp.tileSize; // Empurra o player para baixo do bloco
                }
        	}
        }
        // -- Checagem de chão
        int nextBottomWorldY = (int) (p.worldY + p.currentHeight + p.velocityY);
        int nextBottomRow = nextBottomWorldY / gp.tileSize;
        
        if (nextBottomRow >= 0 && nextBottomRow < gp.maxWorldRow) {
            int t1 = gp.tileM.mapTileNum[pLeftCol][nextBottomRow];
            int t2 = gp.tileM.mapTileNum[pRightCol][nextBottomRow];

            // 1. Elevador
            if (t1 == 16 || t2 == 16) { 
                p.worldY -= 2; 
                p.velocityY = 0;
                p.jumping = false; 
                p.collisionOn = true; 
                return; 
            }

            // 2. Sólidos
            if (gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision) {
                p.collisionOn = true;
                p.velocityY = 0;
                p.jumping = false;
                // SNAP: Garante que o player fique exatamente no topo do bloco
                p.worldY = (nextBottomRow * gp.tileSize) - p.currentHeight;
            } 
            else {
                p.jumping = true; 
            }
        }
    }
}