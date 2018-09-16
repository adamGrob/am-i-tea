package com.codecool.am_i_tea;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Application.launch;

public class AmITea extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Am-I-Tea text editor");

        HTMLEditor editor = new HTMLEditor();

        final Menu fileMenu = new Menu("File");

        final MenuItem saveFileMenuItem = new MenuItem("Save");
        final MenuItem openFileMenuItem = new MenuItem("Open");
        final MenuItem exitFileMenuItem = new MenuItem("Exit");

        saveFileMenuItem.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(primaryStage);

            String textToSave = editor.getHtmlText();

            if (file != null) {
                saveFile(textToSave, file);
            }
        });

        openFileMenuItem.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showOpenDialog(primaryStage);

            String content = "";
            if (file != null) {
                content = openFile(file);
            }
            editor.setHtmlText(content);
        });

        exitFileMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(saveFileMenuItem, openFileMenuItem, exitFileMenuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Node node = editor.lookup(".top-toolbar");
        if (node instanceof ToolBar) {
            ToolBar bar = (ToolBar) node;
            Button button = new Button("Hyperlink");

            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/hyperlink.png")));
            button.setMinSize(26.0, 22.0);
            button.setMaxSize(26.0, 22.0);
            imageView.setFitHeight(16);
            imageView.setFitWidth(16);
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setGraphic(imageView);
            button.setTooltip(new Tooltip("Hypermilnk"));

            button.setOnAction(actionEvent -> {
                String url = JOptionPane.showInputDialog("Enter URL");
                WebView webView = (WebView) editor.lookup("WebView");
                String selected = (String) webView.getEngine().executeScript("window.getSelection().toString();");
                String hyperlinkHtml = "<a href=\"" + url.trim() + "\" title=\"" + selected + "\" target=\"_blank\">" + selected + "</a>";
                webView.getEngine().executeScript(getInsertHtmlAtCursorJS(hyperlinkHtml));
            });

            bar.getItems().add(button);
        }


        Scene root = new Scene(new VBox(), 640, 480);
        ((VBox) root.getRoot()).getChildren().addAll(menuBar, editor);

        primaryStage.setScene(root);
        primaryStage.show();
    }

    private void saveFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.write(content);
        } catch (IOException ex) {
            Logger.getLogger(AmITea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String openFile(File file) {

        String content = "";
        try (FileReader fileReader = new FileReader(file)) {

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder contentBuilder = new StringBuilder();

            String currentLine = bufferedReader.readLine();
            while (currentLine != null) {
                contentBuilder.append(currentLine);
                currentLine = bufferedReader.readLine();
            }
            bufferedReader.close();
            content = contentBuilder.toString();
        } catch (IOException ex) {
            Logger.getLogger(AmITea.class.getName()).log(Level.SEVERE, null, ex);
        }

        return content;
    }

    private String getInsertHtmlAtCursorJS(String html) {
        return "insertHtmlAtCursor('" + html + "');"
                + "function insertHtmlAtCursor(html) {\n"
                + " var range, node;\n"
                + " if (window.getSelection && window.getSelection().getRangeAt) {\n"
                + " window.getSelection().deleteFromDocument();\n"
                + " range = window.getSelection().getRangeAt(0);\n"
                + " node = range.createContextualFragment(html);\n"
                + " range.insertNode(node);\n"
                + " } else if (document.selection && document.selection.createRange) {\n"
                + " document.selection.createRange().pasteHTML(html);\n"
                + " document.selection.clear();"
                + " }\n"
                + "}";
    }
}
