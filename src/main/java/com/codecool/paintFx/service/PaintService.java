package com.codecool.paintFx.service;

import com.codecool.am_i_tea.AmITea;
import com.codecool.am_i_tea.ProjectDAO;
import com.codecool.am_i_tea.TextFileDAO;
import com.codecool.paintFx.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
        List<StoredCustomLine> storedCustomLineList = new ArrayList<>();
        List<StoredRectangle> storedRectangleList = new ArrayList<>();

        for (MyShape myShape: ShapeList.getInstance().getShapeList()) {
            if (myShape instanceof StraightLine) {
                storedLineList.add(new StoredLine( myShape.getStartX(),
                        myShape.getStartY(),
                        ((StraightLine) myShape).getEndX(),
                        ((StraightLine) myShape).getEndY(),
                        myShape.getBrushSize(),
                        myShape.getColor()));
            }

            if (myShape instanceof CustomLine) {
                StoredCustomLine storedCustomLine = new StoredCustomLine();
                List<StoredLine> placeholder = new ArrayList<>();
                storedCustomLine.setStoredLineList(placeholder);
                for (StraightLine linePart: ((CustomLine)myShape).getStraightLineList()) {
                    storedCustomLine.getStoredLineList().add(
                            new StoredLine(linePart.getStartX(),
                                    linePart.getStartY(),
                                    linePart.getEndX(),
                                    linePart.getEndY(),
                                    linePart.getBrushSize(),
                                    linePart.getColor())
                    );
                }
                storedCustomLineList.add(storedCustomLine);
            }

            if (myShape instanceof MyRectangle) {
                storedRectangleList.add(new StoredRectangle(myShape.getStartX(),
                        myShape.getStartY(), ((MyRectangle) myShape).getWidth(),
                        ((MyRectangle) myShape).getHeight(), myShape.getBrushSize(),
                        myShape.getColor()));
            }
        }

        String jsonStoredLineList = new Gson().toJson(storedLineList);
        String jsonStoredCustomLineList = new Gson().toJson(storedCustomLineList);
        String jsonStoredRectangleList = new Gson().toJson(storedRectangleList);

        String fullJson = "[" + jsonStoredLineList + "," + jsonStoredCustomLineList + "," +
                jsonStoredRectangleList + "]";

        if (file.exists()) {
            saveImageFile(fullJson, file);
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
            List<StoredCustomLine> storedCustomLineList = new ArrayList<>();
            List<StoredRectangle> storedRectangleList = new ArrayList<>();

            JsonArray listOfShapeTypes = new Gson().fromJson(jsonImage, JsonArray.class);
            JsonArray straightLineList = new Gson().fromJson(listOfShapeTypes.get(0).toString(), JsonArray.class);

            try {
                ObjectMapper mapper = new ObjectMapper();
                storedLineList = mapper.readValue(straightLineList.toString(), new TypeReference<List<StoredLine>>() {
                });
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            JsonArray customLineList = new Gson().fromJson(listOfShapeTypes.get(1).toString(), JsonArray.class);
            for (JsonElement customLineElement: customLineList) {
                StoredCustomLine currentStoredCustomLine = new StoredCustomLine();
                List<StoredLine> storedCustomLinePartList = new ArrayList<>();

                JsonObject customLine = new Gson().fromJson(customLineElement.toString(), JsonObject.class);
                JsonArray customLinePartList = new Gson().fromJson(customLine.get("storedLineList"), JsonArray.class);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    storedCustomLinePartList = mapper.readValue(customLinePartList.toString(), new TypeReference<List<StoredLine>>() {});
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }

                currentStoredCustomLine.setStoredLineList(storedCustomLinePartList);
                storedCustomLineList.add(currentStoredCustomLine);
            }

            JsonArray rectangleList = new Gson().fromJson(listOfShapeTypes.get(2), JsonArray.class);
            try {
                ObjectMapper mapper = new ObjectMapper();
                storedRectangleList = mapper.readValue(rectangleList.toString(), new TypeReference<List<StoredRectangle>>() {
                });
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            List<MyShape> storedShapeList = ShapeList.getInstance().getShapeList();
            for (StoredLine line: storedLineList) {
                Color color = new Color(line.getRed(), line.getGreen(), line.getBlue(), 1.0);
                storedShapeList.add(new StraightLine(line.getStartX(), line.getStartY(),
                        line.getEndX(), line.getEndY(), color, line.getBrushSize()));
            }
            for (StoredCustomLine line: storedCustomLineList) {
                List<StraightLine> storedCustomLinePartList = new ArrayList<>();
                for (StoredLine linePart: line.getStoredLineList()) {
                    Color color = new Color(linePart.getRed(), linePart.getGreen(), linePart.getBlue(), 1.0);
                    storedCustomLinePartList.add(new StraightLine(linePart.getStartX(),
                            linePart.getStartY(), linePart.getEndX(), linePart.getEndY(),
                            color, linePart.getBrushSize()));
                }
                storedShapeList.add(new CustomLine(storedCustomLinePartList));
            }
            for (StoredRectangle rekt: storedRectangleList) {
                Color color = new Color(rekt.getRed(), rekt.getGreen(), rekt.getBlue(), 1.0);
                storedShapeList.add(new MyRectangle(rekt.getStartX(), rekt.getStartY(),
                        rekt.getWidth(), rekt.getHeight(), color, rekt.getBrushSize()));
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
