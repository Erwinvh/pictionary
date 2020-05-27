package comms;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class DrawUpdate implements Serializable {
    private int brushSize;
    private Color color;

    private Point2D position;

    public DrawUpdate(int brushSize, Color color, Point2D position) {
        this.brushSize = brushSize;
        this.color = color;
        this.position = position;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public Color getColor() {
        return color;
    }

    public Point2D getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "DrawUpdate{" +
                "brushSize=" + brushSize +
                ", color=" + color +
                ", position=" + position +
                '}';
    }
}