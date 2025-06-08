package com.example.controllers;

import com.example.main.Main;
import com.example.storage_manager.MapStorageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CustomGameController extends Controller implements Initializable {

    private static class WaveRow {
        HBox root;
        TextField goblinsField;
        TextField warriorsField;
        WaveRow(int index) {
            Text label = new Text("Wave " + index + ":");
            goblinsField = new TextField("1");
            goblinsField.setPrefWidth(40);
            warriorsField = new TextField("0");
            warriorsField.setPrefWidth(40);
            root = new HBox(10, label, new Text("Goblins"), goblinsField, new Text("Warriors"), warriorsField);
            root.setPadding(new Insets(0,0,0,0));
        }
    }

    @FXML private ListView<String> savedMapsListView;
    @FXML private TextField goldInput;
    @FXML private VBox wavesBox;
    @FXML private Button startBtn;

    private final ObservableList<String> savedMaps = FXCollections.observableArrayList();
    private final List<WaveRow> waveRows = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        goldInput.setText("1000");
        savedMaps.setAll(MapStorageManager.listAvailableMaps());
        savedMapsListView.setItems(savedMaps);
        if (!savedMaps.isEmpty()) {
            savedMapsListView.getSelectionModel().selectFirst();
        }
        addWave();
    }

    @FXML
    public void addWave() {
        if (waveRows.size() >= 10) return;
        WaveRow row = new WaveRow(waveRows.size()+1);
        waveRows.add(row);
        wavesBox.getChildren().add(row.root);
    }

    @FXML
    public void removeWave() {
        if (waveRows.size() <= 1) return;
        WaveRow row = waveRows.remove(waveRows.size()-1);
        wavesBox.getChildren().remove(row.root);
    }

    @FXML
    public void startGame() {
        String mapName = savedMapsListView.getSelectionModel().getSelectedItem();
        if (mapName == null) return;
        int gold = 1000;
        try { gold = Integer.parseInt(goldInput.getText().trim()); } catch (NumberFormatException ignored) {}
        List<int[]> waves = new ArrayList<>();
        for (WaveRow row : waveRows) {
            int g=0,w=0;
            try { g = Integer.parseInt(row.goblinsField.getText().trim()); } catch (NumberFormatException ignored) {}
            try { w = Integer.parseInt(row.warriorsField.getText().trim()); } catch (NumberFormatException ignored) {}
            waves.add(new int[]{g,w});
        }
        Main.getViewManager().switchToGameScreen(mapName, gold, waves);
    }
}
