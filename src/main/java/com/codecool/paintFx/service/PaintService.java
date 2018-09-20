package com.codecool.paintFx.service;

import com.codecool.am_i_tea.AmITea;
import com.codecool.am_i_tea.ProjectDAO;
import com.codecool.am_i_tea.TextFileDAO;
import com.codecool.paintFx.model.MyShape;
import com.codecool.paintFx.model.ShapeList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.codecool.paintFx.model.StoredLine;
import com.codecool.paintFx.model.StraightLine;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.paint.Color;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

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

        List<StoredLine> storedLineList = new ArrayList<>();

        for (MyShape myShape: ShapeList.getInstance().getShapeList()) {
            if (myShape instanceof StraightLine) {
                storedLineList.add(new StoredLine( myShape.getStartX(),
                        myShape.getStartY(),
                        ((StraightLine) myShape).getEndX(),
                        ((StraightLine) myShape).getEndY(),
                        myShape.getBrushSize(),
                        myShape.getColor()));
            }
        }

        String jsonImage = new Gson().toJson(storedLineList);

        if (file.exists()) {
            saveImageFile(jsonImage, file);
            System.out.println("Image file saved successfully!");
        } else {
            System.out.println("The image file doesn't exist!");
        }
    }

    public static void loadImage(){

        File file = new File(projectDAO.getCurrentProject().getPath() +
                File.separator + fileDAO.getCurrentFile().getName() + "_image.txt");

        if (file.exists()) {
            String jsonImage = openImageFile(file);
            List<StoredLine> storedLineList = new ArrayList<>();

            JsonArray jsonArray = new Gson().fromJson(jsonImage, JsonArray.class);
//            JsonObject straightLineList = new Gson().fromJson(jsonArray.get(0).toString(), JsonObject.class);
//            JsonArray lineList = new Gson().fromJson(straightLineList.get("straightLineList"), JsonArray.class);
//
//            System.out.println(lineList);

            try {
                ObjectMapper mapper = new ObjectMapper();
                storedLineList = mapper.readValue(jsonArray.toString(), new TypeReference<List<StoredLine>>() {
                });
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            List<MyShape> storedShapeList = ShapeList.getInstance().getShapeList();
            for (StoredLine line: storedLineList) {
                Color color = new Color(line.getRed(), line.getGreen(), line.getBlue(), 1.0);
                storedShapeList.add(new StraightLine(line.getStartX(), line.getStartX(),
                        line.getEndX(), line.getEndY(), color, line.getBrushSize()));
            }
            System.out.println("Image file opened successfully!");
        } else {
            System.out.println("Could not open imafe file!");
        }

    }

    private static void saveImageFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.write(content);
        } catch (IOException ex) {
            Logger.getLogger(AmITea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String openImageFile(File file) {

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
