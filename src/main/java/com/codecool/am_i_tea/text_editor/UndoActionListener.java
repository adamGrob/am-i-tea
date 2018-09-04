package com.codecool.am_i_tea.text_editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UndoActionListener implements ActionListener {

    private MyEditor.UndoActionType undoActionType;
    private MyEditor myEditor;

    public UndoActionListener(MyEditor myEditor, MyEditor.UndoActionType type) {

        this.myEditor = myEditor;
        undoActionType = type;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (undoActionType) {

            case UNDO:
                if (! myEditor.undoMgr__.canUndo()) {

                    myEditor.editor__.requestFocusInWindow();
                    return; // no edits to undo
                }

                myEditor.undoMgr__.undo();
                break;

            case REDO:
                if (! myEditor.undoMgr__.canRedo()) {

                    myEditor.editor__.requestFocusInWindow();
                    return; // no edits to redo
                }

                myEditor.undoMgr__.redo();
        }

        myEditor.editor__.requestFocusInWindow();
    }
}