package engine;

import java.util.List;


import java.util.ArrayList;

public class LevelConfig {
	public int levelNumber;
    public String mapPath;
    public List<String> allowedItems; 
    public int maxTotalItems;        
    public int spawnRate;
    public int maxCols;
    public int maxRows;
    public int bueiroDestino;
    public int musicID;
    public boolean unlocked = false;

    public LevelConfig(int level, String path, List<String> items, int max, int rate, int cols, int rows, int bueiroDestino, int musicID) {
        this.levelNumber = level;
        this.mapPath = path;
        this.allowedItems = items;
        this.maxTotalItems = max;
        this.spawnRate = rate;
        this.maxCols = cols;
        this.maxRows = rows;
        this.bueiroDestino = bueiroDestino;
        this.musicID = musicID;
        this.unlocked = false;
    }
    
    
}
