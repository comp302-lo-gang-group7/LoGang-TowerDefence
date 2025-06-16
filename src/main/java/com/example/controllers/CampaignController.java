package com.example.controllers;

import com.example.config.CampaignLevel;
import com.example.config.LevelConfig;
import com.example.main.Main;
import com.example.storage_manager.CampaignStorageManager;
import com.example.storage_manager.LevelStorageManager;
import com.example.storage_manager.MapStorageManager;
import com.example.storage_manager.ProgressStorageManager;
import com.example.storage_manager.ProgressStorageManager.LevelProgress;
import com.example.map.TileView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/** Controller for the campaign map screen. */
public class CampaignController extends Controller implements Initializable {
    @FXML private ScrollPane scrollPane;
    @FXML private Pane mapContainer;
    @FXML private Pane bottomMap;
    @FXML private Pane topMap;
    @FXML private Button backBtn;

    private List<CampaignLevel> levels;
    private Map<String, LevelProgress> progress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        levels = CampaignStorageManager.loadCampaign();
        progress = ProgressStorageManager.loadProgress();

        // load background maps
        int tileSize = 64;
        int bottomHeight = renderMap("campaign-bottom", bottomMap, tileSize);
        int topHeight = renderMap("campaign-top", topMap, tileSize);
        topMap.setLayoutY(bottomHeight);
        mapContainer.setPrefWidth(bottomMap.getPrefWidth());
        mapContainer.setPrefHeight(bottomHeight + topHeight);

        boolean allComplete = true;
        for (CampaignLevel l : levels) {
            if (!progress.containsKey(l.levelFile)) {
                allComplete = false;
                break;
            }
        }
        if (!allComplete) {
            topMap.setVisible(false);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        } else {
            scrollPane.setOnScroll(ev -> {
                if (ev.getDeltaY() < 0) {
                    scrollPane.setVvalue(1.0);
                } else {
                    scrollPane.setVvalue(0.0);
                }
                ev.consume();
            });
        }

        for (int i = 0; i < levels.size(); i++) {
            CampaignLevel lvl = levels.get(i);
            Button btn = new Button(String.valueOf(i + 1));

            int iconSize = 40;
            btn.setPrefSize(iconSize, iconSize);
            btn.setStyle("-fx-background-color: #6b4c2e; " +
                    "-fx-background-radius: 20em; " +
                    "-fx-text-fill: #ffd700; -fx-font-weight: bold;");
            btn.setPrefSize(iconSize, iconSize);
            btn.setLayoutX(lvl.col * 64 + 32 - iconSize / 2.0);
            btn.setLayoutY(lvl.row * 64 + 32 - iconSize / 2.0);

            boolean unlocked = i == 0 || progress.containsKey(levels.get(i - 1).levelFile);
            btn.setDisable(!unlocked);

            btn.setOnAction(e -> showLevelDialog(lvl, btn));
            mapContainer.getChildren().add(btn);
        }
    }

    @FXML
    private void handleBack() {
        goToHomePage();
    }

    private void showLevelDialog(CampaignLevel lvl, Button sourceBtn) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width:2;");

        Text name = new Text(lvl.name);
        name.setStyle("-fx-fill: #e8d9b5; -fx-font-size: 20px; -fx-font-weight: bold;");
        Text desc = new Text(lvl.description == null ? "" : lvl.description);
        desc.setWrappingWidth(260);
        desc.setStyle("-fx-fill: #e8d9b5;");

        LevelConfig cfg = null;
        try {
            cfg = LevelStorageManager.loadLevel(lvl.levelFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        LevelProgress lp = progress.get(lvl.levelFile);
        HBox starBox = new HBox(4);
        if (lp != null && lp.stars > 0) {
            Image starImg = new Image(getClass().getResourceAsStream("/com/example/assets/buttons/Star_Button.png"));
            for (int s = 0; s < lp.stars; s++) {
                ImageView iv = new ImageView(starImg);
                iv.setFitWidth(20);
                iv.setFitHeight(20);
                starBox.getChildren().add(iv);
            }
        }

        VBox info = new VBox(2);
        if (cfg != null) {
            Text stats = new Text(String.format("Gold: %d  Lives: %d", cfg.getStartingGold(), cfg.getLives()));
            stats.setStyle("-fx-fill: #d9c9a0;");
            info.getChildren().add(stats);
            int waveNum = cfg.getWaves() != null ? cfg.getWaves().size() : 0;
            Text waveCount = new Text("Waves: " + waveNum);
            waveCount.setStyle("-fx-fill: #d9c9a0;");
            info.getChildren().add(waveCount);
            if (cfg.getWaves() != null) {
                for (int w = 0; w < cfg.getWaves().size(); w++) {
                    var g = cfg.getWaves().get(w).group;
                    Text t = new Text(String.format("Wave %d: goblins %d, warriors %d", w + 1, g.goblins, g.warriors));
                    t.setStyle("-fx-fill: #d9c9a0; -fx-font-size: 11px;");
                    info.getChildren().add(t);
                }
            }
        }

        Button start = new Button("Start");
        start.setOnAction(ev -> {
            dialog.close();
            try {
                LevelConfig lconfig = LevelStorageManager.loadLevel(lvl.levelFile);
                Main.getViewManager().switchToGameScreen(lconfig);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button close = new Button("Close");
        close.setOnAction(ev -> dialog.close());

        root.getChildren().addAll(name, desc, starBox, info, start, close);

        Scene scene = new Scene(root);
        dialog.setScene(scene);

        dialog.setOnShown(ev -> {
            Bounds b = sourceBtn.localToScreen(sourceBtn.getBoundsInLocal());
            dialog.setX(b.getMinX() + b.getWidth() / 2 - dialog.getWidth() / 2);
            dialog.setY(b.getMinY() - dialog.getHeight() - 10);
        });

        dialog.showAndWait();
    }

    private int renderMap(String mapName, Pane target, int tileSize) {
        try {
            TileView[][] tiles = MapStorageManager.loadMap(mapName);
            int rows = tiles.length;
            int cols = tiles[0].length;
            target.setPrefSize(cols * tileSize, rows * tileSize);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    TileView tv = tiles[r][c];
                    tv.setLayoutX(c * tileSize);
                    tv.setLayoutY(r * tileSize);
                    target.getChildren().add(tv);
                }
            }
            return rows * tileSize;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}