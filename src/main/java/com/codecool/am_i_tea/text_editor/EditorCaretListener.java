package com.codecool.am_i_tea.text_editor;

import com.codecool.am_i_tea.text_editor.editor_utility.DocumentUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.NumbersUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.ParaUtility;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Element;

/*
 * This listener is used with the bulleted and numbered para actions.
 * The bulleted item's bullet is made of bullet + space. The cursor (caret)
 * is not allowed to position at the bullet para's first position and at
 * the space after the bullet. This listener controls the cursor position
 * in such cases; the cursor jumps/moves to the after bullet+space position
 * (indicated by startPosPlusBullet__ boolean instance variable).
 *
 * Also, the backspace and left-arrow key usage requires startPosPlusBullet__
 * to perform the routines (see doLeftArrowKeyRoutine() and BulletParaKeyListener).
 *
 * This is also similar for numbered paras (see startPosPlusNum__ and
 * NumbersParaKeyListener).
 */
public class EditorCaretListener implements CaretListener {

    private MyEditor myEditor;
    private DocumentUtility documentUtility;
    private ParaUtility paraUtility;
    private NumbersUtility numbersUtility;

    public EditorCaretListener(MyEditor myEditor,
                               DocumentUtility documentUtility,
                               ParaUtility paraUtility,
                               NumbersUtility numbersUtility) {
        this.myEditor = myEditor;
        this.documentUtility = documentUtility;
        this.paraUtility = paraUtility;
        this.numbersUtility = numbersUtility;
    }

    @Override
    public void caretUpdate(CaretEvent e) {

        myEditor.startPosPlusBullet__ = false;
        myEditor.startPosPlusNum__ = false;
        Element paraEle =
                documentUtility.getEditorDocument().getParagraphElement(myEditor.editor__.getCaretPosition());
        int paraEleStart = paraEle.getStartOffset();

        if (paraUtility.isBulletedPara(paraEleStart)) {

            if (e.getDot() == (paraEleStart + MyEditor.BULLET_LENGTH)) {

                myEditor.startPosPlusBullet__ = true;
            }
            else if (e.getDot() < (paraEleStart + MyEditor.BULLET_LENGTH)) {

                myEditor.editor__.setCaretPosition(paraEleStart + MyEditor.BULLET_LENGTH);
            }
            else {
                // continue
            }
        }
        else if (paraUtility.isNumberedPara(paraEleStart)) {

            int numLen = numbersUtility.getNumberLength(paraEleStart);

            if (e.getDot() < (paraEleStart + numLen)) {

                myEditor.editor__.setCaretPosition(paraEleStart + numLen);
            }
            else if (e.getDot() == (paraEleStart + numLen)) {

                myEditor.startPosPlusNum__ = true;
            }
            else {
                // continue
            }
        }
        else {
            // not a bulleted or numbered para
        }
    }
}