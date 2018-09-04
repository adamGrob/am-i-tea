package com.codecool.am_i_tea.text_editor.editor_utility;

import com.codecool.am_i_tea.text_editor.MyEditor;

import javax.swing.text.BadLocationException;

public class BulletsUtility {

    private DocumentUtility documentUtility;
    private ParaUtility paraUtility;

    public BulletsUtility(DocumentUtility documentUtility, ParaUtility paraUtility) {
        this.documentUtility = documentUtility;
        this.paraUtility = paraUtility;
    }

    public boolean isBulletedPara(int paraEleStart) {
        if (paraUtility.getParaFirstCharacter(paraEleStart) == MyEditor.BULLET_CHAR) {
            return true;
        }
        return false;
    }

    /*
     * The insert bullet routine; inserts the bullet in the editor document. This
     * routine is used from the insert action (ActionListener) as well as bullet
     * para key press actions (keyPressed or keyReleased methods of KeyListener).
     *
     * The parameter insertPos is the position at which the bullet is to be
     * inserted. The parameter attributesPos is the position from which the bullet
     * is to get its attributes (like color, font, size, etc.). The two parameter
     * values are derived differently for bullet insert and bullet para Enter
     * key press actions.
     *
     * Bullet insert action: the insertPos and attributesPos is the same,
     * the paraEleStart.
     * Enter key press: the insertPos is the current caret position of keyReleased(),
     * and the attributesPos is the previous paraEleStart position from
     * keyPressed() method.
     */
    public void insertBullet(int insertPos, int attributesPos) {

        try {
            documentUtility.getEditorDocument().insertString(insertPos,
                    MyEditor.BULLET_STR_WITH_SPACE,
                    paraUtility.getParaStartAttributes(attributesPos));
        } catch (BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }

    /*
     * The remove bullet routine; removes the bullet in the editor document. This
     * routine is used from the delete action (ActionListener) as well as bullet
     * para key press actions (keyPressed or keyRemoved methods of KeyListener).
     * The keys include the Enter, Backspace, Delete keys.
     *
     * The parameter removePos is the start position and the length is the length
     * of text to be removed. Length of characters removed is: BULLET_LENGTH
     * or +1 (includes carriage return folowing the BULLET_LENGTH). The two
     * parameter values are derived differently for bullet remove and bullet
     * para key press actions.
     *
     * Bullet remove action: removePos is paraEleStart and the BULLET_LENGTH.
     * Delete key press: removePos is current caret pos of keyPressed() and
     * the BULLET_LENGTH.
     * Backspace key press: removePos is paraEleStart of keyPressed() and
     * the length is BULLET_LENGTH.
     * Enter key press: removePos is previous paraEleStart of keyPressed() and
     * the length is BULLET_LENGTH + 1 (+1 includes CR).
     */
    public void removeBullet(int removePos, int length) {

        try {
            documentUtility.getEditorDocument().remove(removePos, length);
        } catch (BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }
}
