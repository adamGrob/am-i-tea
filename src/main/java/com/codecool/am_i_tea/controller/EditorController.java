package com.codecool.am_i_tea.controller;

import com.codecool.am_i_tea.JavaApplication;
import com.codecool.paintFx.controller.PaintController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import javax.swing.*;

public class EditorController {

    private HTMLEditor editor;
    private StackPane wrapper;
    private JavaApplication javaApp;
    private PaintController paintController;

    private WebView webView;

    public EditorController(HTMLEditor editor, JavaApplication javaApp) {
        this.editor = editor;
        this.javaApp = javaApp;
        this.webView = (WebView) editor.lookup("WebView");
    }

    public void setWrapper(StackPane wrapper) {
        this.wrapper = wrapper;
    }

    public void setPaintController(PaintController paintController) {
        this.paintController = paintController;
    }

    public void setJavaApplicationConnection() {
        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            final JSObject window = (JSObject) webView.getEngine().executeScript("window");
            window.setMember("app", javaApp);
        });
    }

    public void addButtonsToEditorToolbar() {
        Node node = editor.lookup(".top-toolbar");
        if (node instanceof ToolBar) {
            ToolBar bar = (ToolBar) node;

            Button drawButton = new Button("Drawing mode");
            drawButton.setTooltip(new Tooltip("Draw"));

            Button linkButton = new Button("Link");
            ImageView linkImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/hyperlink.png")));
            linkButton.setMinSize(26.0, 22.0);
            linkButton.setMaxSize(26.0, 22.0);
            linkImageView.setFitHeight(16);
            linkImageView.setFitWidth(16);
            linkButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            linkButton.setGraphic(linkImageView);
            linkButton.setTooltip(new Tooltip("Link another file"));

            linkButton.setOnAction(actionEvent -> changeTextToLink());
            drawButton.setOnAction(actionEvent -> switchToDrawMode());

            bar.getItems().add(drawButton);
            bar.getItems().add(new Separator());
            bar.getItems().add(linkButton);
        }
    }

    private void showDrawSceneToolBars() {
        Node topToolBar = paintController.getTopToolbar();
        Node bottomToolBar = paintController.getBottomToolbar();
        topToolBar.setVisible(true);
        bottomToolBar.setVisible(true);
    }

    private void changeTextToLink() {
        String targetFileName = JOptionPane.showInputDialog("Enter file name");

        String selected = (String) webView.getEngine().executeScript("window.getSelection().toString();");
        selected = formatSelection(selected);
        String hyperlinkHtml = "<span style=\"color:blue; text-decoration:underline; \" onClick=\"" +
                "window.app.openLinkedFile(\\'" + targetFileName + "\\')\"" + ">" + selected + "</span>";
        webView.getEngine().executeScript(getInsertHtmlAtCursorJS(hyperlinkHtml));
    }

    private void switchToDrawMode() {
        wrapper.getChildren().get(1).setMouseTransparent(false);
        showDrawSceneToolBars();
    }

    private String formatSelection(String selected) {
        selected = selected.replaceAll("\\n\\n", "<br>");
        return selected.replaceAll("\\\\", "\\\\\\\\");
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
