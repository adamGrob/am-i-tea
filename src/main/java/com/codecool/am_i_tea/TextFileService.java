package com.codecool.am_i_tea;

import com.codecool.paintFx.service.PaintService;
import javafx.scene.web.HTMLEditor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                return PaintService.createNewImageFile();
            } else {
                System.out.println("File already exists. Choose a unique name!");
                return false;
            }
        } catch (IOException ex){
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public List<String> getAllFilesOfProject(String projectName) {
        String homeFolder = System.getProperty("user.home");
        String projectPath = homeFolder + File.separator + "AmITea" + File.separator + projectName;
        File file = new File(projectPath);

        String[] files = file.list((current, name) -> name.endsWith(".html"));
        if (files != null) {
            System.out.println("Found the list of all files in the project!");
            return new ArrayList<>(Arrays.asList(files));
        } else {
            System.out.println("Couldn't find the list of files in the project!");
            return null;
        }
    }

    public void saveTextFile(String projectPath, String fileName, HTMLEditor editor) {

        File file = new File(projectPath + File.separator + fileName + ".html");

        String textToSave = editor.getHtmlText();

        if (file.exists()) {
            saveFile(textToSave, file);
            PaintService.saveImage();
            System.out.println("File saved successfully!");
        } else {
            System.out.println("The file doesn't exist!");
        }
    }

    public void openTextFile(String fileName, String projectPath, HTMLEditor editor) {

        File file = new File(projectPath + File.separator + fileName + ".html");

        String content = "";
        if (file.exists()) {
            content = openFile(file);
            fileDAO.setCurrentFile(new TextFile(fileName));
            PaintService.loadImage();
            System.out.println("File opened successfully!");
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
