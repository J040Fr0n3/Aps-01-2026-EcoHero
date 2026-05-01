package engine;

import entities.Item;
import entities.Player;
import tile.Tile;
import java.awt.Point;

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
                if ((gp.tileM.tile[t1].collision || gp.tileM.tile[t2].collision)
                	&& t1 != 16 && t2 != 16 && t1 != 8 && t2 != 8 && t1 != 3 && t2 != 3) {
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
            	gp.playSE(0);
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
                
                // --- PLATAFORMA MEIO BLOCO (ID 3) ---
                if (t1 == 3 || t2 == 3) {
                    int topoDaPlataforma = (nextBottomRow * gp.tileSize);
                    if (p.velocityY >= 0 && (p.worldY + p.currentHeight) <= topoDaPlataforma + Math.abs(p.velocityY) + 1) {
                        p.collisionOn = true;
                        p.velocityY = 0;
                        p.jumping = false;
                        p.worldY = topoDaPlataforma - p.currentHeight;
                    } else {
                        // Se ele estiver pulando (velocityY < 0) ou tentando andar pelos lados
                        // a colisão DEVE ser false, senão ele trava "congelado" no lugar
                        p.collisionOn = false;
                    }
                }
                // --- LÓGICA DA NUVEM (ID 8) ---
                else if (t1 == 8 || t2 == 8) {
                    boolean acimaDaNuvem = (p.worldY + p.currentHeight) <= (nextBottomRow * gp.tileSize) + 10;
                    if (p.velocityY > 0 && acimaDaNuvem) {
                        p.collisionOn = true;
                        p.velocityY = 0;
                        p.jumping = false;
                        p.worldY = (nextBottomRow * gp.tileSize) - p.currentHeight;
                        startCloudDisappear(pLeftCol, nextBottomRow);
                        startCloudDisappear(pRightCol, nextBottomRow);
                    } else {
                        p.collisionOn = false; 
                    }
                }
                // --- OUTROS (ÁGUA, ESCADA) ---
                else if (t1 == 5 || t2 == 5 || t1 == 4 || t2 == 4) {
                    p.collisionOn = false;
                } 
                // --- BLOCOS INTEIROS (CHÃO NORMAL, PAREDE) ---
                else {
                    p.collisionOn = true;
                    p.velocityY = 0;
                    p.jumping = false;
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
    
    private void startCloudDisappear(int col, int row) {
        if (gp.tileM.mapTileNum[col][row] == 8) {
            Point p = new Point(col, row);
            
            if (!gp.cloudM.lifeTimer.containsKey(p)) {
                gp.cloudM.lifeTimer.put(p, 30); 
            }
        }
    }
    
}