package com.codecool.am_i_tea;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class AmITea extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Am-I-Tea text editor");

        HTMLEditor editor = new HTMLEditor();

        final Menu fileMenu = new Menu("File");

        final MenuItem saveFileMenuItem = new MenuItem("Save");
        final MenuItem openFileMenuItem = new MenuItem("Open");
        final MenuItem exitFileMenuItem = new MenuItem("Exit");

        exitFileMenuItem.setOnAction(actionEvent -> Platform.exit());

        fileMenu.getItems().addAll(saveFileMenuItem, openFileMenuItem, exitFileMenuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Scene root = new Scene(new VBox(), 640, 480);
        ((VBox) root.getRoot()).getChildren().addAll(menuBar, editor);

        primaryStage.setScene(root);
        primaryStage.show();
    }
}
