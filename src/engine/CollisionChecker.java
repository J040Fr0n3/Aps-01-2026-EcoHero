package engine;

import engine.GamePanel;
import entities.Player; // Ou uma classe Entity genérica depois

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Player p) {
        // Calcula as coordenadas em pixels dos limites do Player
        int pLeftX = p.x;
        int pRightX = p.x + 40; // largura do player
        int pTopY = p.y;
        int pBottomY = p.y + 40; // altura do player

        // Converte para coordenadas de matriz (índices)
        int pLeftCol = pLeftX / gp.tileSize;
        int pRightCol = pRightX / gp.tileSize;
        int pTopRow = pTopY / gp.tileSize;
        int pBottomRow = pBottomY / gp.tileSize;

        int tileNum1, tileNum2;

        // Exemplo simplificado para colisão vertical (Gravidade/Pulo)
        // Checa os dois cantos de baixo do player
        tileNum1 = gp.tileM.mapTileNum[pLeftCol][pBottomRow];
        tileNum2 = gp.tileM.mapTileNum[pRightCol][pBottomRow];

        if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
            p.collisionOn = true;
        }
    }
}