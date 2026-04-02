package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean up, left, right, ctrl;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Esquerda (A ou Seta Esquerda)
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            left = true;
        }

        // Direita (D ou Seta Direita)
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            right = true;
        }

        // Pulo (Espaço, W ou Seta Cima)
        if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            up = true;
        }

        // Sprint (CTRL)
        if(code == KeyEvent.VK_CONTROL) {
            ctrl = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            left = false;
        }

        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            right = false;
        }

        if(code == KeyEvent.VK_SPACE || code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            up = false;
        }

        if(code == KeyEvent.VK_CONTROL) {
            ctrl = false;
        }
    }
}