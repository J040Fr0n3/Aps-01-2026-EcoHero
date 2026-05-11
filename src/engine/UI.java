package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UI {
    
    GamePanel gp;
    Graphics2D g2;
    Font maruMonica; 
    public int commandNum = 0; // 0: Iniciar, 1: Tutorial, 2: Sair
    
    Color laranjaEco = new Color(245, 120, 30);
    Color amareloMenu = new Color(255, 215, 0);
    
    public UI(GamePanel gp) {
        this.gp = gp;
        // Usamos uma fonte do Windows que é quadradinha e não falha
        maruMonica = new Font("Lucida Console", Font.BOLD, 40);
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
    }

    public void drawTitleScreen() {
        // 1. FUNDO
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // 2. TÍTULO
        g2.setFont(new Font("Lucida Console", Font.BOLD, 80));
        String text = "ECO HERO";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 3;
        
        // Sombra
        g2.setColor(Color.gray);
        g2.drawString(text, x + 5, y + 5);
        // Principal
        g2.setColor(laranjaEco);
        g2.drawString(text, x, y);

        // 3. MENU (Fonte menor)
        g2.setFont(new Font("Lucida Console", Font.BOLD, 40));

        // INICIAR
        text = "INICIAR";
        x = getXforCenteredText(text);
        y += gp.tileSize * 4;
        g2.setColor(Color.white);
        if (commandNum == 0) {
            g2.setColor(amareloMenu);
            g2.drawString(">", x - gp.tileSize, y);
        }
        g2.drawString(text, x, y);

        // TUTORIAL
        text = "TUTORIAL";
        x = getXforCenteredText(text);
        y += gp.tileSize * 1.5;
        g2.setColor(Color.white);
        if (commandNum == 1) {
            g2.setColor(amareloMenu);
            g2.drawString(">", x - gp.tileSize, y);
        }
        g2.drawString(text, x, y);

        // SAIR
        text = "SAIR";
        x = getXforCenteredText(text);
        y += gp.tileSize * 1.5;
        g2.setColor(Color.white);
        if (commandNum == 2) {
            g2.setColor(amareloMenu);
            g2.drawString(">", x - gp.tileSize, y);
        }
        g2.drawString(text, x, y);
    }

    public int getXforCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }
}