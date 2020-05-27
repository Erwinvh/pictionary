package comms;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;

public class DrawUpdate implements Serializable {
    private int brushSize;
    private Color color;

    private List<Point2D> positions;

    public DrawUpdate(int brushSize, Color color, List<Point2D> positions) {
        this.brushSize = brushSize;
        this.color = color;
        this.positions = positions;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public Color getColor() {
        return color;
    }

    public List<Point2D> getPositions() {
        return positions;
    }
}