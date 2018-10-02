package com.codecool.paintFx.controller;

import com.codecool.paintFx.model.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PaintController {

    private double prevX, prevY;

    private double startX, startY;

    private List<MyShape> drawnShapeList = new ArrayList<>();

    private final int rangeToSnap = 50;

    private List<StraightLine> straightLineList;

    private Stack<MyShape> redoStack = new Stack<>();

    @FXML
    private Canvas canvas;

    @FXML
    private BorderPane borderPane;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField brushSize;

    @FXML
    private ToggleButton straightLineChecked;

    @FXML
    private Button undo;

    @FXML
    private Button redo;

    @FXML
    private CheckBox lineSnapper;

    @FXML
    ToggleButton square;

    @FXML
    ToggleButton circle;

    @FXML
    Button textEditorButton;

    @FXML ToggleGroup toggleGroup1;

    @FXML
    ToolBar topToolbar;

    @FXML
    ToolBar bottomToolbar;

    public Canvas getCanvas() {
        return canvas;
    }

    public List<MyShape> getDrawnShapeList() {
        return drawnShapeList;
    }

    public void initialize() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        ShapeList.getInstance().setShapeList(drawnShapeList);

        handleMouseDrag(graphicsContext);
        handleMousePressed();
        handleUndo(graphicsContext);
        handleRedo(graphicsContext);
        handleMouseRelease(graphicsContext);
        handleSnapCheckBoxDisable();
        handleWindowResize();
        handleTextEditorButton();

    }

    private void handleWindowResize() {
        borderPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            canvas.setWidth(borderPane.getWidth() - 30);
        });
        borderPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            canvas.setHeight(borderPane.getHeight()-90);
        });
    }

    private void handleTextEditorButton() {
        textEditorButton.setOnAction(actionEvent -> {
            StackPane myStackPane = (StackPane)canvas.getParent().getParent();
            Node drawNode = myStackPane.getChildren().get(1);
            topToolbar.setVisible(false);
            bottomToolbar.setVisible(false);
            drawNode.setMouseTransparent(true);
            VBox editorVbox = (VBox) myStackPane.getChildren().get(0);
            Node node = editorVbox.lookup(".top-toolbar");
            Node node2 = editorVbox.lookup(".bottom-toolbar");
            node.setVisible(true);
            node2.setVisible(true);
            });
    }

    private void handleMousePressed() {
        canvas.setOnMousePressed(this::updatePositions);
    }

    private void handleMouseDrag(GraphicsContext graphicsContext) {
        canvas.setOnMouseDragged(mouseEvent -> {
            drawCurrentShape(graphicsContext, mouseEvent);
        });
    }

    private void handleMouseRelease(GraphicsContext graphicsContext) {
        canvas.setOnMouseReleased(mouseReleaseEvent -> {
            saveShape(graphicsContext, mouseReleaseEvent);
        });
    }

    private void handleRedo(GraphicsContext graphicsContext) {
        redo.setOnAction(event -> {
            if (redoStack.size() != 0) {
                MyShape shapeToRedo = redoStack.pop();
                drawnShapeList.add(shapeToRedo);
                redraw(drawnShapeList, graphicsContext);
            }
        });
    }

    private void handleUndo(GraphicsContext graphicsContext) {
        undo.setOnAction(e -> {
            if (drawnShapeList.size() != 0) {
                MyShape shapeToRemove = drawnShapeList.get(drawnShapeList.size() - 1);
                redoStack.push(shapeToRemove);
                drawnShapeList.remove(shapeToRemove);
                redraw(drawnShapeList, graphicsContext);
            }
        });
    }

    private void handleSnapCheckBoxDisable() {
        straightLineChecked.setOnAction(event -> {
            lineSnapper.setDisable(false);
        });
        square.setOnAction(event -> {
            lineSnapper.setDisable(true);
        });
        circle.setOnAction(event -> {
            lineSnapper.setDisable(true);
        });
    }

    private void drawCurrentShape(GraphicsContext graphicsContext, MouseEvent mouseEvent) {
        if (straightLineChecked.isSelected()) {
            drawShape(graphicsContext, mouseEvent, ShapeEnum.STRAIGHTLINE);
        } else if (square.isSelected()) {
            drawShape(graphicsContext, mouseEvent, ShapeEnum.RECTANGLE);
        } else if (circle.isSelected()) {
            drawShape(graphicsContext, mouseEvent, ShapeEnum.OVAL);
        } else {
            drawShape(graphicsContext, mouseEvent, ShapeEnum.CUSTOMLINE);
        }
    }

    private void updatePositions(MouseEvent e) {
        double size = Double.parseDouble(brushSize.getText());
        straightLineList = new ArrayList<>();
        prevX = e.getX() - size / 2;
        prevY = e.getY() - size / 2;
        startX = e.getX() - size / 2;
        startY = e.getY() - size / 2;
    }

    private void saveShape(GraphicsContext graphicsContext, MouseEvent mouseReleaseEvent) {
        double size = Double.parseDouble(brushSize.getText());
        double endX = mouseReleaseEvent.getX() - size / 2;
        double endY = mouseReleaseEvent.getY() - size / 2;
        if (straightLineChecked.isSelected()) {
            if (lineSnapper.isSelected()) {
                LinePositionController linePositionController = new LinePositionController();
                Position position = linePositionController.PositionSnapper(endX, endY, drawnShapeList, rangeToSnap);
                endX = position.x;
                endY = position.y;
            }
            setupBrush(graphicsContext, size, colorPicker.getValue());
            graphicsContext.strokeLine(startX, startY, endX, endY);
            drawnShapeList.add(new StraightLine(startX, startY, endX, endY, colorPicker.getValue(), size));
        } else if (square.isSelected()) {
            drawnShapeList.add(new MyRectangle(startX, startY, Math.abs(endX - startX), Math.abs(endY - startY), colorPicker.getValue(), size));
        } else if (circle.isSelected()) {
            drawnShapeList.add(new MyOval(startX, startY, Math.abs(endX - startX), Math.abs(endY - startY), colorPicker.getValue(), size));
        } else {
            CustomLine customLine = new CustomLine(straightLineList);
            drawnShapeList.add(customLine);
        }
    }

    private void drawShape(GraphicsContext graphicsContext, MouseEvent mouseEvent, ShapeEnum shapeEnum) {
        double size = Double.parseDouble(brushSize.getText());
        double currX = mouseEvent.getX() - size / 2;
        double currY = mouseEvent.getY() - size / 2;
        setupBrush(graphicsContext, size, colorPicker.getValue());
        if (shapeEnum.equals(ShapeEnum.CUSTOMLINE)) {
            graphicsContext.strokeLine(prevX, prevY, currX, currY);
            straightLineList.add(new StraightLine(prevX, prevY, currX, currY, colorPicker.getValue(), size));
            prevX = currX;
            prevY = currY;
        } else if (shapeEnum.equals(ShapeEnum.STRAIGHTLINE)) {
            if (lineSnapper.isSelected()) {
                LinePositionController linePositionController = new LinePositionController();
                Position startPosition = linePositionController.PositionSnapper(startX, startY, drawnShapeList, rangeToSnap);
                Position endPosition = linePositionController.PositionSnapper(currX, currY, drawnShapeList, rangeToSnap);
                startX = startPosition.x;
                startY = startPosition.y;
                currX = endPosition.x;
                currY = endPosition.y;
            }
            redraw(drawnShapeList, graphicsContext);
            graphicsContext.strokeLine(startX, startY, currX, currY);
        } else if (shapeEnum.equals(ShapeEnum.RECTANGLE)) {
            redraw(drawnShapeList, graphicsContext);
            graphicsContext.strokeRect(startX, startY, Math.abs(currX - startX), Math.abs(currY - startY));
        } else if (shapeEnum.equals(ShapeEnum.OVAL)) {
            redraw(drawnShapeList, graphicsContext);
            graphicsContext.strokeOval(startX, startY, Math.abs(currX - startX), Math.abs(currY - startY));
        }
    }

    private void redraw(List<MyShape> drawnShapeList, GraphicsContext graphicsContext) {
        clearCanvas();
        for (MyShape myShape : drawnShapeList) {
            setupBrush(graphicsContext, myShape.getBrushSize(), myShape.getColor());
            myShape.display(graphicsContext);
        }
        setupBrush(graphicsContext, Double.parseDouble(brushSize.getText()), colorPicker.getValue());
    }

    private void setupBrush(GraphicsContext graphicsContext, double size, Paint value) {
        graphicsContext.setStroke(value);
        graphicsContext.setLineWidth(size);
        graphicsContext.setLineCap(StrokeLineCap.ROUND);
    }

    public void clearCanvas() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void clearShapeList() {
        drawnShapeList.clear();
    }
}