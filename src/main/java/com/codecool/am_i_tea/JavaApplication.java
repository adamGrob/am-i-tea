package com.codecool.am_i_tea;

import com.codecool.am_i_tea.dao.ProjectDAO;
import com.codecool.am_i_tea.dao.TextFileDAO;
import com.codecool.am_i_tea.service.LoggerService;
import com.codecool.am_i_tea.service.TextFileService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaApplication {

    private LoggerService logger;
    private TextFileDAO fileDAO;
    private TextFileService fileService;
    private ProjectDAO projectDAO;
    private HTMLEditor editor;

    public JavaApplication(TextFileDAO fileDAO, TextFileService fileService, ProjectDAO projectDAO,
                           HTMLEditor editor, LoggerService logger) {
        this.fileDAO = fileDAO;
        this.fileService = fileService;
        this.projectDAO = projectDAO;
        this.editor = editor;
        this.logger = logger;
    }

    public void openLinkedFile(String fileName) {
        fileService.saveTextFile(projectDAO.getCurrentProject().getPath(),
                fileDAO.getCurrentFile().getName(),
                editor);
        fileService.openTextFile(fileName, projectDAO.getCurrentProject().getPath(), editor);


//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("paint.fxml"));
//        try {
//            drawScene = new Scene(fxmlLoader.load());
//            paintController = fxmlLoader.getController();
//        } catch (IOException e) {
//            logger.log(e.getMessage());
//            e.printStackTrace();
//        }

        Canvas readOnlyCanvas = new Canvas();

        // todo set canvas content

        BorderPane readOnlyBorderPane = new BorderPane();
        readOnlyBorderPane.getChildren().addAll(readOnlyCanvas);

        WebView readOnlyEditor = new WebView();

        //todo set webView content

        GridPane readOnlyGridPane = new GridPane();
        readOnlyGridPane.getChildren().addAll(readOnlyEditor);

        StackPane readOnlyWrapper = new StackPane();
        readOnlyWrapper.getChildren().addAll(readOnlyBorderPane, readOnlyGridPane);
        Scene readOnlyWindowScene = new Scene(readOnlyWrapper, 640, 480);

        Stage newWindow = new Stage();
        newWindow.setTitle(fileName);
        newWindow.setScene(readOnlyWindowScene);

        newWindow.setX(0.0);
        newWindow.setY(0.0);
        newWindow.show();


    }
}
