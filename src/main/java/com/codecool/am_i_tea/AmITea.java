package com.codecool.am_i_tea;

import com.codecool.am_i_tea.controller.EditorController;
import com.codecool.am_i_tea.controller.EditorMenuController;
import com.codecool.am_i_tea.dao.ProjectDAO;
import com.codecool.am_i_tea.dao.TextFileDAO;
import com.codecool.am_i_tea.service.LoggerService;
import com.codecool.am_i_tea.service.ProjectService;
import com.codecool.am_i_tea.service.PropertyUtil;
import com.codecool.am_i_tea.service.TextFileService;
import com.codecool.paintFx.service.PaintService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import netscape.javascript.JSObject;

import java.io.*;
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
    private EditorController editorController;

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
        WebView webView = (WebView) editor.lookup("WebView");

        editorMenuController = new EditorMenuController(logger, propertyUtil, projectService,
                textFileService, projectDAO, fileDAO, primaryStage, editor, graphicsContext);
        editorMenuController.initializeMenuBar();

        editorController = new EditorController(editor, wrapper, webView);
        editorController.addButtonsToEditorToolbar();

        logger.log("AmITea application started!");





        JavaApplication javaApp = new JavaApplication(fileDAO, textFileService, projectDAO, editor);

        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            final JSObject window = (JSObject) webView.getEngine().executeScript("window");
            window.setMember("app", javaApp);
        });

        primaryStage.setTitle("Am-I-Tea text editor");



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
        ((VBox) wrapperScene.getRoot()).getChildren().addAll(editorMenuController.getMenuBar(), wrapper);

        editorController.showDrawSceneToolBars(false);

        graphicsContext = ((Canvas) drawScene.getRoot().getChildrenUnmodifiable().get(1)).getGraphicsContext2D();
        textFileService.setGraphicsContext(graphicsContext);

        primaryStage.setScene(wrapperScene);
        primaryStage.show();


    }
}
