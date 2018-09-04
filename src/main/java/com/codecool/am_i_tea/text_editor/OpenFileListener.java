package com.codecool.am_i_tea.text_editor;

import com.codecool.am_i_tea.text_editor.editor_utility.DocumentUtility;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class OpenFileListener implements ActionListener {

    private MyEditor myEditor;
    private DocumentUtility documentUtility;

    public OpenFileListener(MyEditor myEditor, DocumentUtility documentUtility) {
        this.myEditor = myEditor;
        this.documentUtility = documentUtility;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        myEditor.file__ = chooseFile();

        if (myEditor.file__ == null) {

            return;
        }

        readFile(myEditor.file__);
        documentUtility.setFrameTitleWithExtn(myEditor.file__.getName());
    }

    private File chooseFile() {

        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(myEditor.frame__) == JFileChooser.APPROVE_OPTION) {

            return chooser.getSelectedFile();
        }
        else {
            return null;
        }
    }

    private void readFile(File file) {

        StyledDocument doc = null;

        try (InputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            doc = (DefaultStyledDocument) ois.readObject();
        }
        catch (FileNotFoundException ex) {

            JOptionPane.showMessageDialog(myEditor.frame__, "Input file was not found!");
            return;
        }
        catch (ClassNotFoundException | IOException ex) {

            throw new RuntimeException(ex);
        }

        myEditor.editor__.setDocument(doc);
        doc.addUndoableEditListener(new UndoEditListener(myEditor));
    }


}