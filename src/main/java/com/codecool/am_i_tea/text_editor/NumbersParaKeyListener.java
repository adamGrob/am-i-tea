package com.codecool.am_i_tea.text_editor;

import com.codecool.am_i_tea.text_editor.editor_utility.BulletsUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.DocumentUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.NumbersUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.ParaUtility;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * Key listener class for key press and release actions within a numbered
 * para. The keys include Enter, Backspace, Delete and Left. The Enter press
 * is implemented with both the keyPressed and keyReleased methods. The Delete,
 * Backspace and Left key press is implemented within the keyPressed.
 *
 * This also includes key press actions (backspace, enter and delete) for
 * the text selected within the numbered paras.
 */
public class NumbersParaKeyListener implements KeyListener {

    private MyEditor myEditor;
    private DocumentUtility documentUtility;
    private ParaUtility paraUtility;
    private NumbersUtility numbersUtility;

    // These two variables are derived in the keyPressed and are used in
    // keyReleased method.
    private String prevParaText_;
    private int prevParaEleStart_;

    // Identifies if a key is pressed in a numbered para.
    // This is required to distinguish from the bulleted para.
    private boolean numberedPara_;

    public NumbersParaKeyListener(MyEditor myEditor,
                                  DocumentUtility documentUtility,
                                  ParaUtility paraUtility,
                                  NumbersUtility numbersUtility) {
        this.myEditor = myEditor;
        this.documentUtility = documentUtility;
        this.paraUtility = paraUtility;
        this.numbersUtility = numbersUtility;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        String selectedText = myEditor.editor__.getSelectedText();
        if ((selectedText == null) || (selectedText.trim().isEmpty())) {
            // continue, processing key press without any selected text
        }
        else {
            // text is selected within numbered para and a key is pressed
            doReplaceSelectionRoutine();
            return;
        }

        numberedPara_ = false;
        int pos = myEditor.editor__.getCaretPosition();

        if (! isNumberedParaForPos(pos)) {
            return;
        }

        Element paraEle = documentUtility.getEditorDocument().getParagraphElement(pos);
        int paraEleStart = paraEle.getStartOffset();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: // same as that of VK_KP_LEFT
            case KeyEvent.VK_KP_LEFT: int newPos = pos -
                    (numbersUtility.getNumberLength(paraEleStart) + 1);
                myEditor.doLeftArrowKeyRoutine(newPos, myEditor.startPosPlusNum__);
                break;
            case KeyEvent.VK_DELETE: doDeleteKeyRoutine(paraEle, pos);
                break;
            case KeyEvent.VK_BACK_SPACE: doBackspaceKeyRoutine(paraEle);
                break;
            case KeyEvent.VK_ENTER: getPrevParaDetails(pos);
                break;
        }

    } // keyPressed()

    private boolean isNumberedParaForPos(int caretPos) {

        Element paraEle = documentUtility.getEditorDocument().getParagraphElement(caretPos);
        return paraUtility.isNumberedPara(paraEle.getStartOffset());
    }

    /*
     * Routine for processing selected text with numbered paras
     * after pressing Enter, Backspace or Delete keys, and the
     * paste insert replacing the selected text.
     */
    private void doReplaceSelectionRoutine() {

        // Get selection start and end para details.
        // Check if there are numbered paras at top and bottom
        // of the selection. Re-number if needed i.e., when selection
        // is replaced in the middle of numbered paras or at the top
        // items of the numbered paras.

        StyledDocument doc = documentUtility.getEditorDocument();
        Element topParaEle = doc.getParagraphElement(myEditor.editor__.getSelectionStart());
        Element bottomParaEle = doc.getParagraphElement(myEditor.editor__.getSelectionEnd());

        int bottomParaEleStart = bottomParaEle.getStartOffset();
        int bottomParaEleEnd = bottomParaEle.getEndOffset();

        // No numbered text at bottom, no processing required -or-
        // no next para after selection end (end of document text).
        if ((! paraUtility.isNumberedPara(bottomParaEleStart)) ||
                (bottomParaEleEnd > doc.getLength())) {
            return;
        }

        // Check if para following the selection end is numbered or not.
        Element paraEle = doc.getParagraphElement(bottomParaEleEnd + 1);
        int paraEleStart = paraEle.getStartOffset();

        if (! paraUtility.isNumberedPara(paraEleStart)) {

            return;
        }

        // Process re-numbering

        Integer numTop = numbersUtility.getParaNumber(topParaEle.getStartOffset());

        if (numTop != null) {
            // There are numbered items above the removed para, and
            // there are numbered items following the removed para;
            // bottom numbers start from numTop + 1.
            doNewNumbers(paraEleStart, numTop);
        }
        else {
            // numTop == null
            // There are no numbered items above the removed para, and
            // there are numbered items following the removed para;
            // bottom numbers start from 1.
            doNewNumbers(paraEleStart, 0);
        }

    } // doReplaceSelectionRoutine()

    /*
     * Common routine to arrive at new numbers and replace the previous
     * ones after the following actions within numbered para list:
     * - Enter, Delete, Backspace key press.
     * - Delete, Backspace and paste-insert selected text.
     */
    private void doNewNumbers(int nextParaEleStart, Integer newNum) {

        StyledDocument doc = documentUtility.getEditorDocument();
        Element nextParaEle = doc.getParagraphElement(nextParaEleStart);
        boolean nextParaIsNumbered = true;

        NUMBERED_PARA_LOOP:
        while (nextParaIsNumbered) {

            Integer oldNum = numbersUtility.getParaNumber(nextParaEleStart);
            newNum++;
            replaceNumbers(nextParaEleStart, oldNum, newNum);
            // nextParaIsNumbered = false;

            // Get following para details after number is replaced for a para

            int nextParaEleEnd = nextParaEle.getEndOffset();
            int nextParaPos = nextParaEleEnd + 1;

            if (nextParaPos > doc.getLength()) {
                break NUMBERED_PARA_LOOP; // no next para, end of document text
            }

            nextParaEle = doc.getParagraphElement(nextParaPos);
            nextParaEleStart = nextParaEle.getStartOffset();
            nextParaIsNumbered = paraUtility.isNumberedPara(nextParaEleStart);
        }
    }

    private void replaceNumbers(int nextParaEleStart, Integer prevNum,
                                Integer newNum) {
        try {
            ((DefaultStyledDocument) documentUtility.getEditorDocument()).replace(
                    nextParaEleStart,
                    numbersUtility.getNumberString(prevNum).length(),
                    numbersUtility.getNumberString(newNum),
                    numbersUtility.getNumbersAttributes(nextParaEleStart, newNum));
        }
        catch(BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Delete key press routine within a numbered para.
    private void doDeleteKeyRoutine(Element paraEle, int pos) {
        int paraEleEnd = paraEle.getEndOffset();
        if (paraEleEnd > documentUtility.getEditorDocument().getLength()) {
            return; // no next para, end of document text
        }

        if (pos == (paraEleEnd - 1)) {
            // last char of para; -1 is for CR
            Element nextParaEle =
                    documentUtility.getEditorDocument().getParagraphElement(paraEleEnd + 1);
            int nextParaEleStart = nextParaEle.getStartOffset();

            if (paraUtility.isNumberedPara(nextParaEleStart)) {
                numbersUtility.removeNumber(pos, numbersUtility.getNumberLength(nextParaEleStart));
                doReNumberingForDeleteKey(paraEleEnd + 1);
            }
            // else, not a numbered para
            // delete happens normally (one char)
        }
    }

    private void doReNumberingForDeleteKey(int delParaPos) {

        // Get para element details where delete key is pressed
        StyledDocument doc = documentUtility.getEditorDocument();
        Element paraEle = doc.getParagraphElement(delParaPos);
        int paraEleStart = paraEle.getStartOffset();
        int paraEleEnd = paraEle.getEndOffset();

        // Get bottom para element details
        Element bottomParaEle = doc.getParagraphElement(paraEleEnd + 1);
        int bottomParaEleStart = bottomParaEle .getStartOffset();

        // In case bottom para is not numbered or end of document,
        // no re-numbering is required.
        if ((paraEleEnd > doc.getLength()) ||
                (! paraUtility.isNumberedPara(bottomParaEleStart))) {
            return;
        }

        Integer n = numbersUtility.getParaNumber(paraEleStart);
        doNewNumbers(bottomParaEleStart, n);
    }

    // Backspace key press routine within a numbered para.
    // Also, see EditorCaretListener.
    private void doBackspaceKeyRoutine(Element paraEle) {

        // In case the position of cursor at the backspace is just after
        // the number: remove the number and re-number the following ones.
        if (myEditor.startPosPlusNum__) {
            int startOffset = paraEle.getStartOffset();
            numbersUtility.removeNumber(startOffset, numbersUtility.getNumberLength(startOffset));
            doReNumberingForBackspaceKey(paraEle, startOffset);
            myEditor.startPosPlusNum__ = false;
        }
    }

    private void doReNumberingForBackspaceKey(Element paraEle, int paraEleStart) {

        // Get bottom para element and check if numbered.
        StyledDocument doc = documentUtility.getEditorDocument();
        Element bottomParaEle = doc.getParagraphElement(paraEle.getEndOffset() + 1);
        int bottomParaEleStart = bottomParaEle.getStartOffset();

        if (! paraUtility.isNumberedPara(bottomParaEleStart)) {
            return; // there are no numbers following this para, and
            // no re-numbering required.
        }

        // Get top para element and number

        Integer numTop = null;

        if (paraEleStart == 0) {
            // beginning of document, no top para exists
            // before the document start; numTop = null
        }
        else {
            Element topParaEle = doc.getParagraphElement(paraEleStart - 1);
            numTop = numbersUtility.getParaNumber(topParaEle.getStartOffset());
        }

        if (numTop == null) {
            // There are no numbered items above the removed para, and
            // there are numbered items following the removed para;
            // bottom numbers start from 1.
            doNewNumbers(bottomParaEleStart, 0);
        }
        else {
            // numTop != null
            // There are numbered items above the removed para, and
            // there are numbered items following the removed para;
            // bottom numbers start from numTop + 1.
            doNewNumbers(bottomParaEleStart, numTop);
        }
    }

    // This method is used with Enter key press routine.
    // Two instance variable values are derived here and are used
    // in the keyReleased() method: prevParaEleStart_ and prevParaText_
    private void getPrevParaDetails(int pos) {

        pos =  pos - 1;

        if (isNumberedParaForPos(pos)) {
            numberedPara_ = true;
            Element paraEle = documentUtility.getEditorDocument().getParagraphElement(pos);
            prevParaEleStart_ = paraEle.getStartOffset();
            prevParaText_ =
                    paraUtility.getPrevParaText(prevParaEleStart_, paraEle.getEndOffset());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (! numberedPara_) {
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER: doEnterKeyRoutine();
                break;
        }
    }


    private void doEnterKeyRoutine() {

        String prevParaText = prevParaText_;
        int prevParaEleStart = prevParaEleStart_;
        int len = numbersUtility.getNumberLength(prevParaEleStart) + 1; // +1 for CR

        // Check if prev para with numbers has text
        if (prevParaText.length() == len) {

            // Para has numbers and no text, remove number from para
            numbersUtility.removeNumber(prevParaEleStart, len);
            myEditor.editor__.setCaretPosition(prevParaEleStart);
            return;
        }
        // Prev para with number and text,
        // insert number for new para (current position)
        Integer num = numbersUtility.getParaNumber(prevParaEleStart);
        num++;
        numbersUtility.insertNumber(myEditor.editor__.getCaretPosition(), prevParaEleStart, num);

        // After insert, check for numbered paras following the newly
        // inserted numberd para; and re-number those paras.

        // Get newly inserted number para details
        StyledDocument doc = documentUtility.getEditorDocument();
        Element newParaEle = doc.getParagraphElement(myEditor.editor__.getCaretPosition());
        int newParaEleEnd = newParaEle.getEndOffset();

        if (newParaEleEnd > doc.getLength()) {

            return; // no next para, end of document text
        }

        // Get next para (following the newly inserted one) and
        // re-number para only if already numered.
        Element nextParaEle = doc.getParagraphElement(newParaEleEnd + 1);
        int nextParaEleStart = nextParaEle.getStartOffset();

        if (paraUtility.isNumberedPara(nextParaEleStart)) {

            doNewNumbers(nextParaEleStart, num);
        }

    }

}