package com.codecool.am_i_tea;

import com.codecool.paintFx.controller.PaintController;
import com.codecool.paintFx.service.PaintService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import netscape.javascript.JSObject;

import javax.swing.*;
import java.io.*;
import java.util.List;


import static javafx.application.Application.launch;

public class AmITea extends Application {

    private StackPane wrapper;
    private Scene drawScene;
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

        PaintService.setfileDAO(fileDAO);
        PaintService.setProjectDAO(projectDAO);

        HTMLEditor editor = new HTMLEditor();
        editor.setVisible(false);

        WebView webView = (WebView) editor.lookup("WebView");
        JavaApplication javaApp = new JavaApplication(fileDAO, textFileService, projectDAO, editor);

        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            final JSObject window = (JSObject) webView.getEngine().executeScript("window");
            window.setMember("app", javaApp);
        });

        final JSObject window = (JSObject) webView.getEngine().executeScript("window");

        primaryStage.setTitle("Am-I-Tea text editor");

        final Menu fileMenu = new Menu("File");
        final Menu projectMenu = new Menu("Project");

        final MenuItem newFileMenuItem = new MenuItem("New");
        final MenuItem saveFileMenuItem = new MenuItem("Save");
        final MenuItem openFileMenuItem = new MenuItem("Open");

        final MenuItem newProjectMenuItem = new MenuItem("New");
        final MenuItem loadProjectMenuItem = new MenuItem("Load");
        final MenuItem closeProjectMenuItem = new MenuItem("Close");
        final MenuItem exitMenuItem = new MenuItem("Exit");

        newProjectMenuItem.setOnAction(actionEvent -> {
            String projectName = JOptionPane.showInputDialog("Project Name");
            if (projectService.createProject(projectName)) {
                fileMenu.setDisable(false);
                editor.setVisible(false);
                editor.setHtmlText("");
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
                    fileMenu.setDisable(false);
                    editor.setVisible(false);
                    editor.setHtmlText("");
                    tempWindow.close();
                }
            });

            tempWindow.show();
        });

        closeProjectMenuItem.setOnAction(actionEvent -> {
            // todo save current file (files?) before closing them
            System.out.println("Project closed!");

            fileDAO.setCurrentFile(null);
            projectDAO.setCurrentProject(null);
            fileMenu.setDisable(true);
            saveFileMenuItem.setDisable(true);
            editor.setVisible(false);
            editor.setHtmlText("");
        });

        exitMenuItem.setOnAction(actionEvent -> Platform.exit());

        projectMenu.getItems().addAll(newProjectMenuItem,
                loadProjectMenuItem,
                closeProjectMenuItem,
                exitMenuItem);

        newFileMenuItem.setOnAction(actionEvent -> {
            String fileName = JOptionPane.showInputDialog("File Name");
            if (textFileService.createNewTextFile(projectDAO.getCurrentProject().getPath(), fileName)) {
                saveFileMenuItem.setDisable(false);
                editor.setVisible(true);
                editor.setHtmlText("");
            } else {
                // todo show error message
            }
        });

        saveFileMenuItem.setOnAction(actionEvent -> textFileService.saveTextFile(projectDAO.getCurrentProject().getPath(), fileDAO.getCurrentFile().getName(), editor));
        saveFileMenuItem.setDisable(true);

        openFileMenuItem.setOnAction(actionEvent -> {
            List<String> files = textFileService.getAllFilesOfProject(projectDAO.getCurrentProject().getName());

            ListView<String> fileList = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList(files);
            fileList.setItems(items);

            StackPane temporaryWindow = new StackPane();
            temporaryWindow.getChildren().addAll(fileList);
            Scene tempScene = new Scene(temporaryWindow, 200, 320);
            Stage tempWindow = new Stage();
            tempWindow.setTitle(projectDAO.getCurrentProject().getName());
            tempWindow.setScene(tempScene);
            tempWindow.setX(primaryStage.getX() + 12);
            tempWindow.setY(primaryStage.getY() + 28);
            fileList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                    String fullFileName = fileList.getSelectionModel().getSelectedItem();
                    String fileName = fullFileName.split("\\.")[0];
                    textFileService.openTextFile(fileName, projectDAO.getCurrentProject().getPath(), editor);
                    saveFileMenuItem.setDisable(false);
                    editor.setVisible(true);
                    tempWindow.close();
                }
            });
            tempWindow.show();
        });
        fileMenu.getItems().addAll(newFileMenuItem, saveFileMenuItem, openFileMenuItem);
        fileMenu.setDisable(true);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(projectMenu, fileMenu);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Node node = editor.lookup(".top-toolbar");
        if (node instanceof ToolBar) {
            ToolBar bar = (ToolBar) node;

            Button drawButton = new Button("Draw");
            ImageView drawImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/draw.png")));
            drawButton.setMinSize(26.0, 22.0);
            drawButton.setMaxSize(26.0, 22.0);
            drawImageView.setFitHeight(16);
            drawImageView.setFitWidth(16);
            drawButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            drawButton.setGraphic(drawImageView);
            drawButton.setMinSize(26.0, 22.0);
            drawButton.setMaxSize(26.0, 22.0);
            drawButton.setTooltip(new Tooltip("Draw"));

            Button linkButton = new Button("Hyperlink");
            ImageView linkImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/hyperlink.png")));
            linkButton.setMinSize(26.0, 22.0);
            linkButton.setMaxSize(26.0, 22.0);
            linkImageView.setFitHeight(16);
            linkImageView.setFitWidth(16);
            linkButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            linkButton.setGraphic(linkImageView);
            linkButton.setTooltip(new Tooltip("Hypermilnk"));

            linkButton.setOnAction(actionEvent -> {
                String targetFileName = JOptionPane.showInputDialog("Enter file name");

                String selected = (String) webView.getEngine().executeScript("window.getSelection().toString();");
                String hyperlinkHtml = "<span style=\"color:blue; text-decoration:underline; \" onClick=\"" +
                        "window.app.openLinkedFile(\\'" + targetFileName + "\\')\"" + ">" + selected + "</span>";
                webView.getEngine().executeScript(getInsertHtmlAtCursorJS(hyperlinkHtml));
            });

            drawButton.setOnAction(actionEvent -> {
                wrapper.getChildren().get(1).setMouseTransparent(false);
                Node topToolBar = editor.lookup(".top-toolbar");
                Node bottomToolBar = editor.lookup(".bottom-toolbar");
                topToolBar.setVisible(false);
                bottomToolBar.setVisible(false);
                showDrawSceneToolBars(true);
            });
            bar.getItems().addAll(linkButton, drawButton);
        }
        try {
            drawScene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("paint.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        drawScene.getRoot().setStyle("-fx-background-color: transparent ;");


        VBox editorVbox = new VBox();
        Scene editorScene = new Scene(editorVbox, 640, 480);
        ((VBox) editorScene.getRoot()).getChildren().addAll(editor);
        wrapper = new StackPane();
        wrapper.getChildren().add(editorScene.getRoot());
        wrapper.getChildren().add(drawScene.getRoot());
        wrapper.getChildren().get(1).setMouseTransparent(true);

        VBox wrapperVbox = new VBox();
        Scene wrapperScene = new Scene(wrapperVbox);
        ((VBox) wrapperScene.getRoot()).getChildren().addAll(menuBar, wrapper);

        showDrawSceneToolBars(false);


        primaryStage.setScene(wrapperScene);
        primaryStage.show();


    }

    private void showDrawSceneToolBars(Boolean show) {
        Node myDrawNode = wrapper.getChildren().get(1);
        BorderPane myDrawScene = (BorderPane) myDrawNode;
        VBox myVbox = (VBox)myDrawScene.getChildren().get(0);
        Node topToolBar = myVbox.getChildren().get(0);
        Node bottomToolBar = myVbox.getChildren().get(1);
        topToolBar.setVisible(show);
        bottomToolBar.setVisible(show);

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
