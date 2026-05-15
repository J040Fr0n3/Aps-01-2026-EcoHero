package engine;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class LevelConfig {
	public int levelNumber;
    public String mapPath;
    public Map<String, Integer> itemsRequired = new HashMap<>();     
    public int spawnRate;
    public int maxCols;
    public int maxRows;
    public int bueiroDestino;
    public int musicID;
    public boolean unlocked = false;
    public java.awt.image.BufferedImage backgroundImage;

    public LevelConfig(int level, String path, Map<String, Integer> requirements, int rate, int cols, int rows, int bueiroDestino, int musicID) {
        this.levelNumber = level;
        this.mapPath = path;
        this.itemsRequired = requirements;
        this.spawnRate = rate;
        this.maxCols = cols;
        this.maxRows = rows;
        this.bueiroDestino = bueiroDestino;
        this.musicID = musicID;
        
        try {
            // Tenta carregar: /textures/fase1.png
            backgroundImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/textures/fase" + level + ".png"));
        } catch (Exception e) {
            backgroundImage = null; // Se não achar, o UI desenha o quadrado com X
        }
        
    }
    
    public int getTotalRequiredItems() {
    	int total = 0;
    	for (int qty : itemsRequired.values()) {
    		total += qty;
    	}
    	return total;
    }
    
    
}
