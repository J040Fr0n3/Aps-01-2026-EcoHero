package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean up, left, right, ctrl, down, descarte, interact;
    public boolean nextLevelRequested = false;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (gp.gameState == gp.dataInputState) {
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) || c == ' ') {
                if (gp.ui.subState == 0 && gp.ui.playerName.length() < 15) gp.ui.playerName += c;
                if (gp.ui.subState == 1 && gp.ui.playerRA.length() < 10) gp.ui.playerRA += c;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
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
                    gp.ui.playerName = ""; 
                    gp.ui.playerRA = "";
                } else { // CONTINUAR
                    gp.gameState = gp.playState;
                }
                return;
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
                        iniciarJogo(); // Método que reseta leveis e inicia
                    }
                } else {
                    gp.ui.subState++; 
                }
            }
        }
        
        // --- LÓGICA DO MENU (TITLE STATE) ---
        else if (gp.gameState == gp.titleState) { // Usei else if para evitar conflitos
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
                    // CORREÇÃO: Leva para o cadastro em vez de ir direto pro jogo
                    gp.gameState = gp.dataInputState; 
                }
                if (gp.ui.commandNum == 1) {
                    gp.gameState = gp.scoreState;
                }
                if (gp.ui.commandNum == 2) {
                    System.exit(0); 
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

    // MODO DE INICIAR O JOGO (Adicione este método no final do KeyHandler ou chame da GP)
    private void iniciarJogo() {
        // 1. Garante que apenas o Level 1 está desbloqueado para o novo jogador
        for (int i = 0; i < gp.levelM.levels.size(); i++) {
            gp.levelM.levels.get(i).unlocked = (i == 0); 
        }
        // 2. Carrega a fase 1
        gp.levelM.loadCurrentLevel();
        // 3. Entra no jogo
        gp.gameState = gp.playState;
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