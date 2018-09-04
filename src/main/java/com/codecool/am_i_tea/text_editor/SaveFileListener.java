package com.codecool.am_i_tea.text_editor;

import com.codecool.am_i_tea.text_editor.editor_utility.DocumentUtility;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class SaveFileListener implements ActionListener {

    private MyEditor myEditor;
    private DocumentUtility documentUtility;

    public SaveFileListener(MyEditor myEditor, DocumentUtility documentUtility) {
        this.myEditor = myEditor;
        this.documentUtility = documentUtility;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (myEditor.file__ == null) {

            myEditor.file__ = chooseFile();

            if (myEditor.file__ == null) {

                return;
            }
        }

        DefaultStyledDocument doc = (DefaultStyledDocument) documentUtility.getEditorDocument();

        try (OutputStream fos = new FileOutputStream(myEditor.file__);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(doc);
        }
        catch (IOException ex) {

            throw new RuntimeException(ex);
        }

        documentUtility.setFrameTitleWithExtn(myEditor.file__.getName());
    }

    private File chooseFile() {

        JFileChooser chooser = new JFileChooser();

        if (chooser.showSaveDialog(myEditor.frame__) == JFileChooser.APPROVE_OPTION) {

            return chooser.getSelectedFile();
        }
        else {
            return null;
        }
    }
}