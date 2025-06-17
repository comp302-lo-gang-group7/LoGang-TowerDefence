package com.example.controllers;

import com.example.main.Main;
import com.example.storage_manager.SettingsManager;
import com.example.storage_manager.SettingsManager.Settings;
import com.example.ui.AudioManager;
import com.example.ui.DialogUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Class SettingsController
 */
public class SettingsController extends Controller implements Initializable {

    @FXML private Slider musicVolumeSlider;
    @FXML private Slider sfxVolumeSlider;
    @FXML private Label musicVolumeLabel;
    @FXML private Label sfxVolumeLabel;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private ComboBox<String> gameSpeedCombo;
    @FXML private Button saveBtn;
    @FXML private Button resetBtn;
    @FXML private Button homeBtn;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    /**
     * TODO
     */
    private void onHeaderBarPressed(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    /**
     * TODO
     */
    private void onHeaderBarDragged(MouseEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @Override
    /**
     * TODO
     */
    public void initialize(URL location, ResourceBundle resources) {

        setupComboBoxes();


        setupSliders();

        loadSavedSettings();


        applyButtonStyle(saveBtn);
        applyButtonStyle(resetBtn);
        applyButtonStyle(homeBtn);
    }

    /**
     * TODO
     */
    private void setupComboBoxes() {

        difficultyCombo.getItems().addAll("Easy", "Normal", "Hard", "Expert");
        difficultyCombo.setValue("Normal");
        styleComboBox(difficultyCombo);


        gameSpeedCombo.getItems().addAll("Slow", "Normal", "Fast", "Turbo");
        gameSpeedCombo.setValue("Normal");
        styleComboBox(gameSpeedCombo);
    }

    /**
     * TODO
     */
    private void styleComboBox(ComboBox<String> comboBox) {

        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            /**
             * TODO
             */
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    /**
                     * TODO
                     */
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            setText(item);
                            setTextFill(Color.web("#e8d9b5"));
                            setStyle("-fx-background-color: #5d4228; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;");
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });


        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            /**
             * TODO
             */
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && !empty) {
                    setText(item);
                    setTextFill(Color.web("#e8d9b5"));
                    setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px;");
                } else {
                    setText(null);
                }
            }
        });
    }

    /**
     * TODO
     */
    private void setupSliders() {

        musicVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            musicVolumeLabel.setText(value + "%");
        });


        sfxVolumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            sfxVolumeLabel.setText(value + "%");
        });
    }

    /**
     * TODO
     */
    private void applyButtonStyle(Button button) {
        String buttonCss =
            "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #e8d9b5; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-border-color: linear-gradient(#a07748, #8a673c); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, 0, 2);";

        String hoverCss =
            "-fx-background-color: linear-gradient(#94704c, #705236); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #f5ead9; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-border-color: linear-gradient(#c6965f, #b88d5a); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 6, 0.0, 0, 2);";

        String pressedCss =
            "-fx-background-color: linear-gradient(#5d4228, #4e3822); " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #d9c9a0; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-padding: 9 15 7 15; " +
            "-fx-border-color: #7d5a3c; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8; " +
            "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.4), 4, 0.0, 0, 1);";


        button.setStyle(buttonCss);


        button.setOnMouseEntered(e -> {
            button.setStyle(hoverCss);
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle(buttonCss);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });


        button.setOnMousePressed(e -> {
            button.setStyle(pressedCss);
            button.setScaleX(1.02);
            button.setScaleY(1.02);
        });

        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                button.setStyle(hoverCss);
                button.setScaleX(1.05);
                button.setScaleY(1.05);
            } else {
                button.setStyle(buttonCss);
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            }
        });
    }

    @FXML
    /**
     * TODO
     */
    public void saveSettings() {
        Settings settings = new Settings();
        settings.musicVolume = (int) musicVolumeSlider.getValue();
        settings.sfxVolume = (int) sfxVolumeSlider.getValue();
        settings.difficulty = difficultyCombo.getValue();
        settings.gameSpeed = gameSpeedCombo.getValue();


        settings.showHints = true;
        settings.autoSave = true;
        settings.fullscreen = false;
        settings.showFps = false;

        SettingsManager.save(settings);
        AudioManager.reloadSettings();
        showMessage("Settings saved successfully!");
    }

    /**
     * TODO
     */
    private void loadSavedSettings() {
        Settings settings = SettingsManager.load();
        musicVolumeSlider.setValue(settings.musicVolume);
        musicVolumeLabel.setText(settings.musicVolume + "%");
        sfxVolumeSlider.setValue(settings.sfxVolume);
        sfxVolumeLabel.setText(settings.sfxVolume + "%");
        difficultyCombo.setValue(settings.difficulty);
        gameSpeedCombo.setValue(settings.gameSpeed);
    }

    @FXML
    /**
     * TODO
     */
    public void resetSettings() {

        musicVolumeSlider.setValue(75);
        sfxVolumeSlider.setValue(100);
        difficultyCombo.setValue("Normal");
        gameSpeedCombo.setValue("Normal");

        Settings settings = new Settings();
        settings.showHints = true;
        settings.autoSave = true;
        settings.fullscreen = false;
        settings.showFps = false;

        SettingsManager.save(settings);
        AudioManager.reloadSettings();
        showMessage("Settings reset to defaults.");
    }

    /**
     * TODO
     */
    private void showMessage(String message) {
        DialogUtil.showWoodenAlert("Settings", message);
    }
}