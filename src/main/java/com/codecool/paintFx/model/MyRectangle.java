package com.codecool.paintFx.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class MyRectangle extends MyPoligon {

    public MyRectangle(double x, double y, double width, double height, Paint color, double brushSize) {
        this.startX = x;
        this.startY = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.brushSize = brushSize;
    }

    @Override
    public void display(GraphicsContext graphicsContext) {
        graphicsContext.strokeRect(startX, startY, width, height);
    }
}
