package com.example.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

import com.example.main.Main;
import com.example.map.TileEnum;
import com.example.map.TileView;
import com.example.storage_manager.MapStorageManager;
import com.example.utils.MapEditorUtils;
import com.example.utils.MapValidator;
import com.example.utils.RoadValidator;
import com.example.utils.TileRenderer;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Class MapEditorController
 */
public class MapEditorController implements Initializable {
    @FXML private GridPane paletteGrid;
    @FXML private Button homeBtn;
    @FXML private Button editModeBtn;
    @FXML private Button deleteModeBtn;
    @FXML private Button clearMapBtn;
    @FXML private Button saveMapBtn;
    @FXML private ImageView homeImage;
    @FXML private ImageView editModeImage;
    @FXML private ImageView deleteModeImage;
    @FXML private ImageView clearMapImage;
    @FXML private ImageView saveMapImage;
    @FXML private Button newMapBtn, deleteMapBtn;

    @FXML private ComboBox<String> mapSelectionCombo;
    @FXML private GridPane mapGrid;

    private TileRenderer tileRenderer;
    private TileView[][] mapTileViews;

    private static final int TILE_SIZE = 64;
    private static final int MAP_ROWS = 9;
    private static final int MAP_COLS = 16;
    private static final int PALETTE_ROWS = 8;
    private static final int PALETTE_COLS = 4;
    private static final double WHITE_THRESHOLD = 0.90;

    private static final String BUTTON_NORMAL_COLOR = "#4CAF50";
    private static final String BUTTON_HOVER_COLOR = "#66BB6A";
    private static final String BUTTON_ACTIVE_COLOR = "#2E7D32";
    private static final String BUTTON_BLUE = "/com/example/assets/ui/Button_Blue.png";
    private static final String BUTTON_BLUE_PRESSED = "/com/example/assets/ui/Button_Blue_Pressed.png";
    private static final String BUTTON_BLUE_3SLIDES = "/com/example/assets/ui/Button_Blue_3Slides.png";


    private enum EditorMode { EDIT, DELETE }


    private EditorMode currentMode = EditorMode.EDIT;


    /**
     * TODO
     */
    private int mapWidth  = TILE_SIZE * (MAP_COLS + 1);
    /**
     * TODO
     */
    private int mapHeight = TILE_SIZE * (MAP_ROWS + 1);
    private int paletteWidth = TILE_SIZE * PALETTE_COLS;
    private int windowWidth  = mapWidth + paletteWidth;
    private int windowHeight = mapHeight;


    private TileView defaultGrassTile;


    private TileView selectedTileView;
    private TileEnum selectedTileType;

    private boolean selectedIsGroup = false;
    private int selectedOffsetRow = 0, selectedOffsetCol = 0;
    private Image selectedGroupImage;
    private int selectedGroupOriginRow, selectedGroupOriginCol;
    /**
     * TODO
     */
    private Map<Pane, Boolean> selectedCellMap = new HashMap<>();


    /**
     * TODO
     */
    private Map<String, Set<String>> groupTileMap = new HashMap<>();


    private Pane dragSourceCell = null;
    private TileView dragSourceTileView = null;
    private boolean isDraggingGroup = false;
    private String draggedGroupKey = null;
    private int dragSourceRow = -1;
    private int dragSourceCol = -1;

    @Override
    /**
     * TODO
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

    Main.getViewManager().resizeWindow(windowWidth, windowHeight);
    tileRenderer = new TileRenderer("/com/example/assets/tiles/Tileset-64x64.png", TILE_SIZE);


    mapTileViews = new TileView[MAP_ROWS][MAP_COLS];


    setupMapManagementButtons();
    setupButtonImages();
    createTilePalette();
    createMapGrid();


    setupMapSelectionComboBox();


    List<String> maps = MapStorageManager.listAvailableMaps();
    if (!maps.isEmpty()) {
        String first = maps.get(0);
        mapSelectionCombo.setValue(first);
        loadMapIntoGrid(first);
    }


    currentMode = EditorMode.EDIT;
    updateModeButtonStyles();


    if (Main.getViewManager().getScene() != null) {
        Image customCursorImage = new Image(getClass().getResourceAsStream("/com/example/assets/ui/01.png"));
        Main.getViewManager().setCustomCursor(customCursorImage);
    }
}


    /**
     * TODO
     */
    private void detectGroupsFromLoaded() {
        groupTileMap.clear();
        boolean[][] used = new boolean[MAP_ROWS][MAP_COLS];


        Predicate<TileEnum> isGroupPiece = t ->
                t.getRow() >= PALETTE_ROWS - 2 && t.getCol() < 2;

        for (int r = 0; r < MAP_ROWS - 1; r++) {
            for (int c = 0; c < MAP_COLS - 1; c++) {
                if (used[r][c]) continue;

                TileEnum t00 = mapTileViews[r][c].getType();
                TileEnum t01 = mapTileViews[r][c+1].getType();
                TileEnum t10 = mapTileViews[r+1][c].getType();
                TileEnum t11 = mapTileViews[r+1][c+1].getType();

                if (isGroupPiece.test(t00)
                        && isGroupPiece.test(t01)
                        && isGroupPiece.test(t10)
                        && isGroupPiece.test(t11))
                {

                    int sr = t00.getRow(), sc = t00.getCol();
                    if (t01.getRow()==sr && t01.getCol()==sc+1
                            && t10.getRow()==sr+1 && t10.getCol()==sc
                            && t11.getRow()==sr+1 && t11.getCol()==sc+1)
                    {
                        String key = "group_" + r + "_" + c;
                        Set<String> coords = new HashSet<>();
                        coords.add(tileKey(r,c));
                        coords.add(tileKey(r,c+1));
                        coords.add(tileKey(r+1,c));
                        coords.add(tileKey(r+1,c+1));
                        groupTileMap.put(key, coords);

                        used[r][c]     = true;
                        used[r][c+1]   = true;
                        used[r+1][c]   = true;
                        used[r+1][c+1] = true;
                    }
                }
            }
        }
    }


    /**
     * TODO
     */
    private void setupMapSelectionComboBox() {

        mapSelectionCombo.setEditable(true);


        refreshMapList();


        mapSelectionCombo.setStyle("-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                                  "-fx-text-fill: #e8d9b5; -fx-font-weight: bold; " +
                                  "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                  "-fx-border-radius: 3;");


        mapSelectionCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            /**
             * TODO
             */
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    setText(item);
                    setStyle("-fx-background-color: #5d4228; -fx-text-fill: #e8d9b5;");
                } else {
                    setText(null);
                }
            }
        });


        mapSelectionCombo.setButtonCell(new ListCell<>() {
            @Override
            /**
             * TODO
             */
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    setText(item);
                    setStyle("-fx-text-fill: #e8d9b5; -fx-font-weight: bold; -fx-background-color: #6b4c2e;");
                } else {
                    setText("Choose or enter a map…");
                    setStyle("-fx-text-fill: #d5c4a1; -fx-font-style: italic; -fx-background-color: #6b4c2e;");
                }
            }
        });


        mapSelectionCombo.getEditor().setStyle("-fx-background-color: #6b4c2e; -fx-text-fill: #e8d9b5; " +
                                             "-fx-font-weight: bold; -fx-highlight-fill: #8a673c;");


        mapSelectionCombo.setOnAction(evt -> {
            String name = mapSelectionCombo.getEditor().getText().trim();
            if (name.isEmpty()) return;


            if (!MapStorageManager.listAvailableMaps().contains(name)) {
                MapStorageManager.saveMap(mapTileViews, MAP_ROWS, MAP_COLS, name);
                refreshMapList();
                mapSelectionCombo.setValue(name);
            }


            loadMapIntoGrid(name);
        });


        javafx.application.Platform.runLater(() -> {
            try {

                if (mapSelectionCombo.lookup(".arrow-button") != null) {
                    mapSelectionCombo.lookup(".arrow-button").setStyle("-fx-background-color: #6b4c2e;");
                }
                if (mapSelectionCombo.lookup(".arrow") != null) {
                    mapSelectionCombo.lookup(".arrow").setStyle("-fx-background-color: #e8d9b5;");
                }


                mapSelectionCombo.getStyleClass().add("wooden-combo-box");
            } catch (Exception e) {

                System.err.println("Error styling ComboBox components: " + e.getMessage());
            }
        });
    }

    /**
     * TODO
     */
    private void refreshMapList() {
        List<String> maps = MapStorageManager.listAvailableMaps();
        mapSelectionCombo.getItems().setAll(maps);
    }

    /**
     * TODO
     */
    private void loadMapIntoGrid(String mapName) {
        try {
            TileView[][] loaded = MapStorageManager.loadMap(mapName);

            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    TileView tv = loaded[r][c];
                    tv.setFitWidth(TILE_SIZE);
                    tv.setFitHeight(TILE_SIZE);
                    tv.setPreserveRatio(false);


                    Pane cell = (Pane) getNodeByRowColumnIndex(r, c, mapGrid);
                    cell.getChildren().setAll(tv);
                    mapTileViews[r][c] = tv;


                    setupDragAndDrop(cell, tv, r, c);
                    detectGroupsFromLoaded();
                }
            }
        } catch (IOException e) {
            MapEditorUtils.showErrorAlert(
                    "Load Failed",
                    "Could not load map \"" + mapName + "\".",
                    Objects.toString(e.getMessage(), "Unknown error"),
                    this
            );
        }
    }


    /**
     * TODO
     */
    private javafx.scene.Node getNodeByRowColumnIndex(final int row, final int column, GridPane grid) {
        for (javafx.scene.Node node : grid.getChildren()) {
            Integer r = GridPane.getRowIndex(node), c = GridPane.getColumnIndex(node);
            if (r != null && c != null && r == row && c == column) {
                return node;
            }
        }
        return null;
    }


    /**
     * TODO
     */
    private boolean validateRoadConnections() {
        List<Point2D> disconnectedRoads = RoadValidator.findDisconnectedRoads(mapTileViews);

        if (!disconnectedRoads.isEmpty()) {

            MapEditorUtils.showErrorAlert(
                "Invalid Road Connections",
                "Disconnected Road Tiles",
                "Some road tiles are not properly connected. Please fix the highlighted paths before saving.",
                this
            );


            highlightDisconnectedRoads(disconnectedRoads);

            return false;
        }

        return true;
    }


    /**
     * TODO
     */
    private void highlightDisconnectedRoads(List<Point2D> disconnectedRoads) {

        List<TileView> highlightedTiles = new ArrayList<>();

        for (Point2D p : disconnectedRoads) {
            int col = (int) p.getX();
            int row = (int) p.getY();

            TileView tileView = mapTileViews[row][col];


            highlightedTiles.add(tileView);


            DropShadow errorEffect = new DropShadow();
            errorEffect.setColor(Color.RED);
            tileView.setEffect(errorEffect);


            FadeTransition fade = new FadeTransition(Duration.millis(800), tileView);
            fade.setFromValue(0.7);
            fade.setToValue(1.0);
            fade.setCycleCount(4);
            fade.setAutoReverse(true);
            fade.play();
        }


        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> {
            for (TileView tile : highlightedTiles) {
                tile.setEffect(null);
                tile.setOpacity(1.0);
            }
        });
        pause.play();
    }


/**
 * TODO
 */
private void highlightIsolatedTowerTiles(List<Point2D> isolatedTowerTiles) {

    List<TileView> highlightedTiles = new ArrayList<>();

    for (Point2D p : isolatedTowerTiles) {
        int col = (int) p.getX();
        int row = (int) p.getY();

        TileView tileView = mapTileViews[row][col];


        highlightedTiles.add(tileView);


        Pane cell = (Pane) tileView.getParent();


        DropShadow errorEffect = new DropShadow();
        errorEffect.setColor(Color.RED);
        tileView.setEffect(errorEffect);


        FadeTransition fade = new FadeTransition(Duration.millis(800), tileView);
        fade.setFromValue(0.7);
        fade.setToValue(1.0);
        fade.setCycleCount(4);
        fade.setAutoReverse(true);
        fade.play();
    }


    PauseTransition pause = new PauseTransition(Duration.seconds(5));
    pause.setOnFinished(e -> {
        for (TileView tile : highlightedTiles) {

            tile.setEffect(null);
            tile.setOpacity(1.0);


            Pane cell = (Pane) tile.getParent();
            cell.setStyle("-fx-border-color: #666; -fx-border-width: 1;");
        }
    });
    pause.play();
}

    @FXML
    /**
     * TODO
     */
    private void saveMap() {
        String mapName = mapSelectionCombo.getEditor().getText().trim();
        if (mapName.isEmpty()) {
            MapEditorUtils.showInfoAlert(
                    "Missing Map Name",
                    "Please enter a map name before saving.",
                    this
            );
            return;
        }


        List<Point2D> disconnectedRoads = RoadValidator.findDisconnectedRoads(mapTileViews);
        if (!disconnectedRoads.isEmpty()) {

            MapEditorUtils.showInfoAlert(
                    "Invalid Road Connections",
                    "Some road tiles are not properly connected. Please fix the highlighted paths before saving.",
                    this
            );


            highlightDisconnectedRoads(disconnectedRoads);
            return;
        }


        List<Point2D> isolatedTowerTiles = RoadValidator.findIsolatedTowerTiles(mapTileViews);
        if (!isolatedTowerTiles.isEmpty()) {
            MapEditorUtils.showInfoAlert(
                    "Invalid Tower Placement",
                    "Tower positions must be adjacent to a path. Please fix the highlighted tower positions.",
                    this
            );


            highlightIsolatedTowerTiles(isolatedTowerTiles);
            return;
        }

        try {
            MapStorageManager.saveMap(mapTileViews, MAP_ROWS, MAP_COLS, mapName);
            refreshMapList();
            mapSelectionCombo.setValue(mapName);
            MapEditorUtils.showInfoAlert("Map Saved", "Successfully saved \"" + mapName + "\".", this);
        } catch (Exception e) {
            MapEditorUtils.showInfoAlert(
                    "Save Failed",
                    "Could not save map \"" + mapName + "\": " + e.getMessage(),
                    this
            );
        }
    }

    /**
     * TODO
     */
    private void setupButtonImages() {

    String buttonStyle = MapEditorUtils.BUTTON_NORMAL_STYLE;
    String buttonHoverStyle = MapEditorUtils.BUTTON_HOVER_STYLE;
    String buttonPressedStyle = MapEditorUtils.BUTTON_PRESSED_STYLE;


    Button[] actionButtons = {homeBtn, editModeBtn, deleteModeBtn, clearMapBtn, saveMapBtn};

    for (Button button : actionButtons) {

        button.setStyle(buttonStyle);


        if (button != editModeBtn && button != deleteModeBtn) {


            button.setStyle(buttonStyle);


            Image customCursorImage = new Image(getClass().getResourceAsStream("/com/example/assets/ui/01.png"));
            ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
            button.setCursor(customCursor);


            button.setOnMouseEntered(e -> button.setStyle(buttonHoverStyle));
            button.setOnMouseExited(e -> button.setStyle(buttonStyle));


            button.setOnMousePressed(e -> button.setStyle(buttonPressedStyle));
            button.setOnMouseReleased(e -> {
                if (button.isHover()) {
                    button.setStyle(buttonHoverStyle);
                } else {
                    button.setStyle(buttonStyle);
                }
            });
        }


        button.setPrefHeight(32);
        button.setPrefWidth(120);
    }


    editModeBtn.setOnMousePressed(e -> editModeBtn.setStyle(buttonPressedStyle));
    deleteModeBtn.setOnMousePressed(e -> deleteModeBtn.setStyle(buttonPressedStyle));


    updateModeButtonStyles();
}

    /**
     * TODO
     */
    private void createTilePalette() {
        for (int row = 0; row < PALETTE_ROWS; row++) {
            for (int col = 0; col < PALETTE_COLS; col++) {
                TileEnum tileType = TileEnum.fromRowCol(row, col);
                TileView tileView = tileRenderer.createTileView(tileType);

                Pane paletteCell = new Pane(tileView);
                paletteCell.setPrefSize(TILE_SIZE, TILE_SIZE);


                String bgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: #555; -fx-border-width: 1;");

                final int r = row;
                final int c = col;

                setupPaletteCellInteractions(paletteCell, tileView, r, c);


                if (tileType == TileEnum.GRASS) {
                    defaultGrassTile = tileView;
                }

                paletteGrid.add(paletteCell, col, row);
            }
        }
    }

    /**
     * TODO
     */
    private void setupPaletteCellInteractions(Pane paletteCell, TileView tileView, int row, int col) {

        paletteCell.setOnMouseEntered(e -> {

            Boolean isSelected = selectedCellMap.get(paletteCell);

            if (isSelected != null && isSelected) {

                String selBorderColor = (row < 3) ? "#ff7700" : "#ff9900";
                String selBgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + selBgColor +
                        "; -fx-border-color: " + selBorderColor +
                        "; -fx-border-width: 3;");
            } else {

                String hoverColor = (row < 3) ? "#c0e3bc" : "#b8e0b3";
                paletteCell.setStyle("-fx-background-color: " + hoverColor +
                        "; -fx-border-color: #66ccff; -fx-border-width: 2;");
            }
        });

        paletteCell.setOnMouseExited(e -> {

            Boolean isSelected = selectedCellMap.get(paletteCell);

            if (isSelected != null && isSelected) {

                String selBorderColor = (row < 3) ? "#ff7700" : "#ff9900";
                String selBgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + selBgColor +
                        "; -fx-border-color: " + selBorderColor +
                        "; -fx-border-width: 3;");
            } else {

                String origColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + origColor +
                        "; -fx-border-color: #666; -fx-border-width: 1;");
            }
        });


        paletteCell.setOnMouseClicked(e -> {
            selectTile(row, col, tileView, paletteCell);
            e.consume();
        });


        tileView.setOnMouseClicked(e -> {
            selectTile(row, col, tileView, paletteCell);
            e.consume();
        });
    }

    /**
     * TODO
     */
    private void selectTile(int row, int col, TileView tileView, Pane paletteCell) {

        if (row >= PALETTE_ROWS - 2 && col < 2) {
            selectedIsGroup = true;
            selectedOffsetRow = row - (PALETTE_ROWS - 2);
            selectedOffsetCol = col;

            selectedGroupOriginRow = row - selectedOffsetRow;
            selectedGroupOriginCol = col - selectedOffsetCol;


            PixelReader reader = tileRenderer.getTilesetReader();
            selectedGroupImage = new WritableImage(
                    reader,
                    selectedGroupOriginCol * TILE_SIZE,
                    selectedGroupOriginRow * TILE_SIZE,
                    TILE_SIZE * 2,
                    TILE_SIZE * 2
            );
        } else {
            selectedIsGroup = false;
            selectedTileType = tileView.getType();
        }


        if (selectedTileView != null) {
            Pane previousParent = (Pane) selectedTileView.getParent();
            int prevRow = GridPane.getRowIndex(previousParent) != null ?
                    GridPane.getRowIndex(previousParent) : 0;
            String bgColor = (prevRow < 3) ? "#a5d3a5" : "#9ed199";
            previousParent.setStyle("-fx-background-color: " + bgColor +
                    "; -fx-border-color: #666; -fx-border-width: 1;");

            selectedCellMap.put(previousParent, false);
        }

        String borderColor = (row < 3) ? "#ff7700" : "#ff9900";
        String bgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
        paletteCell.setStyle("-fx-background-color: " + bgColor +
                "; -fx-border-color: " + borderColor +
                "; -fx-border-width: 3;");


        selectedTileView = tileView;
        selectedCellMap.put(paletteCell, true);


        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(150), tileView);
        st.setFromX(0.9);
        st.setFromY(0.9);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    /**
     * TODO
     */
    private void createMapGrid() {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {

                TileView tileView = tileRenderer.createTileView(TileEnum.GRASS);
                mapTileViews[row][col] = tileView;

                Pane cell = new Pane(tileView);
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);
                cell.setStyle("-fx-border-color: #666; -fx-border-width: 1; -fx-background-color: transparent;");


                cell.setOnMouseEntered(e -> cell.setStyle("-fx-border-color: #66ccff; -fx-border-width: 2;"));
                cell.setOnMouseExited(e -> cell.setStyle("-fx-border-color: #666; -fx-border-width: 1;"));

                final int r = row, c = col;

                setupDragAndDrop(cell, tileView, r, c);

                cell.setOnMouseClicked(e -> {
                    if (!e.isConsumed()) {
                        if (e.getButton() == MouseButton.PRIMARY) {

                            placeTile(r, c);
                        } else if (e.getButton() == MouseButton.SECONDARY) {

                            deleteTile(r, c);
                        }
                    }
                });

                mapGrid.add(cell, col, row);
            }
        }
    }


    /**
     * TODO
     */
    private void deleteTile(int row, int col) {

        String groupKey = getGroupKeyForTile(row, col);
        if (groupKey != null) {
            resetGroup(groupKey);
        } else {
            resetTileToGrass(row, col);
        }
    }

    /**
     * TODO
     */
    private void setupDragAndDrop(Pane cell, TileView tileView, int row, int col) {

        tileView.setOnDragDetected(e -> {
            if (tileView.getType() != TileEnum.GRASS) {
                String groupKey = getGroupKeyForTile(row, col);
                isDraggingGroup = (groupKey != null);
                draggedGroupKey = groupKey;

                dragSourceCell = cell;
                dragSourceTileView = tileView;
                dragSourceRow = row;
                dragSourceCol = col;

                Dragboard db = tileView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("tile");
                db.setContent(content);
                db.setDragView(tileView.getImage());

                e.consume();
            }
        });


        cell.setOnDragOver(e -> {
            if (e.getGestureSource() != cell && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        cell.setOnDragEntered(e -> {
            if (e.getGestureSource() != cell && e.getDragboard().hasString()) {
                cell.setStyle("-fx-border-color: #00ff00; -fx-border-width: 3;");
            }
            e.consume();
        });

        cell.setOnDragExited(e -> {
            cell.setStyle("-fx-border-color: #666; -fx-border-width: 1;");
            e.consume();
        });

        cell.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;

            if (db.hasString() && dragSourceTileView != null) {
                success = true;
                if (isDraggingGroup && draggedGroupKey != null) {
                    moveGroupTile(row, col);
                } else {
                    moveSingleTile(row, col);
                }
            }

            e.setDropCompleted(success);
            e.consume();

            resetDragState();
        });

        cell.setOnDragDone(e -> e.consume());
    }


    /**
     * TODO
     */
    private void resetDragState() {
        dragSourceCell = null;
        dragSourceTileView = null;
        isDraggingGroup = false;
        draggedGroupKey = null;
        dragSourceRow = -1;
        dragSourceCol = -1;
    }

    /**
     * TODO
     */
    private void placeTile(int row, int col) {
        if (currentMode == EditorMode.DELETE) {

            String groupKey = getGroupKeyForTile(row, col);
            if (groupKey != null) {
                resetGroup(groupKey);
            } else {
                resetTileToGrass(row, col);
            }
        } else if (currentMode == EditorMode.EDIT) {

            if (selectedIsGroup) {
                placeGroupTile(row, col);
            } else if (selectedTileType != null) {
                placeSingleTile(row, col);
            }
        }
    }

    /**
     * TODO
     */
    private void placeGroupTile(int row, int col) {

        int baseRow = row - selectedOffsetRow;
        int baseCol = col - selectedOffsetCol;


        Set<String> groupsToReset = new HashSet<>();
        for (int dr = 0; dr < 2; dr++) {
            for (int dc = 0; dc < 2; dc++) {
                int rr = baseRow + dr, cc = baseCol + dc;
                if (inBounds(rr, cc)) {
                    String key = getGroupKeyForTile(rr, cc);
                    if (key != null) groupsToReset.add(key);
                }
            }
        }
        groupsToReset.forEach(this::resetGroup);


        String newGroupKey = "group_" + System.currentTimeMillis();
        Set<String> newGroupTiles = new HashSet<>();

        for (int dr = 0; dr < 2; dr++) {
            for (int dc = 0; dc < 2; dc++) {
                int rr = baseRow + dr, cc = baseCol + dc;
                if (!inBounds(rr, cc)) continue;

                int sheetRow = selectedGroupOriginRow + dr;
                int sheetCol = selectedGroupOriginCol + dc;
                TileEnum tileType = TileEnum.fromRowCol(sheetRow, sheetCol);


                TileView newTileView = tileRenderer.createTileView(tileType);

                Pane cell = (Pane) mapTileViews[rr][cc].getParent();
                installTileViewInCell(rr, cc, cell, newTileView);

                newGroupTiles.add(tileKey(rr, cc));
            }
        }

        groupTileMap.put(newGroupKey, newGroupTiles);
    }


    /**
     * TODO
     */
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < MAP_ROWS && c >= 0 && c < MAP_COLS;
    }



    /**
     * TODO
     */
    private void placeSingleTile(int row, int col) {

        String groupKey = getGroupKeyForTile(row, col);
        if (groupKey != null) resetGroup(groupKey);


        TileView oldTileView = mapTileViews[row][col];
        Pane cell = (Pane) oldTileView.getParent();

        TileView newTileView = tileRenderer.createTileView(selectedTileType);
        mapTileViews[row][col] = newTileView;

        installTileViewInCell(row, col, cell, newTileView);
    }


    /**
     * TODO
     */
    private void moveSingleTile(int targetRow, int targetCol) {

        String targetGroupKey = getGroupKeyForTile(targetRow, targetCol);
        if (targetGroupKey != null) resetGroup(targetGroupKey);


        TileEnum tileType = dragSourceTileView.getType();


        TileView oldTargetTileView = mapTileViews[targetRow][targetCol];
        Pane targetCell = (Pane) oldTargetTileView.getParent();

        TileView newTileView = tileRenderer.createTileView(tileType);
        mapTileViews[targetRow][targetCol] = newTileView;

        installTileViewInCell(targetRow, targetCol, targetCell, newTileView);


        resetTileToGrass(dragSourceRow, dragSourceCol);
    }


    /**
     * TODO
     */
    private void moveGroupTile(int targetRow, int targetCol) {
        if (!groupTileMap.containsKey(draggedGroupKey)) return;


        Set<String> groupTiles = groupTileMap.get(draggedGroupKey);
        int minRow = MAP_ROWS, minCol = MAP_COLS;


        for (String tilePos : groupTiles) {
            String[] parts = tilePos.split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            minRow = Math.min(minRow, r);
            minCol = Math.min(minCol, c);
        }

        int rowOffset = dragSourceRow - minRow;
        int colOffset = dragSourceCol - minCol;

        int newAnchorRow = targetRow - rowOffset;
        int newAnchorCol = targetCol - colOffset;

        if (isValidGroupMove(groupTiles, minRow, minCol, newAnchorRow, newAnchorCol)) {
            executeGroupMove(groupTiles, minRow, minCol, newAnchorRow, newAnchorCol);
        }
    }

    private boolean isValidGroupMove(Set<String> groupTiles, int minRow, int minCol,
                                     int newAnchorRow, int newAnchorCol) {
        for (String tilePos : groupTiles) {
            String[] parts = tilePos.split(",");
            int oldRow = Integer.parseInt(parts[0]);
            int oldCol = Integer.parseInt(parts[1]);
            int newRow = oldRow - minRow + newAnchorRow;
            int newCol = oldCol - minCol + newAnchorCol;

            if (newRow < 0 || newRow >= MAP_ROWS || newCol < 0 || newCol >= MAP_COLS) {
                return false;
            }


            String targetGroupKey = getGroupKeyForTile(newRow, newCol);
            if (targetGroupKey != null && !targetGroupKey.equals(draggedGroupKey)) {
                resetGroup(targetGroupKey);
            }
        }

        return true;
    }

    private void executeGroupMove(Set<String> groupTiles, int minRow, int minCol,
                                  int newAnchorRow, int newAnchorCol) {

        Map<String, TileEnum> oldTypes = new HashMap<>();
        for (String pos : groupTiles) {
            String[] p = pos.split(",");
            int r = Integer.parseInt(p[0]), c = Integer.parseInt(p[1]);
            TileView tv = mapTileViews[r][c];
            oldTypes.put(pos, tv.getType());
        }


        for (String pos : groupTiles) {
            String[] p = pos.split(",");
            int r = Integer.parseInt(p[0]), c = Integer.parseInt(p[1]);
            resetTileToGrass(r, c);
        }


        Set<String> newGroup = new HashSet<>();
        for (String pos : groupTiles) {
            String[] p = pos.split(",");
            int oldR = Integer.parseInt(p[0]), oldC = Integer.parseInt(p[1]);
            int newR = oldR - minRow + newAnchorRow;
            int newC = oldC - minCol + newAnchorCol;

            TileEnum tileType = oldTypes.get(pos);


            TileView oldTileView = mapTileViews[newR][newC];
            Pane cell = (Pane) oldTileView.getParent();

            TileView newTileView = tileRenderer.createTileView(tileType);
            mapTileViews[newR][newC] = newTileView;

            installTileViewInCell(newR, newC, cell, newTileView);

            newGroup.add(tileKey(newR, newC));
        }


        groupTileMap.remove(draggedGroupKey);
        groupTileMap.put(draggedGroupKey, newGroup);
    }

    /**
     * TODO
     */
    private void installTileViewInCell(int row, int col, Pane cell, TileView tileView) {
        tileView.setFitWidth(TILE_SIZE);
        tileView.setFitHeight(TILE_SIZE);
        tileView.setPreserveRatio(false);

        cell.getChildren().setAll(tileView);
        mapTileViews[row][col] = tileView;


        setupDragAndDrop(cell, tileView, row, col);
    }

    /**
     * TODO
     */
    private void updateModeButtonStyles() {

        String normalStyle = MapEditorUtils.BUTTON_NORMAL_STYLE;
        String activeStyle = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                             "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; " +
                             "-fx-font-size: 14px; -fx-font-weight: bold; " +
                             "-fx-border-color: rgb(38, 163, 48); -fx-border-width: 2; " +
                             "-fx-border-radius: 5; -fx-background-radius: 5;";


        if (currentMode == EditorMode.EDIT) {
            editModeBtn.setStyle(activeStyle);
            deleteModeBtn.setStyle(normalStyle);
        } else {
            editModeBtn.setStyle(normalStyle);
            deleteModeBtn.setStyle(activeStyle);
        }


        editModeBtn.setOnMouseEntered(e -> {
            if (currentMode != EditorMode.EDIT) {
                editModeBtn.setStyle(MapEditorUtils.BUTTON_HOVER_STYLE);
            }
        });
        editModeBtn.setOnMouseExited(e -> {
            if (currentMode != EditorMode.EDIT) {
                editModeBtn.setStyle(normalStyle);
            }
        });

        deleteModeBtn.setOnMouseEntered(e -> {
            if (currentMode != EditorMode.DELETE) {
                deleteModeBtn.setStyle(MapEditorUtils.BUTTON_HOVER_STYLE);
            }
        });
        deleteModeBtn.setOnMouseExited(e -> {
            if (currentMode != EditorMode.DELETE) {
                deleteModeBtn.setStyle(normalStyle);
            }
        });
    }

    @FXML
    /**
     * TODO
     */
    public void toggleEditMode() {
        currentMode = EditorMode.EDIT;
        updateModeButtonStyles();
        MapEditorUtils.showInfoAlert("Edit Mode",
                "You can now place tiles on the map by selecting from the palette and clicking on the grid.",
                this);
    }

    @FXML
    /**
     * TODO
     */
    public void toggleDeleteMode() {
        currentMode = EditorMode.DELETE;
        updateModeButtonStyles();
        MapEditorUtils.showInfoAlert("Delete Mode",
                               "You can now remove tiles from the map by clicking on them.",
                               this);
    }

    @FXML
    /**
     * TODO
     */
    public void clearMap() {

        boolean confirmed = MapEditorUtils.showCustomConfirmDialog(
            "Clear Map",
            "Are you sure you want to clear the map? This action will reset all tiles to grass and cannot be undone.",
            this
        );

        if (confirmed) {

            MapEditorUtils.animateButtonClick(
                clearMapBtn,
                clearMapImage,
                BUTTON_BLUE_PRESSED,
                this
            );


            for (int row = 0; row < MAP_ROWS; row++) {
                for (int col = 0; col < MAP_COLS; col++) {
                    resetTileToGrass(row, col);
                }
            }
        }
    }

    @FXML
    /**
     * TODO
     */
    public void goToHome() {
        boolean canLeave = true;
        boolean hasChanges = checkForUnsavedChanges();

        if (hasChanges) {
            canLeave = MapEditorUtils.showCustomConfirmDialog(
                "Leave Editor",
                "Are you sure you want to leave? Any unsaved changes will be lost.",
                this
            );
        }


        if (canLeave) {
            Main.getViewManager().resizeWindow(800, 600);
            Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
        }
    }

    /**
     * TODO
     */
    private boolean checkForUnsavedChanges() {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                if (mapTileViews[row][col].getImage() != defaultGrassTile.getImage()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * TODO
     */
    private String tileKey(int row, int col) {
        return row + "," + col;
    }

    /**
     * TODO
     */
    private String getGroupKeyForTile(int row, int col) {
        String tileKey = tileKey(row, col);
        for (Map.Entry<String, Set<String>> entry : groupTileMap.entrySet()) {
            if (entry.getValue().contains(tileKey)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * TODO
     */
    private void resetTileToGrass(int row, int col) {
        if (row >= 0 && row < MAP_ROWS && col >= 0 && col < MAP_COLS) {
            TileView tv = mapTileViews[row][col];
            tv.setImage(defaultGrassTile.getImage());
            tv.setType(TileEnum.GRASS);
        }
    }

    /**
     * TODO
     */
    private void resetGroup(String groupKey) {
        if (groupKey != null && groupTileMap.containsKey(groupKey)) {
            Set<String> tilesToReset = groupTileMap.get(groupKey);
            for (String tilePos : tilesToReset) {
                String[] parts = tilePos.split(",");
                int r = Integer.parseInt(parts[0]);
                int c = Integer.parseInt(parts[1]);
                resetTileToGrass(r, c);
            }
            groupTileMap.remove(groupKey);
        }
    }

    /**
     * TODO
     */
    private void setupMapManagementButtons() {

        String buttonStyle = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                            "-fx-text-fill: #e8d9b5; " +
                            "-fx-font-family: 'Segoe UI'; " +
                            "-fx-font-size: 12px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-border-color: #8a673c; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 3; " +
                            "-fx-background-radius: 3;";

        String buttonHoverStyle = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
                                 "-fx-text-fill: #f5ead9; " +
                                 "-fx-font-family: 'Segoe UI'; " +
                                 "-fx-font-size: 12px; " +
                                 "-fx-font-weight: bold; " +
                                 "-fx-border-color: #a07748; " +
                                 "-fx-border-width: 2; " +
                                 "-fx-border-radius: 3; " +
                                 "-fx-background-radius: 3; ";

        String buttonPressedStyle = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                  "-fx-text-fill: #d9c9a0; " +
                                  "-fx-font-family: 'Segoe UI'; " +
                                  "-fx-font-size: 12px; " +
                                  "-fx-font-weight: bold; " +
                                  "-fx-border-color: #8a673c; " +
                                  "-fx-border-width: 2; " +
                                  "-fx-border-radius: 3; " +
                                  "-fx-background-radius: 3;";


        newMapBtn.setStyle(buttonStyle);
        newMapBtn.setOnMouseEntered(e -> newMapBtn.setStyle(buttonHoverStyle));
        newMapBtn.setOnMouseExited(e -> newMapBtn.setStyle(buttonStyle));
        newMapBtn.setOnMousePressed(e -> newMapBtn.setStyle(buttonPressedStyle));
        newMapBtn.setOnMouseReleased(e -> newMapBtn.setStyle(buttonHoverStyle));


        deleteMapBtn.setStyle(buttonStyle);
        deleteMapBtn.setOnMouseEntered(e -> deleteMapBtn.setStyle(buttonHoverStyle));
        deleteMapBtn.setOnMouseExited(e -> deleteMapBtn.setStyle(buttonStyle));
        deleteMapBtn.setOnMousePressed(e -> deleteMapBtn.setStyle(buttonPressedStyle));
        deleteMapBtn.setOnMouseReleased(e -> deleteMapBtn.setStyle(buttonHoverStyle));


        newMapBtn.setOnAction(e -> showNewMapDialog());

        deleteMapBtn.setOnAction(e -> {
            String selected = mapSelectionCombo.getValue();
            if (selected == null) {
                MapEditorUtils.showErrorAlert("No Selection",
                        "Please select a map to delete.", null, this);
                return;
            }
            boolean confirm = MapEditorUtils.showCustomConfirmDialog(
                    "Delete Map",
                    "Are you sure you want to permanently delete \"" + selected + "\"?",
                    this
            );
            if (confirm) {
                MapStorageManager.deleteMap(selected);
                refreshMapList();
                mapSelectionCombo.getSelectionModel().clearSelection();
                clearGrid();
            }
        });
    }

    /**
     * TODO
     */
    private void clearGrid() {
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                resetTileToGrass(r, c);
            }
        }
    }

    /**
     * TODO
     */
    private void showNewMapDialog() {

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle("Create New Map");


        HBox titleBar = createTitleBar(dialogStage, "Create New Map");


        VBox contentArea = new VBox(15);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20, 20, 20, 20));
        contentArea.setStyle("-fx-background-color: #5d4228;");


        Text promptText = new Text("Enter a name for your new map:");
        promptText.setFont(Font.font("Segoe UI", 14));
        promptText.setFill(Color.web("#e8d9b5"));


        TextField mapNameField = new TextField();
        mapNameField.setPromptText("Map name");
        mapNameField.setPrefWidth(250);
        mapNameField.setStyle("-fx-background-color: #7d5a3c; " +
                             "-fx-text-fill: #e8d9b5; " +
                             "-fx-border-color: #8a673c; " +
                             "-fx-border-width: 2;");


        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        buttonBox.setStyle("-fx-background-color: #5d4228;");


        Button okButton = new Button("Create");
        okButton.setPrefWidth(100);
        okButton.setPrefHeight(30);
        okButton.setStyle(MapEditorUtils.OK_BUTTON_NORMAL_STYLE);


        okButton.setOnMouseEntered(e -> okButton.setStyle(MapEditorUtils.OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(MapEditorUtils.OK_BUTTON_NORMAL_STYLE));


        okButton.setOnAction(e -> {
            String name = mapNameField.getText().trim();
            if (name.isEmpty()) {
                MapEditorUtils.showErrorAlert("Invalid Name",
                        "Map name cannot be empty.", null, this);
            } else if (MapStorageManager.listAvailableMaps().contains(name)) {
                MapEditorUtils.showErrorAlert("Name Exists",
                        "A map called \"" + name + "\" already exists.", null, this);
            } else {

                clearGrid();
                MapStorageManager.saveMap(mapTileViews, MAP_ROWS, MAP_COLS, name);
                refreshMapList();
                mapSelectionCombo.setValue(name);
                dialogStage.close();
            }
        });


        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(100);
        cancelButton.setPrefHeight(30);
        cancelButton.setStyle(MapEditorUtils.BUTTON_NORMAL_STYLE);


        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(MapEditorUtils.BUTTON_HOVER_STYLE));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(MapEditorUtils.BUTTON_NORMAL_STYLE));


        cancelButton.setOnAction(e -> dialogStage.close());


        buttonBox.getChildren().addAll(okButton, cancelButton);


        contentArea.getChildren().addAll(promptText, mapNameField, buttonBox);


        VBox root = new VBox();
        root.getChildren().addAll(titleBar, contentArea);
        root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");


        root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));


        Scene dialogScene = new Scene(root, 400, 200);
        dialogScene.setFill(Color.web("#5d4228"));
        dialogStage.setScene(dialogScene);


        dialogStage.centerOnScreen();


        mapNameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                okButton.fire();
            }
        });


        MapEditorUtils.setupDraggableStage(titleBar, dialogStage);


        dialogStage.showAndWait();
    }

    /**
     * TODO
     */
    private HBox createTitleBar(Stage stage, String title) {

        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPrefHeight(25);
        titleBar.setStyle(MapEditorUtils.TITLE_BAR_STYLE);
        titleBar.setPadding(new Insets(0, 5, 0, 10));


        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#e8d9b5"));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setAlignment(Pos.CENTER_LEFT);


        Button closeButton = new Button("×");
        closeButton.setStyle(MapEditorUtils.BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;");
        closeButton.setOnAction(e -> stage.close());


        closeButton.setOnMouseEntered(e -> closeButton.setStyle(MapEditorUtils.CLOSE_BUTTON_HOVER + "-fx-font-size: 16px;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(MapEditorUtils.BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;"));


        titleBar.getChildren().addAll(titleLabel, closeButton);

        return titleBar;
    }
}
