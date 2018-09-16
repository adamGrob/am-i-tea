package com.codecool.am_i_tea;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Application.launch;

public class AmITea extends Application {

    private TextFileService textFileService;
    private ProjectService projectService;
    private ProjectDAO projectDAO;
    private TextFileDAO fileDAO;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        projectDAO = new ProjectDAO();
        fileDAO = new TextFileDAO();
        textFileService = new TextFileService(fileDAO);
        projectService = new ProjectService(projectDAO);

        primaryStage.setTitle("Am-I-Tea text editor");

        HTMLEditor editor = new HTMLEditor();
        editor.setVisible(false);

        final Menu fileMenu = new Menu("File");
        final Menu projectMenu = new Menu("Project");

        final MenuItem newFileMenuItem = new MenuItem("New");
        final MenuItem saveFileMenuItem = new MenuItem("Save");
        final MenuItem openFileMenuItem = new MenuItem("Open");
        final MenuItem exitFileMenuItem = new MenuItem("Exit");

        final MenuItem newProjectMenuItem = new MenuItem("New");
        final MenuItem loadProjectMenuItem = new MenuItem("Load");
        final MenuItem closeProjectMenuItem = new MenuItem("Close");

        newProjectMenuItem.setOnAction(actionEvent -> {
            String projectName = JOptionPane.showInputDialog("Project Name");
            if (projectService.createProject(projectName)) {
                fileMenu.setDisable(false);
                //todo show editor window and other menus only then
            } else {
                // todo show error message
            }
        });

        loadProjectMenuItem.setOnAction(actionEvent -> {
            List<String> projects = projectService.getAllProjects();

            ListView<String> projectList = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList(projects);
            projectList.setItems(items);

            StackPane temporaryWindow = new StackPane();
            temporaryWindow.getChildren().addAll(projectList);
            Scene tempScene = new Scene(temporaryWindow, 200, 320);
            Stage tempWindow = new Stage();
            tempWindow.setTitle("Projects");
            tempWindow.setScene(tempScene);

            tempWindow.setX(primaryStage.getX() + 12);
            tempWindow.setY(primaryStage.getY() + 28);

            projectList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                    String projectName = projectList.getSelectionModel().getSelectedItem();
                    projectService.loadProject(projectName);
                    tempWindow.close();
                }
            });

            tempWindow.show();
        });

        projectMenu.getItems().addAll(newProjectMenuItem, loadProjectMenuItem);

        newFileMenuItem.setOnAction(actionEvent -> {
            String fileName = JOptionPane.showInputDialog("File Name");
            if (textFileService.createNewTextFile(projectDAO.getCurrentProject().getPath(), fileName)) {
                saveFileMenuItem.setDisable(false);
                editor.setVisible(true);
            } else {
                // todo show error message
            }

        });

        saveFileMenuItem.setOnAction(actionEvent -> textFileService.saveTextFile(projectDAO.getCurrentProject().getPath(), fileDAO.getCurrentFile().getName(), editor));
        saveFileMenuItem.setDisable(true);

        openFileMenuItem.setOnAction(actionEvent -> textFileService.openTextFile(primaryStage, editor));

        exitFileMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(newFileMenuItem, saveFileMenuItem, openFileMenuItem, exitFileMenuItem);
        fileMenu.setDisable(true);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(projectMenu, fileMenu);
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
