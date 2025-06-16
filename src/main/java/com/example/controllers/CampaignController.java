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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        }

        for (int i = 0; i < levels.size(); i++) {
            CampaignLevel lvl = levels.get(i);
            Button btn = new Button();
            btn.setStyle("-fx-background-color: transparent;");
            if (lvl.icon != null) {
                Image img = new Image(getClass().getResourceAsStream("/com/example/assets/" + lvl.icon));
                ImageView iv = new ImageView(img);
                iv.setFitWidth(40);
                iv.setFitHeight(40);
                btn.setGraphic(iv);
            } else {
                btn.setText(lvl.name);
            }
            int iconSize = 40;
            btn.setPrefSize(iconSize, iconSize);
            btn.setLayoutX(lvl.col * 64 + 32 - iconSize / 2.0);
            btn.setLayoutY(lvl.row * 64 + 32 - iconSize / 2.0);

            boolean unlocked = i == 0 || progress.containsKey(levels.get(i - 1).levelFile);
            btn.setDisable(!unlocked);

            btn.setOnAction(e -> showLevelDialog(lvl));
            mapContainer.getChildren().add(btn);
        }
    }

    @FXML
    private void handleBack() {
        goToHomePage();
    }

    private void showLevelDialog(CampaignLevel lvl) {
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

        LevelProgress lp = progress.get(lvl.levelFile);
        Text prog = new Text();
        if (lp != null && (lp.stars > 0 || lp.time > 0)) {
            prog.setText(String.format("Best: %d star(s), %ds", lp.stars, lp.time));
        }
        prog.setStyle("-fx-fill: #d9c9a0; -fx-font-size: 12px;");

        Button start = new Button("Start");
        start.setOnAction(ev -> {
            dialog.close();
            try {
                LevelConfig cfg = LevelStorageManager.loadLevel(lvl.levelFile);
                Main.getViewManager().switchToGameScreen(cfg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button close = new Button("Close");
        close.setOnAction(ev -> dialog.close());

        root.getChildren().addAll(name, desc, prog, start, close);

        Scene scene = new Scene(root);
        dialog.setScene(scene);
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