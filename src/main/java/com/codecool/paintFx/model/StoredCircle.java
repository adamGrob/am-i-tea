package com.codecool.paintFx.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class StoredCircle {

    private double startX;
    private double startY;
    private double width;
    private double height;
    private double brushSize;
    private double red;
    private double green;
    private double blue;

    public StoredCircle(double startX, double startY, double width, double height, double brushSize, Paint color) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.brushSize = brushSize;
        Color col = (Color) color;
        this.red = col.getRed();
        this.green = col.getGreen();
        this.blue = col.getBlue();
    }

    public StoredCircle(){}

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

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
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
