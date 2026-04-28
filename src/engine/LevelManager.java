package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelManager {
    GamePanel gp;
    public List<LevelConfig> levels = new ArrayList<>();
    public int currentLevelIndex = 0;

    public LevelManager(GamePanel gp) {
        this.gp = gp;
        setupLevels();
    }

    private void setupLevels() {
        // Fase 1: Tutorial
        levels.add(new LevelConfig(1, "/maps/map01.txt", 
            Arrays.asList("papel"), 10, 5));

        // Fase 2: Papel e Vidro
        levels.add(new LevelConfig(2, "/maps/map02.txt", 
            Arrays.asList("papel", "vidro"), 15, 7));
        
        levels.add(new LevelConfig(3, "/maps/map03.txt",
        		Arrays.asList("papel", "vidro", "meral"), 15, 7));

    }

    public LevelConfig getCurrentLevel() {
        return levels.get(currentLevelIndex);
    }

    public void nextLevel() {
        if (currentLevelIndex + 1 < levels.size()) {
            currentLevelIndex++;
            loadCurrentLevel();
        }
    }

    public void loadCurrentLevel() {
        LevelConfig config = getCurrentLevel();
        
        // Limpa dados da fase anterior no GamePanel
        gp.spawnerLocations.clear();
        // gp.itemM.clearItems(); 

        // Comanda o TileManager a carregar o mapa da configuração
        gp.tileM.loadMap(config.mapPath);

        // Reposiciona o Player
        gp.player.worldX = gp.tileSize * 5;
        gp.player.worldY = gp.tileSize * 10;
        
        System.out.println("Nível carregado: " + config.levelNumber);
    }
}