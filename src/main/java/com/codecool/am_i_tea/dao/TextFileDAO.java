package com.codecool.am_i_tea.dao;

import com.codecool.am_i_tea.model.TextFile;

public class TextFileDAO {

    private TextFile currentFile;

    public TextFile getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(TextFile currentFile) {
        this.currentFile = currentFile;
    }
}
