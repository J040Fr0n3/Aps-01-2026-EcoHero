package engine;

import entities.Player;

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
        int nextBottomWorldY = (int) (p.worldY + 40 + p.velocityY);
        int nextBottomRow = nextBottomWorldY / gp.tileSize;

        if (nextBottomRow < gp.maxWorldRow && nextBottomRow >= 0) {
            int tileNum1 = gp.tileM.mapTileNum[pLeftCol][nextBottomRow];
            int tileNum2 = gp.tileM.mapTileNum[pRightCol][nextBottomRow];

            if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                p.collisionOn = true;
                p.velocityY = 0;
                p.jumping = false;

                // SNAP: Coloca o player exatamente no topo do tile colidido
                // Isso mata o tremor
                p.worldY = (nextBottomRow * gp.tileSize) - 40;
            }
        }
    }
}