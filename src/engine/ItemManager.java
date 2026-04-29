package engine;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import entities.Item;

public class ItemManager {
    GamePanel gp;
    public ArrayList<Item> activeItems = new ArrayList<>();
    private int spawnCounter = 0;
    private Random random = new Random();

    public ItemManager(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        // Lógica de Spawn baseada no FPS (60 FPS)
        spawnCounter++;
        // Tenta spawnar a cada 2 segundos (120 frames) se houver espaço
        if (spawnCounter >= 120) {
            spawnAttempt();
            spawnCounter = 0;
        }
    }

    private void spawnAttempt() {
        LevelConfig config = gp.levelM.getCurrentLevel();
        int limitePorSpawner = 3; // X = quantidade máxima por ponto

        if (activeItems.size() < config.maxTotalItems && !gp.spawnerData.isEmpty()) {
            
            // Transforma as chaves do Map em uma lista para sortear
            java.util.List<java.awt.Point> locations = new java.util.ArrayList<>(gp.spawnerData.keySet());
            java.awt.Point spawnPos = locations.get(random.nextInt(locations.size()));

            int itensJaGerados = gp.spawnerData.get(spawnPos);

            if (itensJaGerados < limitePorSpawner && !isLocationOccupied(spawnPos.x * gp.tileSize, spawnPos.y * gp.tileSize)) {
                String type = config.allowedItems.get(random.nextInt(config.allowedItems.size()));
                
                activeItems.add(new Item(type, spawnPos.x * gp.tileSize, spawnPos.y * gp.tileSize, getItemColor(type)));
                
                // Aumenta o contador desse spawner específico
                gp.spawnerData.put(spawnPos, itensJaGerados + 1);
            }
        }
    }
    
    public void preSpawnItems() {
        // Tenta spawnar um item em cada spawner do mapa uma vez
        for (java.awt.Point p : gp.spawnerData.keySet()) {
            LevelConfig config = gp.levelM.getCurrentLevel();
            String type = config.allowedItems.get(random.nextInt(config.allowedItems.size()));
            
            activeItems.add(new Item(type, p.x * gp.tileSize, p.y * gp.tileSize, getItemColor(type)));
            gp.spawnerData.put(p, 1); // Marca que o primeiro item já foi gerado
        }
    }

    private boolean isLocationOccupied(int x, int y) {
        for (Item i : activeItems) {
            if (i.worldX == x && i.worldY == y) return true;
        }
        return false;
    }

    public Color getItemColor(String type) {
        switch (type.toLowerCase()) {
            case "papel":    return Color.BLUE;
            case "vidro":    return Color.GREEN;
            case "metal":    return Color.YELLOW;
            case "plastico": return Color.RED;
            case "organico": return new Color(139, 69, 19); // Marrom
            default:         return Color.WHITE;
        }
    }

    public void draw(Graphics g) {
        for (int i = 0; i < activeItems.size(); i++) {
            Item item = activeItems.get(i);

            // 1. Calcula a posição relativa básica (Câmera livre)
            int screenX = item.worldX - gp.player.worldX + gp.player.screenX;
            int screenY = item.worldY - gp.player.worldY + gp.player.screenY;

            // 2. APLICA AS TRAVAS DA CÂMERA (Igual ao TileManager)
            
            // Trava Esquerda
            if (gp.player.screenX > gp.player.worldX) {
                screenX = item.worldX;
            }
            // Trava Topo
            if (gp.player.screenY > gp.player.worldY) {
                screenY = item.worldY;
            }
            // Trava Direita
            int rightOffset = gp.screenWidth - gp.player.screenX;
            if (rightOffset > gp.worldWidth - gp.player.worldX) {
                screenX = item.worldX - (gp.worldWidth - gp.screenWidth);
            }
            // Trava Fundo
            int bottomOffset = gp.screenHeight - gp.player.screenY;
            if (bottomOffset > gp.worldHeight - gp.player.worldY) {
                screenY = item.worldY - (gp.worldHeight - gp.screenHeight);
            }

            // 3. Só desenha se estiver dentro da tela (para performance)
            if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
                
                g.setColor(item.color);
                // Desenha o círculo do item (lixo)
                g.fillOval(screenX + 12, screenY + 12, 24, 24); 
            }
        }
    }
    
    public void clearItems() {
        activeItems.clear();
    }
}