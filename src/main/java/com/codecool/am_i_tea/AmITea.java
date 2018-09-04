package com.codecool.am_i_tea;

import com.codecool.am_i_tea.text_editor.MyEditor;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class AmITea {

    public static void main(String[] args) throws Exception {

        UIManager.put("TextPane.font",
                new Font(MyEditor.DEFAULT_FONT_FAMILY, Font.PLAIN, MyEditor.DEFAULT_FONT_SIZE));
        UIManager.setLookAndFeel(new NimbusLookAndFeel());

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                new MyEditor().createAndShowGUI();
            }
        });
    }

}
