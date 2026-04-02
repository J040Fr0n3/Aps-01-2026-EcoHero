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

        tile = new Tile[10]; 
        // A matriz precisa do tamanho do MUNDO (50x12)
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap("/maps/map01.txt");
    }

    public void getTileImage() {
        tile[0] = new Tile();
        tile[0].collision = false;

        tile[1] = new Tile();
        tile[1].collision = true;
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;

                // O "\\s+" limpa QUALQUER quantidade de espaços (1, 2 ou 10 espaços)
                String numbers[] = line.trim().split("\\s+"); 

                // Usamos Math.min para não estourar se a linha do TXT for maior que 50
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    if (col < numbers.length) {
                        int num = Integer.parseInt(numbers[col]);
                        mapTileNum[col][row] = num;
                    }
                }
                row++;
            }
            br.close();
            System.out.println("Mapa carregado com sucesso!"); // Se aparecer isso no console, o arquivo foi lido
        } catch (Exception e) {
            System.out.println("ERRO AO LER O ARQUIVO TXT!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        int worldCol = 0;
        int worldRow = 0;

        // O loop precisa percorrer as 50 colunas e as 12 linhas do seu mundo
        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            
            int tileNum = mapTileNum[worldCol][worldRow];

            // 1. Posição real do bloco no seu mapa gigante
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            // 2. A "Câmera": calcula onde o bloco deve aparecer na janela
            // Se o player anda para a direita (worldX aumenta), o bloco deve ir para a esquerda (screenX diminui)
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            // 3. Desenha apenas o que é visível (Otimização opcional, mas recomendada)
            if (tileNum == 1) {
                g.setColor(Color.GRAY);
                // IMPORTANTE: use screenX e screenY, NÃO worldX e worldY
                g.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            }

            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++; // Certifique-se de que esta variável é a que está no WHILE
            }
        }
    }
}