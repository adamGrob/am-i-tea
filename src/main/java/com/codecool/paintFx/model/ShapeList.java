package com.codecool.paintFx.model;

import java.util.List;

public class ShapeList {

    private static ShapeList instance;

    public static ShapeList getInstance(){
        if (instance == null) {
            instance = new ShapeList();
        }
        return instance;
    }

    private List<MyShape> shapeList;

    public List<MyShape> getShapeList() {
        return shapeList;
    }

    public void setShapeList(List<MyShape> shapeList) {
        this.shapeList = shapeList;
    }

    public void emptyShapeList() {
        this.shapeList.clear();
    }
}
