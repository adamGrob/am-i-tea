package com.codecool.am_i_tea.text_editor;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

// TODO this class is not yet used, need to figure out how to do it

public class UndoEditListener implements UndoableEditListener {

    MyEditor myEditor;

    public UndoEditListener(MyEditor myEditor) {
        this.myEditor = myEditor;
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {

        myEditor.undoMgr__.addEdit(e.getEdit()); // remember the edit
    }
}