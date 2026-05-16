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
        
        if (gp.gameState == gp.titleState || gp.gameState == gp.scoreState) {
            if (code == konamiCode[konamiIndex]) {
                konamiIndex++;
                if (konamiIndex == konamiCode.length) {
                    konamiIndex = 0; // Reseta o índice para uma próxima vez
                    
                    // INTERAÇÃO 1: Na Tela de Título
                    if (gp.gameState == gp.titleState) {
                        gp.gameState = gp.creditsState; // Vamos criar esse estado (ex: id 10)
                        System.out.println("Easter Egg ativado: Tela de Créditos!");
                    } 
                    // INTERAÇÃO 2: Na Tela de Scores
                    else if (gp.gameState == gp.scoreState) {
                        gp.gameState = gp.adminDeleteState; // Vamos criar esse estado (ex: id 11)
                        gp.ui.subState = 0; // Foco no campo de digitação
                        raParaDeletar = ""; // Limpa digitações anteriores
                        System.out.println("Easter Egg ativado: Modo Admin (Deletar Registro)!");
                    }
                    return; // Corta a execução para não disparar outros comandos do menu
                }
            } else {
                // Se errou a sequência, reseta o progresso (mas checa se a tecla digitada era o primeiro UP)
                konamiIndex = (code == konamiCode[0]) ? 1 : 0;
            }
        }
        
     // --- LÓGICA DO ESTADO DE CRÉDITOS ---
        else if (gp.gameState == gp.creditsState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState;
            }
        }

        // --- LÓGICA DO ESTADO ADMIN DELETE ---
        else if (gp.gameState == gp.adminDeleteState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.scoreState; // Volta para os scores normais
            }
            
            if (code == KeyEvent.VK_BACK_SPACE) {
                if (raParaDeletar.length() > 0) {
                    raParaDeletar = raParaDeletar.substring(0, raParaDeletar.length() - 1);
                }
            }
            
            if (code == KeyEvent.VK_ENTER) {
                if (!raParaDeletar.trim().isEmpty()) {
                    // Chama o banco para deletar
                    DataBase db = new DataBase();
                    db.apagar(raParaDeletar);
                    
                    // Limpa o campo e força a UI a atualizar o ranking na tela instantaneamente
                    raParaDeletar = "";
                    gp.ui.updateRanking(); 
                }
            }
        }
        
     // Dentro do keyPressed
        if (code == KeyEvent.VK_ESCAPE) {
            if (gp.gameState == gp.playState) gp.gameState = gp.pauseState;
            else if (gp.gameState == gp.pauseState) gp.gameState = gp.playState;
        }

        // Navegação no Menu de Pausa
        if (gp.gameState == gp.pauseState) {
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) gp.ui.commandNum = 1; // 0: Continuar, 1: Sair
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNum++;
                if (gp.ui.commandNum > 1) gp.ui.commandNum = 0;
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.commandNum == 0) gp.gameState = gp.playState;
                if (gp.ui.commandNum == 1) gp.gameState = gp.quitConfirmationState;
            }
        }

        // Navegação na Confirmação de Saída
        else if (gp.gameState == gp.quitConfirmationState) {
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.ui.subState = (gp.ui.subState == 0) ? 1 : 0; // Alterna entre Sair (0) e Continuar (1)
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gp.ui.subState == 0) { // SAIR REALMENTE
                    gp.gameState = gp.titleState;
                    // Resetar dados aqui se necessário
                    gp.player.inventory.clear();
                    gp.ui.playerName = ""; 
                    gp.ui.playerRA = "";
                } else { // CONTINUAR
                    gp.gameState = gp.playState;
                }
                return;
            }
        }
        else if (gp.gameState == gp.gameOverState) {
        	if(code == KeyEvent.VK_ENTER) {
        		gp.resetarFaseAtual();
        	}
        }
        
        // --- LÓGICA DA TELA DE CADASTRO (DATA INPUT) ---
        if (gp.gameState == gp.dataInputState) {
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
        
     // --- LÓGICA DE SELEÇÃO DE FASES ---
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
                // Só entra na fase se estiver unlocked
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
        
     // --- LÓGICA DO MENU (TITLE STATE) ---
        else if (gp.gameState == gp.titleState) {
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
                    // ATUALIZAÇÃO: Carrega os dados do banco antes de mostrar a tela
                    gp.ui.updateRanking(); 
                    gp.gameState = gp.scoreState;
                }
                if (gp.ui.commandNum == 2) {
                    System.exit(0); 
                }
            }
        }

        // --- LÓGICA DE FIM DE FASE (FINISH STATE) CONSOLIDADA ---
        else if (gp.gameState == gp.finishState) {
            if (code == KeyEvent.VK_ENTER) {
                boolean eUltimaFase = (gp.levelM.currentLevelIndex == gp.levelM.levels.size() - 1);

                if (eUltimaFase) {
                    // Gravação no Banco
                    DataBase db = new DataBase();
                    String ra = gp.ui.playerRA;
                    String nome = gp.ui.playerName;
                    int scoreFinal = gp.score;
                    int tempoFinal = (int) gp.playTime;
                    
                    db.gravar(ra, nome, scoreFinal, tempoFinal);
                    
                    gp.levelM.resetLevelProgress();
                    
                    // RESET PÓS-JOGO: Limpa tudo para o próximo herói
                    gp.score = 0;
                    gp.playTime = 0;
                    gp.player.inventory.clear();
                    gp.ui.playerName = "";
                    gp.ui.playerRA = "";
                    
                    
                    gp.gameState = gp.titleState;
                } else {
                    // Próxima fase
                    gp.levelM.unlockNextLevel(); 
                    gp.gameState = gp.levelSelectState;
                    gp.ui.commandNum = gp.levelM.currentLevelIndex + 1;
                }
            }
        }
        
        // --- LÓGICA DO SCORE (SCORE STATE) ---
        else if (gp.gameState == gp.scoreState) {
            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = gp.titleState;
            }
        } 
        
        // --- LÓGICA DO JOGO (PLAY STATE) ---
        else if (gp.gameState == gp.playState) {
            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = true;
            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = true;
            if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = true;
            if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) down = true;
            if(code == KeyEvent.VK_CONTROL) ctrl = true;
            if (code == KeyEvent.VK_N) nextLevelRequested = true;
            if(code == KeyEvent.VK_E) descarte = true;
            if(code == KeyEvent.VK_G) interact = true;
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