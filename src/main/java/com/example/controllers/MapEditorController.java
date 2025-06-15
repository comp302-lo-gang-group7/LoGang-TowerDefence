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
import java.util.function.Predicate;

import com.example.main.Main;
import com.example.map.TileEnum;
import com.example.map.TileView;
import com.example.storage_manager.MapStorageManager;
import com.example.utils.MapEditorUtils;
import com.example.utils.TileRenderer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

    // Editor mode enum
    private enum EditorMode { EDIT, DELETE }

    // Current editor mode
    private EditorMode currentMode = EditorMode.EDIT;

    // Window dimensions
    private int mapWidth  = TILE_SIZE * (MAP_COLS + 1);
    private int mapHeight = TILE_SIZE * (MAP_ROWS + 1);
    private int paletteWidth = TILE_SIZE * PALETTE_COLS;
    private int windowWidth  = mapWidth + paletteWidth;
    private int windowHeight = mapHeight;

    // Tile management;
    private TileView defaultGrassTile;

    // Selection state
    private TileView selectedTileView;
    private TileEnum selectedTileType;

    private boolean selectedIsGroup = false;
    private int selectedOffsetRow = 0, selectedOffsetCol = 0;
    private Image selectedGroupImage;
    private int selectedGroupOriginRow, selectedGroupOriginCol;
    private Map<Pane, Boolean> selectedCellMap = new HashMap<>();

    // Group tracking
    private Map<String, Set<String>> groupTileMap = new HashMap<>();

    // Drag and drop state
    private Pane dragSourceCell = null;
    private TileView dragSourceTileView = null;
    private boolean isDraggingGroup = false;
    private String draggedGroupKey = null;
    private int dragSourceRow = -1;
    private int dragSourceCol = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    // 1) Resize and renderer
    Main.getViewManager().resizeWindow(windowWidth, windowHeight);
    tileRenderer = new TileRenderer("/com/example/assets/tiles/Tileset-64x64.png", TILE_SIZE);

    // 2) Allocate the backing array
    mapTileViews = new TileView[MAP_ROWS][MAP_COLS];

    // 3) Build the static UI
    setupMapManagementButtons();  // Do this first
    setupButtonImages();          // Apply button styling
    createTilePalette();
    createMapGrid();

    // 4) Wire up the combo exactly once
    setupMapSelectionComboBox();

    // 5) If there's at least one saved map, select & load it
    List<String> maps = MapStorageManager.listAvailableMaps();
    if (!maps.isEmpty()) {
        String first = maps.get(0);
        mapSelectionCombo.setValue(first);
        loadMapIntoGrid(first);
    }

    // 6) Set initial mode
    currentMode = EditorMode.EDIT;
    updateModeButtonStyles();
}

    /**
     * After loading tiles into mapTileViews, scan for any 2×2 blocks
     * of “group” tiles (e.g. your castle quadrants) and register them
     * in groupTileMap so dragging one corner will move all four.
     */
    private void detectGroupsFromLoaded() {
        groupTileMap.clear();
        boolean[][] used = new boolean[MAP_ROWS][MAP_COLS];

        // anything in bottom-two‐rows of tileset & first two cols is a “group” piece
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
                    // check they form a contiguous 2×2 in the tileset
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


    private void setupMapSelectionComboBox() {
        // Allow typing a new map name
        mapSelectionCombo.setEditable(true);
    
        // Populate with saved maps
        refreshMapList();
    
        // Apply wooden theme styling to the ComboBox itself
        mapSelectionCombo.setStyle("-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                                  "-fx-text-fill: #e8d9b5; -fx-font-weight: bold; " +
                                  "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                  "-fx-border-radius: 3;");
    
        // Style the list cells
        mapSelectionCombo.setCellFactory(param -> new ListCell<>() {
            @Override
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
        
        // Style the button cell (what's displayed when closed)
        mapSelectionCombo.setButtonCell(new ListCell<>() {
            @Override
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
    
        // Style the text field in the editable combo box
        mapSelectionCombo.getEditor().setStyle("-fx-background-color: #6b4c2e; -fx-text-fill: #e8d9b5; " +
                                             "-fx-font-weight: bold; -fx-highlight-fill: #8a673c;");
    
        // When the user selects or types and presses Enter, load that map
        mapSelectionCombo.setOnAction(evt -> {
            String name = mapSelectionCombo.getEditor().getText().trim();
            if (name.isEmpty()) return;
    
            // If new, create an empty map immediately
            if (!MapStorageManager.listAvailableMaps().contains(name)) {
                MapStorageManager.saveMap(mapTileViews, MAP_ROWS, MAP_COLS, name);
                refreshMapList();
                mapSelectionCombo.setValue(name);
            }
    
            // Then load it
            loadMapIntoGrid(name);
        });
        
        // Add a platform.runLater to style elements that might not be available immediately
        javafx.application.Platform.runLater(() -> {
            try {
                // Style the arrow button and arrow separately after the UI is fully loaded
                if (mapSelectionCombo.lookup(".arrow-button") != null) {
                    mapSelectionCombo.lookup(".arrow-button").setStyle("-fx-background-color: #6b4c2e;");
                }
                if (mapSelectionCombo.lookup(".arrow") != null) {
                    mapSelectionCombo.lookup(".arrow").setStyle("-fx-background-color: #e8d9b5;");
                }
                
                // Add CSS to style the dropdown popup
                mapSelectionCombo.getStyleClass().add("wooden-combo-box");
            } catch (Exception e) {
                // Log the error but don't crash the application
                System.err.println("Error styling ComboBox components: " + e.getMessage());
            }
        });
    }

    private void refreshMapList() {
        List<String> maps = MapStorageManager.listAvailableMaps();
        mapSelectionCombo.getItems().setAll(maps);
    }

    private void loadMapIntoGrid(String mapName) {
        try {
            TileView[][] loaded = MapStorageManager.loadMap(mapName);
            // assume mapTileViews and mapGrid already sized MAP_ROWS×MAP_COLS
            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    TileView tv = loaded[r][c];
                    tv.setFitWidth(TILE_SIZE);
                    tv.setFitHeight(TILE_SIZE);
                    tv.setPreserveRatio(false);

                    // replace the node in your grid cell
                    Pane cell = (Pane) getNodeByRowColumnIndex(r, c, mapGrid);
                    cell.getChildren().setAll(tv);
                    mapTileViews[r][c] = tv;

                    // re-apply any drag/drop or click handlers you have
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

    // helper to find the Pane at (row,col) in a GridPane
    private javafx.scene.Node getNodeByRowColumnIndex(final int row, final int column, GridPane grid) {
        for (javafx.scene.Node node : grid.getChildren()) {
            Integer r = GridPane.getRowIndex(node), c = GridPane.getColumnIndex(node);
            if (r != null && c != null && r == row && c == column) {
                return node;
            }
        }
        return null;
    }

    @FXML
    private void saveMap() {
        String mapName = mapSelectionCombo.getEditor().getText().trim();
        if (mapName.isEmpty()) {
            MapEditorUtils.showErrorAlert(
                    "Missing Map Name",
                    "Please enter a map name before saving.",
                    "You can type a new name or pick an existing one from the dropdown.",
                    this
            );
            return;
        }

        try {
            MapStorageManager.saveMap(mapTileViews, MAP_ROWS, MAP_COLS, mapName);
            refreshMapList();
            mapSelectionCombo.setValue(mapName);
            MapEditorUtils.showInfoAlert("Map Saved", "Successfully saved \"" + mapName + "\".", this);
        } catch (Exception e) {
            MapEditorUtils.showErrorAlert(
                    "Save Failed",
                    "Could not save map \"" + mapName + "\".",
                    e.getMessage(),
                    this
            );
        }
    }

    private void setupButtonImages() {
        // Define the button styles
        String buttonStyle = MapEditorUtils.BUTTON_NORMAL_STYLE;
        String buttonHoverStyle = MapEditorUtils.BUTTON_HOVER_STYLE;
        String buttonPressedStyle = MapEditorUtils.BUTTON_PRESSED_STYLE;
        
        // Apply styles to all navigation/action buttons
        Button[] actionButtons = {homeBtn, editModeBtn, deleteModeBtn, clearMapBtn, saveMapBtn};
        
        for (Button button : actionButtons) {
            // Apply normal style
            button.setStyle(buttonStyle);
            
            // Add hover effects
            button.setOnMouseEntered(e -> button.setStyle(buttonHoverStyle));
            button.setOnMouseExited(e -> button.setStyle(buttonStyle));
            
            // Add pressed effects
            button.setOnMousePressed(e -> button.setStyle(buttonPressedStyle));
            button.setOnMouseReleased(e -> {
                if (button.isHover()) {
                    button.setStyle(buttonHoverStyle);
                } else {
                    button.setStyle(buttonStyle);
                }
            });
            
            // Set button dimensions
            button.setPrefHeight(32);
            button.setPrefWidth(120);
        }
        
        // Set the current mode button (initially Edit Mode) to be highlighted
        updateModeButtonStyles();
    }

    private void createTilePalette() {
        for (int row = 0; row < PALETTE_ROWS; row++) {
            for (int col = 0; col < PALETTE_COLS; col++) {
                TileEnum tileType = TileEnum.fromRowCol(row, col);
                TileView tileView = tileRenderer.createTileView(tileType);

                Pane paletteCell = new Pane(tileView);
                paletteCell.setPrefSize(TILE_SIZE, TILE_SIZE);

                // Different style for road tiles (rows 0-2) vs structures
                String bgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: #555; -fx-border-width: 1;");

                final int r = row;
                final int c = col;

                setupPaletteCellInteractions(paletteCell, tileView, r, c);

                // Mark the default grass tile (row 1, col 1)
                if (tileType == TileEnum.GRASS) {
                    defaultGrassTile = tileView;
                }

                paletteGrid.add(paletteCell, col, row);
            }
        }
    }

    private void setupPaletteCellInteractions(Pane paletteCell, TileView tileView, int row, int col) {
        // Apply hover styles with different colors for roads vs structures
        paletteCell.setOnMouseEntered(e -> {
            // Check if this cell is selected
            Boolean isSelected = selectedCellMap.get(paletteCell);

            if (isSelected != null && isSelected) {
                // This cell is selected, maintain selection style
                String selBorderColor = (row < 3) ? "#ff7700" : "#ff9900";
                String selBgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + selBgColor +
                        "; -fx-border-color: " + selBorderColor +
                        "; -fx-border-width: 3;");
            } else {
                // Not selected, show hover style
                String hoverColor = (row < 3) ? "#c0e3bc" : "#b8e0b3";
                paletteCell.setStyle("-fx-background-color: " + hoverColor +
                        "; -fx-border-color: #66ccff; -fx-border-width: 2;");
            }
        });

        paletteCell.setOnMouseExited(e -> {
            // Check if this cell is selected
            Boolean isSelected = selectedCellMap.get(paletteCell);

            if (isSelected != null && isSelected) {
                // This cell is selected, maintain selection style
                String selBorderColor = (row < 3) ? "#ff7700" : "#ff9900";
                String selBgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + selBgColor +
                        "; -fx-border-color: " + selBorderColor +
                        "; -fx-border-width: 3;");
            } else {
                // Not selected, return to normal style
                String origColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + origColor +
                        "; -fx-border-color: #666; -fx-border-width: 1;");
            }
        });

        // Make the entire pane clickable
        paletteCell.setOnMouseClicked(e -> {
            selectTile(row, col, tileView, paletteCell);
            e.consume();
        });

        // Also keep the ImageView clickable for better responsiveness
        tileView.setOnMouseClicked(e -> {
            selectTile(row, col, tileView, paletteCell);
            e.consume();
        });
    }

    private void selectTile(int row, int col, TileView tileView, Pane paletteCell) {
        // Handle castle/group tile selection (bottom two rows, first two columns)
        if (row >= PALETTE_ROWS - 2 && col < 2) {
            selectedIsGroup = true;
            selectedOffsetRow = row - (PALETTE_ROWS - 2);
            selectedOffsetCol = col;
            // compute the **absolute** top-left in the tileset:
            selectedGroupOriginRow = row - selectedOffsetRow;
            selectedGroupOriginCol = col - selectedOffsetCol;

            // Get the group image from the tileset
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

        // Reset previous selection styling
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

        // Save reference to the currently selected tile
        selectedTileView = tileView;
        selectedCellMap.put(paletteCell, true);

        // Animation for selection feedback
        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(150), tileView);
        st.setFromX(0.9);
        st.setFromY(0.9);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    private void createMapGrid() {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                // Use TileRenderer to create a grass tile view
                TileView tileView = tileRenderer.createTileView(TileEnum.GRASS);
                mapTileViews[row][col] = tileView;

                Pane cell = new Pane(tileView);
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);
                cell.setStyle("-fx-border-color: #666; -fx-border-width: 1; -fx-background-color: transparent;");

                // Hover effects
                cell.setOnMouseEntered(e -> cell.setStyle("-fx-border-color: #66ccff; -fx-border-width: 2;"));
                cell.setOnMouseExited(e -> cell.setStyle("-fx-border-color: #666; -fx-border-width: 1;"));

                final int r = row, c = col;

                setupDragAndDrop(cell, tileView, r, c);

                cell.setOnMouseClicked(e -> {
                    if (!e.isConsumed() && e.getButton() == MouseButton.PRIMARY) {
                        placeTile(r, c);
                    }
                });

                mapGrid.add(cell, col, row);
            }
        }
    }

    private void setupDragAndDrop(Pane cell, TileView tileView, int row, int col) {
        // Setup on the TileView instead of the parent Pane
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

        // Leave the rest of the DnD handlers on the cell
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


    private void resetDragState() {
        dragSourceCell = null;
        dragSourceTileView = null;
        isDraggingGroup = false;
        draggedGroupKey = null;
        dragSourceRow = -1;
        dragSourceCol = -1;
    }

    private void placeTile(int row, int col) {
        if (currentMode == EditorMode.DELETE) {
            // Delete mode: reset tile to grass and remove from any group
            String groupKey = getGroupKeyForTile(row, col);
            if (groupKey != null) {
                resetGroup(groupKey);
            } else {
                resetTileToGrass(row, col);
            }
        } else if (currentMode == EditorMode.EDIT) {
            // Edit mode: place selected tile
            if (selectedIsGroup) {
                placeGroupTile(row, col);
            } else if (selectedTileType != null) {
                placeSingleTile(row, col);
            }
        }
    }

    private void placeGroupTile(int row, int col) {

        int baseRow = row - selectedOffsetRow;
        int baseCol = col - selectedOffsetCol;

        // 1. clear any groups we would overlap
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

        // 2. place the 2×2 composite tiles
        String newGroupKey = "group_" + System.currentTimeMillis();
        Set<String> newGroupTiles = new HashSet<>();

        for (int dr = 0; dr < 2; dr++) {
            for (int dc = 0; dc < 2; dc++) {
                int rr = baseRow + dr, cc = baseCol + dc;
                if (!inBounds(rr, cc)) continue;

                int sheetRow = selectedGroupOriginRow + dr;
                int sheetCol = selectedGroupOriginCol + dc;
                TileEnum tileType = TileEnum.fromRowCol(sheetRow, sheetCol);

                // ✅  composite image already has grass baked in
                TileView newTileView = tileRenderer.createTileView(tileType);

                Pane cell = (Pane) mapTileViews[rr][cc].getParent();
                installTileViewInCell(rr, cc, cell, newTileView);   // reuse helper from previous answer

                newGroupTiles.add(tileKey(rr, cc));
            }
        }

        groupTileMap.put(newGroupKey, newGroupTiles);
    }

    /** small helper so the loop is tidy */
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < MAP_ROWS && c >= 0 && c < MAP_COLS;
    }



    private void placeSingleTile(int row, int col) {
        // if it was part of a group we reset it first
        String groupKey = getGroupKeyForTile(row, col);
        if (groupKey != null) resetGroup(groupKey);

        // Replace the current tile with the selected tile type
        TileView oldTileView = mapTileViews[row][col];
        Pane cell = (Pane) oldTileView.getParent();

        TileView newTileView = tileRenderer.createTileView(selectedTileType);
        mapTileViews[row][col] = newTileView;

        installTileViewInCell(row, col, cell, newTileView);
    }


    private void moveSingleTile(int targetRow, int targetCol) {
        // if target is in a group, clear it
        String targetGroupKey = getGroupKeyForTile(targetRow, targetCol);
        if (targetGroupKey != null) resetGroup(targetGroupKey);

        // Get the tile type from the source
        TileEnum tileType = dragSourceTileView.getType();

        // Replace the target tile with a new tile of the source type
        TileView oldTargetTileView = mapTileViews[targetRow][targetCol];
        Pane targetCell = (Pane) oldTargetTileView.getParent();

        TileView newTileView = tileRenderer.createTileView(tileType);
        mapTileViews[targetRow][targetCol] = newTileView;

        installTileViewInCell(targetRow, targetCol, targetCell, newTileView);

        // Reset the source tile to grass
        resetTileToGrass(dragSourceRow, dragSourceCol);
    }


    private void moveGroupTile(int targetRow, int targetCol) {
        if (!groupTileMap.containsKey(draggedGroupKey)) return;

        // Calculate offset from the group's anchor point
        Set<String> groupTiles = groupTileMap.get(draggedGroupKey);
        int minRow = MAP_ROWS, minCol = MAP_COLS;

        // Find the top-left corner of the group (anchor point)
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

            // Check if destination would overlap with another group
            String targetGroupKey = getGroupKeyForTile(newRow, newCol);
            if (targetGroupKey != null && !targetGroupKey.equals(draggedGroupKey)) {
                resetGroup(targetGroupKey);
            }
        }

        return true;
    }

    private void executeGroupMove(Set<String> groupTiles, int minRow, int minCol,
                                  int newAnchorRow, int newAnchorCol) {
        // 1) remember tile types for each position
        Map<String, TileEnum> oldTypes = new HashMap<>();
        for (String pos : groupTiles) {
            String[] p = pos.split(",");
            int r = Integer.parseInt(p[0]), c = Integer.parseInt(p[1]);
            TileView tv = mapTileViews[r][c];
            oldTypes.put(pos, tv.getType());
        }

        // 2) clear the old group
        for (String pos : groupTiles) {
            String[] p = pos.split(",");
            int r = Integer.parseInt(p[0]), c = Integer.parseInt(p[1]);
            resetTileToGrass(r, c);
        }

        // 3) place tiles at new coords
        Set<String> newGroup = new HashSet<>();
        for (String pos : groupTiles) {
            String[] p = pos.split(",");
            int oldR = Integer.parseInt(p[0]), oldC = Integer.parseInt(p[1]);
            int newR = oldR - minRow + newAnchorRow;
            int newC = oldC - minCol + newAnchorCol;

            TileEnum tileType = oldTypes.get(pos);

            // Replace the tile at the new position
            TileView oldTileView = mapTileViews[newR][newC];
            Pane cell = (Pane) oldTileView.getParent();

            TileView newTileView = tileRenderer.createTileView(tileType);
            mapTileViews[newR][newC] = newTileView;

            installTileViewInCell(newR, newC, cell, newTileView);

            newGroup.add(tileKey(newR, newC));
        }

        // 4) update the group map
        groupTileMap.remove(draggedGroupKey);
        groupTileMap.put(draggedGroupKey, newGroup);
    }

    private void installTileViewInCell(int row, int col, Pane cell, TileView tileView) {
        tileView.setFitWidth(TILE_SIZE);
        tileView.setFitHeight(TILE_SIZE);
        tileView.setPreserveRatio(false);

        cell.getChildren().setAll(tileView);        // replace old node
        mapTileViews[row][col] = tileView;          // keep reference

        // attach DnD handlers to **this** TileView + its parent pane
        setupDragAndDrop(cell, tileView, row, col);
    }

    private void updateModeButtonStyles() {
        // Reset styles for both buttons
        String normalStyle = MapEditorUtils.BUTTON_NORMAL_STYLE;
        String activeStyle = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                             "-fx-text-fill: #ffcc66; -fx-font-family: 'Segoe UI'; " +
                             "-fx-font-size: 14px; -fx-font-weight: bold; " +
                             "-fx-border-color: #ffcc66; -fx-border-width: 2; " +
                             "-fx-border-radius: 5; -fx-background-radius: 5;";
        
        // Update based on current mode
        if (currentMode == EditorMode.EDIT) {
            editModeBtn.setStyle(activeStyle);
            deleteModeBtn.setStyle(normalStyle);
        } else {
            editModeBtn.setStyle(normalStyle);
            deleteModeBtn.setStyle(activeStyle);
        }
        
        // Re-attach the hover handlers
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
    public void toggleEditMode() {
        currentMode = EditorMode.EDIT;
        updateModeButtonStyles();
        MapEditorUtils.showInfoAlert("Edit Mode",
                "You can now place tiles on the map by selecting from the palette and clicking on the grid.",
                this);
    }

    @FXML
    public void toggleDeleteMode() {
        currentMode = EditorMode.DELETE;
        updateModeButtonStyles();
        MapEditorUtils.showInfoAlert("Delete Mode", 
                               "You can now remove tiles from the map by clicking on them.", 
                               this);
    }

    @FXML
    public void clearMap() {
        // Ask for confirmation
        boolean confirmed = MapEditorUtils.showCustomConfirmDialog(
            "Clear Map",
            "Are you sure you want to clear the map? This action will reset all tiles to grass and cannot be undone.",
            this
        );

        if (confirmed) {
            // Visual feedback
            MapEditorUtils.animateButtonClick(
                clearMapBtn,
                clearMapImage,
                BUTTON_BLUE_PRESSED,
                this
            );

            // Clear all tiles
            for (int row = 0; row < MAP_ROWS; row++) {
                for (int col = 0; col < MAP_COLS; col++) {
                    resetTileToGrass(row, col);
                }
            }
        }
    }

    @FXML
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

    private String tileKey(int row, int col) {
        return row + "," + col;
    }
    
    private String getGroupKeyForTile(int row, int col) {
        String tileKey = tileKey(row, col);
        for (Map.Entry<String, Set<String>> entry : groupTileMap.entrySet()) {
            if (entry.getValue().contains(tileKey)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    private void resetTileToGrass(int row, int col) {
        if (row >= 0 && row < MAP_ROWS && col >= 0 && col < MAP_COLS) {
            TileView tv = mapTileViews[row][col];
            tv.setImage(defaultGrassTile.getImage());
            tv.setType(TileEnum.GRASS);
        }
    }
    
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

    private void setupMapManagementButtons() {
        // Apply woody styling to buttons
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
                                 "-fx-background-radius: 3; " +
                                 "-fx-cursor: hand;";
    
        String buttonPressedStyle = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                  "-fx-text-fill: #d9c9a0; " +
                                  "-fx-font-family: 'Segoe UI'; " +
                                  "-fx-font-size: 12px; " +
                                  "-fx-font-weight: bold; " +
                                  "-fx-border-color: #8a673c; " +
                                  "-fx-border-width: 2; " +
                                  "-fx-border-radius: 3; " +
                                  "-fx-background-radius: 3;";
        
        // Apply styles to New Map button
        newMapBtn.setStyle(buttonStyle);
        newMapBtn.setOnMouseEntered(e -> newMapBtn.setStyle(buttonHoverStyle));
        newMapBtn.setOnMouseExited(e -> newMapBtn.setStyle(buttonStyle));
        newMapBtn.setOnMousePressed(e -> newMapBtn.setStyle(buttonPressedStyle));
        newMapBtn.setOnMouseReleased(e -> newMapBtn.setStyle(buttonHoverStyle));
        
        // Apply styles to Delete Map button
        deleteMapBtn.setStyle(buttonStyle);
        deleteMapBtn.setOnMouseEntered(e -> deleteMapBtn.setStyle(buttonHoverStyle));
        deleteMapBtn.setOnMouseExited(e -> deleteMapBtn.setStyle(buttonStyle));
        deleteMapBtn.setOnMousePressed(e -> deleteMapBtn.setStyle(buttonPressedStyle));
        deleteMapBtn.setOnMouseReleased(e -> deleteMapBtn.setStyle(buttonHoverStyle));
        
        // Button actions
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

    private void clearGrid() {
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                resetTileToGrass(r, c);
            }
        }
    }

    private void showNewMapDialog() {
        // Create a new stage for our custom dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle("Create New Map");
        
        // Create the custom title bar
        HBox titleBar = createTitleBar(dialogStage, "Create New Map");
        
        // Create content area
        VBox contentArea = new VBox(15);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20, 20, 20, 20));
        contentArea.setStyle("-fx-background-color: #5d4228;");
        
        // Create prompt text
        Text promptText = new Text("Enter a name for your new map:");
        promptText.setFont(Font.font("Segoe UI", 14));
        promptText.setFill(Color.web("#e8d9b5"));
        
        // Create text field
        TextField mapNameField = new TextField();
        mapNameField.setPromptText("Map name");
        mapNameField.setPrefWidth(250);
        mapNameField.setStyle("-fx-background-color: #7d5a3c; " +
                             "-fx-text-fill: #e8d9b5; " +
                             "-fx-border-color: #8a673c; " +
                             "-fx-border-width: 2;");
        
        // Create button area
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        buttonBox.setStyle("-fx-background-color: #5d4228;");
        
        // Create OK button
        Button okButton = new Button("Create");
        okButton.setPrefWidth(100);
        okButton.setPrefHeight(30);
        okButton.setStyle(MapEditorUtils.OK_BUTTON_NORMAL_STYLE);
        
        // OK button hover effect
        okButton.setOnMouseEntered(e -> okButton.setStyle(MapEditorUtils.OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(MapEditorUtils.OK_BUTTON_NORMAL_STYLE));
        
        // OK button click action
        okButton.setOnAction(e -> {
            String name = mapNameField.getText().trim();
            if (name.isEmpty()) {
                MapEditorUtils.showErrorAlert("Invalid Name",
                        "Map name cannot be empty.", null, this);
            } else if (MapStorageManager.listAvailableMaps().contains(name)) {
                MapEditorUtils.showErrorAlert("Name Exists",
                        "A map called \"" + name + "\" already exists.", null, this);
            } else {
                // Save an empty grid
                MapStorageManager.saveMap(mapTileViews, MAP_ROWS, MAP_COLS, name);
                refreshMapList();
                mapSelectionCombo.setValue(name);
                clearGrid();  // Reset all tiles to grass
                dialogStage.close();
            }
        });
        
        // Create Cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(100);
        cancelButton.setPrefHeight(30);
        cancelButton.setStyle(MapEditorUtils.BUTTON_NORMAL_STYLE);
        
        // Cancel button hover effect
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(MapEditorUtils.BUTTON_HOVER_STYLE));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(MapEditorUtils.BUTTON_NORMAL_STYLE));
        
        // Cancel button click action
        cancelButton.setOnAction(e -> dialogStage.close());
        
        // Add buttons to button area
        buttonBox.getChildren().addAll(okButton, cancelButton);
        
        // Build the content area
        contentArea.getChildren().addAll(promptText, mapNameField, buttonBox);
        
        // Create main container with title bar and content
        VBox root = new VBox();
        root.getChildren().addAll(titleBar, contentArea);
        root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");
        
        // Apply drop shadow effect
        root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));
        
        // Set up the scene
        Scene dialogScene = new Scene(root, 400, 200);
        dialogScene.setFill(Color.web("#5d4228"));
        dialogStage.setScene(dialogScene);
        
        // Center on parent
        dialogStage.centerOnScreen();
        
        // Add enter key handler for the text field
        mapNameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                okButton.fire();
            }
        });
        
        // Make the dialog draggable by the title bar
        MapEditorUtils.setupDraggableStage(titleBar, dialogStage);
        
        // Show dialog and wait for it to close
        dialogStage.showAndWait();
    }

    private HBox createTitleBar(Stage stage, String title) {
        // Create the title bar
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPrefHeight(25);
        titleBar.setStyle(MapEditorUtils.TITLE_BAR_STYLE);
        titleBar.setPadding(new Insets(0, 5, 0, 10));
        
        // Title text on the left
        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#e8d9b5"));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        
        // Close button on the right
        Button closeButton = new Button("×");
        closeButton.setStyle(MapEditorUtils.BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;");
        closeButton.setOnAction(e -> stage.close());
        
        // Hover effect for close button
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(MapEditorUtils.CLOSE_BUTTON_HOVER + "-fx-font-size: 16px;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(MapEditorUtils.BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;"));
        
        // Add components to title bar
        titleBar.getChildren().addAll(titleLabel, closeButton);
        
        return titleBar;
    }
}

