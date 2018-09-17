package com.codecool.paintFx.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import java.util.List;

public class CustomLine extends MyShape {

    private List<StraightLine> straightLineList;

    public CustomLine(List<StraightLine> straightLineList) {
        this.straightLineList = straightLineList;
    }

    private List<StraightLine> getStraightLineList() {
        return straightLineList;
    }

    public void setStraightLineList(List<StraightLine> straightLineList) {
        this.straightLineList = straightLineList;
    }

    @Override
    public Paint getColor() {
        return straightLineList.get(0).getColor();
    }

    @Override
    public double getBrushSize() {
        return straightLineList.get(0).getBrushSize();
    }

    @Override
    public void display(GraphicsContext graphicsContext) {
        for (StraightLine currStraightLine: getStraightLineList()) {
            currStraightLine.display(graphicsContext);
        }
    }


}
