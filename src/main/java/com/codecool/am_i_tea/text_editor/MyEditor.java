package com.codecool.am_i_tea.text_editor;

import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.UIManager;

import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.PasteAction;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.swing.text.StyledEditorKit.ItalicAction;
import javax.swing.text.StyledEditorKit.UnderlineAction;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.undo.UndoManager;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

import java.util.List;
import java.util.Vector;
import java.util.Arrays;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;


public class MyEditor {

    JFrame frame__;
    JTextPane editor__;
    JComboBox<String> fontSizeComboBox__;
    JComboBox<String> textAlignComboBox__;
    JComboBox<String> fontFamilyComboBox__;
    UndoManager undoMgr__;
    File file__;

    enum BulletActionType {INSERT, REMOVE};
    enum NumbersActionType {INSERT, REMOVE};
    enum UndoActionType {UNDO, REDO};

    // This flag checks true if the caret position within a bulleted para
    // is at the first text position after the bullet (bullet char + space).
    // Also see EditorCaretListener and BulletParaKeyListener.
    boolean startPosPlusBullet__;

    // This flag checks true if the caret position within a numbered para
    // is at the first text position after the number (number + dot + space).
    // Alse see EditorCaretListener and NumbersParaKeyListener.
    boolean startPosPlusNum__;

    private static final String MAIN_TITLE = "My Editor - ";
    public static final String DEFAULT_FONT_FAMILY = "SansSerif";
    public static final int DEFAULT_FONT_SIZE = 18;
    private static final List<String> FONT_LIST = Arrays.asList(new String [] {"Arial", "Calibri", "Cambria", "Courier New", "Comic Sans MS", "Dialog", "Georgia", "Helevetica", "Lucida Sans", "Monospaced", "Tahoma", "Times New Roman", "Verdana"});
    private static final String [] FONT_SIZES  = {"Font Size", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30"};
    private static final String [] TEXT_ALIGNMENTS = {"Text Align", "Left", "Center", "Right", "Justified"};
    private static final char BULLET_CHAR = '\u2022';
    private static final String BULLET_STR = new String(new char [] {BULLET_CHAR});
    private static final String BULLET_STR_WITH_SPACE = BULLET_STR + " ";
    static final int BULLET_LENGTH = BULLET_STR_WITH_SPACE.length();
    private static final String NUMBERS_ATTR = "NUMBERS";
    private static final String ELEM = AbstractDocument.ElementNameAttribute;
    private static final String COMP = StyleConstants.ComponentElementName;

    public void createAndShowGUI() {

        frame__ = new JFrame();
        setFrameTitleWithExtn("New file");
        editor__ = new JTextPane();
        JScrollPane editorScrollPane = new JScrollPane(editor__);

        editor__.setDocument(getNewDocument());
        editor__.addKeyListener(new BulletParaKeyListener(this));
        editor__.addKeyListener(new NumbersParaKeyListener(this));
        editor__.addCaretListener(new EditorCaretListener(this));

        undoMgr__ = new UndoManager();
        EditButtonActionListener editButtonActionListener =
                new EditButtonActionListener(this);

        JButton cutButton = new JButton(new CutAction());
        cutButton.setHideActionText(true);
        cutButton.setText("Cut");
        cutButton.addActionListener(editButtonActionListener);
        JButton copyButton = new JButton(new CopyAction());
        copyButton.setHideActionText(true);
        copyButton.setText("Copy");
        copyButton.addActionListener(editButtonActionListener);
        JButton pasteButton = new JButton(new PasteAction());
        pasteButton.setHideActionText(true);
        pasteButton.setText("Paste");
        pasteButton.addActionListener(editButtonActionListener);

        JButton boldButton = new JButton(new BoldAction());
        boldButton.setHideActionText(true);
        boldButton.setText("Bold");
        boldButton.addActionListener(editButtonActionListener);
        JButton italicButton = new JButton(new ItalicAction());
        italicButton.setHideActionText(true);
        italicButton.setText("Italic");
        italicButton.addActionListener(editButtonActionListener);
        JButton underlineButton = new JButton(new UnderlineAction());
        underlineButton.setHideActionText(true);
        underlineButton.setText("Underline");
        underlineButton.addActionListener(editButtonActionListener);

        JButton colorButton = new JButton("Set Color");
        colorButton.addActionListener(new ColorActionListener(this));

        textAlignComboBox__ = new JComboBox<String>(TEXT_ALIGNMENTS);
        textAlignComboBox__.setEditable(false);
        textAlignComboBox__.addItemListener(new TextAlignItemListener(this));

        fontSizeComboBox__ = new JComboBox<String>(FONT_SIZES);
        fontSizeComboBox__.setEditable(false);
        fontSizeComboBox__.addItemListener(new FontSizeItemListener(this));

        Vector<String> editorFonts = getEditorFonts();
        editorFonts.add(0, "Font Family");
        fontFamilyComboBox__ = new JComboBox<String>(editorFonts);
        fontFamilyComboBox__.setEditable(false);
        fontFamilyComboBox__.addItemListener(new FontFamilyItemListener(this));

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new UndoActionListener(this, UndoActionType.UNDO));
        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(new UndoActionListener(this, UndoActionType.REDO));

        JButton bulletInsertButton = new JButton("Bullets Insert");
        bulletInsertButton.addActionListener(
                new BulletActionListener(this, BulletActionType.INSERT));
        JButton bulletRemoveButton = new JButton("Bullets Remove");
        bulletRemoveButton.addActionListener(
                new BulletActionListener(this, BulletActionType.REMOVE));

        JButton numbersInsertButton = new JButton("Numbers Insert");
        numbersInsertButton.addActionListener(
                new NumbersActionListener(this, NumbersActionType.INSERT));
        JButton numbersRemoveButton = new JButton("Numbers Remove");
        numbersRemoveButton.addActionListener(
                new NumbersActionListener(this, NumbersActionType.REMOVE));

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(cutButton);
        panel1.add(copyButton);
        panel1.add(pasteButton);
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(boldButton);
        panel1.add(italicButton);
        panel1.add(underlineButton);
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(colorButton);
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(textAlignComboBox__);
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(fontSizeComboBox__);
        panel1.add(new JSeparator(SwingConstants.VERTICAL));
        panel1.add(fontFamilyComboBox__);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel2.add(new JSeparator(SwingConstants.VERTICAL));
        panel2.add(bulletInsertButton);
        panel2.add(bulletRemoveButton);
        panel2.add(new JSeparator(SwingConstants.VERTICAL));
        panel2.add(numbersInsertButton);
        panel2.add(numbersRemoveButton);
        panel2.add(new JSeparator(SwingConstants.VERTICAL));
        panel2.add(undoButton);
        panel2.add(redoButton);

        JPanel toolBarPanel = new JPanel();
        toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.PAGE_AXIS));
        toolBarPanel.add(panel1);
        toolBarPanel.add(panel2);

        frame__.add(toolBarPanel, BorderLayout.NORTH);
        frame__.add(editorScrollPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newItem	= new JMenuItem("New");
        newItem.setMnemonic(KeyEvent.VK_N);
        newItem.addActionListener(new NewFileListener(this));
        JMenuItem openItem	= new JMenuItem("Open...");
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.addActionListener(new OpenFileListener(this));
        JMenuItem saveItem	= new JMenuItem("Save (...)");
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.addActionListener(new SaveFileListener(this));
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });

        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(openItem);
        fileMenu.add(saveItem);

        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        frame__.setJMenuBar(menuBar);

        frame__.setSize(900, 500);
        frame__.setLocation(150, 80);
        frame__.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame__.setVisible(true);

        editor__.requestFocusInWindow();
    }

    void setFrameTitleWithExtn(String titleExtn) {

        frame__.setTitle(MAIN_TITLE + titleExtn);
    }

    StyledDocument getNewDocument() {

        StyledDocument doc = new DefaultStyledDocument();
        doc.addUndoableEditListener(new UndoEditListener(this));
        return doc;
    }

    StyledDocument getEditorDocument() {

        StyledDocument doc = (DefaultStyledDocument) editor__.getDocument();
        return doc;
    }

    /*
     * Returns a collection of Font names that are available from the
     * system fonts and are matched with the desired font list (FONT_LIST).
     */
    private Vector<String> getEditorFonts() {

        String [] availableFonts =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Vector<String> returnList = new Vector<>();

        for (String font : availableFonts) {

            if (FONT_LIST.contains(font)) {

                returnList.add(font);
            }
        }

        return returnList;
    }

    boolean isBulletedPara(int paraEleStart) {

        if (getParaFirstCharacter(paraEleStart) == BULLET_CHAR) {

            return true;
        }

        return false;
    }

    private char getParaFirstCharacter(int paraEleStart) {

        String firstChar = "";

        try {
            firstChar = editor__.getText(paraEleStart, 1);
        }
        catch (BadLocationException ex) {

            throw new RuntimeException(ex);
        }

        return firstChar.charAt(0);
    }

    boolean isNumberedPara(int paraEleStart) {

        AttributeSet attrSet = getParaStartAttributes(paraEleStart);
        Integer paraNum = (Integer) attrSet.getAttribute(NUMBERS_ATTR);

        if ((paraNum == null) || (! isFirstCharNumber(paraEleStart))) {

            return false;
        }

        return true;
    }

    private boolean isFirstCharNumber(int paraEleStart) {

        if (Character.isDigit(getParaFirstCharacter(paraEleStart))) {

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
    void insertBullet(int insertPos, int attributesPos) {

        try {
            getEditorDocument().insertString(insertPos,
                    BULLET_STR_WITH_SPACE,
                    getParaStartAttributes(attributesPos));
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }

    private AttributeSet getParaStartAttributes(int pos) {

        StyledDocument doc = (DefaultStyledDocument) editor__.getDocument();
        Element	charEle = doc.getCharacterElement(pos);
        return charEle.getAttributes();
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
    void removeBullet(int removePos, int length) {

        try {
            getEditorDocument().remove(removePos, length);
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }

    String getPrevParaText(int prevParaEleStart, int prevParaEleEnd) {

        String prevParaText = "";

        try {
            prevParaText = getEditorDocument().getText(prevParaEleStart,
                    (prevParaEleEnd -  prevParaEleStart));
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }

        return prevParaText;
    }

    /*
     * Left arrow key press routine within a bulleted and numbered paras.
     * Moves the cursor when caret is at position startPosPlusBullet__ or at
     * startPosPlusNum__ for bullets or numbers respectively.
     * Also see EditorCaretListener.
     *
     * The parameter startTextPos indicates if startPosPlusBullet__ or
     * startPosPlusNum__. pos is the present caret postion.
     */
    void doLeftArrowKeyRoutine(int pos, boolean startTextPos) {

        if (! startTextPos) {

            return;
        }

        // Check if this is start of document
        Element paraEle =
                getEditorDocument().getParagraphElement(editor__.getCaretPosition());
        int newPos = (paraEle.getStartOffset() == 0) ? 0 : pos;

        // Position the caret in an EDT, otherwise the caret is
        // positioned at one less position than intended.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                editor__.setCaretPosition(newPos);
            }
        });
    }

    /*
     * Returns the numbered para's number length. This length includes
     * the number + dot + space. For example, the text "12. A Numbered para..."
     * has the number length of 4.
     */
    int getNumberLength(int paraEleStart) {

        Integer num = getParaNumber(paraEleStart);
        int len = num.toString().length() + 2; // 2 = dot + space after number
        return len;
    }

    Integer getParaNumber(int paraEleStart) {

        AttributeSet attrSet = getParaStartAttributes(paraEleStart);
        Integer paraNum = (Integer) attrSet.getAttribute(NUMBERS_ATTR);
        return paraNum;
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
    void insertNumber(int insertPos, int attributesPos, Integer num) {

        try {
            getEditorDocument().insertString(insertPos,
                    getNumberString(num),
                    getNumbersAttributes(attributesPos, num));
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }

    String getNumberString(Integer nextNumber) {

        return new String(nextNumber.toString() + "." + " ");
    }

    AttributeSet getNumbersAttributes(int paraEleStart, Integer number) {

        AttributeSet attrs1 = getParaStartAttributes(paraEleStart);
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
    void removeNumber(int removePos, int length) {

        try {
            getEditorDocument().remove(removePos, length);
        }
        catch(BadLocationException ex) {

            throw new RuntimeException(ex);
        }
    }
}