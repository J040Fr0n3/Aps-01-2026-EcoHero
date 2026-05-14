package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class UI {
    
    GamePanel gp;
    Graphics2D g2;
    Font maruMonica; 
    public int commandNum = 0; // 0: Iniciar, 1: Tutorial, 2: Sair
    public int subState = 0;
    public String playerName = "";
    public String playerRA = "";
    
    Color laranjaEco = new Color(245, 120, 30);
    Color amareloMenu = new Color(255, 215, 0);
    
    public UI(GamePanel gp) {
        this.gp = gp;
       

    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        // Desativar antialiasing para manter o estilo Pixel Art das fontes
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // 1. TELA DE MENU PRINCIPAL
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        // 2. TELA DE CADASTRO (NOME E RA)
        if (gp.gameState == gp.dataInputState) {
            drawDataInputScreen();
        }
        // 3. TELA DE RECORDES (SCORE)
        if (gp.gameState == gp.scoreState) {
            drawScoreScreen();
        }
        // 4. INTERFACE DURANTE O JOGO (HUD)
        if (gp.gameState == gp.playState) { 
        }
        if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        if (gp.gameState == gp.quitConfirmationState) {
            drawQuitConfirmationScreen();
        }
    }

    public void drawTitleScreen() {
        // 1. FUNDO
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // 2. TÍTULO
        g2.setFont(Fonts.getPixelFont(80f));
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
        g2.setFont(Fonts.getPixelFont(40f));

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
        text = "SCORE";
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
    
    public void drawScoreScreen() {
        // 1. FUNDO
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. TÍTULO
        g2.setFont(Fonts.getPixelFont(60f));
        String title = "TOP SCORE / TIME";
        int x = getXforCenteredText(title);
        int y = gp.tileSize * 2;

        g2.setColor(Color.gray);
        g2.drawString(title, x + 3, y + 3);
        g2.setColor(laranjaEco);
        g2.drawString(title, x, y);

        // 3. DEFINIÇÃO DAS COLUNAS (Baseadas na largura da tela)
        // Usamos margens de 5% para não colar nas bordas
        int margin = gp.screenWidth / 20; 
        int availableWidth = gp.screenWidth - (margin * 2);
        
        // Proporções das colunas (em % da largura disponível)
        int colRankX  = margin;
        int colNomeX  = margin + (int)(availableWidth * 0.15); // 15% para Rank
        int colRAX    = margin + (int)(availableWidth * 0.45); // Nome longo
        int colScoreX = margin + (int)(availableWidth * 0.70); // RA
        int colTempoX = margin + (int)(availableWidth * 0.85); // Score e Tempo no final

        // 4. CABEÇALHO
        g2.setFont(Fonts.getPixelFont(30f));
        g2.setColor(Color.white);
        y += gp.tileSize * 1.5;

        g2.drawString("RK", colRankX, y);
        g2.drawString("NOME", colNomeX, y);
        g2.drawString("RA", colRAX, y);
        g2.drawString("PTS", colScoreX, y);
        g2.drawString("TIME", colTempoX, y);

        // Linha divisória respeitando as margens
        g2.drawLine(margin, y + 10, gp.screenWidth - margin, y + 10);

        // 5. LISTAGEM (Top 10)
        // Calculamos o espaçamento dinâmico para caber na altura da tela
        int lineSpacing = (gp.screenHeight - y - gp.tileSize * 2) / 11; 
        g2.setFont(Fonts.getPixelFont(26f));
        y += lineSpacing;

        for (int i = 1; i <= 10; i++) {
            if (i == 1) {
                g2.setColor(amareloMenu);
                g2.drawString(i + " >", colRankX, y);
                g2.drawString("PLAYER UM", colNomeX, y);
                g2.drawString("2024001", colRAX, y);
                g2.drawString("9999", colScoreX, y);
                g2.drawString("01:45", colTempoX, y);
            } else {
                g2.setColor(Color.white);
                g2.drawString(i + ".", colRankX, y);
                g2.drawString("---", colNomeX, y);
                g2.drawString("---", colRAX, y);
                g2.drawString("---", colScoreX, y);
                g2.drawString("---", colTempoX, y);
            }
            y += lineSpacing;
        }

        // 6. RODAPÉ
        g2.setFont(Fonts.getPixelFont(25f));
        g2.setColor(Color.white);
        String backText = "ESC PARA VOLTAR";
        g2.drawString(backText, getXforCenteredText(backText), gp.screenHeight - 30);
    }
    
    public void drawDataInputScreen() {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(Fonts.getPixelFont(50f));
        g2.setColor(laranjaEco);
        String title = "REGISTRO DO HEROI";
        g2.drawString(title, getXforCenteredText(title), gp.tileSize * 2);

        g2.setFont(Fonts.getPixelFont(35f));
        
        // Campo Nome
        int y = gp.tileSize * 5;
        g2.setColor(subState == 0 ? amareloMenu : Color.white);
        g2.drawString("NOME: " + playerName + (subState == 0 ? "_" : ""), gp.tileSize * 3, y);

        // Campo RA
        y += gp.tileSize * 2;
        g2.setColor(subState == 1 ? amareloMenu : Color.white);
        g2.drawString("RA: " + playerRA + (subState == 1 ? "_" : ""), gp.tileSize * 3, y);

        // Botão Confirmar
        y += gp.tileSize * 2;
        boolean pronto = !playerName.trim().isEmpty() && !playerRA.trim().isEmpty();
        
        if (!pronto) {
            g2.setColor(Color.darkGray);
            g2.drawString("PREENCHA TUDO PARA INICIAR", getXforCenteredText("PREENCHA TUDO PARA INICIAR"), y);
        } else {
            g2.setColor(subState == 2 ? amareloMenu : Color.green);
            String msg = "APERTE ENTER PARA COMECAR";
            g2.drawString(msg, getXforCenteredText(msg), y);
        }
        
        y += gp.tileSize * 2;
        g2.setColor(Color.darkGray);
        String exit = "ESC PARA SAIR";
        g2.drawString(exit, getXforCenteredText(exit), y);
    }
    
    public void drawPauseScreen() {
        // Escurecer o fundo do jogo
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Janela Central
        int x = gp.screenWidth / 4;
        int y = gp.screenHeight / 4;
        int width = gp.screenWidth / 2;
        int height = gp.screenHeight / 2;
        drawSubWindow(x, y, width, height);

        g2.setColor(Color.white);
        g2.setFont(Fonts.getPixelFont(40f));
        
        // Título da Fase
        String text = "FASE " + (gp.levelM.currentLevelIndex + 1);
        g2.drawString(text, getXforCenteredText(text), y + gp.tileSize);

        // Mensagem
        g2.setFont(Fonts.getPixelFont(25f));
        text = "(Score / Tempo pausado)";
        g2.drawString(text, getXforCenteredText(text), y + gp.tileSize * 2);

        // Botão Continuar
        text = "CONTINUAR";
        int ty = y + gp.tileSize * 4;
        if (commandNum == 0) g2.setColor(amareloMenu); else g2.setColor(Color.white);
        g2.drawString(text, getXforCenteredText(text), ty);
        
        // Botão Sair
        text = "SAIR";
        ty += gp.tileSize;
        if (commandNum == 1) g2.setColor(amareloMenu); else g2.setColor(Color.white);
        g2.drawString(text, getXforCenteredText(text), ty);
    }

    public void drawQuitConfirmationScreen() {
        // Overlay ainda mais escuro
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int x = gp.screenWidth / 6;
        int y = gp.screenHeight / 3;
        int width = gp.screenWidth * 2/3;
        int height = gp.screenHeight / 3;
        drawSubWindow(x, y, width, height);

        g2.setColor(Color.red);
        g2.setFont(Fonts.getPixelFont(45f));
        String text = "AVISO";
        g2.drawString(text, getXforCenteredText(text), y + gp.tileSize);

        g2.setColor(Color.white);
        g2.setFont(Fonts.getPixelFont(20f));
        text = "Ao sair sua pontuação ate agora será apagada";
        g2.drawString(text, getXforCenteredText(text), y + gp.tileSize * 1.5f);

        // Opções Lado a Lado
        g2.setFont(Fonts.getPixelFont(30f));
        
        // SAIR
        text = "SAIR";
        if(subState == 0) g2.setColor(amareloMenu); else g2.setColor(Color.white);
        g2.drawString(text, x + gp.tileSize, y + height - gp.tileSize);

        // CONTINUAR
        text = "CONTINUAR";
        if(subState == 1) g2.setColor(amareloMenu); else g2.setColor(Color.white);
        g2.drawString(text, x + width - gp.tileSize * 4, y + height - gp.tileSize);
    }

    // Método auxiliar para criar o fundo da janelinha
    public void drawSubWindow(int x, int y, int width, int height) {
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRoundRect(x, y, width, height, 35, 35);
        g2.setColor(Color.white);
        g2.setStroke(new java.awt.BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public int getXforCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }
}