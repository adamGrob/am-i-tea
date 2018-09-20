package com.codecool.paintFx.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class StoredLine {

    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private double brushSize;
    private double red;
    private double green;
    private double blue;

    public StoredLine(double startX, double startY, double endX, double endY, double brushSize, Paint color) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.brushSize = brushSize;
        Color col = (Color) color;
        this.red = col.getRed();
        this.green = col.getGreen();
        this.blue = col.getBlue();
    }

    public StoredLine(){}

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

    public double getBrushSize() {
        return brushSize;
    }

    public void setBrushSize(double brushSize) {
        this.brushSize = brushSize;
    }

    public double getRed() {
        return red;
    }

    public void setRed(double red) {
        this.red = red;
    }

    public double getGreen() {
        return green;
    }

    public void setGreen(double green) {
        this.green = green;
    }

    public double getBlue() {
        return blue;
    }

    public void setBlue(double blue) {
        this.blue = blue;
    }
}
