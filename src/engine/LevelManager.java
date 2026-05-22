package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class LevelManager {
    GamePanel gp;
    public List<LevelConfig> levels = new ArrayList<>();
    public int currentLevelIndex = 0;

    public LevelManager(GamePanel gp) {
        this.gp = gp;
        setupLevels();
    }
    private void setupLevels() {
    	// Fase 1: Tutorial - Precisa de 10 papéis
        Map<String, Integer> f1Req = new HashMap<>();
        f1Req.put("papel", 5);
        levels.add(new LevelConfig(1, "/maps/map01.txt", f1Req, 2, 50, 14, 1, 13));
        // Fase 2: Papel e Vidro
        Map<String, Integer> f2Req = new HashMap<>();
        f2Req.put("papel", 5);
        f2Req.put("vidro", 5);
        levels.add(new LevelConfig(2, "/maps/map02.txt", f2Req, 7, 50, 14, 2, 14));
        // Fase 3: Diversos
        Map<String, Integer> f3Req = new HashMap<>();
        f3Req.put("papel", 3);
        f3Req.put("plastico", 7);
        f3Req.put("metal", 4);
        f3Req.put("vidro", 6);
        levels.add(new LevelConfig(3, "/maps/map03.txt", f3Req, 7, 50, 14, 0, 15));
        Map<String, Integer> f4Req = new HashMap<>();
        f4Req.put("papel", 5);
        f4Req.put("plastico", 5);
        f4Req.put("metal", 5);
        f4Req.put("vidro", 5);
        f4Req.put("organico", 5);
        levels.add(new LevelConfig(4, "/maps/map04.txt", f4Req, 7, 75, 28, 0, 16));
        if(!levels.isEmpty()) {
        	levels.get(0).unlocked = true;
        }
    }
    
    public void unlockNextLevel() {
    	if (currentLevelIndex + 1 < levels.size()) {
    		levels.get(currentLevelIndex + 1).unlocked = true;
    	}
    }
    
    public void resetLevelProgress() {
    	for(int i = 0; i< levels.size(); i++) {
    		if (i ==0) {
    			levels.get(i).unlocked = true;
    		} else {
    			levels.get(i).unlocked = false;
    		}
    	}
    	this.currentLevelIndex = 0;
    	System.out.println("Progresso de fases resetado para o próximo jogador");
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
        
        // 1. Limpa dados da fase anterior
        gp.spawnerData.clear();
        gp.itemM.clearItems(); 
        gp.itensEntreguesFase.clear(); // Limpa o progresso de tasks

        // 2. Define o objetivo baseado na soma dos requisitos individuais
        gp.totalItemsNoNivel = config.getTotalRequiredItems(); 
        gp.itensColetadosTotal = 0; // Total geral para a barra de progresso
        
        // Inicializa o mapa de entregas com 0 para cada item exigido na fase
        for (String tipo : config.itemsRequired.keySet()) {
            gp.itensEntreguesFase.put(tipo, 0);
        }
        
        // 3. Carrega o mapa e spawns
        gp.tileM.loadMap(config.mapPath, config.maxCols, config.maxRows);
        gp.itemM.preSpawnItems();

        // 4. Reposiciona o Player (Seu código original)
        gp.player.worldX = gp.tileSize * 5;
        gp.player.worldY = gp.tileSize * 10;
        gp.player.velocityY = 0;
        gp.player.jumping = false;
        gp.player.onLadder = false;
        gp.player.inWater = false;
        
        // Sons e Música
       
        gp.stopMusic();
        gp.playFundo(config.musicID);
        
        gp.player.currentFootstepSound = -1;
        gp.player.waterSoundPlaying = false;
        gp.player.waterToxicSoundPlaying = false;
        gp.player.elevatorSoundPlaying = false;
        
        System.out.println("Nível: " + config.levelNumber + " | Tasks: " + config.itemsRequired);
    }
}