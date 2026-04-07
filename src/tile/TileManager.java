package tile;

import java.awt.Graphics;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import engine.GamePanel;

public class TileManager {

    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;

        tile = new Tile[20]; 
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap("/maps/map01.txt");
    }

    public void getTileImage() {
    	// 0 - Vazio
        setup(0, "vazio", false, Color.BLACK); 
        
        // 1 - Chão
        setup(1, "chao", true, new Color(100, 100, 100)); // Cinza Escuro
        
        // 2 - Parede
        setup(2, "parede", true, new Color(50, 50, 50)); // Quase Preto
        
        // 3 - Plataforma
        setup(3, "plataforma", true, new Color(150, 75, 0)); // Marrom
        
        // 4 - Escada
        setup(4, "escada", false, new Color(200, 150, 50)); // Bege/Dourado
        
        // 5 - Àgua
        setup(5, "agua", false, new Color(0, 100, 255)); // Azul Claro
        
        // 6 - Trampolim
        setup(6, "trampolim", true, Color.PINK);
        
        // 7 - Bueiro
        setup(7, "bueiro", false, new Color(30, 30, 30)); // Cinza bem escuro
        
        // 8 - Nuvem
        setup(8, "nuvem", true, Color.WHITE);
        
        // 9 - Gerador Lixo
        setup(9, "gerador_lixo", false, Color.MAGENTA);

        // A-E Lixeiras (Cores da coleta seletiva)
        setup(10, "lixeira_papel", true, Color.BLUE);      // A
        setup(11, "lixeira_vidro", true, Color.GREEN);     // B
        setup(12, "lixeira_metal", true, Color.YELLOW);    // C
        setup(13, "lixeira_plastico", true, Color.RED);     // D
        setup(14, "lixeira_organico", true, new Color(139, 69, 19)); // E (Marrom escuro)

        // F - Água Tóxica
        setup(15, "agua_toxica", false, new Color(50, 200, 50)); // Verde Neon
        
        // G - Elevador
        setup(16, "elevador", false, Color.CYAN); // Ciano
    }
    public void setup(int index, String type, boolean collision, Color color) {
    	tile[index] = new Tile();
    	tile[index].type = type;
    	tile[index].collision = collision;
    	tile[index].color = color;
    }

    public void loadMap(String filePath) {
    	try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;

                // Divide a linha por qualquer espaço em branco
                String numbers[] = line.trim().split("\\s+"); 

                for (int col = 0; col < gp.maxWorldCol; col++) {
                    if (col < numbers.length) {
                        try {
                            // 1. Limpamos a string (trim) e garantimos que seja Maiúscula
                            String val = numbers[col].trim().toUpperCase();
                            
                            int num;
                            if (val.equals("G")) {
                                num = 16; // Atribui manualmente o ID do elevador
                            } else {
                                num = Integer.parseInt(val, 16);
                            }
                            mapTileNum[col][row] = num;
                        } catch (NumberFormatException e) {
                            // 3. Se encontrar um 'G' ou algo que falhe, tratamos manualmente
                            // ou apenas definimos como 0 para o mapa não quebrar
                            mapTileNum[col][row] = 0; 
                            System.out.println("Aviso: Caractere inválido na posição ["+col+"]["+row+"]");
                        }
                    }
                }
                row++;
            }
            br.close();
            System.out.println("Mapa carregado com sucesso!");
        } catch (Exception e) {
            System.out.println("ERRO CRÍTICO AO LER O ARQUIVO TXT!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
    	int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            
            int tileNum = mapTileNum[worldCol][worldRow];

            // Proteção contra IDs inválidos no mapa
            if (tileNum < 0 || tileNum >= tile.length || tile[tileNum] == null) {
                tileNum = 0; // Trata como vazio se o ID não existir
            }

            // 1. Posição real do bloco no mundo
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            // 2. Cálculo da Câmera
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            // 3. Otimização: Desenha apenas se estiver na tela
            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                
                // Define a cor sólida correspondente ao tileNum
                g.setColor(tile[tileNum].color);
                
                // Desenha o quadrado preenchido
                g.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            }

            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}