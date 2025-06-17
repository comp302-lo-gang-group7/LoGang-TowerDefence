package com.example.map;


import java.util.Set;


/**
 * Class TileEnum
 */
public enum TileEnum {
    TOP_LEFT_PATH_CORNER(0, 0, 0),
    DOWN_CURVING_PATH(0, 1, 1),
    TOP_RIGHT_PATH_CORNER(0, 2, 2),
    VERTICAL_UPPER_PATH_END(0, 3, 3),

    RIGHT_CURVING_PATH(1, 0 ,4),
    GRASS(1, 1, 5),
    LEFT_CURVING_PATH(1, 2, 6),
    VERTICAL_PATH(1, 3, 7),

    BOTTOM_LEFT_PATH_CORNER(2, 0, 8),
    UP_CURVING_PATH(2, 1, 9),
    BOTTOM_RIGHT_PATH_CORNER(2, 2, 10),
    VERTICAL_LOWER_PATH_END(2, 3, 11),

    HORIZONTAL_LEFT_PATH_END(3, 0, 12),
    HORIZONTAL_PATH(3, 1, 13),
    HORIZONTAL_RIGHT_PATH_END(3, 2, 14),
    EMPTY_TOWER_TILE(3, 3, 15),

    TREE_1(4, 0, 16),
    TREE_2(4, 1, 17),
    TREE_3(4, 2, 18),
    BOULDER_1(4, 3, 19),

    ARTILLERY_TOWER(5, 0, 20),
    MAGE_TOWER(5, 1, 21),
    TAVERN(5, 2, 22),
    BOULDER_2(5, 3, 23),

    CASTLE_TOP_LEFT(6, 0, 24),
    CASTLE_TOP_RIGHT(6, 1, 25),
    ARCHERY_TOWER(6, 2, 26),
    WATER_WELL(6, 3, 27),

    CASTLE_BOTTOM_LEFT(7, 0, 28),
    CASTLE_BOTTOM_RIGHT(7, 1, 29),
    BARRACKS(7, 2, 30),
    WOOD_LOGS(7, 3, 31);


    private final int row;
    private final int col;
    private final int flatIndex;


    public static final Set<TileEnum> PATH_TILES = Set.of(
            TileEnum.TOP_LEFT_PATH_CORNER,
            TileEnum.DOWN_CURVING_PATH,
            TileEnum.TOP_RIGHT_PATH_CORNER,
            TileEnum.VERTICAL_UPPER_PATH_END,
            TileEnum.RIGHT_CURVING_PATH,
            TileEnum.LEFT_CURVING_PATH,
            TileEnum.VERTICAL_PATH,
            TileEnum.BOTTOM_LEFT_PATH_CORNER,
            TileEnum.UP_CURVING_PATH,
            TileEnum.BOTTOM_RIGHT_PATH_CORNER,
            TileEnum.VERTICAL_LOWER_PATH_END,
            TileEnum.HORIZONTAL_LEFT_PATH_END,
            TileEnum.HORIZONTAL_PATH,
            TileEnum.HORIZONTAL_RIGHT_PATH_END
    );


    public static final Set<TileEnum> CASTLE_TILES = Set.of(
            TileEnum.CASTLE_TOP_LEFT,
            TileEnum.CASTLE_TOP_RIGHT,
            TileEnum.CASTLE_BOTTOM_LEFT,
            TileEnum.CASTLE_BOTTOM_RIGHT
    );

    TileEnum(int row, int col, int flatIndex) {
        this.row = row;
        this.col = col;
        this.flatIndex = flatIndex;
    }

    /**
     * TODO
     */
    public int getRow() { return row; }
    /**
     * TODO
     */
    public int getCol() { return col; }
    /**
     * TODO
     */
    public int getFlatIndex() { return flatIndex; }

    /**
     * TODO
     */
    public static TileEnum fromFlatIndex(int index) {
        for (TileEnum tile : values()) {
            if (tile.flatIndex == index) return tile;
        }
        throw new IllegalArgumentException("Invalid flat index: " + index);
    }

    /**
     * TODO
     */
    public static TileEnum fromRowCol(int row, int col) {
        for (TileEnum tile : values()) {
            if (tile.row == row && tile.col == col) return tile;
        }
        throw new IllegalArgumentException("No tile found for row: " + row + ", col: " + col);
    }
}
