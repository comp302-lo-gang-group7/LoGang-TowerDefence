package com.example.utils;

import com.example.map.TileEnum;
import com.example.map.TileView;
import com.example.ui.ImageLoader;
import javafx.scene.image.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * Class TileRenderer
 */
public class TileRenderer {
    private final Image tileset;
    private final int tileSize;
    private final PixelReader pr;
    private final Image grassUnderlay;
    /**
     * TODO
     */
    private final Map<TileEnum, Image> cache = new EnumMap<>(TileEnum.class);

    /**
     * TODO
     */
    public TileRenderer(String tilesetResource, int tileSize) {
        this.tileset  = ImageLoader.getImage(tilesetResource);
        this.tileSize = tileSize;
        this.pr       = tileset.getPixelReader();

        int gx = TileEnum.GRASS.getCol() * tileSize;
        int gy = TileEnum.GRASS.getRow() * tileSize;
        this.grassUnderlay = new WritableImage(pr, gx, gy, tileSize, tileSize);
    }

    /**
     * TODO
     */
    public Image getComposite(TileEnum type) {
        return cache.computeIfAbsent(type, this::makeComposite);
    }

    /**
     * TODO
     */
    public TileView createTileView(TileEnum type) {
        TileView tv = new TileView(getComposite(type), type);
        tv.setFitWidth(tileSize);
        tv.setFitHeight(tileSize);
        tv.setPreserveRatio(false);
        return tv;
    }

    /**
     * TODO
     */
    public PixelReader getTilesetReader() {
        return tileset.getPixelReader();
    }

    /**
     * TODO
     */
    public Image getRawTileImage(int col, int row) {
        return new WritableImage(pr, col * tileSize, row * tileSize, tileSize, tileSize);
    }

    /**
     * TODO
     */
    public Image getRawTileRegion(int colStart, int rowStart, int colCount, int rowCount) {
        return new WritableImage(pr, colStart * tileSize, rowStart * tileSize, colCount * tileSize, rowCount * tileSize);
    }

    /**
     * TODO
     */
    private Image makeComposite(TileEnum type) {
        WritableImage out = new WritableImage(tileSize, tileSize);
        PixelWriter pw    = out.getPixelWriter();
        PixelReader gpr   = grassUnderlay.getPixelReader();
        int sx = type.getCol() * tileSize;
        int sy = type.getRow() * tileSize;

        for (int y = 0; y < tileSize; y++) {
            for (int x = 0; x < tileSize; x++) {
                int bg = gpr.getArgb(x, y);
                int fg = pr.getArgb(sx + x, sy + y);
                int a  = (fg >>> 24) & 0xFF;

                if (a == 0) {
                    pw.setArgb(x, y, bg);
                } else if (a == 255) {
                    pw.setArgb(x, y, fg);
                } else {
                    double alpha = a / 255.0;
                    int fgR = (fg >> 16) & 0xFF, fgG = (fg >> 8) & 0xFF, fgB = fg & 0xFF;
                    int bgR = (bg >> 16) & 0xFF, bgG = (bg >> 8) & 0xFF, bgB = bg & 0xFF;

                    int r = (int)(fgR * alpha + bgR * (1 - alpha));
                    int g = (int)(fgG * alpha + bgG * (1 - alpha));
                    int b = (int)(fgB * alpha + bgB * (1 - alpha));

                    int outArgb = (0xFF << 24) | (r << 16) | (g << 8) | b;
                    pw.setArgb(x, y, outArgb);
                }
            }
        }

        return out;
    }
}
