package com.example.controllers;

import com.example.main.Main;
import com.example.storage_manager.MapStorageManager;
import com.example.utils.StyleManager;
import com.example.utils.MapEditorUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * Controller for the load game page. It displays saved games and provides functionality
 * to load or delete them, or return to the home page.
 */
public class GameConfigController implements Initializable {

	// Button styling constants
	private static final String BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
			"-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; " +
			"-fx-font-size: 14px; -fx-font-weight: bold; " +
			"-fx-border-color: #8a673c; -fx-border-width: 2; " +
			"-fx-border-radius: 5; -fx-background-radius: 5;";

	private static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
			"-fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; " +
			"-fx-font-size: 14px; -fx-font-weight: bold; " +
			"-fx-border-color: #a07748; -fx-border-width: 2; " +
			"-fx-border-radius: 5; -fx-background-radius: 5; " +
			"-fx-cursor: hand;";

	private static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
			"-fx-text-fill: #d9c9a0; -fx-font-family: 'Segoe UI'; " +
			"-fx-font-size: 14px; -fx-font-weight: bold; " +
			"-fx-border-color: #8a673c; -fx-border-width: 2; " +
			"-fx-border-radius: 5; -fx-background-radius: 5;";

	// Title bar constants
	private static final String TITLE_BAR_STYLE = "-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 0 0 1 0;";
	private static final String BUTTON_TRANSPARENT_STYLE = "-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold;";
	private static final String CLOSE_BUTTON_HOVER = "-fx-background-color: #a05454; -fx-text-fill: #f5ead9;";

	@FXML
	private ListView<String> savedMapsListView;

	@FXML
	private Button loadBtn;

	@FXML
	private Button homeBtn;

	@FXML
	private TextField goldInput;

	private int startingGold = 1000;


	private final ObservableList<String> savedMaps = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		goldInput.setText("1000");
		savedMaps.setAll(MapStorageManager.listAvailableMaps());
		savedMapsListView.setItems(savedMaps);

		// Select first item by default
		if (!savedMaps.isEmpty()) {
			savedMapsListView.getSelectionModel().selectFirst();
		}

		// Disable buttons if no selection
		loadBtn.setDisable(savedMaps.isEmpty());

		// Add listener for selection changes
		savedMapsListView.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					boolean noSelection = (newValue == null);
					loadBtn.setDisable(noSelection);
				}
		);

		// Setup buttons with StyleManager
		StyleManager.setupButtonWithCustomCursor(loadBtn);
		StyleManager.setupButtonWithCustomCursor(homeBtn);

		// Style list cells
		setupCustomListView();

		// Set custom cursor for text field
		goldInput.setCursor(StyleManager.getCustomCursor());
	}

	private void setupCustomListView() {
		savedMapsListView.setCellFactory(list -> new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("-fx-background-color: transparent;");
				} else {
					setText(item);
					setStyle("-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");

					// Add hover effect
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

					// Set custom cursor
					setCursor(StyleManager.getCustomCursor());
				}
			}
		});
	}

	private void setupButtonEffects(Button button) {
		// Apply initial style
		button.setStyle(BUTTON_NORMAL_STYLE);

		// Hover effect
		button.setOnMouseEntered(e -> button.setStyle(BUTTON_HOVER_STYLE));
		button.setOnMouseExited(e -> button.setStyle(BUTTON_NORMAL_STYLE));

		// Click effect
		button.setOnMousePressed(e -> {
			button.setStyle(BUTTON_PRESSED_STYLE);
			animateButtonClick(button);
		});

		button.setOnMouseReleased(e -> button.setStyle(BUTTON_HOVER_STYLE));
	}

	private void animateButtonClick(Button button) {
		ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
		st.setFromX(1.0);
		st.setFromY(1.0);
		st.setToX(0.95);
		st.setToY(0.95);
		st.setCycleCount(2);
		st.setAutoReverse(true);
		st.play();
	}

	@FXML
	private void loadSelectedMap() {
		String selectedMapName = savedMapsListView.getSelectionModel().getSelectedItem();
		if (selectedMapName == null) {
			MapEditorUtils.showInfoAlert(
				"No Selection",
				"Please select a map to load.",
				this
			);
			return;
		}

		// Show confirmation dialog
		boolean confirmed = MapEditorUtils.showCustomConfirmDialog(
			"Load Map",
			"Are you sure you want to load: " + selectedMapName + "?\nAny unsaved progress will be lost.",
			this
		);

		if (confirmed) {
			try {
				startingGold = Integer.parseInt(goldInput.getText().trim());
			} catch (NumberFormatException e) {
				MapEditorUtils.showErrorAlert(
					"Invalid Gold",
					"Invalid Input",
					"Please enter a valid number for starting gold.",
					this
				);
				return;
			}

			Main.getViewManager().switchToGameScreen(selectedMapName, startingGold);
		}
	}

	@FXML
	private void goToHomePage() {
		Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
	}
}