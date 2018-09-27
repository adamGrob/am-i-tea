package com.codecool.am_i_tea.model;

public class TextFile {

    private String name;

    //todo read the number of pictures whenever a file is opened as well !!!!
    private int pictures;

    public TextFile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int nextPicture() {
        pictures++;
        return pictures;
    }

    public void nextPictureRollback() {
        pictures --;
    }
}
