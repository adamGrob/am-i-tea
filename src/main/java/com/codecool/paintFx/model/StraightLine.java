package com.codecool.paintFx.model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class StraightLine extends MyShape {

    private double endX, endY;

    public StraightLine(double startX, double startY, double endX, double endY, Paint color, double brushSize) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;
        this.brushSize = brushSize;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    @Override
    public void display(GraphicsContext graphicsContext) {
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }
}
