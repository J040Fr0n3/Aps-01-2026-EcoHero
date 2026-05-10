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
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // --- LÓGICA DO MENU (TITLE STATE) ---
        if (gp.gameState == gp.titleState) {
            
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
                    gp.gameState = gp.playState; 
                }
                if (gp.ui.commandNum == 1) {
                    System.out.print("Tutorial Selecionado!");
                }
                if (gp.ui.commandNum == 2) {
                    System.exit(0); 
                }
            }
        } 
        
        // --- LÓGICA DO JOGO (PLAY STATE) ---
        // Usamos o ELSE IF para que os comandos de jogo só funcionem fora do menu
        else if (gp.gameState == gp.playState) {
            
            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                left = true;
            }
            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                right = true;
            }
            if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                up = true;
            }
            if(code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                down = true;
            }
            if(code == KeyEvent.VK_CONTROL) {
                ctrl = true;
            }
            if (code == KeyEvent.VK_N) {
                nextLevelRequested = true;
            }
            if(code == KeyEvent.VK_E) {
                descarte = true;
            }
            if(code == KeyEvent.VK_G) {
                interact = true;
            }
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