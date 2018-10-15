package com.codecool.paintFx.service;

import com.codecool.am_i_tea.dao.ProjectDAO;
import com.codecool.am_i_tea.dao.TextFileDAO;
import com.codecool.am_i_tea.service.LoggerService;
import com.codecool.paintFx.controller.PaintController;
import com.codecool.paintFx.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.paint.Color;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class PaintService {

    private ProjectDAO projectDAO;
    private TextFileDAO fileDAO;
    private LoggerService logger;
    private PaintController paintController;

    public PaintService(ProjectDAO projectDAO, TextFileDAO fileDAO, LoggerService logger) {
        this.projectDAO = projectDAO;
        this.fileDAO = fileDAO;
        this.logger = logger;
    }

    public void setPaintController(PaintController paintController) {
        this.paintController = paintController;
    }

    public void setLogger(LoggerService loggerService) {
        logger = loggerService;
    }

    public void setProjectDAO(ProjectDAO projectDAOStuff) {
        projectDAO = projectDAOStuff;
    }

    public void setfileDAO(TextFileDAO fileDAOStuff) {
        fileDAO = fileDAOStuff;
    }

    public boolean createNewImageFile() {
        File file = new File(projectDAO.getCurrentProject().getPath() +
                File.separator + fileDAO.getCurrentFile().getName() + "_image.txt");
        try {
            if (file.createNewFile()) {
                logger.log(file.getName() + " image file created successfully!");
                return true;
            } else {
                logger.log(file.getName() + " image file already exists. Choose a unique name!");
                return false;
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return false;
        }
    }

    public void saveImage() {

        String path = projectDAO.getCurrentProject().getPath() + File.separator +
                fileDAO.getCurrentFile().getName() + "_image.txt";
        File file = new File(path);

        List<StoredLine> storedLineList = new ArrayList<>();
        List<StoredCustomLine> storedCustomLineList = new ArrayList<>();
        List<StoredRectangle> storedRectangleList = new ArrayList<>();
        List<StoredCircle> storedCircleList = new ArrayList<>();

        for (MyShape myShape : paintController.getDrawnShapeList()) {
            if (myShape instanceof StraightLine) {
                storedLineList.add(new StoredLine(myShape.getStartX(),
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
                for (StraightLine linePart : ((CustomLine) myShape).getStraightLineList()) {
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

            if (myShape instanceof MyOval) {
                storedCircleList.add(new StoredCircle(myShape.getStartX(),
                        myShape.getStartY(), ((MyOval) myShape).getWidth(),
                        ((MyOval) myShape).getHeight(), myShape.getBrushSize(),
                        myShape.getColor()));
            }
        }

        String jsonStoredLineList = new Gson().toJson(storedLineList);
        String jsonStoredCustomLineList = new Gson().toJson(storedCustomLineList);
        String jsonStoredRectangleList = new Gson().toJson(storedRectangleList);
        String jsonStoredCircleList = new Gson().toJson(storedCircleList);

        String fullJson = "[" + jsonStoredLineList + "," + jsonStoredCustomLineList + "," +
                jsonStoredRectangleList + "," + jsonStoredCircleList + "]";

        if (file.exists()) {
            saveImageFile(fullJson, file);
            logger.log(file.getName() + " image file saved successfully!");
        } else {
            logger.log("The " + file.getName() + " image file doesn't exist!");
        }
    }

    public void loadImage(String fileName, List<MyShape> storedShapeList) {

        File file = new File(projectDAO.getCurrentProject().getPath() +
                File.separator + fileName + "_image.txt");

        if (file.exists()) {
            String jsonImage = openImageFile(file);
            List<StoredLine> storedLineList = new ArrayList<>();
            List<StoredCustomLine> storedCustomLineList = new ArrayList<>();
            List<StoredRectangle> storedRectangleList = new ArrayList<>();
            List<StoredCircle> storedCircleList = new ArrayList<>();

            JsonArray listOfShapeTypes = new Gson().fromJson(jsonImage, JsonArray.class);
            JsonArray straightLineList = new Gson().fromJson(listOfShapeTypes.get(0).toString(), JsonArray.class);

            try {
                ObjectMapper mapper = new ObjectMapper();
                storedLineList = mapper.readValue(straightLineList.toString(), new TypeReference<List<StoredLine>>() {
                });
            } catch (IOException ex) {
                logger.log(ex.getMessage());
            }

            JsonArray customLineList = new Gson().fromJson(listOfShapeTypes.get(1).toString(), JsonArray.class);
            for (JsonElement customLineElement : customLineList) {
                StoredCustomLine currentStoredCustomLine = new StoredCustomLine();
                List<StoredLine> storedCustomLinePartList = new ArrayList<>();

                JsonObject customLine = new Gson().fromJson(customLineElement.toString(), JsonObject.class);
                JsonArray customLinePartList = new Gson().fromJson(customLine.get("storedLineList"), JsonArray.class);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    storedCustomLinePartList = mapper.readValue(customLinePartList.toString(), new TypeReference<List<StoredLine>>() {
                    });
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

            JsonArray circleList = new Gson().fromJson(listOfShapeTypes.get(3), JsonArray.class);
            try {
                ObjectMapper mapper = new ObjectMapper();
                storedCircleList = mapper.readValue(circleList.toString(), new TypeReference<List<StoredCircle>>() {
                });
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            for (StoredLine line : storedLineList) {
                Color color = new Color(line.getRed(), line.getGreen(), line.getBlue(), 1.0);
                storedShapeList.add(new StraightLine(line.getStartX(), line.getStartY(),
                        line.getEndX(), line.getEndY(), color, line.getBrushSize()));
            }
            for (StoredCustomLine line : storedCustomLineList) {
                List<StraightLine> storedCustomLinePartList = new ArrayList<>();
                for (StoredLine linePart : line.getStoredLineList()) {
                    Color color = new Color(linePart.getRed(), linePart.getGreen(), linePart.getBlue(), 1.0);
                    storedCustomLinePartList.add(new StraightLine(linePart.getStartX(),
                            linePart.getStartY(), linePart.getEndX(), linePart.getEndY(),
                            color, linePart.getBrushSize()));
                }
                storedShapeList.add(new CustomLine(storedCustomLinePartList));
            }
            for (StoredRectangle rekt : storedRectangleList) {
                Color color = new Color(rekt.getRed(), rekt.getGreen(), rekt.getBlue(), 1.0);
                storedShapeList.add(new MyRectangle(rekt.getStartX(), rekt.getStartY(),
                        rekt.getWidth(), rekt.getHeight(), color, rekt.getBrushSize()));
            }
            for (StoredCircle circle : storedCircleList) {
                Color color = new Color(circle.getRed(), circle.getGreen(), circle.getBlue(), 1.0);
                storedShapeList.add(new MyOval(circle.getStartX(), circle.getStartY(),
                        circle.getWidth(), circle.getHeight(), color, circle.getBrushSize()));
            }


            logger.log(file.getName() + " image file opened successfully!");
        } else {
            logger.log("Could not open " + file.getName() + " image file!");
        }

    }

    private void saveImageFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.write(content);
        } catch (IOException ex) {
            logger.log(ex.getMessage());
        }
    }

    private String openImageFile(File file) {

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
            logger.log(ex.getMessage());
        }

        return content;
    }
}
