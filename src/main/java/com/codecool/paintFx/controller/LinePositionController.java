package com.codecool.paintFx.controller;

import com.codecool.paintFx.model.MyShape;
import com.codecool.paintFx.model.StraightLine;
import com.codecool.paintFx.model.Position;

import java.util.ArrayList;
import java.util.List;

public class LinePositionController {

    public Position PositionSnapper (double xToSnap, double yToSnap, List<MyShape> shapeList, int rangeToSnap) {
        Position positionToSnap = new Position(xToSnap, yToSnap);
        List<Position> closestPositionList = new ArrayList<>();
        for (MyShape myShape: shapeList) {
            if(myShape.getClass() == StraightLine.class) {
                StraightLine straightLine = (StraightLine)myShape;
                closestPositionList.add(new Position(straightLine.getStartX(), straightLine.getStartY()));
                closestPositionList.add(new Position(straightLine.getEndX(), straightLine.getEndY()));
            }
        }
        return getClosestPosition(closestPositionList, positionToSnap, rangeToSnap);
    }

    private Position getClosestPosition(List<Position> positionList, Position referencePosition, int range) {
        double smallestDistance = range;
        Position closestPosition = referencePosition;
        for (Position position: positionList) {
            double currDistance = Math.sqrt((referencePosition.x - position.x) * (referencePosition.x - position.x) +
                                    (referencePosition.y - position.y) * (referencePosition.y - position.y));
            if(currDistance < smallestDistance) {
               smallestDistance = currDistance;
               closestPosition = position;
            }
        }
        return closestPosition;
    }
}
