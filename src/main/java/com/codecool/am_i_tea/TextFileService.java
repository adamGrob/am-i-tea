package com.codecool.am_i_tea;

import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextFileService {

    private TextFileDAO fileDAO;

    public TextFileService(TextFileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    public boolean createNewTextFile(String projectPath, String fileName){
        File file = new File(projectPath + File.separator + fileName + ".html");
        try {
            if (file.createNewFile()){
                System.out.println("File created successfully!");
                fileDAO.setCurrentFile(new TextFile(fileName));
                return true;
            } else {
                System.out.println("File already exists. Choose a unique name!");
                return false;
            }
        } catch (IOException ex){
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public void saveTextFile(String projectPath, String fileName, HTMLEditor editor) {

        File file = new File(projectPath + File.separator + fileName + ".html");

        String textToSave = editor.getHtmlText();

        if (file.exists()) {
            saveFile(textToSave, file);
            System.out.println("File saved successfully!");
        } else {
            System.out.println("The file doesn't exist!");
        }
    }

    public void openTextFile(Stage primaryStage, HTMLEditor editor) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(primaryStage);

        String content = "";
        if (file != null) {
            content = openFile(file);
        }
        editor.setHtmlText(content);
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
}
