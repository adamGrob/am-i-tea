package com.codecool.am_i_tea.service;

import com.codecool.am_i_tea.model.TextFile;
import com.codecool.am_i_tea.dao.TextFileDAO;
import com.codecool.paintFx.model.MyShape;
import com.codecool.paintFx.model.ShapeList;
import com.codecool.paintFx.service.PaintService;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.web.HTMLEditor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextFileService {

    private TextFileDAO fileDAO;
    private GraphicsContext graphicsContext;
    private PropertyUtil propertyUtil;
    private LoggerService logger;
    private PaintService paintService;

    public TextFileService(TextFileDAO fileDAO, PropertyUtil propertyUtil,
                           LoggerService loggerService, PaintService paintService) {
        this.fileDAO = fileDAO;
        this.propertyUtil = propertyUtil;
        this.logger = loggerService;
        this.paintService = paintService;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public boolean createNewTextFile(String projectPath, String fileName, HTMLEditor editor) {
        File file = new File(projectPath + File.separator + fileName + ".html");
        try {
            if (file.createNewFile()) {
                logger.log(file.getName() + " created successfully!");
                fileDAO.setCurrentFile(new TextFile(fileName));
                ShapeList.getInstance().emptyShapeList();
                graphicsContext.clearRect(0, 0, editor.getWidth(), editor.getHeight());
                return paintService.createNewImageFile();
            } else {
                logger.log(file.getName() + " already exists. Choose a unique name!");
                return false;
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return false;
        }
    }

    public List<String> getAllFilesOfProject(String projectName) {
        String homeFolder = propertyUtil.getLocationProperty();
        String projectPath = homeFolder + File.separator + projectName;
        File file = new File(projectPath);

        String[] files = file.list((current, name) -> name.endsWith(".html"));
        if (files != null) {
            logger.log("Found the list of all files in the " + projectName + " project!");
            return new ArrayList<>(Arrays.asList(files));
        } else {
            logger.log("Couldn't find the list of files in the " + projectName + " project!");
            return null;
        }
    }

    public void saveTextFile(String projectPath, String fileName, HTMLEditor editor) {

        File file = new File(projectPath + File.separator + fileName + ".html");

        String textToSave = editor.getHtmlText();

        if (file.exists()) {
            saveFile(textToSave, file);
            paintService.saveImage();
            logger.log(file.getName() + " saved successfully!");
        } else {
            logger.log("The " + file.getName() + " file doesn't exist!");
        }
    }

    public void openTextFile(String fileName, String projectPath, HTMLEditor editor) {

        File file = new File(projectPath + File.separator + fileName + ".html");

        ShapeList.getInstance().emptyShapeList();
        graphicsContext.clearRect(0, 0, editor.getWidth(), editor.getHeight());

        String content = "";
        if (file.exists()) {
            content = openFile(file);
            fileDAO.setCurrentFile(new TextFile(fileName));
            paintService.loadImage();
            logger.log(file.getName() + " file opened successfully!");
        }
        editor.setHtmlText(content);
        redraw(ShapeList.getInstance().getShapeList(), graphicsContext, editor);
    }

    private void saveFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.write(content);
        } catch (IOException ex) {
            logger.log(ex.getMessage());
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
            logger.log(ex.getMessage());
        }

        return content;
    }

    private void redraw(List<MyShape> drawnShapeList, GraphicsContext graphicsContext, HTMLEditor editor) {
        graphicsContext.clearRect(0, 0, editor.getWidth(), editor.getHeight());
        for (MyShape myShape : drawnShapeList) {
            setupBrush(graphicsContext, myShape.getBrushSize(), myShape.getColor());
            myShape.display(graphicsContext);
        }
    }

    private void setupBrush(GraphicsContext graphicsContext, double size, Paint value) {
        graphicsContext.setStroke(value);
        graphicsContext.setLineWidth(size);
        graphicsContext.setLineCap(StrokeLineCap.ROUND);
    }
}
