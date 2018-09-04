package com.codecool.am_i_tea.text_editor.editor_utility;

import com.codecool.am_i_tea.text_editor.MyEditor;
import com.codecool.am_i_tea.text_editor.UndoEditListener;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

public class DocumentUtility {

    private MyEditor myEditor;

    public DocumentUtility(MyEditor myEditor) {
        this.myEditor = myEditor;
    }

    public void setFrameTitleWithExtn(String titleExtn) {
        myEditor.frame__.setTitle(MyEditor.MAIN_TITLE + titleExtn);
    }

    public StyledDocument getNewDocument() {
        StyledDocument doc = new DefaultStyledDocument();
        doc.addUndoableEditListener(new UndoEditListener(myEditor));
        return doc;
    }

    public StyledDocument getEditorDocument() {
        return (DefaultStyledDocument) myEditor.editor__.getDocument();
    }
}
