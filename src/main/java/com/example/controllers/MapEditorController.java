package com.example.controllers;

import com.example.main.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

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
        InputStream stream = getClass().getResourceAsStream("/com/example/assets/tiles/Tileset-64x64.png");
        if (stream != null) {
            tileset = new Image(stream);
            createTilePalette();
            createMapGrid();
        } else {
            System.err.println("Tileset image not found!");
        }
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
                paletteCell.setStyle("-fx-background-color: #9ed199; -fx-border-color: #555; -fx-border-width: 1;");

                // Apply hover styles directly to the pane for better responsiveness
                paletteCell.setOnMouseEntered(e -> {
                    if (selectedTileView != tileView) {
                        paletteCell.setStyle("-fx-background-color: #b8e0b3; -fx-border-color: #66ccff; -fx-border-width: 2;");
                    }
                });
                
                paletteCell.setOnMouseExited(e -> {
                    if (selectedTileView != tileView) {
                        paletteCell.setStyle("-fx-background-color: #9ed199; -fx-border-color: #666; -fx-border-width: 1;");
                    }
                });

                final int r = row;
                final int c = col;
                
                // Make the entire pane clickable, not just the image
                paletteCell.setOnMouseClicked(e -> {
                    selectTile(r, c, tileView, tileImage, reader, paletteCell);
                    e.consume(); // Prevent event bubbling
                });
                
                // Also keep the ImageView clickable to ensure we catch all clicks
                tileView.setOnMouseClicked(e -> {
                    selectTile(r, c, tileView, tileImage, reader, paletteCell);
                    e.consume(); // Prevent event bubbling
                });

                // Mark the default grass tile (row 1, col 1)
                if (row == 1 && col == 1) {
                    defaultGrassTile = tileView;
                }

                paletteGrid.add(paletteCell, col, row);
            }
        }
    }

    // Extract tile selection logic to a separate method for reusability
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

        // Reset previous selection styling
        if (selectedTileView != null) {
            Pane previousParent = (Pane) selectedTileView.getParent();
            previousParent.setStyle("-fx-background-color: #9ed199; -fx-border-color: #666; -fx-border-width: 1;");
        }

        // Apply selection styling
        paletteCell.setStyle("-fx-background-color: #9ed199; -fx-border-color: #ff9900; -fx-border-width: 3;");
        selectedTileView = tileView;
        
        // Add a subtle animation or visual feedback for better responsiveness
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(
            javafx.util.Duration.millis(100), tileView);
        ft.setFromValue(0.8);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.play();
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
     * Places a tile from the palette onto the map
     */
    private void placeTile(int row, int col) {
        // Handle placing group tiles (e.g., castle)
        if (selectedIsGroup) {
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
            // Handle placing single tiles
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
}

