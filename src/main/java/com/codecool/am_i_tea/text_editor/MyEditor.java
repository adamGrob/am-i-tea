package com.codecool.am_i_tea.text_editor;

import com.codecool.am_i_tea.text_editor.editor_utility.BulletsUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.DocumentUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.NumbersUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.ParaUtility;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;

import java.awt.event.ActionListener;
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
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;

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

import javax.swing.undo.UndoManager;

import java.util.List;
import java.util.Vector;
import java.util.Arrays;

import java.io.File;


public class MyEditor {

    private DocumentUtility documentUtility;
    private ParaUtility paraUtility;
    private BulletsUtility bulletsUtility;
    private NumbersUtility numbersUtility;

    public JFrame frame__;
    public JTextPane editor__;
    JComboBox<String> fontSizeComboBox__;
    JComboBox<String> textAlignComboBox__;
    JComboBox<String> fontFamilyComboBox__;
    UndoManager undoMgr__;
    File file__;

    enum BulletActionType {INSERT, REMOVE}

    ;

    enum NumbersActionType {INSERT, REMOVE}

    ;

    enum UndoActionType {UNDO, REDO}

    ;

    // This flag checks true if the caret position within a bulleted para
    // is at the first text position after the bullet (bullet char + space).
    // Also see EditorCaretListener and BulletParaKeyListener.
    boolean startPosPlusBullet__;

    // This flag checks true if the caret position within a numbered para
    // is at the first text position after the number (number + dot + space).
    // Alse see EditorCaretListener and NumbersParaKeyListener.
    boolean startPosPlusNum__;

    public static final String MAIN_TITLE = "My Editor - ";
    public static final String DEFAULT_FONT_FAMILY = "SansSerif";
    public static final int DEFAULT_FONT_SIZE = 18;
    private static final List<String> FONT_LIST = Arrays.asList(new String[]{"Arial", "Calibri", "Cambria", "Courier New", "Comic Sans MS", "Dialog", "Georgia", "Helevetica", "Lucida Sans", "Monospaced", "Tahoma", "Times New Roman", "Verdana"});
    private static final String[] FONT_SIZES = {"Font Size", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30"};
    private static final String[] TEXT_ALIGNMENTS = {"Text Align", "Left", "Center", "Right", "Justified"};
    public static final char BULLET_CHAR = '\u2022';
    private static final String BULLET_STR = new String(new char[]{BULLET_CHAR});
    public static final String BULLET_STR_WITH_SPACE = BULLET_STR + " ";
    public static final int BULLET_LENGTH = BULLET_STR_WITH_SPACE.length();
    public static final String NUMBERS_ATTR = "NUMBERS";
    private static final String ELEM = AbstractDocument.ElementNameAttribute;
    private static final String COMP = StyleConstants.ComponentElementName;

    public void createAndShowGUI() {

        documentUtility = new DocumentUtility(this);
        paraUtility = new ParaUtility(this, documentUtility);
        bulletsUtility = new BulletsUtility(documentUtility, paraUtility);
        numbersUtility = new NumbersUtility(documentUtility, paraUtility);

        frame__ = new JFrame();
        documentUtility.setFrameTitleWithExtn("New file");
        editor__ = new JTextPane();
        JScrollPane editorScrollPane = new JScrollPane(editor__);

        editor__.setDocument(documentUtility.getNewDocument());
        editor__.addKeyListener(new BulletParaKeyListener(this,
                documentUtility,
                paraUtility,
                bulletsUtility));
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
                new BulletActionListener(this,
                        BulletActionType.INSERT,
                        documentUtility,
                        paraUtility,
                        bulletsUtility));
        JButton bulletRemoveButton = new JButton("Bullets Remove");
        bulletRemoveButton.addActionListener(
                new BulletActionListener(this,
                        BulletActionType.REMOVE,
                        documentUtility,
                        paraUtility,
                        bulletsUtility));

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

        JMenuItem newItem = new JMenuItem("New");
        newItem.setMnemonic(KeyEvent.VK_N);
        newItem.addActionListener(new NewFileListener(this));
        JMenuItem openItem = new JMenuItem("Open...");
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.addActionListener(new OpenFileListener(this));
        JMenuItem saveItem = new JMenuItem("Save (...)");
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

    /*
     * Returns a collection of Font names that are available from the
     * system fonts and are matched with the desired font list (FONT_LIST).
     */
    private Vector<String> getEditorFonts() {

        String[] availableFonts =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Vector<String> returnList = new Vector<>();

        for (String font : availableFonts) {

            if (FONT_LIST.contains(font)) {

                returnList.add(font);
            }
        }

        return returnList;
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

        if (!startTextPos) {

            return;
        }

        // Check if this is start of document
        Element paraEle =
                documentUtility.getEditorDocument().getParagraphElement(editor__.getCaretPosition());
        int newPos = (paraEle.getStartOffset() == 0) ? 0 : pos;

        // Position the caret in an EDT, otherwise the caret is
        // positioned at one less position than intended.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                editor__.setCaretPosition(newPos);
            }
        });
    }
}