package com.codecool.am_i_tea;

import com.codecool.am_i_tea.controller.EditorController;
import com.codecool.am_i_tea.controller.EditorMenuController;
import com.codecool.am_i_tea.dao.ProjectDAO;
import com.codecool.am_i_tea.dao.TextFileDAO;
import com.codecool.am_i_tea.service.LoggerService;
import com.codecool.am_i_tea.service.ProjectService;
import com.codecool.am_i_tea.service.PropertyUtil;
import com.codecool.am_i_tea.service.TextFileService;
import com.codecool.paintFx.controller.PaintController;
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
    private PaintService paintService;
    private EditorMenuController editorMenuController;
    private EditorController editorController;
    private PaintController paintController;
    private JavaApplication javaApp;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.initialize();

        logger = new LoggerService(applicationProperties);
        logger.initializeLogger();
        propertyUtil = new PropertyUtil(new Properties(), logger, applicationProperties);
        propertyUtil.initializeProperties();

        projectDAO = new ProjectDAO();
        fileDAO = new TextFileDAO();

        paintService = new PaintService(projectDAO, fileDAO, logger);
        textFileService = new TextFileService(fileDAO, propertyUtil, logger, paintService);
        projectService = new ProjectService(projectDAO, propertyUtil, logger);

        HTMLEditor editor = new HTMLEditor();
        javaApp = new JavaApplication(fileDAO, textFileService, projectDAO, editor);

        editorMenuController = new EditorMenuController(logger, propertyUtil, projectService,
                textFileService, projectDAO, fileDAO, primaryStage, editor);
        editorMenuController.initializeMenuBar();

        editorController = new EditorController(editor, javaApp);
        editorController.addButtonsToEditorToolbar();
        editorController.setJavaApplicationConnection();

        primaryStage.setTitle("Am-I-Tea text editor");
        editor.setVisible(false);

        logger.log("AmITea application started!");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("paint.fxml"));
        try {
            drawScene = new Scene(fxmlLoader.load());
            paintController = fxmlLoader.getController();
        } catch (IOException e) {
            logger.log(e.getMessage());
            e.printStackTrace();
        }

        graphicsContext = paintController.getCanvas().getGraphicsContext2D();
        textFileService.setGraphicsContext(graphicsContext);

        wrapper = new StackPane();
        editorController.setWrapper(wrapper);
        editorController.setPaintController(paintController);
        editorMenuController.setPaintController(paintController);
        paintService.setPaintController(paintController);

        drawScene.getRoot().setStyle("-fx-background-color: transparent ;");

        VBox editorVbox = new VBox();
        Scene editorScene = new Scene(editorVbox, 640, 480);
        ((VBox) editorScene.getRoot()).getChildren().addAll(editor);
        wrapper.getChildren().add(editorScene.getRoot());
        wrapper.getChildren().add(drawScene.getRoot());
        wrapper.getChildren().get(1).setMouseTransparent(true);

        VBox wrapperVbox = new VBox();
        Scene wrapperScene = new Scene(wrapperVbox);
        ((VBox) wrapperScene.getRoot()).getChildren().addAll(editorMenuController.getMenuBar(), wrapper);

        primaryStage.setScene(wrapperScene);
        primaryStage.show();
    }
}
