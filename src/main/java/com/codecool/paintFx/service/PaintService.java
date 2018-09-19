package com.codecool.paintFx.service;

import com.codecool.am_i_tea.AmITea;
import com.codecool.am_i_tea.ProjectDAO;
import com.codecool.am_i_tea.TextFileDAO;
import com.codecool.paintFx.model.ShapeList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class PaintService {

    private static ProjectDAO projectDAO;
    private static TextFileDAO fileDAO;

    public static void setProjectDAO(ProjectDAO projectDAOStuff) {
        projectDAO = projectDAOStuff;
    }

    public static void setfileDAO(TextFileDAO fileDAOStuff) {
        fileDAO = fileDAOStuff;
    }

    public static boolean createNewImageFile() {
        File file = new File(projectDAO.getCurrentProject().getPath() +
                File.separator + fileDAO.getCurrentFile().getName() + "_image.txt");
        try {
            if (file.createNewFile()) {
                System.out.println("Image file created successfully!");
                return true;
            } else {
                System.out.println("Image file already exists. Choose a unique name!");
                return false;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public static void saveImage() {

        String path = projectDAO.getCurrentProject().getPath() + File.separator +
                fileDAO.getCurrentFile().getName() + "_image.txt";
        File file = new File(path);

        String jsonImage = new Gson().toJson(ShapeList.getInstance().getShapeList());

        if (file.exists()) {
            saveImageFile(jsonImage, file);
            System.out.println("Image file saved successfully!");
        } else {
            System.out.println("The image file doesn't exist!");
        }
    }

    private static void saveImageFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.write(content);
        } catch (IOException ex) {
            Logger.getLogger(AmITea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
