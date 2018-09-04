package com.codecool.am_i_tea.text_editor;

import javax.swing.text.StyledEditorKit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FontFamilyItemListener implements ItemListener {

    MyEditor myEditor;

    public FontFamilyItemListener(MyEditor myEditor) {
        this.myEditor = myEditor;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

        if ((e.getStateChange() != ItemEvent.SELECTED) ||
                (myEditor.fontFamilyComboBox__.getSelectedIndex() == 0)) {

            return;
        }

        String fontFamily = (String) e.getItem();
        myEditor.fontFamilyComboBox__.setAction(new StyledEditorKit.FontFamilyAction(fontFamily, fontFamily));
        myEditor.fontFamilyComboBox__.setSelectedIndex(0); // initialize to (default) select
        myEditor.editor__.requestFocusInWindow();
    }
}