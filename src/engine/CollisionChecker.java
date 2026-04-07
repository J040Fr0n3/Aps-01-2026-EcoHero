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
        
        // Onde o "pé" do player estaria no próximo frame
        int nextBottomWorldY = (int) (p.worldY + p.currentHeight + p.velocityY);
        int nextBottomRow = nextBottomWorldY / gp.tileSize;

        if (nextBottomRow < gp.maxWorldRow && nextBottomRow >= 0) {
        	int t1 = gp.tileM.mapTileNum[pLeftCol][nextBottomRow];
            int t2 = gp.tileM.mapTileNum[pRightCol][nextBottomRow];

            // 1. Prioridade: Elevador (ID 16)
            if (t1 == 16 || t2 == 16) { 
                p.worldY -= 2; 
                p.velocityY = 0;
                p.jumping = false; 
                p.collisionOn = true; 
                return; 
            }

            // 2. Sólidos (Paredes, Chão, Lixeiras)
            if (gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision) {
                p.collisionOn = true;
                p.velocityY = 0;
                p.jumping = false;
                p.worldY = (nextBottomRow * gp.tileSize) - p.currentHeight;
            } 
            else {
                // 3. Vazio ou Tiles atravessáveis
                p.jumping = true; 
            }
        }
    }
}