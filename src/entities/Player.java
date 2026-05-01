package entities;

import java.awt.Graphics;
import java.awt.Color;
import engine.GamePanel;
import engine.KeyHandler;

public class Player {

    GamePanel gp;
    KeyHandler keyH;
    //agachamento player
    
    public int maxLife = 6;
    public int life = 6;
    public int lifeRecoveryCounter = 0;
    
    int currentFootstepSound = -1;
    boolean isWalking = false;
    
    boolean waterSoundPlaying = false;
    public boolean elevatorSoundPlaying = false;
    
    public int airRecoveryCounter = 0;
    
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
    
    public boolean onElevator = false;
    
    public boolean inWater = false;
    public int airTimer = 0; // Contador para o sistema de dano futuro
    
    public java.util.ArrayList<String> inventory = new java.util.ArrayList<>();
    public int maxInventorySize = 3;

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
        if (keyH.down && !onLadder && !inWater) {
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
            speed = keyH.ctrl ? 7 : 5;
        }

        // 2. MOVIMENTO HORIZONTAL 
        int horizontalSpeed = inWater ? speed / 2 : speed;
        if (keyH.left && !checkWallCollision(worldX - horizontalSpeed, worldY)) worldX -= horizontalSpeed;
        if (keyH.right && !checkWallCollision(worldX + horizontalSpeed, worldY)) worldX += horizontalSpeed;
        
        
        isWalking = (keyH.left || keyH.right) && !jumping && !onLadder && !inWater && !onElevator;
        //Bug de ficar tocando o som repetidamente sem parar
        if (isWalking) {
        	int col = worldX / gp.tileSize;
        	int row = (worldY + currentHeight + 1) / (gp.tileSize);
        	
        	int tileAbaixo = gp.tileM.mapTileNum[col][row];
        	int novoSom;
        	
        	// ADICIONAR AQUI NOVOS ID's
        	if(tileAbaixo == 3) novoSom = 6;
        	else if (tileAbaixo == 8) novoSom = 7;
        	else novoSom = 5;
        	
        	if (novoSom != currentFootstepSound) {
        		gp.stopMusic();
        		gp.playMusic(novoSom);
        		currentFootstepSound = novoSom;
        	}
        } else {
        	if (currentFootstepSound != -1) {
        		gp.stopMusic();
        		currentFootstepSound = -1;
        	}
        }
        
        // 3. VERIFICAÇÃO DE COLISÃO
        gp.cChecker.checkTile(this); 

        // 4. LÓGICA VERTICAL E SISTEMA DE AR
        if (onLadder) {
            velocityY = 0; 
            jumping = false; 
            if (keyH.up) worldY -= speed;
            if (keyH.down) worldY += speed;
        } 
        else if (inWater) {
        	if(!waterSoundPlaying) {
        		gp.playMusic(2);
        		waterSoundPlaying = true;
        	}
        	
            airTimer++;
            if (airTimer > 600) {
            	airTimer = 600;
            }
            velocityY = 1.2; 
            jumping = false; 

            if (keyH.up) velocityY = -7;
            else if(keyH.down) velocityY = 7;
            else velocityY = 1.2;
            
            worldY += velocityY;
            
            // Dano por sufocamento (após 10 segundos)
            if (airTimer >= 600) {
            	
            	airRecoveryCounter++;
            	
                if (airRecoveryCounter >= 90) {
                    if(life > 0) {
                    	life -= 1;
                    	gp.playSE(1);
                    	System.out.println("Sufocando! Vida: " + life);
                    }
                    airRecoveryCounter = 0;
                }
            } else {
            	airRecoveryCounter = 0;
            }
        } else {
        	
        	if (waterSoundPlaying) {
        		gp.stopMusic();
        		waterSoundPlaying = false;
        	}
            // FÍSICA NORMAL (FORA DA ÁGUA)
            velocityY += gravity;

            if (keyH.up && !jumping && !onElevator) {
                velocityY = isCrouching ? -5 : -10;
                jumping = true;
            }

            if (!collisionOn || velocityY < 0) {
                worldY += velocityY;
            } else {
                velocityY = 0;
                trampoLineJumpCount = 0;
            }
            
            
            atualizarAr(false);
        }

        // 5. ITENS E DESCARTE
        if (keyH.descarte) {
            discardItem();
            keyH.descarte = false;
        }
        
        int itemIndex = gp.cChecker.checkItem(this);
        if (itemIndex != -1) {
            if (inventory.size() < maxInventorySize) {
                String type = gp.itemM.activeItems.get(itemIndex).type;
                
                gp.playSE(4);
                
                inventory.add(type);
                gp.itemM.activeItems.remove(itemIndex);
            } else {
                System.out.println("Inventário cheio!");
            }
        }
        
     // 6. RECUPERAÇÃO DE VIDA (1 HP a cada 10 segundos)
        if (!inWater && life < maxLife) {
            lifeRecoveryCounter++;
            
            if (lifeRecoveryCounter >= 600) { // 600 frames = 10 segundos
                life++;
                lifeRecoveryCounter = 0;
                System.out.println("Vida recuperada automaticamente! Vida: " + life);
            }
        } else {
            // Reseta se entrar na água (interrompe cura) ou se a vida já estiver cheia
            lifeRecoveryCounter = 0;
        }
    }
    
    private void atualizarAr(boolean naEscada) {
        if (airTimer > 0) {
            airRecoveryCounter++; 
            if (airRecoveryCounter >= 90) {
                airTimer -= 60; 
                if (airTimer < 0) airTimer = 0;
                airRecoveryCounter = 0;
            }
        } else {
            airRecoveryCounter = 0;
        }
    }
    
    public void discardItem() {
        if (inventory.isEmpty()) return;

        // 1. Identifica em qual tile o player está tentando interagir
        // Vamos checar o tile à frente ou no centro do player
        int col = worldX / gp.tileSize;
        int row = (worldY + currentHeight / 2) / gp.tileSize;
        int tileID = gp.tileM.mapTileNum[col][row];

        // 2. Verifica se o tile é uma lixeira (IDs 10 a 14)
        if (tileID >= 10 && tileID <= 14) {
            String itemParaDescartar = inventory.get(0);
            boolean acerto = false;

            // Lógica de Validação: Item vs Lixeira
            if (itemParaDescartar.equals("papel") && tileID == 10) acerto = true;
            else if (itemParaDescartar.equals("vidro") && tileID == 11) acerto = true;
            else if (itemParaDescartar.equals("metal") && tileID == 12) acerto = true;
            else if (itemParaDescartar.equals("plastico") && tileID == 13) acerto = true;
            else if (itemParaDescartar.equals("organico") && tileID == 14) acerto = true;

            if (acerto) {
                // Regra de Acerto: Soma 10 * Multiplicador
                gp.score += (10 * gp.comboMultiplier);
                gp.itensColetadosTotal++;
                gp.correctSequence++;
                
                // Sobe o multiplicador a cada acerto (limite x5)
                if (gp.comboMultiplier < 5) {
                    gp.comboMultiplier++;
                }
                System.out.println("Acertou! Score: " + gp.score + " Combo: x" + gp.comboMultiplier);
            } else {
                // Regra de Erro: Subtrai 10 e reseta combo
                gp.score -= 10;
                if (gp.score < 0) gp.score = 0; // Evita score negativo se preferir
                gp.comboMultiplier = 1;
                gp.correctSequence = 0;
                System.out.println("Errou a lixeira! Combo resetado.");
            }

            // Remove o item do inventário após a tentativa
            inventory.remove(0);
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
    	
    	if (tileID == 8 || tileID == 3) {
    		return false;
    	}
    	
    	return gp.tileM.tile[tileID].collision;
    }

    public void draw(Graphics g) {
        int x = screenX;
        int y = screenY;

        // Ajusta a posição visual do player se a câmera estiver travada na borda
        
        // Eixo X
        if (screenX > worldX) {
            x = worldX;
        } else {
            int rightOffset = gp.screenWidth - screenX;
            if (rightOffset > gp.worldWidth - worldX) {
                x = gp.screenWidth - (gp.worldWidth - worldX);
            }
        }

        // Eixo Y
        if (screenY > worldY) {
            y = worldY;
        } else {
            int bottomOffset = gp.screenHeight - screenY;
            if (bottomOffset > gp.worldHeight - worldY) {
                y = gp.screenHeight - (gp.worldHeight - worldY);
            }
        }

        g.setColor(Color.blue);
        g.fillRect(x, y, 40, currentHeight);
    }
}