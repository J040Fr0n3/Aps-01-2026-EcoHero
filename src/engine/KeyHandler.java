package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean up, left, right, ctrl, down, descarte, interact;
    public boolean nextLevelRequested = false;
    
    private final int[] konamiCode = {
    	    KeyEvent.VK_UP, KeyEvent.VK_UP, 
    	    KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, 
    	    KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
    	    KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
    	    KeyEvent.VK_B, KeyEvent.VK_A
    	};
    	private int konamiIndex = 0;
    	public String raParaDeletar = "";

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    	char c = e.getKeyChar();
        if (gp.gameState == gp.dataInputState) {
            if (Character.isLetterOrDigit(c) || c == ' ') {
                if (gp.ui.subState == 0 && gp.ui.playerName.length() < 15) gp.ui.playerName += c;
                if (gp.ui.subState == 1 && gp.ui.playerRA.length() < 10) gp.ui.playerRA += Character.toUpperCase(c);
            }
        }
        else if (gp.gameState == gp.adminDeleteState) {
            if (Character.isLetterOrDigit(c) && raParaDeletar.length() < 10) {
                raParaDeletar += c;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // --- 1. CHECAGEM ISOLADA DO CÓDIGO KONAMI ---
        // (Roda em paralelo, sem travar os 'else if' debaixo)
        if (gp.gameState == gp.titleState || gp.gameState == gp.scoreState) {
            if (code == konamiCode[konamiIndex]) {
                konamiIndex++;
                if (konamiIndex == konamiCode.length) {
                    konamiIndex = 0; 
                    
                    if (gp.gameState == gp.titleState) {
                        gp.gameState = gp.creditsState; 
                        System.out.println("Easter Egg ativado: Tela de Créditos!");
                    } 
                    else if (gp.gameState == gp.scoreState) {
                        gp.gameState = gp.adminDeleteState; 
                        gp.ui.subState = 0; 
                        raParaDeletar = ""; 
                        System.out.println("Easter Egg ativado: Modo Admin (Deletar Registro)!");
                    }
                    return; // Para o Konami, cortamos aqui para não ativar outra tecla do menu
                }
            } else {
                konamiIndex = (code == konamiCode[0]) ? 1 : 0;
            }
        }
        
        // --- 2. ÁRVORE PRINCIPAL DE ESTADOS DO JOGO ---
        if (gp.gameState == gp.titleState) { // <--- AGORA O TITLESTATE TEM SEU PRÓPRIO IF PRINCIPAL!
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 2;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 2) gp.ui.commandNum = 0;
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) {
                    gp.gameState = gp.dataInputState; 
                }
                if (gp.ui.commandNum == 1) {
                    gp.ui.updateRanking(); 
                    gp.gameState = gp.scoreState;
                }
                if (gp.ui.commandNum == 2) {
                    System.exit(0); 
                }
            }
        }
        else if (gp.gameState == gp.creditsState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState;
            }
        }
        else if (gp.gameState == gp.adminDeleteState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.scoreState; 
            }
            if (code == KeyEvent.VK_BACK_SPACE) {
                if (raParaDeletar.length() > 0) {
                    raParaDeletar = raParaDeletar.substring(0, raParaDeletar.length() - 1);
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (!raParaDeletar.trim().isEmpty()) {
                    DataBase db = new DataBase();
                    db.apagar(raParaDeletar);
                    raParaDeletar = "";
                    gp.ui.updateRanking(); 
                }
            }
        }
        else if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.playState;
                gp.retomarSomDoJogo();
            }
            else if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 1; 
            }
            else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 1) gp.ui.commandNum = 0;
            }
            else if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) {
                    gp.gameState = gp.playState;
                    gp.retomarSomDoJogo();
                }
                if (gp.ui.commandNum == 1) {
                    gp.gameState = gp.quitConfirmationState;
                }
            }
        }
        else if (gp.gameState == gp.quitConfirmationState) {
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.ui.subState = (gp.ui.subState == 0) ? 1 : 0; 
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.subState == 0) { 
                    gp.gameState = gp.titleState;
                    gp.player.inventory.clear();
                    gp.ui.playerName = ""; 
                    gp.ui.playerRA = "";
                } else { 
                    gp.gameState = gp.playState;
                    gp.retomarSomDoJogo();
                }
                return;
            }
        }
        else if (gp.gameState == gp.gameOverState) {
            if(code == KeyEvent.VK_ENTER) {
                gp.resetarFaseAtual();
            }
        }
        else if (gp.gameState == gp.dataInputState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState;
            }
            if (code == KeyEvent.VK_UP) {
                gp.ui.subState--;
                if (gp.ui.subState < 0) gp.ui.subState = 2; 
            }
            if (code == KeyEvent.VK_DOWN) {
                gp.ui.subState++;
                if (gp.ui.subState > 2) gp.ui.subState = 0;
            }
            if (code == KeyEvent.VK_BACK_SPACE) {
                if (gp.ui.subState == 0 && gp.ui.playerName.length() > 0) {
                    gp.ui.playerName = gp.ui.playerName.substring(0, gp.ui.playerName.length() - 1);
                } else if (gp.ui.subState == 1 && gp.ui.playerRA.length() > 0) {
                    gp.ui.playerRA = gp.ui.playerRA.substring(0, gp.ui.playerRA.length() - 1);
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.subState == 2) {
                    if (!gp.ui.playerName.trim().isEmpty() && !gp.ui.playerRA.trim().isEmpty()) {
                        gp.gameState = gp.levelSelectState;
                        gp.ui.commandNum = 0;
                    }
                } else {
                    gp.ui.subState++; 
                }
            }
        }
        else if (gp.gameState == gp.levelSelectState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = gp.levelM.levels.size() - 1;
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum >= gp.levelM.levels.size()) gp.ui.commandNum = 0;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.dataInputState;
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.levelM.levels.get(gp.ui.commandNum).unlocked) {
                    if (gp.ui.commandNum == 0) {
                        gp.playTime = 0;
                        System.out.println("Cronômetro iniciado para nova partida!");
                    }
                    gp.levelM.currentLevelIndex = gp.ui.commandNum;
                    gp.levelM.loadCurrentLevel();
                    gp.gameState = gp.playState;
                }
            }
        }
        else if (gp.gameState == gp.finishState) {
            if (code == KeyEvent.VK_ENTER) {
                boolean eUltimaFase = (gp.levelM.currentLevelIndex == gp.levelM.levels.size() - 1);

                if (eUltimaFase) {
                    DataBase db = new DataBase();
                    String ra = gp.ui.playerRA;
                    String nome = gp.ui.playerName;
                    int scoreFinal = gp.score;
                    int tempoFinal = (int) gp.playTime;
                    
                    db.gravar(ra, nome, scoreFinal, tempoFinal);
                    gp.levelM.resetLevelProgress();
                    
                    gp.score = 0;
                    gp.playTime = 0;
                    gp.player.inventory.clear();
                    gp.ui.playerName = "";
                    gp.ui.playerRA = "";
                    
                    gp.gameState = gp.titleState;
                } else {
                    gp.levelM.unlockNextLevel(); 
                    gp.gameState = gp.levelSelectState;
                    gp.ui.commandNum = gp.levelM.currentLevelIndex + 1;
                }
            }
        }
        else if (gp.gameState == gp.scoreState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState;
            }
        } 
        else if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.pauseState;
                gp.pausarSomDoJogo();
            }
            else if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = true;
            else if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = true;
            else if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = true;
            else if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) down = true;
            else if(code == KeyEvent.VK_CONTROL) ctrl = true;
            else if(code == KeyEvent.VK_N) nextLevelRequested = true;
            else if(code == KeyEvent.VK_E) descarte = true;
            else if(code == KeyEvent.VK_G) interact = true;
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = false;
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = false;
        if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = false;
        if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) down = false;
        if(code == KeyEvent.VK_CONTROL) ctrl = false;
        if(code == KeyEvent.VK_E) descarte = false;
        if(code == KeyEvent.VK_G) interact = false;
    }
}