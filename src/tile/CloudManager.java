package tile;

import engine.GamePanel;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CloudManager {
    GamePanel gp;
    public HashMap<Point, Integer> respawnTimer = new HashMap<>();
    public HashMap<Point, Integer> lifeTimer = new HashMap<>();
    public CloudManager(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        Iterator<Map.Entry<Point, Integer>> lifeIt = lifeTimer.entrySet().iterator();
        while (lifeIt.hasNext()) {
            Map.Entry<Point, Integer> entry = lifeIt.next();
            int timeLeft = entry.getValue() - 1;

            if (timeLeft <= 0) {
                Point p = entry.getKey();
                gp.tileM.mapTileNum[p.x][p.y] = 0; 
                respawnTimer.put(p, 300); 
                lifeIt.remove();
            } else {
                entry.setValue(timeLeft);
            }
        }

        Iterator<Map.Entry<Point, Integer>> respawnIt = respawnTimer.entrySet().iterator();
        while (respawnIt.hasNext()) {
            Map.Entry<Point, Integer> entry = respawnIt.next();
            int timeLeft = entry.getValue() - 1;
            if (timeLeft <= 0) {
                gp.tileM.mapTileNum[entry.getKey().x][entry.getKey().y] = 8;
                respawnIt.remove();
            } else {
                entry.setValue(timeLeft);
            }
        }
    }
}