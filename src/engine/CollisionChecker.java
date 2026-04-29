package engine;

import entities.Item;
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
            
            if(gp.tileM.tile[t1] == null || gp.tileM.tile[t2] == null) return;
            
            if(t1 == 6 || t2 == 6) {
            	if (p.velocityY > 0) {
            		if (p.trampoLineJumpCount < p.maxTrampoLineJumps) {
            			p.trampoLineJumpCount++;
            		}
            		double boost = 1.0 + (p.trampoLineJumpCount -1) * 0.5;
            		p.velocityY = -10 * boost;
            		
            		p.jumping = true;
            		p.collisionOn = false;
            		return;
            	}
            }

            // 1. Elevador
            if (t1 == 16 || t2 == 16) { 
                p.worldY -= 4; 
                p.velocityY = 0;
                p.jumping = false; 
                p.collisionOn = true; 
                p.onElevator = true;
                return; 
            } else {
            	p.onElevator = false;
            }
            // 2. Sólidos
            if (gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision) {
            	if (t1 == 5 || t2 == 5 || t1 == 4 || t2 == 4) {
            		p.collisionOn = false;
            	} else {
	                p.collisionOn = true;
	                p.velocityY = 0;
	                p.jumping = false;
	                // SNAP: Garante que o player fique exatamente no topo do bloco
	                p.worldY = (nextBottomRow * gp.tileSize) - p.currentHeight;
            	}
            } 
            else {
                p.jumping = true; 
            }
        }
        int centerX = (p.worldX + 20) / gp.tileSize; // Checa pelo centro do player
        int centerY = (p.worldY + p.currentHeight / 2) / gp.tileSize;
        if (centerX >= 0 && centerX < gp.maxWorldCol && centerY >= 0 && centerY < gp.maxWorldRow) {
        	int tileIdCenter = gp.tileM.mapTileNum[centerX][centerY];
        	p.onLadder = (tileIdCenter == 4);// Escada
        	p.inWater = (tileIdCenter == 5); //água
        }
    }
    public int checkItem(Player p) {
        int index = -1;

        for (int i = 0; i < gp.itemM.activeItems.size(); i++) {
            Item item = gp.itemM.activeItems.get(i);

            // Define a área de colisão do player e do item
            // Player: worldX, worldY até tileSize
            // Item: worldX, worldY até tileSize
            
            if (p.worldX < item.worldX + gp.tileSize &&
                p.worldX + gp.tileSize > item.worldX &&
                p.worldY < item.worldY + gp.tileSize &&
                p.worldY + gp.tileSize > item.worldY) {
                
                index = i; // Retorna o índice do item que encostamos
                break;
            }
        }
        return index;
    }
}