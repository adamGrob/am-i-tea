package com.codecool.am_i_tea.text_editor.editor_utility;

import com.codecool.am_i_tea.text_editor.MyEditor;

import javax.swing.text.*;

public class ParaUtility {

    private MyEditor myEditor;
    private DocumentUtility documentUtility;

    public ParaUtility(MyEditor myEditor, DocumentUtility documentUtility) {
        this.myEditor = myEditor;
        this.documentUtility = documentUtility;
    }

    public char getParaFirstCharacter(int paraEleStart) {
        String firstChar = "";
        try {
            firstChar = myEditor.editor__.getText(paraEleStart, 1);
        } catch (BadLocationException ex) {

            throw new RuntimeException(ex);
        }
        return firstChar.charAt(0);
    }

    public boolean isBulletedPara(int paraEleStart) {
        if (getParaFirstCharacter(paraEleStart) == MyEditor.BULLET_CHAR) {
            return true;
        }
        return false;
    }

    public boolean isNumberedPara(int paraEleStart) {
        AttributeSet attrSet = getParaStartAttributes(paraEleStart);
        Integer paraNum = (Integer) attrSet.getAttribute(MyEditor.NUMBERS_ATTR);
        if ((paraNum == null) || (!isFirstCharNumber(paraEleStart))) {
            return false;
        }
        return true;
    }

    public AttributeSet getParaStartAttributes(int pos) {
        StyledDocument doc = (DefaultStyledDocument) myEditor.editor__.getDocument();
        Element charEle = doc.getCharacterElement(pos);
        return charEle.getAttributes();
    }

    public String getPrevParaText(int prevParaEleStart, int prevParaEleEnd) {
        String prevParaText = "";
        try {
            prevParaText = documentUtility.getEditorDocument().getText(prevParaEleStart,
                    (prevParaEleEnd - prevParaEleStart));
        } catch (BadLocationException ex) {

            throw new RuntimeException(ex);
        }
        return prevParaText;
    }

    public boolean isFirstCharNumber(int paraEleStart) {
        if (Character.isDigit(getParaFirstCharacter(paraEleStart))) {

            return true;
        }
        return false;
    }


}
