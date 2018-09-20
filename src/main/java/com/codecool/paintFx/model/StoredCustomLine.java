package com.codecool.paintFx.model;

import java.util.List;

public class StoredCustomLine {

    private List<StoredLine> storedLineList;

    public StoredCustomLine(List<StoredLine> storedLineList) {
        this.storedLineList = storedLineList;
    }

    public StoredCustomLine(){}

    public List<StoredLine> getStoredLineList() {
        return storedLineList;
    }

    public void setStoredLineList(List<StoredLine> storedLineList) {
        this.storedLineList = storedLineList;
    }
}
