package com.codecool.paintFx.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public abstract class MyShape {

    double startX, startY;

    Paint color;

    double brushSize;

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public Paint getColor() {
        return color;
    }

    public void setColor(Paint color) {
        this.color = color;
    }

    public double getBrushSize() {
        return brushSize;
    }

    public void setBrushSize(double brushSize) {
        this.brushSize = brushSize;
    }

    public abstract void display(GraphicsContext graphicsContext);

}
