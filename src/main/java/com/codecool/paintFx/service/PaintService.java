package com.codecool.paintFx.service;

import com.codecool.am_i_tea.ProjectDAO;
import com.codecool.am_i_tea.TextFileDAO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class PaintService {

    private static ProjectDAO projectDAO;
    private static TextFileDAO fileDAO;

    public static void setProjectDAO(ProjectDAO projectDAOStuff) {
        projectDAO = projectDAOStuff;
    }

    public static void setfileDAO(TextFileDAO fileDAOStuff) {
        fileDAO = fileDAOStuff;
    }

    public static void saveImage(Image snapshot){

        String path = projectDAO.getCurrentProject().getPath() + File.separator +
                fileDAO.getCurrentFile().getName() + "_img" +
                fileDAO.getCurrentFile().nextPicture() + ".png";
        File file = new File(path);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
            System.out.println("Image saved successfully!");
        } catch (IOException ex) {
            fileDAO.getCurrentFile().nextPictureRollback();
            System.out.println(ex.getMessage());
        }
    }
}
