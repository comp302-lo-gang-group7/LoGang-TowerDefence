package com.example.controllers;

import com.example.main.Main;
import com.example.storage_manager.MapStorageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Class CustomGameController
 */
public class CustomGameController extends Controller implements Initializable {


    /**
     * TODO
     */
    private static final String BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                                                     "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; " +
                                                     "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                     "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                     "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * TODO
     */
    private static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
                                                    "-fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; " +
                                                    "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                    "-fx-border-color: #a07748; -fx-border-width: 2; " +
                                                    "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * TODO
     */
    private static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                                      "-fx-text-fill: #d9c9a0; -fx-font-family: 'Segoe UI'; " +
                                                      "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                      "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                      "-fx-border-radius: 5; -fx-background-radius: 5;";

    private static class WaveRow {
        HBox root;
        TextField goblinsField;
        TextField warriorsField;
        WaveRow(int index) {
            Text label = new Text("Wave " + index + ":");
            label.setFill(Color.web("#e8d9b5"));
            label.setFont(Font.font("Segoe UI", 14));

            goblinsField = new TextField("1");
            goblinsField.setPrefWidth(40);
            goblinsField.setStyle("-fx-background-color: #e8d9b5; -fx-text-fill: #4e331f; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");

            warriorsField = new TextField("0");
            warriorsField.setPrefWidth(40);
            warriorsField.setStyle("-fx-background-color: #e8d9b5; -fx-text-fill: #4e331f; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");

            Text goblinsText = new Text("Goblins");
            Text warriorsText = new Text("Warriors");

            goblinsText.setFill(Color.web("#e8d9b5"));
            goblinsText.setFont(Font.font("Segoe UI", 14));
            warriorsText.setFill(Color.web("#e8d9b5"));
            warriorsText.setFont(Font.font("Segoe UI", 14));

            root = new HBox(10, label, goblinsText, goblinsField, warriorsText, warriorsField);
            root.setPadding(new Insets(0,0,0,0));
            root.setAlignment(Pos.CENTER_LEFT);
        }
    }

    @FXML private ListView<String> savedMapsListView;
    @FXML private TextField goldInput;
    @FXML private ScrollPane wavesScrollPane;
    @FXML private VBox wavesBox;
    @FXML private Button homeBtn;
    @FXML private Button startBtn;
    @FXML private Button addWaveBtn;
    @FXML private Button removeWaveBtn;

    /**
     * TODO
     */
    private final ObservableList<String> savedMaps = FXCollections.observableArrayList();
    /**
     * TODO
     */
    private final List<WaveRow> waveRows = new ArrayList<>();

    @Override
    /**
     * TODO
     */
    public void initialize(URL location, ResourceBundle resources) {

        try {
            Image customCursorImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/assets/ui/01.png")));

            savedMapsListView.setCursor(Cursor.DEFAULT);
            goldInput.setCursor(Cursor.DEFAULT);
            wavesBox.setCursor(Cursor.DEFAULT);
        } catch (Exception e) {
            System.err.println("Could not load custom cursor: " + e.getMessage());
        }





        wavesBox.setStyle("-fx-background-color: #6b4c2e; -fx-padding: 10;");

        goldInput.setText("1000");
        savedMaps.setAll(MapStorageManager.listAvailableMaps());
        savedMapsListView.setItems(savedMaps);

        if (!savedMaps.isEmpty()) {
            savedMapsListView.getSelectionModel().selectFirst();
        }

        setupCustomListView();
        setupButtonEffects(homeBtn);
        setupButtonEffects(startBtn);
        setupButtonEffects(addWaveBtn);
        setupButtonEffects(removeWaveBtn);

        addWave();
    }

    /**
     * TODO
     */
    private void setupCustomListView() {
        savedMapsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            /**
             * TODO
             */
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");


                    setOnMouseEntered(e -> {
                        if (!isSelected()) {
                            setStyle("-fx-background-color: #604631; -fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                        }
                    });

                    setOnMouseExited(e -> {
                        if (!isSelected()) {
                            setStyle("-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                        }
                    });
                }
            }
        });


        savedMapsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            savedMapsListView.refresh();
        });
    }

    /**
     * TODO
     */
    private void setupButtonEffects(Button button) {

        button.setStyle(BUTTON_NORMAL_STYLE);


        button.setOnMouseEntered(e -> {
            button.setStyle(BUTTON_HOVER_STYLE);
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle(BUTTON_NORMAL_STYLE);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });


        button.setOnMousePressed(e -> {
            button.setStyle(BUTTON_PRESSED_STYLE);
            animateButtonClick(button);
        });

        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                button.setStyle(BUTTON_HOVER_STYLE);
                button.setScaleX(1.05);
                button.setScaleY(1.05);
            } else {
                button.setStyle(BUTTON_NORMAL_STYLE);
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            }
        });
    }

    /**
     * TODO
     */
    private void animateButtonClick(Button button) {
        button.setScaleX(0.95);
        button.setScaleY(0.95);
    }

    @FXML
    /**
     * TODO
     */
    public void addWave() {
        if (waveRows.size() >= 10) return;
        WaveRow row = new WaveRow(waveRows.size()+1);
        waveRows.add(row);
        wavesBox.getChildren().add(row.root);
    }

    @FXML
    /**
     * TODO
     */
    public void removeWave() {
        if (waveRows.size() <= 1) return;
        WaveRow row = waveRows.remove(waveRows.size()-1);
        wavesBox.getChildren().remove(row.root);
    }

    @FXML
    /**
     * TODO
     */
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
        Main.getViewManager().resizeWindow(1024,576);
        Main.getViewManager().switchToGameScreen(mapName, gold, waves);
    }

    @FXML
    /**
     * TODO
     */
    public void goToHomePage() {
        Main.getViewManager().resizeWindow(800, 600);
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }
}