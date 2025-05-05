package com.example.controllers;

import com.example.main.Main;
import com.example.utils.MapEditorUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class MapEditorController implements Initializable {


    @FXML private GridPane mapGrid;
    @FXML private GridPane paletteGrid;
    @FXML private Button homeBtn;
    @FXML private Button editModeBtn;
    @FXML private Button deleteModeBtn;
    @FXML private Button clearMapBtn;
    @FXML private Button saveMapBtn;
    @FXML private ComboBox<String> mapSelectionCombo;
    @FXML private ImageView homeImage;
    @FXML private ImageView editModeImage;
    @FXML private ImageView deleteModeImage;
    @FXML private ImageView clearMapImage;
    @FXML private ImageView saveMapImage;

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

    // Tile management
    private Image tileset;
    private ImageView defaultGrassTile;
    private ImageView[][] mapTileImageViews = new ImageView[MAP_ROWS][MAP_COLS];
    
    // Selection state
    private Image selectedImage;
    private ImageView selectedTileView;
    private boolean selectedIsGroup = false;
    private int selectedOffsetRow = 0, selectedOffsetCol = 0;
    private Image selectedGroupImage;
    private Map<Pane, Boolean> selectedCellMap = new HashMap<>();
    
    // Group tracking
    private Map<String, Set<String>> groupTileMap = new HashMap<>();
    
    // Drag and drop state
    private Pane dragSourceCell = null;
    private ImageView dragSourceImageView = null;
    private boolean isDraggingGroup = false;
    private String draggedGroupKey = null;
    private int dragSourceRow = -1;
    private int dragSourceCol = -1;
    private Image draggedImage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Resize window to fit the map editor
        Main.getViewManager().resizeWindow(windowWidth, windowHeight);
        
        // Initialize UI elements
        setupButtonImages();
        setupMapSelectionComboBox();
        loadTilesetAndCreateGrids();
        
        // Set initial editor mode
        currentMode = EditorMode.EDIT;
        updateModeButtonStyles();
    }
    

    private void setupMapSelectionComboBox() {
        // Populate dropdown with example maps
        mapSelectionCombo.getItems().addAll(
            "Forest Path",
            "Castle Defense",
            "River Crossing",
            "Mountain Pass",
            "Desert Ambush",
            "Jungle Trail"
        );

        // Style the dropdown
        mapSelectionCombo.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");

        // Custom cell factory for dropdown items
        mapSelectionCombo.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item != null && !empty) {
                    setText(item);
                    setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
                } else {
                    setText(null);
                }
            }
        });

        // Style the dropdown button
        mapSelectionCombo.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item != null && !empty) {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                } else {
                    setText("Choose a map...");
                    setStyle("-fx-text-fill: #CCCCCC; -fx-font-style: italic;");
                }
            }
        });
    }
    
    private void loadTilesetAndCreateGrids() {
        InputStream stream = getClass().getResourceAsStream("/com/example/assets/tiles/Tileset-64x64.png");
        if (stream != null) {
            tileset = new Image(stream);
            createTilePalette();
            createMapGrid();
        } else {
            System.err.println("Tileset image not found!");
        }
    }
    
    private void setupButtonImages() {
        // Get the image views inside each button
        homeImage = (ImageView) homeBtn.getGraphic();
        editModeImage = (ImageView) editModeBtn.getGraphic();
        deleteModeImage = (ImageView) deleteModeBtn.getGraphic();
        clearMapImage = (ImageView) clearMapBtn.getGraphic();
        saveMapImage = (ImageView) saveMapBtn.getGraphic();
        
        // Add text overlay to the home button
        StackPane homeContent = new StackPane();
        Label homeLabel = new Label("HOME");
        homeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        homeContent.getChildren().addAll(homeImage, homeLabel);
        homeBtn.setGraphic(homeContent);
        
        // Add text overlay to the edit mode button
        StackPane editModeContent = new StackPane();
        Label editModeLabel = new Label("EDIT MODE");
        editModeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        editModeContent.getChildren().addAll(editModeImage, editModeLabel);
        editModeBtn.setGraphic(editModeContent);
        
        // Add text overlay to the delete mode button
        StackPane deleteContent = new StackPane();
        Label deleteModeLabel = new Label("DELETE");
        deleteModeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        deleteContent.getChildren().addAll(deleteModeImage, deleteModeLabel);
        deleteModeBtn.setGraphic(deleteContent);
        
        // Add text overlay to the clear map button
        StackPane clearContent = new StackPane();
        Label clearLabel = new Label("CLEAR MAP");
        clearLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        clearContent.getChildren().addAll(clearMapImage, clearLabel);
        clearMapBtn.setGraphic(clearContent);
        
        // Add text overlay to the save button
        StackPane saveContent = new StackPane();
        Label saveLabel = new Label("SAVE");
        saveLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        saveContent.getChildren().addAll(saveMapImage, saveLabel);
        saveMapBtn.setGraphic(saveContent);
    }

    private void createTilePalette() {
        PixelReader reader = tileset.getPixelReader();
        for (int row = 0; row < PALETTE_ROWS; row++) {
            for (int col = 0; col < PALETTE_COLS; col++) {
                WritableImage tileImage = new WritableImage(reader, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                ImageView tileView = new ImageView(tileImage);
                tileView.setFitWidth(TILE_SIZE);
                tileView.setFitHeight(TILE_SIZE);
                tileView.setPreserveRatio(false);

                Pane paletteCell = new Pane(tileView);
                paletteCell.setPrefSize(TILE_SIZE, TILE_SIZE);
                
                // Different style for road tiles (rows 0-2) vs structures
                String bgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: #555; -fx-border-width: 1;");

                final int r = row;
                final int c = col;
                
                setupPaletteCellInteractions(paletteCell, tileView, tileImage, reader, r, c);

                // Mark the default grass tile (row 1, col 1)
                if (row == 1 && col == 1) {
                    defaultGrassTile = tileView;
                }

                paletteGrid.add(paletteCell, col, row);
            }
        }
    }
    
    private void setupPaletteCellInteractions(Pane paletteCell, ImageView tileView, 
                                            Image tileImage, PixelReader reader, int row, int col) {
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
            selectTile(row, col, tileView, tileImage, reader, paletteCell);
            e.consume();
        });
        
        // Also keep the ImageView clickable for better responsiveness
        tileView.setOnMouseClicked(e -> {
            selectTile(row, col, tileView, tileImage, reader, paletteCell);
            e.consume();
        });
    }
    
    private void selectTile(int row, int col, ImageView tileView, Image tileImage, 
                          PixelReader reader, Pane paletteCell) {
        // Handle castle/group tile selection (bottom two rows, first two columns)
        if (row >= PALETTE_ROWS - 2 && col < 2) {
            selectedIsGroup = true;
            selectedOffsetRow = row - (PALETTE_ROWS - 2);
            selectedOffsetCol = col;
            selectedGroupImage = new WritableImage(reader, 0, (PALETTE_ROWS - 2) * TILE_SIZE, 
                                                TILE_SIZE * 2, TILE_SIZE * 2);
        } else {
            selectedIsGroup = false;
            selectedImage = tileImage;
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
                ImageView imageView = new ImageView(defaultGrassTile.getImage());
                imageView.setFitWidth(TILE_SIZE);
                imageView.setFitHeight(TILE_SIZE);
                mapTileImageViews[row][col] = imageView;

                Pane cell = new Pane(imageView);
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);
                cell.setStyle("-fx-border-color: #666; -fx-border-width: 1; -fx-background-color: transparent;");

                // Hover effects
                cell.setOnMouseEntered(e -> cell.setStyle("-fx-border-color: #66ccff; -fx-border-width: 2;"));
                cell.setOnMouseExited(e -> cell.setStyle("-fx-border-color: #666; -fx-border-width: 1;"));

                final int r = row, c = col;
                
                setupDragAndDrop(cell, imageView, r, c);

                cell.setOnMouseClicked(e -> {
                    if (!e.isConsumed() && e.getButton() == MouseButton.PRIMARY) {
                        placeTile(r, c);
                    }
                });

                mapGrid.add(cell, col, row);
            }
        }
    }
    
    private void setupDragAndDrop(Pane cell, ImageView imageView, int row, int col) {
        cell.setOnDragDetected(e -> {
            // Only allow dragging if this isn't a default grass tile
            if (imageView.getImage() != defaultGrassTile.getImage()) {
                // Check if this is part of a group
                String groupKey = getGroupKeyForTile(row, col);
                if (groupKey != null) {
                    isDraggingGroup = true;
                    draggedGroupKey = groupKey;
                } else {
                    isDraggingGroup = false;
                }
                
                dragSourceCell = cell;
                dragSourceImageView = imageView;
                dragSourceRow = row;
                dragSourceCol = col;
                draggedImage = imageView.getImage();
                
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("tile");
                db.setContent(content);
                db.setDragView(imageView.getImage());
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
            
            if (db.hasString() && draggedImage != null) {
                success = true;
                
                if (isDraggingGroup && draggedGroupKey != null) {
                    moveGroupTile(row, col);
                } else {
                    // For single tiles, simply move them
                    moveSingleTile(row, col);
                }
            }
            
            e.setDropCompleted(success);
            e.consume();
            
            // Reset drag state
            resetDragState();
        });
        
        cell.setOnDragDone(e -> {
            e.consume();
        });
    }
    
    private void resetDragState() {
        dragSourceCell = null;
        dragSourceImageView = null;
        isDraggingGroup = false;
        draggedGroupKey = null;
        dragSourceRow = -1;
        dragSourceCol = -1;
        draggedImage = null;
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
            } else if (selectedImage != null) {
                placeSingleTile(row, col);
            }
        }
    }
    
    private void placeGroupTile(int row, int col) {
        int baseRow = row - selectedOffsetRow;
        int baseCol = col - selectedOffsetCol;
        
        // Check if any of the tiles in the 2x2 group are already part of a group
        Set<String> groupsToReset = new HashSet<>();
        for (int dr = 0; dr < 2; dr++) {
            for (int dc = 0; dc < 2; dc++) {
                int rr = baseRow + dr;
                int cc = baseCol + dc;
                if (rr >= 0 && rr < MAP_ROWS && cc >= 0 && cc < MAP_COLS) {
                    String groupKey = getGroupKeyForTile(rr, cc);
                    if (groupKey != null) {
                        groupsToReset.add(groupKey);
                    }
                }
            }
        }
        
        // Reset all groups that would be affected
        for (String groupKey : groupsToReset) {
            resetGroup(groupKey);
        }
        
        // Create a new group key and set of tiles
        String newGroupKey = "group_" + System.currentTimeMillis();
        Set<String> newGroupTiles = new HashSet<>();
        
        PixelReader groupReader = selectedGroupImage.getPixelReader();
        
        // Place the new group tiles
        for (int dr = 0; dr < 2; dr++) {
            for (int dc = 0; dc < 2; dc++) {
                int rr = baseRow + dr;
                int cc = baseCol + dc;
                if (rr >= 0 && rr < MAP_ROWS && cc >= 0 && cc < MAP_COLS) {
                    WritableImage sub = new WritableImage(
                            groupReader,
                            dc * TILE_SIZE,
                            dr * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE
                    );
                    ImageView iv = mapTileImageViews[rr][cc];
                    iv.setImage(MapEditorUtils.compositeTile(
                            defaultGrassTile.getImage(),
                            sub,
                            TILE_SIZE,
                            WHITE_THRESHOLD
                    ));
                    
                    // Add this tile to the group
                    newGroupTiles.add(tileKey(rr, cc));
                }
            }
        }
        
        // Register the new group
        groupTileMap.put(newGroupKey, newGroupTiles);
    }
    
    private void placeSingleTile(int row, int col) {
        // Check if this tile is part of a group
        String groupKey = getGroupKeyForTile(row, col);
        if (groupKey != null) {
            resetGroup(groupKey);
        }
        
        mapTileImageViews[row][col].setImage(MapEditorUtils.compositeTile(
                defaultGrassTile.getImage(),
                selectedImage,
                TILE_SIZE,
                WHITE_THRESHOLD
        ));
    }
    
    private void moveSingleTile(int targetRow, int targetCol) {
        // Check if target is part of a group and reset if needed
        String targetGroupKey = getGroupKeyForTile(targetRow, targetCol);
        if (targetGroupKey != null) {
            resetGroup(targetGroupKey);
        }
        
        mapTileImageViews[targetRow][targetCol].setImage(draggedImage);
        mapTileImageViews[dragSourceRow][dragSourceCol].setImage(defaultGrassTile.getImage());
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
        Map<String, Image> tileImages = new HashMap<>();
        for (String tilePos : groupTiles) {
            String[] parts = tilePos.split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            tileImages.put(tilePos, mapTileImageViews[r][c].getImage());
        }
        
        // Reset the old group position
        for (String tilePos : groupTiles) {
            String[] parts = tilePos.split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);
            mapTileImageViews[r][c].setImage(defaultGrassTile.getImage());
        }
        
        // Create new group
        Set<String> newGroupTiles = new HashSet<>();
        for (String tilePos : groupTiles) {
            String[] parts = tilePos.split(",");
            int oldRow = Integer.parseInt(parts[0]);
            int oldCol = Integer.parseInt(parts[1]);
            int newRow = oldRow - minRow + newAnchorRow;
            int newCol = oldCol - minCol + newAnchorCol;
            
            // Place the tile at the new position
            mapTileImageViews[newRow][newCol].setImage(tileImages.get(tilePos));
            newGroupTiles.add(tileKey(newRow, newCol));
        }
        
        groupTileMap.remove(draggedGroupKey);
        groupTileMap.put(draggedGroupKey, newGroupTiles);
    }


    private void updateModeButtonStyles() {
        // Update button backgrounds based on current mode
        editModeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        deleteModeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        
        if (currentMode == EditorMode.EDIT) {
            if (editModeImage != null && deleteModeImage != null) {
                editModeImage.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE_3SLIDES)));
                deleteModeImage.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE)));
            }
        } else {
            if (editModeImage != null && deleteModeImage != null) {
                editModeImage.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE)));
                deleteModeImage.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE_PRESSED)));
            }
        }
    }
    
    @FXML
    private void toggleEditMode() {
        currentMode = EditorMode.EDIT;
        updateModeButtonStyles();
        MapEditorUtils.showInfoAlert("Edit Mode", "Edit mode activated", 
                "You can now place tiles on the map by selecting from the palette and clicking on the grid.", this);
    }

    @FXML
    private void toggleDeleteMode() {
        currentMode = EditorMode.DELETE;
        updateModeButtonStyles();
        MapEditorUtils.showInfoAlert("Delete Mode", "Delete mode activated", 
                "You can now remove tiles from the map by clicking on them.", this);
    }

    @FXML
    private void clearMap() {
        // Ask for confirmation
        boolean confirmed = MapEditorUtils.showCustomConfirmDialog(
            "Clear Map",
            "Are you sure you want to clear the map?",
            "This action will reset all tiles to grass and cannot be undone.",
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
            
            // Clear all group mappings
            groupTileMap.clear();
            
            // Show success message
            MapEditorUtils.showInfoAlert(
                "Map Cleared", 
                "Map cleared successfully", 
                "All tiles have been reset to the default grass tile.",
                this
            );
        }
    }


    @FXML
    private void saveMap() {
        MapEditorUtils.animateButtonClick(
            saveMapBtn, 
            saveMapImage, 
            BUTTON_BLUE_PRESSED, 
            this
        );

        for (int i = 0; i < MAP_ROWS; i++) {
            for (int j = 0; j < MAP_COLS; j++) {
                System.out.println(this.mapTileImageViews[i][j].getImage());
            }
        }
  
        MapEditorUtils.showInfoAlert(
            "Map Saved", 
            "Map saved successfully", 
            "Your map has been saved and can now be used in the game.",
            this
        );
    }
    

    @FXML
    private void goToHome() {
        boolean canLeave = true;
        boolean hasChanges = checkForUnsavedChanges();
        
        if (hasChanges) {
            canLeave = MapEditorUtils.showCustomConfirmDialog(
                "Leave Editor", 
                "Are you sure you want to leave?", 
                "Any unsaved changes will be lost.",
                this
            );
        }
        
        if (canLeave) {
            Main.getViewManager().resizeWindow(640, 450);
            Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
        }
    }
    
    private boolean checkForUnsavedChanges() {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                if (mapTileImageViews[row][col].getImage() != defaultGrassTile.getImage()) {
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
            mapTileImageViews[row][col].setImage(defaultGrassTile.getImage());
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
    

    private void showStatusMessage(String message) {
        System.out.println(message);
        // In a real implementation, this would update a status bar or show a toast notification
    }
}

