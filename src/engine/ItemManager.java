package engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import entities.Item;

public class ItemManager {
    GamePanel gp;
    public ArrayList<Item> activeItems = new ArrayList<>();
    private int spawnCounter = 0;
    private Random random = new Random();
    private double floatingCounter = 0;
    
    public BufferedImage papelImage,
    plasticoImage,
    metalImage,
    vidroImage,
    organicImage;

    public ItemManager(GamePanel gp) {
        this.gp = gp;
        getItemImages();
    }

    public void update() {
        spawnCounter++;
        if (spawnCounter >= 120) {
            spawnAttempt();
            spawnCounter = 0;
        }
        
        floatingCounter += 0.05;
        if(floatingCounter > Math.PI *2) {
        	floatingCounter = 0;
        }
    }

    private void spawnAttempt() {
        LevelConfig config = gp.levelM.getCurrentLevel();

        if (activeItems.isEmpty() && gp.itensColetadosTotal < gp.totalItemsNoNivel) {
            
            // 1. Filtra o que ainda falta (Tasks)
            ArrayList<String> tiposNecessarios = new ArrayList<>();
            for (String tipo : config.itemsRequired.keySet()) {
                int objetivo = config.itemsRequired.get(tipo);
                int entregues = gp.itensEntreguesFase.getOrDefault(tipo, 0);
                
                if (entregues < objetivo) {
                    tiposNecessarios.add(tipo);
                }
            }

            // Se não falta mais nada, sai do método
            if (tiposNecessarios.isEmpty()) return;

            // 2. Escolhe um local de spawn aleatório entre os disponíveis no mapa
            if (!gp.spawnerData.isEmpty()) {
                java.util.List<java.awt.Point> locations = new java.util.ArrayList<>(gp.spawnerData.keySet());
                java.awt.Point spawnPos = locations.get(random.nextInt(locations.size()));

                // 3. Escolhe o tipo de item
                String type = tiposNecessarios.get(random.nextInt(tiposNecessarios.size()));
                
                // 4. Gera o item exatamente na posição do spawner
                activeItems.add(new Item(type, 
                    spawnPos.x * gp.tileSize, 
                    spawnPos.y * gp.tileSize, 
                    getItemColor(type)));
                
                System.out.println("Gerado: " + type + " | Aguardando coleta...");
            }
        }
    }
    
    public void preSpawnItems() {
        LevelConfig config = gp.levelM.getCurrentLevel();
        java.util.List<java.awt.Point> locations = new java.util.ArrayList<>(gp.spawnerData.keySet());
        
        // Lista de tipos que a fase exige
        ArrayList<String> requirementList = new ArrayList<>(config.itemsRequired.keySet());
        if (requirementList.isEmpty()) return;

        for (java.awt.Point p : locations) {
            // Escolhe um tipo que faz parte dos requisitos da fase
            String type = requirementList.get(random.nextInt(requirementList.size()));
            
            activeItems.add(new Item(type, p.x * gp.tileSize, p.y * gp.tileSize, getItemColor(type)));
            gp.spawnerData.put(p, 1);
            
            // Opcional: Parar o pre-spawn se já encher o mapa ou atingir a meta
            if (activeItems.size() >= config.getTotalRequiredItems()) break;
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
    
    public void getItemImages() {
        papelImage = setup("/textures/papel.png");
        plasticoImage = setup("/textures/plastico.png");
        metalImage = setup("/textures/metal.png");
        vidroImage = setup("/textures/vidro.png");     
        organicImage = setup("/textures/organico.png");
    }
    
    public BufferedImage setup(String imagePath) {
        try {
            return ImageIO.read(getClass().getResource(imagePath));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Erro ao carregar: " + imagePath);
            return null; 
        }
    }
    
    public BufferedImage getImageByType(String type) {
        if (type == null) return null;
        
        switch (type.toLowerCase()) {
            case "papel":    return papelImage;
            case "vidro":    return vidroImage;
            case "metal":    return metalImage;
            case "plastico": return plasticoImage;
            case "organico": return organicImage;
            default:         return null;
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
            	
            	double individualTime = floatingCounter + item.animationOffset;
            	int animationY = (int) (Math.sin(individualTime) * 5);
            	
            	BufferedImage image = getImageByType(item.type);

                if (image != null) {
                	g.drawImage(image, screenX + 8, screenY + 8 + animationY, 32, 32, null);
                } else {
                    g.setColor(item.color);
                    g.fillOval(screenX + 12, screenY + 12, 24, 24); 
                }
            }
        }
    }
    
    public void clearItems() {
        activeItems.clear();
    }
}