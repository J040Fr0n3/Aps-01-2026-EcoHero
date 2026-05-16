package tile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import engine.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class TileManager {

    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    BufferedImage fundoGeral = setupBackground("fundo");
    BufferedImage fundoCidade = setupBackground("cidade");
    
    public BufferedImage setupBackground(String name) {
        try {
            return ImageIO.read(getClass().getResourceAsStream("/textures/" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public TileManager(GamePanel gp) {
        this.gp = gp;

        tile = new Tile[20]; 
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
    }

    public void getTileImage() {
        // 0 - Vazio
        setup(0, "vazio", false, Color.BLACK); 
        
        // 1 - Chão
        setup(1, "chao", true, new Color(100, 100, 100)); // com fisica
        
        // 2 - Parede
        setup(2, "parede", true, Color.BLACK); // com fisica
        // 3 - Plataforma
        setup(3, "plataforma", true, new Color(150, 75, 0)); // com fisica; sem configutação
        
        // 4 - Escada
        setup(4, "escada", false, new Color(200, 150, 50)); // com fisica
        
        // 5 - Àgua
        setup(5, "agua", false, new Color(0, 100, 255)); // com fisica
        
        // 6 - Trampolim
        setup(6, "trampolim", true, Color.PINK); // com fisica
        
        // 7 - Bueiro
        setup(7, "bueiro", false, new Color(30, 30, 30)); // sem fisica e sem configuração
        
        // 8 - Nuvem
        setup(8, "nuvem", true, Color.WHITE); // sem fisica e sem configuração
        
        // 9 - Gerador Lixo
        setup(9, "gerador_lixo", false, Color.MAGENTA); //sem configuração

        // A-E Lixeiras (Cores da coleta seletiva)
        setup(10, "lixeira_papel", false, Color.BLUE);      // sem fisica e sem configuração
        setup(11, "lixeira_vidro", false, Color.GREEN);     // sem fisica e sem configuração
        setup(12, "lixeira_metal", false, Color.YELLOW);    // sem fisica e sem configuração
        setup(13, "lixeira_plastico", false, Color.RED);     // sem fisica e sem configuração
        setup(14, "lixeira_organico", false, new Color(139, 69, 19)); // sem fisica e sem configuração

        // F - Água Tóxica
        setup(15, "agua_toxica", false, new Color(50, 200, 50)); // sem fisica e sem configuração
        
        // G - Elevador
        setup(16, "elevador", false, Color.CYAN); // com fisica
    }
    
    public void setup(int index, String imageName, boolean collision, Color color) {
        tile[index] = new Tile();
        tile[index].collision = collision;
        tile[index].color = color;
        
        try {
            // 1. Pegamos o caminho completo
            String path = "/textures/" + imageName + ".png";
            InputStream is = getClass().getResourceAsStream(path);

            // 2. Verificamos se o arquivo existe antes de tentar ler
            if (is != null) {
                tile[index].image = ImageIO.read(is);
            } else {
                System.out.println("AVISO: Imagem não encontrada: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath, int col, int row) {
        
        gp.maxWorldCol = col;
        gp.maxWorldRow = row;
        gp.worldWidth = gp.tileSize * col;
        gp.worldHeight = gp.tileSize * row;
        
        // 2. Reinicializa a array do mapa com o novo tamanho da fase
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int currentRow = 0;

            while (currentRow < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;

                // Divide a linha por espaços (suporta múltiplos espaços entre números)
                String numbers[] = line.trim().split("\\s+"); 

                for (int currentCol = 0; currentCol < gp.maxWorldCol; currentCol++) {
                    if (currentCol < numbers.length) {
                        try {
                            String val = numbers[currentCol].trim().toUpperCase();
                            
                            int num;
                            if (val.equals("G")) {
                                num = 16; // Elevador
                            } else {
                                num = Integer.parseInt(val, 16);
                            }
                            
                            mapTileNum[currentCol][currentRow] = num;
                            
                            // Lógica de Spawner de lixo
                            if (num == 9) {
                                java.awt.Point p = new java.awt.Point(currentCol, currentRow);
                                gp.spawnerData.put(p, 0);
                            }
                        } catch (NumberFormatException e) {
                            mapTileNum[currentCol][currentRow] = 0; 
                            System.out.println("Aviso: Caractere inválido em ["+currentCol+"]["+currentRow+"]");
                        }
                    }
                }
                currentRow++;
            }
            br.close();
            System.out.println("Mapa carregado: " + col + "x" + row);
        } catch (Exception e) {
            System.out.println("ERRO CRÍTICO AO LER O ARQUIVO TXT!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        
        if (fundoGeral != null) {
            g.drawImage(fundoGeral, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }
        if (fundoCidade != null) {
            int bgX = 0;
            int bgHeight = gp.screenHeight; 
            
            // --- CÁLCULO PARA PRENDER NA BASE ---
            int bgY = (gp.worldHeight - bgHeight) - gp.player.worldY + gp.player.screenY;
            
            if (gp.player.screenY > gp.player.worldY) {
                bgY = gp.worldHeight - bgHeight;
            }
            
            int bottomOffset = gp.screenHeight - gp.player.screenY;
            if (bottomOffset > gp.worldHeight - gp.player.worldY) {
                bgY = gp.screenHeight - bgHeight;
            }

            g.drawImage(fundoCidade, bgX, bgY, gp.screenWidth, bgHeight, null);
        }
        boolean[][] pularTile = new boolean[gp.maxWorldCol][gp.maxWorldRow];

        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            
            if (pularTile[worldCol][worldRow]) {
                worldCol++;
                if (worldCol == gp.maxWorldCol) { worldCol = 0; worldRow++; }
                continue;
            }

            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            // --- CÁLCULO DE SCREEN X/Y ---
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if (gp.player.screenX > gp.player.worldX) screenX = worldX;
            int rightOffset = gp.screenWidth - gp.player.screenX;
            if (rightOffset > gp.worldWidth - gp.player.worldX) screenX = worldX - (gp.worldWidth - gp.screenWidth);
            
            if (gp.player.screenY > gp.player.worldY) screenY = worldY;
            int bottomOffset = gp.screenHeight - gp.player.screenY;
            if (bottomOffset > gp.worldHeight - gp.player.worldY) screenY = worldY - (gp.worldHeight - gp.screenHeight);

            // --- VERIFICAÇÃO DE BUEIRO 2x2 (ID 7) ---
            if (tileNum == 7) {
                if (worldCol + 1 < gp.maxWorldCol && worldRow + 1 < gp.maxWorldRow &&
                    mapTileNum[worldCol + 1][worldRow] == 7 &&
                    mapTileNum[worldCol][worldRow + 1] == 7 &&
                    mapTileNum[worldCol + 1][worldRow + 1] == 7) {

                    if (tile[tileNum].image != null) {
                        g.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize * 2, gp.tileSize * 2, null);
                    }
                    
                    pularTile[worldCol][worldRow] = true;
                    pularTile[worldCol + 1][worldRow] = true;
                    pularTile[worldCol][worldRow + 1] = true;
                    pularTile[worldCol + 1][worldRow + 1] = true;
                } 
                else {
                    // CHAMANDO DO GAMEPANEL
                    gp.desenharTile(g, tileNum, screenX, screenY);
                }
            } 
            // --- DESENHO DE OUTROS TILES ---
            else if (tileNum != 0 && tileNum != 9) {
                // CHAMANDO DO GAMEPANEL
                gp.desenharTile(g, tileNum, screenX, screenY);
            }

            worldCol++;
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}