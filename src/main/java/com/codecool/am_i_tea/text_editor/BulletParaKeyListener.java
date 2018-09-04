package com.codecool.am_i_tea.text_editor;

import com.codecool.am_i_tea.text_editor.editor_utility.BulletsUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.DocumentUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.ParaUtility;

import javax.swing.text.Element;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 * Key listener class for key press and release actions within a bulleted
 * para. The keys include Enter, Backspace, Delete and Left. The Enter press
 * is implemented with both the keyPressed and keyReleased methods. The Delete,
 * Backspace and Left key press is implemented within the keyPressed.
 */
public class BulletParaKeyListener implements KeyListener {

    private MyEditor myEditor;
    private DocumentUtility documentUtility;
    private ParaUtility paraUtility;
    private BulletsUtility bulletsUtility;

    // These two variables are derived in the keyPressed and are used in
    // keyReleased method.
    private String prevParaText_;
    private int prevParaEleStart_;

    // Identifies if a key is pressed in a bulleted para.
    // This is required to distinguish from the numbered para.
    private boolean bulletedPara_;

    public BulletParaKeyListener(MyEditor myEditor,
                                 DocumentUtility documentUtility,
                                 ParaUtility paraUtility,
                                 BulletsUtility bulletsUtility) {
        this.myEditor = myEditor;
        this.documentUtility = documentUtility;
        this.paraUtility = paraUtility;
        this.bulletsUtility = bulletsUtility;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        bulletedPara_ = false;
        int pos = myEditor.editor__.getCaretPosition();

        if (! isBulletedParaForPos(pos)) {

            return;
        }

        Element paraEle = documentUtility.getEditorDocument().getParagraphElement(pos);
        int paraEleStart = paraEle.getStartOffset();

        switch (e.getKeyCode()) {

            case KeyEvent.VK_LEFT: // same as that of VK_KP_LEFT
            case KeyEvent.VK_KP_LEFT: int newPos = pos - (MyEditor.BULLET_LENGTH + 1);
                myEditor.doLeftArrowKeyRoutine(newPos, myEditor.startPosPlusBullet__);
                break;
            case KeyEvent.VK_DELETE: doDeleteKeyRoutine(paraEle, pos);
                break;
            case KeyEvent.VK_BACK_SPACE: doBackspaceKeyRoutine(paraEle);
                break;
            case KeyEvent.VK_ENTER: getPrevParaDetails(pos);
        }

    }

    private boolean isBulletedParaForPos(int caretPos) {
        Element paraEle = documentUtility.getEditorDocument().getParagraphElement(caretPos);
        if (paraUtility.isBulletedPara(paraEle.getStartOffset())) {
            return true;
        }

        return false;
    }

    // This method is used with Enter key press routine.
    // Two instance variable values are derived here and are used
    // in the keyReleased() method: prevParaEleStart_ and prevParaText_
    private void getPrevParaDetails(int pos) {

        pos =  pos - 1;

        if (isBulletedParaForPos(pos)) {
            bulletedPara_ = true;
            Element paraEle = documentUtility.getEditorDocument().getParagraphElement(pos);
            prevParaEleStart_ = paraEle.getStartOffset();
            prevParaText_ =
                    paraUtility.getPrevParaText(prevParaEleStart_, paraEle.getEndOffset());
        }
    }

    // Delete key press routine within bulleted para.
    private void doDeleteKeyRoutine(Element paraEle, int pos) {

        int paraEleEnd = paraEle.getEndOffset();

        if (paraEleEnd > documentUtility.getEditorDocument().getLength()) {

            return; // no next para, end of document text
        }

        if (pos == (paraEleEnd - 1)) { // last char of para; -1 is for CR

            if (isBulletedParaForPos(paraEleEnd + 1)) {

                // following para is bulleted, remove
                bulletsUtility.removeBullet(pos, MyEditor.BULLET_LENGTH);
            }
            // else, not a bulleted para
            // delete happens normally (one char)
        }
    }

    // Backspace key press routine within a bulleted para.
    // Also, see EditorCaretListener.
    private void doBackspaceKeyRoutine(Element paraEle) {

        // In case the position of cursor at the backspace is just
        // before the bullet (that is BULLET_LENGTH).
        if (myEditor.startPosPlusBullet__) {

            bulletsUtility.removeBullet(paraEle.getStartOffset(), myEditor.BULLET_LENGTH);
            myEditor.startPosPlusBullet__ = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (! bulletedPara_) {

            return;
        }

        switch (e.getKeyCode()) {

            case KeyEvent.VK_ENTER: doEnterKeyRoutine();
                break;
        }
    }

    // Enter key press routine within a bulleted para.
    // Also, see keyPressed().
    private void doEnterKeyRoutine() {

        String prevParaText = prevParaText_;
        int prevParaEleStart = prevParaEleStart_;

        // Check if prev para with bullet has text
        if (prevParaText.length() < 4) {

            // Para has bullet and no text, remove bullet+CR from para
            bulletsUtility.removeBullet(prevParaEleStart, (MyEditor.BULLET_LENGTH + 1));
            myEditor.editor__.setCaretPosition(prevParaEleStart);
            return;
        }
        // Prev para with bullet and text

        // Insert bullet for next para (current position), and
        // prev para attributes are used for this bullet.
        bulletsUtility.insertBullet(myEditor.editor__.getCaretPosition(), prevParaEleStart);
    }

} // BulletParaKeyListener