package tile;

import java.awt.Graphics;
import engine.GamePanel;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10]; // Suporta até 10 tipos de blocos (0=vazio, 1=chão, etc)
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        
        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        tile[0] = new Tile(); // Vazio
        tile[0].collision = false;

        tile[1] = new Tile(); // Chão/Plataforma
        tile[1].collision = true;
    }

    public void loadMap() {
        // Aqui você usaria um BufferedReader para ler seu arquivo .txt
        // Por enquanto, você pode preencher manualmente para testar
    }

    public void draw(Graphics g) {
        // Loop que percorre a matriz mapTileNum e desenha onde for 1
    }
}