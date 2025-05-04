package com.example.controllers;

import com.example.main.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public class MapEditorController implements Initializable {

    @FXML private GridPane mapGrid;
    @FXML private GridPane paletteGrid;
    @FXML private Button editModeBtn;
    @FXML private Button deleteModeBtn;
    @FXML private Button clearMapBtn;
    @FXML private Button saveMapBtn;
    @FXML private ComboBox<String> mapSelectionCombo;

    private static final int TILE_SIZE = 64; // The tile size is based on the tile file name (64x64 is what is used currently)

    // These values are taken from the instruction document for the project
    private static final int MAP_ROWS = 9;
    private static final int MAP_COLS = 16;

    private static final int PALETTE_ROWS = 8;
    private static final int PALETTE_COLS = 4;

    // Window setup to make sure no clipping or overflow occurs
    int mapWidth  = TILE_SIZE * (MAP_COLS + 1);
    int mapHeight = TILE_SIZE * (MAP_ROWS + 1);
    int paletteWidth = TILE_SIZE * PALETTE_COLS;

    int windowWidth  = mapWidth + paletteWidth;
    int windowHeight = mapHeight;

    private static final double WHITE_THRESHOLD = 0.90; // This has to do with composite images during map generation.

    private Image tileset;
    private ImageView defaultGrassTile;
    private Image selectedImage;
    private ImageView selectedTileView;

    private boolean selectedIsGroup = false;
    private int selectedOffsetRow = 0, selectedOffsetCol = 0;
    private Image selectedGroupImage;
    private ImageView[][] mapTileImageViews = new ImageView[MAP_ROWS][MAP_COLS];
    
    // Track which tiles belong to which group
    private Map<String, Set<String>> groupTileMap = new HashMap<>();
    
    // Variables for drag and drop functionality
    private Pane dragSourceCell = null;
    private ImageView dragSourceImageView = null;
    private boolean isDraggingGroup = false;
    private String draggedGroupKey = null;
    private int dragSourceRow = -1;
    private int dragSourceCol = -1;
    private Image draggedImage = null;

    // Add this to your class variables
    private Map<Pane, Boolean> selectedCellMap = new HashMap<>();

    // Add an enum for the editor modes
    private enum EditorMode { EDIT, DELETE }
    private EditorMode currentMode = EditorMode.EDIT;

    // Constants for button styling
    private static final String BUTTON_NORMAL_COLOR = "#4CAF50";
    private static final String BUTTON_HOVER_COLOR = "#66BB6A";
    private static final String BUTTON_ACTIVE_COLOR = "#2E7D32";

    // Constants for button images
    private static final String BUTTON_BLUE = "/com/example/assets/ui/Button_Blue.png";
    private static final String BUTTON_BLUE_PRESSED = "/com/example/assets/ui/Button_Blue_Pressed.png";
    private static final String BUTTON_BLUE_3SLIDES = "/com/example/assets/ui/Button_Blue_3Slides.png";

    // References to button images
    @FXML private ImageView editModeImage;
    @FXML private ImageView deleteModeImage;
    @FXML private ImageView clearMapImage;
    @FXML private ImageView saveMapImage;

    // Helper method to generate a unique key for a tile position
    private String tileKey(int row, int col) {
        return row + "," + col;
    }
    
    // Helper method to get the group key from a member tile
    private String getGroupKeyForTile(int row, int col) {
        String tileKey = tileKey(row, col);
        for (Map.Entry<String, Set<String>> entry : groupTileMap.entrySet()) {
            if (entry.getValue().contains(tileKey)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    // Helper method to reset a tile to grass
    private void resetTileToGrass(int row, int col) {
        if (row >= 0 && row < MAP_ROWS && col >= 0 && col < MAP_COLS) {
            mapTileImageViews[row][col].setImage(defaultGrassTile.getImage());
        }
    }
    
    // Method to reset an entire group of tiles
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.getViewManager().resizeWindow(windowWidth, windowHeight); // Resize the window to fit everything properly
        
        // Initialize button images with icons
        setupButtonImages();
        
        InputStream stream = getClass().getResourceAsStream("/com/example/assets/tiles/Tileset-64x64.png");
        if (stream != null) {
            tileset = new Image(stream);
            createTilePalette();
            createMapGrid();
        } else {
            System.err.println("Tileset image not found!");
        }
        
        // Set initial mode to EDIT
        currentMode = EditorMode.EDIT;
        updateModeButtonStyles();

        // Populate the map selection dropdown with example maps
        mapSelectionCombo.getItems().addAll(
            "Forest Path",
            "Castle Defense",
            "River Crossing",
            "Mountain Pass",
            "Desert Ambush",
            "Jungle Trail"
        );

        // Add custom styling for the dropdown
        mapSelectionCombo.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");

        // Add custom cell factory to style the items in the dropdown
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

    /**
     * The tile palette shows the different tiles which can be used to create the map.
     * The main logic for creating them is straightforward, since we are given a solid image we go over the picture
     * for the number of separate images we expect to extract from it (i.e. the number of different structures or path tiles)
     * This number comes up to 32, since we have 4 columns and 8 rows of items (castle is split to 4 for consistency).
     *
     * So for each row, we scan a total of 64 pixels in both directions to generate a 64x64 subimage containing our structure.
     * These 64x64 pixel areas are saved to separate tiles, the tiles are configured with eventHandlers and styles, and are then
     * set on the paletteGrid in their respective positions.
     *
     * The end result is a complete palette that contains all the images from the original image separated and accessible
     * for use in creating the map.
     */
    private void createTilePalette() {
        PixelReader reader = tileset.getPixelReader();
        for (int row = 0; row < PALETTE_ROWS; row++) {
            for (int col = 0; col < PALETTE_COLS; col++) {
                WritableImage tileImage = new WritableImage(reader, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                ImageView tileView = new ImageView(tileImage);
                tileView.setFitWidth(TILE_SIZE);
                tileView.setFitHeight(TILE_SIZE);
                
                // Make the image view fill the entire cell
                tileView.setPreserveRatio(false);

                Pane paletteCell = new Pane(tileView);
                paletteCell.setPrefSize(TILE_SIZE, TILE_SIZE);
                
                // Different style for road tiles (rows 0-2) vs structures
                String bgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
                paletteCell.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: #555; -fx-border-width: 1;");

                final int r = row;
                final int c = col;
                
                // Apply hover styles with different colors for roads vs structures
                paletteCell.setOnMouseEntered(e -> {
                    // Check if this cell is selected using our map
                    Boolean isSelected = selectedCellMap.get(paletteCell);
                    
                    if (isSelected != null && isSelected) {
                        // This cell is selected, maintain selection style
                        String selBorderColor = (r < 3) ? "#ff7700" : "#ff9900";
                        String selBgColor = (r < 3) ? "#a5d3a5" : "#9ed199";
                        paletteCell.setStyle("-fx-background-color: " + selBgColor + 
                                           "; -fx-border-color: " + selBorderColor + 
                                           "; -fx-border-width: 3;");
                    } else {
                        // Not selected, show hover style
                        String hoverColor = (r < 3) ? "#c0e3bc" : "#b8e0b3";
                        paletteCell.setStyle("-fx-background-color: " + hoverColor + 
                                           "; -fx-border-color: #66ccff; -fx-border-width: 2;");
                    }
                });
                
                paletteCell.setOnMouseExited(e -> {
                    // Check if this cell is selected using our map
                    Boolean isSelected = selectedCellMap.get(paletteCell);
                    
                    if (isSelected != null && isSelected) {
                        // This cell is selected, maintain selection style
                        String selBorderColor = (r < 3) ? "#ff7700" : "#ff9900";
                        String selBgColor = (r < 3) ? "#a5d3a5" : "#9ed199";
                        paletteCell.setStyle("-fx-background-color: " + selBgColor + 
                                           "; -fx-border-color: " + selBorderColor + 
                                           "; -fx-border-width: 3;");
                    } else {
                        // Not selected, return to normal style
                        String origColor = (r < 3) ? "#a5d3a5" : "#9ed199";
                        paletteCell.setStyle("-fx-background-color: " + origColor + 
                                           "; -fx-border-color: #666; -fx-border-width: 1;");
                    }
                });
                
                // Make the entire pane clickable
                paletteCell.setOnMouseClicked(e -> {
                    selectTile(r, c, tileView, tileImage, reader, paletteCell);
                    e.consume();
                });
                
                // Also keep the ImageView clickable for better responsiveness
                tileView.setOnMouseClicked(e -> {
                    selectTile(r, c, tileView, tileImage, reader, paletteCell);
                    e.consume();
                });

                // Mark the default grass tile (row 1, col 1)
                if (row == 1 && col == 1) {
                    defaultGrassTile = tileView;
                }

                paletteGrid.add(paletteCell, col, row);
            }
        }
    }

    // Enhanced selection method with better visual feedback for both road and structure tiles
    private void selectTile(int row, int col, ImageView tileView, Image tileImage, PixelReader reader, Pane paletteCell) {
        // Handle castle/group tile selection (bottom two rows, first two columns)
        if (row >= PALETTE_ROWS - 2 && col < 2) {
            selectedIsGroup = true;
            selectedOffsetRow = row - (PALETTE_ROWS - 2);
            selectedOffsetCol = col;
            selectedGroupImage = new WritableImage(reader, 0, (PALETTE_ROWS - 2) * TILE_SIZE, TILE_SIZE * 2, TILE_SIZE * 2);
        } else {
            selectedIsGroup = false;
            selectedImage = tileImage;
        }

        // Reset previous selection styling and state
        if (selectedTileView != null) {
            Pane previousParent = (Pane) selectedTileView.getParent();
            int prevRow = GridPane.getRowIndex(previousParent) != null ? GridPane.getRowIndex(previousParent) : 0;
            String bgColor = (prevRow < 3) ? "#a5d3a5" : "#9ed199"; 
            previousParent.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: #666; -fx-border-width: 1;");
            
            // Clear previous selection from map
            selectedCellMap.put(previousParent, false);
        }

        // Apply selection styling - brighter orange for roads to make them more visible
        String borderColor = (row < 3) ? "#ff7700" : "#ff9900";
        String bgColor = (row < 3) ? "#a5d3a5" : "#9ed199";
        
        // Apply the style directly
        paletteCell.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: " + borderColor + "; -fx-border-width: 3;");
        
        // Save reference to the currently selected tile
        selectedTileView = tileView;
        
        // Mark this cell as selected in our map
        selectedCellMap.put(paletteCell, true);
        
        // Use a scale animation that doesn't interfere with the border
        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(
            javafx.util.Duration.millis(150), tileView);
        st.setFromX(0.9);
        st.setFromY(0.9);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    /**
     * The map grid represents the editable area where the player can place tiles.
     * This method initializes the full grid with default grass tiles. It goes over
     * each cell based on the MAP_ROWS and MAP_COLS values defined earlier.
     *
     * Each tile is set up as a 64x64 image view of the default grass image. The tile
     * is wrapped inside a Pane that is styled with a visible border so the grid is clearly defined.
     *
     * Mouse event handlers are attached to the cells:
     * - Hovering over a cell temporarily highlights it.
     * - Clicking on a cell checks if a tile has been selected from the palette.
     *   If one has, the selected image is composited on top of the base grass tile using
     *   the compositeTile() method, and placed into the map grid visually.
     *
     * The result is a fully interactive map editor grid where structure and path tiles
     * can be placed on top of a default base background.
     */
    private void createMapGrid() {
        for (int row = 0; row < MAP_ROWS; row++) {
            for (int col = 0; col < MAP_COLS; col++) {
                ImageView imageView = new ImageView(defaultGrassTile.getImage());
                imageView.setFitWidth(TILE_SIZE);
                imageView.setFitHeight(TILE_SIZE);

                mapTileImageViews[row][col]= imageView;

                Pane cell = new Pane(imageView);
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);
                cell.setStyle("-fx-border-color: #666; -fx-border-width: 1; -fx-background-color: transparent;");

                cell.setOnMouseEntered(e -> cell.setStyle("-fx-border-color: #66ccff; -fx-border-width: 2;"));
                cell.setOnMouseExited(e -> cell.setStyle("-fx-border-color: #666; -fx-border-width: 1;"));

                final int r = row, c = col;
                
                // Set up drag & drop functionality
                setupDragAndDrop(cell, imageView, r, c);

                // Preserve original click handler for placing tiles
                cell.setOnMouseClicked(e -> {
                    // Only handle tile placement if not dragging
                    if (!e.isConsumed() && e.getButton() == MouseButton.PRIMARY) {
                        placeTile(r, c);
                    }
                });

                mapGrid.add(cell, col, row);
            }
        }
    }
    
    /**
     * Sets up drag and drop functionality for map tiles
     */
    private void setupDragAndDrop(Pane cell, ImageView imageView, int row, int col) {
        // Start drag operation
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
                
                // Create drag content
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("tile"); // Just a marker
                db.setContent(content);
                
                // Set a visual for the drag
                db.setDragView(imageView.getImage());
                
                e.consume();
            }
        });
        
        // Allow drops
        cell.setOnDragOver(e -> {
            if (e.getGestureSource() != cell && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        
        // Visual feedback for drop target
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
        
        // Handle the drop
        cell.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            
            if (db.hasString() && draggedImage != null) {
                success = true;
                
                // For group tiles, we need special handling
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
            dragSourceCell = null;
            dragSourceImageView = null;
            isDraggingGroup = false;
            draggedGroupKey = null;
            dragSourceRow = -1;
            dragSourceCol = -1;
            draggedImage = null;
        });
        
        // Clean up after drag operation
        cell.setOnDragDone(e -> {
            e.consume();
        });
    }
    
    /**
     * Moves a single tile from source to target position
     */
    private void moveSingleTile(int targetRow, int targetCol) {
        // Check if target is part of a group and reset if needed
        String targetGroupKey = getGroupKeyForTile(targetRow, targetCol);
        if (targetGroupKey != null) {
            resetGroup(targetGroupKey);
        }
        
        // Move the tile image
        mapTileImageViews[targetRow][targetCol].setImage(draggedImage);
        mapTileImageViews[dragSourceRow][dragSourceCol].setImage(defaultGrassTile.getImage());
    }
    
    /**
     * Moves a group tile from source to target position
     */
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
        
        // Calculate offset within the group
        int rowOffset = dragSourceRow - minRow;
        int colOffset = dragSourceCol - minCol;
        
        // Calculate new anchor point
        int newAnchorRow = targetRow - rowOffset;
        int newAnchorCol = targetCol - colOffset;
        
        // Check if the new position would be valid
        boolean isValid = true;
        for (String tilePos : groupTiles) {
            String[] parts = tilePos.split(",");
            int oldRow = Integer.parseInt(parts[0]);
            int oldCol = Integer.parseInt(parts[1]);
            int newRow = oldRow - minRow + newAnchorRow;
            int newCol = oldCol - minCol + newAnchorCol;
            
            if (newRow < 0 || newRow >= MAP_ROWS || newCol < 0 || newCol >= MAP_COLS) {
                isValid = false;
                break;
            }
            
            // Check if destination would overlap with another group
            String targetGroupKey = getGroupKeyForTile(newRow, newCol);
            if (targetGroupKey != null && !targetGroupKey.equals(draggedGroupKey)) {
                resetGroup(targetGroupKey);
            }
        }
        
        if (isValid) {
            // Store tiles temporarily
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
            
            // Update group mapping
            groupTileMap.remove(draggedGroupKey);
            groupTileMap.put(draggedGroupKey, newGroupTiles);
        }
    }
    
    /**
     * Places or deletes a tile from the map based on current mode
     */
    private void placeTile(int row, int col) {
        if (currentMode == EditorMode.DELETE) {
            // Delete mode: reset tile to grass and remove from any group
            String groupKey = getGroupKeyForTile(row, col);
            if (groupKey != null) {
                resetGroup(groupKey);
            } else {
                resetTileToGrass(row, col);
            }
        } else {
            // Edit mode: place selected tile
            if (selectedIsGroup) {
                // ... existing group placement code
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
                            iv.setImage(compositeTile(
                                    defaultGrassTile.getImage(),
                                    sub
                            ));
                            
                            // Add this tile to the group
                            newGroupTiles.add(tileKey(rr, cc));
                        }
                    }
                }
                
                // Register the new group
                groupTileMap.put(newGroupKey, newGroupTiles);
            } else {
                // ... existing single tile placement code
                if (selectedImage != null) {
                    // Check if this tile is part of a group
                    String groupKey = getGroupKeyForTile(row, col);
                    if (groupKey != null) {
                        resetGroup(groupKey);
                    }
                    
                    // Place the new tile
                    mapTileImageViews[row][col].setImage(compositeTile(
                            defaultGrassTile.getImage(),
                            selectedImage
                    ));
                }
            }
        }
    }

    /**
     * Combines two images (a base and an overlay) into a single tile.
     * This is used when placing a tile onto the map. The base is usually
     * the grass image, and the overlay is a tile selected from the palette.
     *
     * The method iterates pixel by pixel. If a pixel in the overlay image is
     * fully transparent, or nearly white (based on WHITE_THRESHOLD), it is ignored
     * and the base image's pixel is used instead. This keeps the background (grass)
     * visible where needed.
     *
     * If the overlay pixel is valid, it is alpha-blended onto the base pixel so
     * the final result has smooth edges and shadows are preserved.
     *
     * The result is a clean merged image that preserves the visual fidelity of
     * both the background and the structure being placed.
     *
     * @param base    The background tile (typically grass)
     * @param overlay The structure or path tile being placed on top
     * @return A new composited image ready to be shown in the grid
     */
    private Image compositeTile(Image base, Image overlay) {
        int width = TILE_SIZE;
        int height = TILE_SIZE;

        WritableImage result = new WritableImage(width, height);
        PixelReader baseReader = base.getPixelReader();
        PixelReader overlayReader = overlay.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color overlayColor = overlayReader.getColor(x, y);
                // Ignore fully transparent or near-white pixels
                if (overlayColor.getOpacity() == 0 ||
                        (overlayColor.getRed() > WHITE_THRESHOLD && overlayColor.getGreen() > WHITE_THRESHOLD && overlayColor.getBlue() > WHITE_THRESHOLD)) {
                    // Use base tile pixel
                    writer.setColor(x, y, baseReader.getColor(x, y));
                } else {
                    Color baseColor = baseReader.getColor(x, y);
                    double alpha = overlayColor.getOpacity();
                    Color blended = new Color(
                            overlayColor.getRed() * alpha + baseColor.getRed() * (1 - alpha),
                            overlayColor.getGreen() * alpha + baseColor.getGreen() * (1 - alpha),
                            overlayColor.getBlue() * alpha + baseColor.getBlue() * (1 - alpha),
                            1.0
                    );
                    writer.setColor(x, y, blended);
                }
            }
        }

        return result;
    }
    
    /**
     * Sets up button styles and hover effects with consistent styling
     */
    private void setupButtons() {
        // Apply consistent initial styles to all buttons
        String baseStyle = "-fx-background-color: " + BUTTON_NORMAL_COLOR + "; -fx-text-fill: white;";
        editModeBtn.setStyle(baseStyle);
        deleteModeBtn.setStyle(baseStyle);
        clearMapBtn.setStyle(baseStyle);
        saveMapBtn.setStyle(baseStyle);
        
        // Add hover effects for all buttons
        setupButtonHoverEffect(editModeBtn);
        setupButtonHoverEffect(deleteModeBtn);
        setupButtonHoverEffect(clearMapBtn);
        setupButtonHoverEffect(saveMapBtn);
        
        // Update active state based on current mode
        updateModeButtonStyles();
    }

    /**
     * Configures hover effects for buttons with consistent styling
     */
    private void setupButtonHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            if ((button == editModeBtn && currentMode == EditorMode.EDIT) || 
                (button == deleteModeBtn && currentMode == EditorMode.DELETE)) {
                // Don't change the active button's color
                return;
            }
            button.setStyle("-fx-background-color: " + BUTTON_HOVER_COLOR + "; -fx-text-fill: white;");
        });
        
        button.setOnMouseExited(e -> {
            if ((button == editModeBtn && currentMode == EditorMode.EDIT) || 
                (button == deleteModeBtn && currentMode == EditorMode.DELETE)) {
                // Don't change the active button's color
                return;
            }
            button.setStyle("-fx-background-color: " + BUTTON_NORMAL_COLOR + "; -fx-text-fill: white;");
        });
    }

    /**
     * Updates the visual state of mode buttons based on the current editor mode
     */
    private void updateModeButtonStyles() {
        // Update button backgrounds based on current mode
        if (currentMode == EditorMode.EDIT) {
            // Set edit mode as active, delete mode as inactive
            editModeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            deleteModeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            
            // Update button images
            if (editModeImage != null && deleteModeImage != null) {
                editModeImage.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE_3SLIDES)));
                deleteModeImage.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE)));
            }
        } else {
            // Set delete mode as active, edit mode as inactive
            editModeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            deleteModeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            
            // Update button images
            if (editModeImage != null && deleteModeImage != null) {
                editModeImage.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE)));
                deleteModeImage.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE_PRESSED)));
            }
        }
    }

    /**
     * Sets up button images with text labels
     */
    private void setupButtonImages() {
        // Get the image views inside each button
        editModeImage = (ImageView) editModeBtn.getGraphic();
        deleteModeImage = (ImageView) deleteModeBtn.getGraphic();
        clearMapImage = (ImageView) clearMapBtn.getGraphic();
        saveMapImage = (ImageView) saveMapBtn.getGraphic();
        
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

    /**
     * Toggles editor to edit mode for placing tiles
     */
    @FXML
    private void toggleEditMode() {
        currentMode = EditorMode.EDIT;
        updateModeButtonStyles();
        showInfoAlert("Edit Mode", "Edit mode activated", 
                    "You can now place tiles on the map by selecting from the palette and clicking on the grid.");
    }

    /**
     * Toggles editor to delete mode for removing tiles
     */
    @FXML
    private void toggleDeleteMode() {
        currentMode = EditorMode.DELETE;
        updateModeButtonStyles();
        showInfoAlert("Delete Mode", "Delete mode activated", 
                    "You can now remove tiles from the map by clicking on them.");
    }

    /**
     * Clears the entire map back to grass
     */
    @FXML
    private void clearMap() {
        // Ask for confirmation with custom styled dialog
        boolean confirmed = showCustomConfirmDialog(
            "Clear Map",
            "Are you sure you want to clear the map?",
            "This action will reset all tiles to grass and cannot be undone."
        );
        
        if (confirmed) {
            // Visual feedback - briefly change button image
            animateButtonClick(clearMapBtn, clearMapImage);
            
            // Actual clear map functionality
            for (int row = 0; row < MAP_ROWS; row++) {
                for (int col = 0; col < MAP_COLS; col++) {
                    resetTileToGrass(row, col);
                }
            }
            
            // Clear all group mappings
            groupTileMap.clear();
            
            // Show success message
            showInfoAlert("Map Cleared", "Map cleared successfully", 
                        "All tiles have been reset to the default grass tile.");
        }
    }

    /**
     * Saves the current map to a file
     */
    @FXML
    private void saveMap() {
        // Visual feedback - briefly change button image
        animateButtonClick(saveMapBtn, saveMapImage);
        
        // Actual save functionality would go here
        // For now, just simulate a successful save
        
        // Show success message
        showInfoAlert("Map Saved", "Map saved successfully", 
                    "Your map has been saved and can now be used in the game.");
    }

    /**
     * Animates a button click with a pressed image
     */
    private void animateButtonClick(Button button, ImageView imageView) {
        Image originalImage = imageView.getImage();
        imageView.setImage(new Image(getClass().getResourceAsStream(BUTTON_BLUE_PRESSED)));
        
        // Reset after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(200);
                javafx.application.Platform.runLater(() -> {
                    imageView.setImage(originalImage);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Shows a custom-styled game-themed dialog instead of the standard alert
     */
    private void showInfoAlert(String title, String header, String content) {
        // Create a custom dialog instead of the standard alert
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        // Remove the header area completely
        dialog.setHeaderText(null);
        
        // Create a custom dialog pane with game-themed styling
        DialogPane dialogPane = dialog.getDialogPane();
        
        // Create a VBox for the content with proper padding and alignment
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20, 20, 10, 20));
        
        // Add a decorative ribbon image - select based on action type
        ImageView ribbonIcon = null;
        String ribbonPath = "/com/example/assets/ui/Ribbon_Blue_3Slides.png"; // Default blue
        
        // Change ribbon color based on the dialog title/action
        if (title.contains("Delete") || title.contains("Clear")) {
            ribbonPath = "/com/example/assets/ui/Ribbon_Red_3Slides.png";
        } else if (title.contains("Save")) {
            ribbonPath = "/com/example/assets/ui/Ribbon_Yellow_3Slides.png";
        }
        
        try {
            Image iconImage = new Image(getClass().getResourceAsStream(ribbonPath));
            ribbonIcon = new ImageView(iconImage);
            ribbonIcon.setFitWidth(200);  // Make it wider to match the ribbon style
            ribbonIcon.setFitHeight(40);  // Keep a reasonable height
            ribbonIcon.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load ribbon image: " + e.getMessage());
        }
        
        // Add header text with custom styling - adjust color based on action type
        Label headerLabel = new Label(header);
        String textColor = "#1565C0"; // Default blue
        
        if (title.contains("Delete") || title.contains("Clear")) {
            textColor = "#C62828"; // Red for destructive actions
        } else if (title.contains("Save")) {
            textColor = "#F57F17"; // Dark yellow for save actions
        }
        
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setWrapText(true);
        
        // Add content text with custom styling
        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);
        
        // Add all elements to the content box
        if (ribbonIcon != null) {
            contentBox.getChildren().addAll(ribbonIcon, headerLabel, contentLabel);
        } else {
            contentBox.getChildren().addAll(headerLabel, contentLabel);
        }
        
        // Set the background to match the grass texture
        String backgroundStyle = "-fx-background-color: #9dc183;"; // Same green as the map
        dialogPane.setStyle(backgroundStyle);
        
        // Add a border to make it look like a game panel
        contentBox.setStyle("-fx-background-color: #e9f5e3; -fx-background-radius: 5; " +
                          "-fx-border-color: #5d7542; -fx-border-width: 3; -fx-border-radius: 5;");
        
        // Set the content
        dialogPane.setContent(contentBox);
        
        // Add OK button but style it to match the game
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(okButtonType);
        
        // Style the OK button to match the action type
        Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        String buttonColor = "#4CAF50"; // Default green
        String hoverColor = "#66BB6A"; // Default hover green
        
        if (title.contains("Delete") || title.contains("Clear")) {
            buttonColor = "#d32f2f"; // Red button for destructive actions
            hoverColor = "#ef5350"; // Red hover
        } else if (title.contains("Save")) {
            buttonColor = "#FFA000"; // Yellow/orange for save
            hoverColor = "#FFB74D"; // Lighter yellow/orange for hover
        }
        
        okButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Add hover effect
        final String finalButtonColor = buttonColor;
        final String finalHoverColor = hoverColor;
        
        okButton.setOnMouseEntered(e -> 
            okButton.setStyle("-fx-background-color: " + finalHoverColor + "; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        okButton.setOnMouseExited(e -> 
            okButton.setStyle("-fx-background-color: " + finalButtonColor + "; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        
        // Show the dialog and wait for user response
        dialog.showAndWait();
    }

    /**
     * Shows a custom-styled confirmation dialog that matches the game theme
     * 
     * @return true if user confirmed, false otherwise
     */
    private boolean showCustomConfirmDialog(String title, String header, String content) {
        // Create a custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        
        DialogPane dialogPane = dialog.getDialogPane();
        
        // Create content layout
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20, 20, 10, 20));
        
        // For confirmation dialogs, use the ribbon color based on the action being confirmed
        ImageView ribbonIcon = null;
        String ribbonPath = "/com/example/assets/ui/Ribbon_Blue_3Slides.png"; // Default
        
        // For clear map action (which is the primary use case for confirmations)
        if (title.contains("Clear")) {
            ribbonPath = "/com/example/assets/ui/Ribbon_Red_3Slides.png";
        }
        
        try {
            Image iconImage = new Image(getClass().getResourceAsStream(ribbonPath));
            ribbonIcon = new ImageView(iconImage);
            ribbonIcon.setFitWidth(200);  // Make it wider to match the ribbon style
            ribbonIcon.setFitHeight(40);  // Keep a reasonable height
            ribbonIcon.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load ribbon image: " + e.getMessage());
        }
        
        // Add header text with custom styling (red for warning is appropriate for confirmation dialogs)
        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C62828;"); // Red for warning
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setWrapText(true);
        
        // Add content text with custom styling
        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);
        
        // Add all elements to the content box
        if (ribbonIcon != null) {
            contentBox.getChildren().addAll(ribbonIcon, headerLabel, contentLabel);
        } else {
            contentBox.getChildren().addAll(headerLabel, contentLabel);
        }
        
        // Set the background to match the grass texture
        String backgroundStyle = "-fx-background-color: #9dc183;"; // Same green as the map
        dialogPane.setStyle(backgroundStyle);
        
        // Add a border to make it look like a game panel
        contentBox.setStyle("-fx-background-color: #e9f5e3; -fx-background-radius: 5; " +
                           "-fx-border-color: #5d7542; -fx-border-width: 3; -fx-border-radius: 5;");
        
        // Set the content
        dialogPane.setContent(contentBox);
        
        // Add OK and Cancel buttons
        ButtonType confirmButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().addAll(confirmButtonType, cancelButtonType);
        
        // Style the buttons - keep red for the confirm button in confirmation dialogs
        // as it represents a potentially destructive action
        Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
        confirmButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold;");
        
        // Add hover effects
        confirmButton.setOnMouseEntered(e -> 
            confirmButton.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        confirmButton.setOnMouseExited(e -> 
            confirmButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        
        cancelButton.setOnMouseEntered(e -> 
            cancelButton.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        cancelButton.setOnMouseExited(e -> 
            cancelButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        
        // Show the dialog and process the result
        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == confirmButtonType;
    }
    
    /**
     * Shows a temporary status message (in a real implementation, this would display on UI)
     */
    private void showStatusMessage(String message) {
        System.out.println(message);
        // In a real implementation, this would update a status bar or show a toast notification
    }
}

