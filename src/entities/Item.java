package entities;

import java.awt.Color;

public class Item {
	public int worldX, worldY;
    public String type; // "papel", "vidro", etc.
    public Color color;
    public boolean collected = false;

    public Item(String type, int x, int y, Color color) {
        this.type = type;
        this.worldX = x;
        this.worldY = y;
        this.color = color;
    }
}
