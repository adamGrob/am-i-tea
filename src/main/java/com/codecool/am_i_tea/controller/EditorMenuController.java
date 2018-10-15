package com.codecool.am_i_tea.controller;

import com.codecool.am_i_tea.dao.ProjectDAO;
import com.codecool.am_i_tea.dao.TextFileDAO;
import com.codecool.am_i_tea.service.LoggerService;
import com.codecool.am_i_tea.service.ProjectService;
import com.codecool.am_i_tea.service.PropertyUtil;
import com.codecool.am_i_tea.service.TextFileService;
import com.codecool.paintFx.controller.PaintController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.List;

public class EditorMenuController {

    private LoggerService logger;
    private PropertyUtil propertyUtil;
    private ProjectService projectService;
    private TextFileService fileService;
    private ProjectDAO projectDAO;
    private TextFileDAO fileDAO;
    private PaintController paintController;

    private Stage stage;
    private HTMLEditor editor;

    private MenuBar menuBar;

    public EditorMenuController(LoggerService logger, PropertyUtil propertyUtil,
                                ProjectService projectService, TextFileService fileService,
                                ProjectDAO projectDAO, TextFileDAO fileDAO, Stage stage,
                                HTMLEditor editor) {
        this.logger = logger;
        this.propertyUtil = propertyUtil;
        this.projectService = projectService;
        this.fileService = fileService;
        this.projectDAO = projectDAO;
        this.fileDAO = fileDAO;
        this.stage = stage;
        this.editor = editor;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void setPaintController(PaintController paintController) {
        this.paintController = paintController;
    }

    public void initializeMenuBar() {
        final Menu fileMenu = new Menu("File");
        final Menu projectMenu = new Menu("Project");
        final Menu settingsMenu = new Menu("Settings");

        final MenuItem newFileMenuItem = new MenuItem("New");
        final MenuItem saveFileMenuItem = new MenuItem("Save");
        final MenuItem openFileMenuItem = new MenuItem("Open");

        final MenuItem newProjectMenuItem = new MenuItem("New");
        final MenuItem loadProjectMenuItem = new MenuItem("Open");
        final MenuItem closeProjectMenuItem = new MenuItem("Close");
        final MenuItem exitMenuItem = new MenuItem("Exit");

        final MenuItem locationSettingsMenuItem = new MenuItem("Location");

        locationSettingsMenuItem.setOnAction(actionEvent -> setLocationAction());
        settingsMenu.getItems().addAll(locationSettingsMenuItem);

        newProjectMenuItem.setOnAction(actionEvent -> newProjectAction(fileMenu));
        loadProjectMenuItem.setOnAction(actionEvent -> displayProjectList(fileMenu));
        closeProjectMenuItem.setOnAction(actionEvent -> closeProjectAction(fileMenu, saveFileMenuItem));
        exitMenuItem.setOnAction(actionEvent -> exitApplication());
        projectMenu.getItems().addAll(newProjectMenuItem,
                loadProjectMenuItem,
                closeProjectMenuItem,
                exitMenuItem);

        newFileMenuItem.setOnAction(actionEvent -> newFileAction(saveFileMenuItem));
        saveFileMenuItem.setOnAction(actionEvent -> fileService.saveTextFile(projectDAO.getCurrentProject().getPath(), fileDAO.getCurrentFile().getName(), editor));
        openFileMenuItem.setOnAction(actionEvent -> displayFileList(saveFileMenuItem));
        fileMenu.getItems().addAll(newFileMenuItem, saveFileMenuItem, openFileMenuItem);

        saveFileMenuItem.setDisable(true);
        fileMenu.setDisable(true);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(projectMenu, fileMenu, settingsMenu);
        menuBar.prefWidthProperty().bind(stage.widthProperty());
    }

    private void displayFileList(MenuItem saveFileMenuItem) {
        Stage tempWindow = new Stage();

        List<String> files = fileService.getAllFilesOfProject(projectDAO.getCurrentProject().getName());
        ListView<String> fileList = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList(files);
        fileList.setItems(items);
        fileList.setOnMouseClicked(event -> openFileIfDoubleClicked(saveFileMenuItem, fileList, tempWindow, event));

        StackPane temporaryWindow = new StackPane();
        temporaryWindow.getChildren().addAll(fileList);
        Scene tempScene = new Scene(temporaryWindow, 200, 320);
        tempWindow.setTitle(projectDAO.getCurrentProject().getName());
        tempWindow.setScene(tempScene);
        tempWindow.setX(stage.getX() + 12);
        tempWindow.setY(stage.getY() + 28);
        tempWindow.show();
    }

    private void openFileIfDoubleClicked(MenuItem saveFileMenuItem, ListView<String> fileList, Stage tempWindow, MouseEvent event) {
        if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
            String fullFileName = fileList.getSelectionModel().getSelectedItem();
            String fileName = fullFileName.split("\\.")[0];
            fileService.openTextFile(fileName, projectDAO.getCurrentProject().getPath(), editor);
            saveFileMenuItem.setDisable(false);
            editor.setVisible(true);
            tempWindow.close();
        }
    }

    private void newFileAction(MenuItem saveFileMenuItem) {
        String fileName = JOptionPane.showInputDialog("File Name");
        if (fileService.createNewTextFile(projectDAO.getCurrentProject().getPath(), fileName, editor)) {
            saveFileMenuItem.setDisable(false);
            editor.setVisible(true);
            editor.setHtmlText("");
        } else {
            // todo show error message
        }
    }

    private void exitApplication() {
        logger.log("AmITea application closed!");
        Platform.exit();
    }

    private void closeProjectAction(Menu fileMenu, MenuItem saveFileMenuItem) {
        // todo save current file (files?) before closing them
        logger.log("Project closed!");

        paintController.clearShapeList();
        paintController.clearCanvas();

        fileDAO.setCurrentFile(null);
        projectDAO.setCurrentProject(null);
        fileMenu.setDisable(true);
        saveFileMenuItem.setDisable(true);
        editor.setVisible(false);
        editor.setHtmlText("");
    }

    private void displayProjectList(Menu fileMenu) {
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

        tempWindow.setX(stage.getX() + 12);
        tempWindow.setY(stage.getY() + 28);

        projectList.setOnMouseClicked(event -> openProjectIfDoubleClicked(fileMenu, projectList, tempWindow, event));

        tempWindow.show();
    }

    private void openProjectIfDoubleClicked(Menu fileMenu, ListView<String> projectList, Stage tempWindow, MouseEvent event) {
        if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
            String projectName = projectList.getSelectionModel().getSelectedItem();
            projectService.loadProject(projectName);
            fileMenu.setDisable(false);
            editor.setVisible(false);
            editor.setHtmlText("");
            tempWindow.close();
        }
    }

    private void newProjectAction(Menu fileMenu) {
        String projectName = JOptionPane.showInputDialog("Project Name");
        if (projectService.createProject(projectName)) {
            fileMenu.setDisable(false);
            editor.setVisible(false);
            editor.setHtmlText("");
        } else {
            // todo display error message on UI
        }
    }

    private void setLocationAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("AmITea projects location");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String location = fileChooser.getSelectedFile().getPath();
            propertyUtil.setLocationProperty(location);
            logger.log("New projects folder selected successfully!");
        } else {
            logger.log("No project folder was selected!");
        }
    }
}
