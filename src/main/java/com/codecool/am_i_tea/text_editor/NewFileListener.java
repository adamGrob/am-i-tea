package com.codecool.am_i_tea.text_editor;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewFileListener implements ActionListener {

    private MyEditor myEditor;

    public NewFileListener(MyEditor myEditor) {
        this.myEditor = myEditor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        initEditorAttributes();
        myEditor.editor__.setDocument(myEditor.getNewDocument());
        myEditor.file__ = null;
        myEditor.setFrameTitleWithExtn("New file");
    }

    private void initEditorAttributes() {

        AttributeSet attrs1 = myEditor.editor__.getCharacterAttributes();
        SimpleAttributeSet attrs2 = new SimpleAttributeSet(attrs1);
        attrs2.removeAttributes(attrs1);
        myEditor.editor__.setCharacterAttributes(attrs2, true);
    }
}