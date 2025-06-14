package com.example.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.main.Main;
import com.example.storage_manager.MapStorageManager;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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

	private static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#548e4f, #3b6e2c); " +
			"-fx-text-fill: #f5ffe9; " +
			"-fx-font-family: 'Segoe UI'; " +
			"-fx-font-size: 14px; " +
			"-fx-font-weight: bold; " +
			"-fx-border-color: #6a894d; " +
			"-fx-border-width: 2; " +
			"-fx-border-radius: 5; " +
			"-fx-background-radius: 5; ";

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

	// To track if dialog result was confirmed
	private boolean dialogConfirmed = false;

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

		// Setup button hover and click effects
		setupButtonEffects(loadBtn);
		setupButtonEffects(homeBtn);

		// Style list cells
		setupCustomListView();
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
	private void loadSelectedMap()
	{
		String selectedMapName = savedMapsListView.getSelectionModel().getSelectedItem();
		if (selectedMapName != null) {
			// Show custom confirmation dialog
			boolean confirmed = showCustomConfirmDialog(
					"Load Map",
					"Are you sure you want to load: " + selectedMapName + "?\n"
			);

			if (confirmed) {
				try {
					startingGold = Integer.parseInt(goldInput.getText().trim());
				} catch (NumberFormatException ignored) { }

				Main.getViewManager().switchToGameScreen(selectedMapName, startingGold);
			}
		}
	}

	@FXML
	private void goToHomePage() {
		Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
	}

	/**
	 * Shows a fully custom styled confirmation dialog with our custom title bar
	 */
	private boolean showCustomConfirmDialog(String title, String content) {
		// Create a new stage for our custom dialog
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		dialogStage.initStyle(StageStyle.UNDECORATED);
		dialogStage.setTitle(title);

		// Reset dialog result
		dialogConfirmed = false;

		// Create the custom title bar
		HBox titleBar = createTitleBar(dialogStage, title);

		// Create content area
		VBox contentArea = new VBox(10);
		contentArea.setAlignment(Pos.CENTER);
		contentArea.setPadding(new Insets(20, 20, 20, 20));
		contentArea.setStyle("-fx-background-color: #5d4228;");

		// Create content text
		Text contentText = new Text(content);
		contentText.setFont(Font.font("Segoe UI", 14));
		contentText.setFill(Color.web("#e8d9b5"));
		contentText.setWrappingWidth(350);

		// Create button area
		HBox buttonBox = new HBox(20);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 10, 0));
		buttonBox.setStyle("-fx-background-color: #5d4228;"); // Ensure the button area has the same background

		// Create OK button
		Button okButton = new Button("OK");
		okButton.setPrefWidth(100);
		okButton.setPrefHeight(30);
		okButton.setStyle(BUTTON_NORMAL_STYLE);

		// OK button hover effect
		okButton.setOnMouseEntered(e -> okButton.setStyle(BUTTON_HOVER_STYLE));
		okButton.setOnMouseExited(e -> okButton.setStyle(BUTTON_NORMAL_STYLE));

		// OK button click action
		okButton.setOnAction(e -> {
			dialogConfirmed = true;
			dialogStage.close();
		});

		// Create Cancel button
		Button cancelButton = new Button("Cancel");
		cancelButton.setPrefWidth(100);
		cancelButton.setPrefHeight(30);
		cancelButton.setStyle(BUTTON_NORMAL_STYLE);

		// Cancel button hover effect
		cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(BUTTON_HOVER_STYLE));
		cancelButton.setOnMouseExited(e -> cancelButton.setStyle(BUTTON_NORMAL_STYLE));

		// Cancel button click action
		cancelButton.setOnAction(e -> {
			dialogConfirmed = false;
			dialogStage.close();
		});

		// Add buttons to button area
		buttonBox.getChildren().addAll(okButton, cancelButton);

		// Build the content area
		contentArea.getChildren().addAll(contentText, buttonBox);

		// Create main container with title bar and content
		VBox root = new VBox();
		root.getChildren().addAll(titleBar, contentArea);
		root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");

		// Apply drop shadow effect
		root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));

		// Set up the scene with more height to prevent buttons from being cut off
		Scene dialogScene = new Scene(root, 400, 220);
		// Add a style to ensure the scene background is also properly colored
		dialogScene.setFill(Color.web("#5d4228"));
		dialogStage.setScene(dialogScene);

		// Center on parent
		dialogStage.centerOnScreen();

		// Make the dialog draggable by the title bar
		setupDraggableStage(titleBar, dialogStage);

		// Show dialog and wait for it to close
		dialogStage.showAndWait();

		// Return result
		return dialogConfirmed;
	}


	/**
	 * Shows a wood-styled info alert with custom title bar
	 */
	private void showWoodenAlert(String title, String content) {
		// Create a new stage for our custom dialog
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		dialogStage.initStyle(StageStyle.UNDECORATED);
		dialogStage.setTitle(title);

		// Create the custom title bar
		HBox titleBar = createTitleBar(dialogStage, title);

		// Create content area
		VBox contentArea = new VBox(10);
		contentArea.setAlignment(Pos.CENTER);
		contentArea.setPadding(new Insets(20, 20, 20, 20));
		contentArea.setStyle("-fx-background-color: #5d4228;");

		// Create content text
		Text contentText = new Text(content);
		contentText.setFont(Font.font("Segoe UI", 14));
		contentText.setFill(Color.web("#e8d9b5"));
		contentText.setWrappingWidth(350);

		// Create button area
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 10, 0));
		buttonBox.setStyle("-fx-background-color: #5d4228;"); // Ensure the button area has the same background

		// Create OK button
		Button okButton = new Button("OK");
		okButton.setPrefWidth(100);
		okButton.setPrefHeight(30);
		okButton.setStyle(BUTTON_NORMAL_STYLE);

		// OK button hover effect
		okButton.setOnMouseEntered(e -> okButton.setStyle(BUTTON_HOVER_STYLE));
		okButton.setOnMouseExited(e -> okButton.setStyle(BUTTON_NORMAL_STYLE));

		// OK button click action
		okButton.setOnAction(e -> dialogStage.close());

		// Add button to button area
		buttonBox.getChildren().add(okButton);

		// Build the content area
		contentArea.getChildren().addAll(contentText, buttonBox);

		// Create main container with title bar and content
		VBox root = new VBox();
		root.getChildren().addAll(titleBar, contentArea);
		root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");

		// Apply drop shadow effect
		root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));

		// Set up the scene with more height to prevent button from being cut off
		Scene dialogScene = new Scene(root, 400, 200);
		// Ensure scene background is properly colored
		dialogScene.setFill(Color.web("#5d4228"));
		dialogStage.setScene(dialogScene);

		// Center on parent
		dialogStage.centerOnScreen();

		// Make the dialog draggable by the title bar
		setupDraggableStage(titleBar, dialogStage);

		// Show dialog and wait for it to close
		dialogStage.showAndWait();
	}

	/**
	 * Creates a custom title bar for dialogs
	 */
	private HBox createTitleBar(Stage stage, String title) {
		// Create the title bar
		HBox titleBar = new HBox();
		titleBar.setAlignment(Pos.CENTER_RIGHT);
		titleBar.setPrefHeight(25);
		titleBar.setStyle(TITLE_BAR_STYLE);
		titleBar.setPadding(new Insets(0, 5, 0, 10));

		// Title text on the left
		Label titleLabel = new Label(title);
		titleLabel.setTextFill(Color.web("#e8d9b5"));
		titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
		HBox.setHgrow(titleLabel, Priority.ALWAYS);
		titleLabel.setAlignment(Pos.CENTER_LEFT);

		// Close button on the right
		Button closeButton = new Button("×");
		closeButton.setStyle(BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;");
		closeButton.setOnAction(e -> stage.close());

		// Hover effect for close button
		closeButton.setOnMouseEntered(e -> closeButton.setStyle(CLOSE_BUTTON_HOVER + "-fx-font-size: 16px;"));
		closeButton.setOnMouseExited(e -> closeButton.setStyle(BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;"));

		// Add components to title bar
		titleBar.getChildren().addAll(titleLabel, closeButton);

		return titleBar;
	}

	/**
	 * Makes a stage draggable by a node
	 */
	private void setupDraggableStage(HBox titleBar, Stage stage) {
		final double[] xOffset = {0};
		final double[] yOffset = {0};

		titleBar.setOnMousePressed(event -> {
			xOffset[0] = event.getSceneX();
			yOffset[0] = event.getSceneY();
		});

		titleBar.setOnMouseDragged(event -> {
			stage.setX(event.getScreenX() - xOffset[0]);
			stage.setY(event.getScreenY() - yOffset[0]);
		});
	}
}