package com.codecool.am_i_tea.text_editor;

import com.codecool.am_i_tea.text_editor.editor_utility.BulletsUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.DocumentUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.ParaUtility;

import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BulletActionListener implements ActionListener {

    private MyEditor myEditor;
    private DocumentUtility documentUtility;
    private ParaUtility paraUtility;
    private BulletsUtility bulletsUtility;

    private MyEditor.BulletActionType bulletActionType;

    public BulletActionListener(MyEditor myEditor,
                                MyEditor.BulletActionType actionType,
                                DocumentUtility documentUtility,
                                ParaUtility paraUtility,
                                BulletsUtility bulletsUtility) {
        this.myEditor = myEditor;
        bulletActionType = actionType;
        this.documentUtility = documentUtility;
        this.paraUtility = paraUtility;
        this.bulletsUtility = bulletsUtility;
    }

    /*
     * Common routine for insert and remove bullet actions. This routine
     * loops thru the selected text and inserts or removes a bullet.
     * - For insert action: inserts a bullet at the beginning of each para
     * of selected text. The paras already bulleted or numbered are ignored.
     * - For remove bullet action: removes the bullet in case a para is
     * bulleted for the selected text.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        String selectedText = myEditor.editor__.getSelectedText();

        if ((selectedText == null) || (selectedText.trim().isEmpty())) {

            myEditor.editor__.requestFocusInWindow();
            return;
        }

        StyledDocument doc = documentUtility.getEditorDocument();
        Element paraEle = doc.getParagraphElement(myEditor.editor__.getSelectionStart());
        int paraEleStart = paraEle.getStartOffset();
        int paraEleEnd = 0;

        BULLETS_PARA_LOOP:
        do {
            paraEle = doc.getParagraphElement(paraEleStart);
            paraEleEnd = paraEle.getEndOffset();

            if ((paraEleEnd - paraEleStart) <= 1) { // empty line/para

                paraEleStart = paraEleEnd;
                paraEle = doc.getParagraphElement(paraEleStart);
                continue BULLETS_PARA_LOOP;
            }

            switch (bulletActionType) {

                case INSERT:
                    if ((! paraUtility.isBulletedPara(paraEleStart)) &&
                            (! paraUtility.isNumberedPara(paraEleStart))) {

                        bulletsUtility.insertBullet(paraEleStart, paraEleStart);
                    }

                    break; // switch

                case REMOVE:
                    if (paraUtility.isBulletedPara(paraEleStart)) {

                        bulletsUtility.removeBullet(paraEleStart, MyEditor.BULLET_LENGTH);
                    }
            }

            // Get the updated para element details after bulleting
            paraEle = doc.getParagraphElement(paraEleStart);
            paraEleEnd = paraEle.getEndOffset();

            paraEleStart = paraEleEnd;

        } while (paraEleEnd <= myEditor.editor__.getSelectionEnd());
        // BULLETS_PARA_LOOP

        myEditor.editor__.requestFocusInWindow();
    }
}
