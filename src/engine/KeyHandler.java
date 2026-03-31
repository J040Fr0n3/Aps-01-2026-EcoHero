package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyHandler implements KeyListener {

    public boolean up;
    public boolean left;
    public boolean right;
    public boolean ctrl;

    @Override
    public void keyTyped(KeyEvent e) {}

    // Detecta quando uma tecla é pressionada e ativa os controles do jogador
    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_A) {
            left = true;
        }
        
        if(code == KeyEvent.VK_LEFT) {
        	left = true;
        }

        if(code == KeyEvent.VK_D) {
            right = true;
        }
        
        if(code == KeyEvent.VK_RIGHT) {
            right = true;
        }
        
        if(code == KeyEvent.VK_SPACE) {
            up = true;
        }
        if(code == KeyEvent.VK_CONTROL) {
        	ctrl = true;
        }
    }

    // Detecta quando a tecla é solta e desativa o movimento correspondente
    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_A) {
            left = false;
        }
        
        if(code == KeyEvent.VK_LEFT) {
            left = false;
        }

        if(code == KeyEvent.VK_D) {
            right = false;
        }
        
        if(code == KeyEvent.VK_RIGHT) {
            right = false;
        }

        if(code == KeyEvent.VK_SPACE) {
            up = false;
        }
        if(code == KeyEvent.VK_CONTROL) {
        	ctrl = false;
        }
    }
}