package com.codecool.am_i_tea.text_editor;

import javax.swing.text.StyledEditorKit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class TextAlignItemListener implements ItemListener {

    MyEditor myEditor;

    public TextAlignItemListener(MyEditor myEditor){
        this.myEditor = myEditor;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

        if ((e.getStateChange() != ItemEvent.SELECTED) ||
                (myEditor.textAlignComboBox__.getSelectedIndex() == 0)) {

            return;
        }

        String alignmentStr = (String) e.getItem();
        int newAlignment = myEditor.textAlignComboBox__.getSelectedIndex() - 1;
        // New alignment is set based on these values defined in StyleConstants:
        // ALIGN_LEFT 0, ALIGN_CENTER 1, ALIGN_RIGHT 2, ALIGN_JUSTIFIED 3
        myEditor.textAlignComboBox__.setAction(new StyledEditorKit.AlignmentAction(alignmentStr, newAlignment));
        myEditor.textAlignComboBox__.setSelectedIndex(0); // initialize to (default) select
        myEditor.editor__.requestFocusInWindow();
    }
}