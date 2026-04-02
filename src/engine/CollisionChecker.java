package engine;

import entities.Player;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Player p) {
        
        // 1. Definimos as bordas do Player no Mundo
        int pLeftWorldX = p.worldX;
        int pRightWorldX = p.worldX + 40; // largura do player
        int pTopWorldY = p.worldY;
        int pBottomWorldY = p.worldY + 40; // altura do player

        // 2. Traduzimos pixels para "índices" da matriz do mapa
        int pLeftCol = pLeftWorldX / gp.tileSize;
        int pRightCol = pRightWorldX / gp.tileSize;
        int pTopRow = pTopWorldY / gp.tileSize;
        int pBottomRow = pBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

        // 3. Checagem de Colisão para BAIXO (Gravidade)
        // Prevemos onde o pé do player estará no próximo frame
        int nextBottomWorldY = (int) (pBottomWorldY + p.velocityY);
        int nextBottomRow = nextBottomWorldY / gp.tileSize;

        // Evita erro de ArrayIndexOutOfBounds (sair do mapa por baixo)
        if (nextBottomRow < gp.maxWorldRow && nextBottomRow >= 0) {
            
            // Checamos os dois cantos de baixo (esquerda e direita)
            tileNum1 = gp.tileM.mapTileNum[pLeftCol][nextBottomRow];
            tileNum2 = gp.tileM.mapTileNum[pRightCol][nextBottomRow];

            if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
                p.collisionOn = true;
                
                // Ajuste de "Snap": coloca o player exatamente no topo do bloco
                // Isso evita que ele fique "tremendo" ou entre um pouco no chão
                p.worldY = (nextBottomRow * gp.tileSize) - 40;
                p.velocityY = 0;
                p.jumping = false;
            }
        }
    }
}