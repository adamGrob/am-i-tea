package com.codecool.am_i_tea.text_editor;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditButtonActionListener implements ActionListener {

    private MyEditor myEditor;

    public EditButtonActionListener(MyEditor myEditor){
        this.myEditor = myEditor;
    }

    public void actionPerformed(ActionEvent e) {

        myEditor.editor__.requestFocusInWindow();
    }
}