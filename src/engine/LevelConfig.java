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

    public LevelConfig(int level, String path, Map<String, Integer> requirements, int rate, int cols, int rows, int bueiroDestino, int musicID) {
        this.levelNumber = level;
        this.mapPath = path;
        this.itemsRequired = requirements;
        this.spawnRate = rate;
        this.maxCols = cols;
        this.maxRows = rows;
        this.bueiroDestino = bueiroDestino;
        this.musicID = musicID;
    }
    
    public int getTotalRequiredItems() {
    	int total = 0;
    	for (int qty : itemsRequired.values()) {
    		total += qty;
    	}
    	return total;
    }
    
    
}
