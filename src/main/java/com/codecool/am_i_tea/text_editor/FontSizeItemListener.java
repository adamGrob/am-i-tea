package com.codecool.am_i_tea.text_editor;

import javax.swing.text.StyledEditorKit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FontSizeItemListener implements ItemListener {

    MyEditor myEditor;

    public FontSizeItemListener(MyEditor myEditor) {
        this.myEditor = myEditor;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

        if ((e.getStateChange() != ItemEvent.SELECTED) ||
                (myEditor.fontSizeComboBox__.getSelectedIndex() == 0)) {

            return;
        }

        String fontSizeStr = (String) e.getItem();
        int newFontSize = 0;

        try {
            newFontSize = Integer.parseInt(fontSizeStr);
        }
        catch (NumberFormatException ex) {

            return;
        }

        myEditor.fontSizeComboBox__.setAction(new StyledEditorKit.FontSizeAction(fontSizeStr, newFontSize));
        myEditor.fontSizeComboBox__.setSelectedIndex(0); // initialize to (default) select
        myEditor.editor__.requestFocusInWindow();
    }
}