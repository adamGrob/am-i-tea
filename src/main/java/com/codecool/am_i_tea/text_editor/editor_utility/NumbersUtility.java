package com.codecool.am_i_tea.text_editor.editor_utility;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import static com.codecool.am_i_tea.text_editor.MyEditor.NUMBERS_ATTR;

public class NumbersUtility {

    private DocumentUtility documentUtility;
    private ParaUtility paraUtility;

    public NumbersUtility(DocumentUtility documentUtility, ParaUtility paraUtility) {
        this.documentUtility = documentUtility;
        this.paraUtility = paraUtility;
    }

    /*
     * Returns the numbered para's number length. This length includes
     * the number + dot + space. For example, the text "12. A Numbered para..."
     * has the number length of 4.
     */
    public int getNumberLength(int paraEleStart) {
        Integer num = getParaNumber(paraEleStart);
        return num.toString().length() + 2;
        // 2 = dot + space after number
    }

    public Integer getParaNumber(int paraEleStart) {
        AttributeSet attrSet = paraUtility.getParaStartAttributes(paraEleStart);
        return (Integer) attrSet.getAttribute(NUMBERS_ATTR);
    }

    /*
     * The insert number routine; inserts the number in the editor document. This
     * routine is used from the insert action (ActionListener) as well as number
     * para key press actions (keyPressed or keyReleased methods of KeyListener).
     *
     * The parameter insertPos is the position at which the number is to be
     * inserted. The parameter attributesPos is the position from which the number
     * is to get its attributes (like color, font, size, etc.). The two parameter
     * values are derived differently for the insert and the number para key press
     * actions. The patameter num is the number being inserted.
     *
     * Number insert action: the insertPos and attributesPos is the same,
     * the paraEleStart.
     * Enter key press: the insertPos is the current caret position of keyReleased(),
     * and the attributesPos is the previous paraEleStart position from
     * keyPressed() method.
     */
    public void insertNumber(int insertPos, int attributesPos, Integer num) {
        try {
            documentUtility.getEditorDocument().insertString(insertPos,
                    getNumberString(num),
                    getNumbersAttributes(attributesPos, num));
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getNumberString(Integer nextNumber) {
        return new String(nextNumber.toString() + "." + " ");
    }

    public AttributeSet getNumbersAttributes(int paraEleStart, Integer number) {
        AttributeSet attrs1 = paraUtility.getParaStartAttributes(paraEleStart);
        SimpleAttributeSet attrs2 = new SimpleAttributeSet(attrs1);
        attrs2.addAttribute(NUMBERS_ATTR, number);
        return attrs2;
    }

    /*
     * The remove number routine; removes the number in the editor document. This
     * routine is used from the delete action (ActionListener) as well as the number
     * para key press actions (keyPressed or keyRemoved methods of KeyListener).
     * The keys include the Enter, Backspace, Delete keys.
     *
     * The parameter removePos is the start position and the length is the length
     * of text to be removed. Length of characters removed is derived from the
     * method getNumberLength() or +1 (includes carriage return folowing the
     * number length). The two parameter values are derived differently for
     * number remove action and number para key press actions.
     *
     * Number remove action: removePos is paraEleStart and the length from
     * the method getNumberLength().
     * Delete key press: removePos is current caret pos of keyPressed() and
     * the length from the method getNumberLength().
     * Backspace key press: removePos is paraEleStart of keyPressed() and
     * the length from the method getNumberLength().
     * Enter key press: removePos is previous paraEleStart of keyPressed() and
     * the length from the method getNumberLength() + 1 (+1 includes CR).
     */
    public void removeNumber(int removePos, int length) {
        try {
            documentUtility.getEditorDocument().remove(removePos, length);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
