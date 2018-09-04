package com.codecool.am_i_tea.text_editor;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorActionListener implements ActionListener {

    MyEditor myEditor;

    public  ColorActionListener(MyEditor myEditor){
        this.myEditor = myEditor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Color newColor =
                JColorChooser.showDialog(myEditor.frame__, "Choose a color", Color.BLACK);
        if (newColor == null) {

            myEditor.editor__.requestFocusInWindow();
            return;
        }

        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, newColor);
        myEditor.editor__.setCharacterAttributes(attr, false);
        myEditor.editor__.requestFocusInWindow();
    }
}