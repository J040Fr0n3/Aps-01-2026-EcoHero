package entities;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Item {
	public int worldX, worldY;
    public String type;
    public BufferedImage image;
    public Color color;
    public boolean collected = false;
    public double animationOffset;

    public Item(String type, int x, int y, Color color) {
        this.type = type;
        this.worldX = x;
        this.worldY = y;
        this.color = color;
        
        this.animationOffset = Math.random() * (Math.PI * 2);
    }
}
