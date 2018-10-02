package com.codecool.am_i_tea;

import com.codecool.am_i_tea.controller.EditorMenuController;
import com.codecool.am_i_tea.dao.ProjectDAO;
import com.codecool.am_i_tea.dao.TextFileDAO;
import com.codecool.am_i_tea.service.LoggerService;
import com.codecool.am_i_tea.service.ProjectService;
import com.codecool.am_i_tea.service.PropertyUtil;
import com.codecool.am_i_tea.service.TextFileService;
import com.codecool.paintFx.model.ShapeList;
import com.codecool.paintFx.service.PaintService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import java.util.Properties;


import static javafx.application.Application.launch;

public class AmITea extends Application {

    private StackPane wrapper;
    private Scene drawScene;
    private TextFileService textFileService;
    private ProjectService projectService;
    private PropertyUtil propertyUtil;
    private ProjectDAO projectDAO;
    private TextFileDAO fileDAO;
    private GraphicsContext graphicsContext;
    private LoggerService logger;
    private EditorMenuController editorMenuController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.initialize();

        projectDAO = new ProjectDAO();
        fileDAO = new TextFileDAO();

        logger = new LoggerService(applicationProperties);
        logger.initializeLogger();
        propertyUtil = new PropertyUtil(new Properties(), logger, applicationProperties);
        propertyUtil.initializeProperties();

        textFileService = new TextFileService(fileDAO, propertyUtil, logger);
        projectService = new ProjectService(projectDAO, propertyUtil, logger);
        PaintService.setLogger(logger);
        PaintService.setfileDAO(fileDAO);
        PaintService.setProjectDAO(projectDAO);

        //todo: this is not in the right order
        HTMLEditor editor = new HTMLEditor();
        editor.setVisible(false);

        editorMenuController = new EditorMenuController(logger, propertyUtil, projectService,
                textFileService, projectDAO, fileDAO, primaryStage, editor, graphicsContext);

        logger.log("AmITea application started!");






        WebView webView = (WebView) editor.lookup("WebView");
        JavaApplication javaApp = new JavaApplication(fileDAO, textFileService, projectDAO, editor);

        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            final JSObject window = (JSObject) webView.getEngine().executeScript("window");
            window.setMember("app", javaApp);
        });

        primaryStage.setTitle("Am-I-Tea text editor");


        editorMenuController.initializeMenuBar();
        MenuBar menuBar = editorMenuController.getMenuBar();

        Node node = editor.lookup(".top-toolbar");
        if (node instanceof ToolBar) {
            ToolBar bar = (ToolBar) node;

            Button drawButton = new Button("Drawing mode");
            drawButton.setTooltip(new Tooltip("Draw"));

            Button linkButton = new Button("Hyperlink");
            ImageView linkImageView = new ImageView(new Image(getClass().getResourceAsStream("/images/hyperlink.png")));
            linkButton.setMinSize(26.0, 22.0);
            linkButton.setMaxSize(26.0, 22.0);
            linkImageView.setFitHeight(16);
            linkImageView.setFitWidth(16);
            linkButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            linkButton.setGraphic(linkImageView);
            linkButton.setTooltip(new Tooltip("Link another file"));

            linkButton.setOnAction(actionEvent -> {
                String targetFileName = JOptionPane.showInputDialog("Enter file name");

                String selected = (String) webView.getEngine().executeScript("window.getSelection().toString();");
                selected = formatSelection(selected);
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

            Separator separator = new Separator();

            bar.getItems().add(drawButton);
            bar.getItems().add(separator);
            bar.getItems().add(linkButton);

        }
        try {
            drawScene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("paint.fxml")));
        } catch (IOException e) {
            logger.log(e.getMessage());
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

        graphicsContext = ((Canvas) drawScene.getRoot().getChildrenUnmodifiable().get(1)).getGraphicsContext2D();
        textFileService.setGraphicsContext(graphicsContext);

        primaryStage.setScene(wrapperScene);
        primaryStage.show();


    }

    private String formatSelection(String selected) {
        selected = selected.replaceAll("\\n\\n", "<br>");
        return selected.replaceAll("\\\\", "\\\\\\\\");
    }

    private void showDrawSceneToolBars(Boolean show) {
        Node myDrawNode = wrapper.getChildren().get(1);
        BorderPane myDrawScene = (BorderPane) myDrawNode;
        VBox myVbox = (VBox) myDrawScene.getChildren().get(0);
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
