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

    public LevelConfig(int level, String path, List<String> items, int max, int rate, int cols, int rows) {
        this.levelNumber = level;
        this.mapPath = path;
        this.allowedItems = items;
        this.maxTotalItems = max;
        this.spawnRate = rate;
        this.maxCols = cols;
        this.maxRows = rows;
    }
    
    
}
