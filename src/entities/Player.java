package entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
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
    
    public int currentFootstepSound = -1;
    boolean isWalking = false;
    
    public boolean waterSoundPlaying = false;
    public boolean waterToxicSoundPlaying = false;
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
    public final int maxTrampoLineJumps = 2;
    public int trampoSoundTimer = 0;
    
    public boolean onLadder = false;
    
    public boolean onElevator = false;
    
    public boolean inWater = false;
    public int airTimer = 0; // Contador para o sistema de dano futuro
    
    public java.util.ArrayList<String> inventory = new java.util.ArrayList<>();
    public int maxInventorySize = 3;
    
    public boolean inToxicWater = false;
    
    public final int visualWidth = 80;  
    public final int visualHeight = 80;
    
    public boolean pertoDaLixeira = false;
    
    public Image walkGif;
    public BufferedImage idle, jump;
    private String direction = "right";

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
        
        getPlayerImage();
    }
    
    public void getPlayerImage() {
    	idle = setup("/textures/player_stopeted.png");
    	jump = setup("/textures/player_jumping.png");
    	
    	try {
    		walkGif = java.awt.Toolkit.getDefaultToolkit().createImage(getClass().getResource("/textures/player_walking.gif"));
    	} catch (Exception e) {
    		System.err.println("Erro ao carregar Gif: " + e.getMessage());
    	}
    }
    public BufferedImage setup(String imagePath) {
        try {
            return javax.imageio.ImageIO.read(getClass().getResource(imagePath));
        } catch (java.io.IOException | IllegalArgumentException e) {
            System.err.println("Erro ao carregar textura do player: " + imagePath);
            return null;
        }
    }

    public void update() {
    	if(trampoSoundTimer > 0) trampoSoundTimer--;
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
            	if (!checkWallCollision(worldX, worldY - (defaultHeight - crouchHeight))) {
	                worldY -= (defaultHeight - crouchHeight);
	                isCrouching = false;
            	}else {
            		currentHeight = crouchHeight;
            		speed = 2;
            	}
            }else {
	            currentHeight = defaultHeight;
	            speed = keyH.ctrl ? 7 : 5;
            }
        }
        
        if(keyH.interact) {
        	int col = worldX / gp.tileSize;
        	int row = (worldY + currentHeight / 2) / gp.tileSize;
        	int tileID = gp.tileM.mapTileNum[col][row];
        	
        	if(tileID == 7) {
        		int destino = gp.levelM.getCurrentLevel().bueiroDestino;
        		gp.levelM.currentLevelIndex = destino;
        		gp.levelM.loadCurrentLevel();
        		
        		keyH.interact = false;
        	}
        }
        
        // 2. MOVIMENTO HORIZONTAL 
        int horizontalSpeed = inWater ? speed / 2 : speed;
        if (keyH.left && !checkWallCollision(worldX - horizontalSpeed, worldY)) {
        	direction = "right";
        	worldX -= horizontalSpeed;
        }
        if (keyH.right && !checkWallCollision(worldX + horizontalSpeed, worldY)) {
        	direction = "left";
        	worldX += horizontalSpeed;
        } 
        
        
        isWalking = (keyH.left || keyH.right) && !jumping && !onLadder && !inWater && !inToxicWater && !onElevator;
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
        	
        	if (currentFootstepSound != -1 && !onLadder) {
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
            
            int ladderSpeed = 3;
            
            if (keyH.up) {
                // --- CHECAGEM DE TOPO: O jogador quer subir na plataforma? ---
                int pLeftCol = worldX / gp.tileSize;
                int pRightCol = (worldX + 39) / gp.tileSize;
                
                // Verificamos a linha exatamente onde estão os pés do jogador
                int currentFeetRow = (worldY + currentHeight) / gp.tileSize;
                
                // Verificamos o bloco que está exatamente NA LINHA DOS PÉS dele
                int tileIdPeEsquerdo = gp.tileM.mapTileNum[pLeftCol][currentFeetRow];
                int tileIdPeDireito = gp.tileM.mapTileNum[pRightCol][currentFeetRow];
                
                // SE OS PÉS do jogador chegaram no último bloco de escada (ou seja, o bloco de baixo NÃO é escada)
                // Significa que a cintura/cabeça dele já passou do chão!
                if (tileIdPeEsquerdo != 4 && tileIdPeDireito != 4) {
                    
                    // 1. Desliga o estado de escada imediatamente
                    onLadder = false;
                    
                    // 2. Dá um "puxão" físico para cima para garantir que ele suba o degrau por completo
                    worldY -= 12; // Teleporta ele um pouquinho para cima da quina do chão
                    
                    // 3. Aplica uma força de pulo vertical decente para ele vencer a gravidade do frame seguinte
                    velocityY = -7; 
                    jumping = true;
                    
                    System.out.println("Subida finalizada! EcoHero impulsionado para fora da escada.");
                    return; 
                }
                
                // Se ainda não chegou no topo, apenas continua subindo a escada
                worldY -= ladderSpeed;
            }
            else if (keyH.down) {
                worldY += ladderSpeed;
            }

            // --- GERENCIAMENTO DO SOM DA ESCADA ---
            if (keyH.up || keyH.down) {
                if (currentFootstepSound != 12) { 
                    gp.stopMusic(); 
                    gp.playMusic(12); 
                    currentFootstepSound = 12;
                }
            } else {
                if (currentFootstepSound == 12) {
                    gp.stopMusic();
                    currentFootstepSound = -1;
                }
            }
            
            gp.cChecker.checkTile(this);
            atualizarAr(true);
            return;
        }
        if (inWater || inToxicWater) {
        	
        	
        	if (currentFootstepSound != -1) {
        		gp.stopMusic();
        		currentFootstepSound = -1;
        	}
        	if(inToxicWater) {
        		velocityY = 1.5;
        		if (keyH.down) {
        			velocityY = -.5;
        		}
        	} else {
        		if(velocityY >= -7) {
	        		velocityY = 1.2; 
	        		if (keyH.up) velocityY = -7;
	        		else if(keyH.down) velocityY = 7;
	        	}
        	}
	        	
            worldY += velocityY;

            // --- LÓGICA DE ÁGUA TÓXICA ---
            if (inToxicWater) {
                if (!waterToxicSoundPlaying) {
                	gp.stopMusic();
                    gp.playMusic(11);
                    waterToxicSoundPlaying = true;
                    waterSoundPlaying = false;
                }

                airRecoveryCounter++; 
                if (airRecoveryCounter >= 60) {
                    if(life > 0) life -= 1;
                    gp.playSE(1);
                    airRecoveryCounter = 0;
                }
                airTimer = 0; 
            } 
            // --- LÓGICA DE ÁGUA NORMAL ---
            else if (inWater) {
                if (!waterSoundPlaying) {
                	gp.stopMusic();
                    gp.playMusic(2);
                    waterSoundPlaying = true;
                    waterToxicSoundPlaying = false;
                }

                // Sistema de Fôlego
                airTimer++;
                if (airTimer >= 600) {
                    airRecoveryCounter++;
                    if (airRecoveryCounter >= 90) {
                        if(life > 0) life -= 1;
                        gp.playSE(1);
                        airRecoveryCounter = 0;
                    }
                }
            }
        } else {
            // FÍSICA NORMAL (FORA DA ÁGUA)
        	
        	if (waterSoundPlaying || waterToxicSoundPlaying) {
        		gp.stopMusic();
        		waterSoundPlaying = false;
        		waterToxicSoundPlaying = false;
        	}
        	
        	if(onElevator){
        		if (currentFootstepSound != -1) {
        			gp.stopMusic();
        			currentFootstepSound = -1;
        		}
        		
        		if(!elevatorSoundPlaying) {
        			gp.stopMusic();
        			gp.playMusic(3);
        			elevatorSoundPlaying = true;
        		}
        		worldY -= 4;
        	}else {
        		if (elevatorSoundPlaying) {
        			gp.stopMusic();
        			elevatorSoundPlaying = false;
        		}
        	}
        	
            velocityY += gravity;

            if (keyH.up && !jumping && !onElevator) {
                velocityY = isCrouching ? -5 : -10;
                jumping = true;
                
               gp.playSE(8);
            }

            if (!collisionOn || velocityY < 0) {
                worldY += velocityY;
            
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

        // 1. Identifica o tile onde o player está
        int col = worldX / gp.tileSize;
        int row = (worldY + currentHeight / 2) / gp.tileSize;
        int tileID = gp.tileM.mapTileNum[col][row];

        // 2. Verifica se o tile é uma lixeira (IDs 10 a 14)
        if (tileID >= 10 && tileID <= 14) {
            String itemParaDescartar = inventory.get(0);
            boolean acertoLixeiraCorreta = false;

            // Lógica de Validação: Item vs Lixeira
            if (itemParaDescartar.equalsIgnoreCase("papel") && tileID == 10) acertoLixeiraCorreta = true;
            else if (itemParaDescartar.equalsIgnoreCase("vidro") && tileID == 11) acertoLixeiraCorreta = true;
            else if (itemParaDescartar.equalsIgnoreCase("metal") && tileID == 12) acertoLixeiraCorreta = true;
            else if (itemParaDescartar.equalsIgnoreCase("plastico") && tileID == 13) acertoLixeiraCorreta = true;
            else if (itemParaDescartar.equalsIgnoreCase("organico") && tileID == 14) acertoLixeiraCorreta = true;

            if (acertoLixeiraCorreta) {
                // --- NOVA TRAVA DE SEGURANÇA ---
                engine.LevelConfig config = gp.levelM.getCurrentLevel();
                int objetivo = config.itemsRequired.getOrDefault(itemParaDescartar, 0);
                int jaEntregues = gp.itensEntreguesFase.getOrDefault(itemParaDescartar, 0);

                if (jaEntregues < objetivo) {
                	
                	int pontosBase = 10;
                	
                	switch (itemParaDescartar.toLowerCase()) {
                	case "papel":
                		pontosBase = 13;
                		break;
                	case "plastico":
                		pontosBase = 23;
                		break;
                	case "metal":
                		pontosBase = 33;
                		break;
                	case "vidro":
                		pontosBase = 43;
                		break;
                	case "organico":
                		pontosBase = 86;
                		break;
                	}
                	
                    gp.score += (pontosBase * 1.25 * gp.comboMultiplier);
                    
                    
                    gp.itensColetadosTotal++; 
                    gp.correctSequence++;
                    
                    gp.itensEntreguesFase.put(itemParaDescartar, jaEntregues + 1);

                    if (gp.comboMultiplier < 5) {
                        gp.comboMultiplier++;
                    }
                    gp.playSE(4); // Som de sucesso (ajuste o ID se necessário)
                    System.out.println("Acertou! Task " + itemParaDescartar + ": " + (jaEntregues + 1) + "/" + objetivo);
                } else {
                    // REGRA DE META BATIDA: O item é descartado, mas não soma pontos nem barra
                    System.out.println("Meta de " + itemParaDescartar + " já atingida. Item reciclado sem pontos extras.");
                    // Você pode tocar um som neutro aqui
                }
            } else {
                // REGRA DE ERRO: Errou a lixeira
                gp.score -= 10;
                if (gp.score < 0) gp.score = 0;
                gp.comboMultiplier = 1;
                gp.correctSequence = 0;
                System.out.println("Errou a lixeira! Combo resetado.");
                // gp.playSE(ID_SOM_ERRO); 
            }

            // Remove o item do inventário após a tentativa (sucesso, meta batida ou erro)
            inventory.remove(0);
            
            checkLevelCompletion();
        }
    }
    
    private void checkLevelCompletion () {
    	if (gp.itensColetadosTotal >= gp.totalItemsNoNivel) {
    		gp.gameState = gp.finishState;
    		inventory.clear();
    		gp.stopFundo();
    		System.out.println("Fase concluída! Itens totais: " + gp.itensColetadosTotal);
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
    	
    	Graphics g2 = (Graphics2D) g;
    	java.awt.Image currentImage = null;
    	
    	if(jumping || inWater || inToxicWater || onLadder) {
    		currentImage = jump;
    	} else if (keyH.left || keyH.right) {
    		currentImage = walkGif;
    	} else {
    		currentImage = idle;
    	}
    	
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
        
        int offsetX = (visualWidth - 40) / 2;
        int offsetY = (visualHeight - currentHeight);
        
        if (currentImage != null) {
            if (direction.equals("right")) {
                // Inverte usando visualWidth
                g2.drawImage(currentImage, x - offsetX + visualWidth, y - offsetY, -visualWidth, visualHeight, gp);
            } else {
                g2.drawImage(currentImage, x - offsetX, y - offsetY, visualWidth, visualHeight, gp);
            }
        }
        // g.drawRect(x, y, 40, currentHeight);
    }
    
    public void concluirFase() {
    	gp.levelM.unlockNextLevel();
    	
    	if(gp.levelM.currentLevelIndex == gp.levelM.levels.size() -1) {
    		// Função para registrar tudo no banco
    		gp.gameState = gp.scoreState;
    	} else {
    		gp.gameState = gp.scoreState;
    	}
    }
    
}