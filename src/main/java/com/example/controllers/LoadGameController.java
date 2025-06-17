package com.example.controllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.example.main.Main;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
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
 * Class LoadGameController
 */
public class LoadGameController implements Initializable {


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
                                                    "-fx-border-radius: 5; -fx-background-radius: 5; ";

    /**
     * TODO
     */
    private static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                                      "-fx-text-fill: #d9c9a0; -fx-font-family: 'Segoe UI'; " +
                                                      "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                      "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                      "-fx-border-radius: 5; -fx-background-radius: 5;";


    private static final String TITLE_BAR_STYLE = "-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 0 0 1 0;";
    private static final String BUTTON_TRANSPARENT_STYLE = "-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold;";
    private static final String CLOSE_BUTTON_HOVER = "-fx-background-color: #a05454; -fx-text-fill: #f5ead9;";

    @FXML
    private ListView<String> savedGamesListView;

    @FXML
    private Button loadBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button homeBtn;

    /**
     * TODO
     */
    private ObservableList<String> savedGames = FXCollections.observableArrayList();


    private boolean dialogConfirmed = false;

    @Override
    /**
     * TODO
     */
    public void initialize(URL location, ResourceBundle resources) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


        savedGames.addAll(
            "Forest Campaign - Level 3 - " + dateFormat.format(new Date(System.currentTimeMillis() - 86400000)),
            "Desert Map - " + dateFormat.format(new Date()),
            "Mountain Pass - Level 7 - " + dateFormat.format(new Date(System.currentTimeMillis() - 172800000)),
            "Tutorial Map - " + dateFormat.format(new Date(System.currentTimeMillis() - 259200000)),
            "Custom Map 1 - " + dateFormat.format(new Date(System.currentTimeMillis() - 432000000))
        );

        savedGamesListView.setItems(savedGames);


        if (!savedGames.isEmpty()) {
            savedGamesListView.getSelectionModel().selectFirst();
        }


        loadBtn.setDisable(savedGames.isEmpty());
        deleteBtn.setDisable(savedGames.isEmpty());


        savedGamesListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean noSelection = (newValue == null);
                loadBtn.setDisable(noSelection);
                deleteBtn.setDisable(noSelection);
            }
        );


        setupButtonEffects(loadBtn);
        setupButtonEffects(deleteBtn);
        setupButtonEffects(homeBtn);


        setupCustomListView();
    }

    /**
     * TODO
     */
    private void setupCustomListView() {
        savedGamesListView.setCellFactory(list -> new ListCell<String>() {
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
    }

    /**
     * TODO
     */
    private void setupButtonEffects(Button button) {

        button.setStyle(BUTTON_NORMAL_STYLE);


        button.setOnMouseEntered(e -> button.setStyle(BUTTON_HOVER_STYLE));
        button.setOnMouseExited(e -> button.setStyle(BUTTON_NORMAL_STYLE));


        button.setOnMousePressed(e -> {
            button.setStyle(BUTTON_PRESSED_STYLE);
            animateButtonClick(button);
        });

        button.setOnMouseReleased(e -> button.setStyle(BUTTON_HOVER_STYLE));
    }

    /**
     * TODO
     */
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
    /**
     * TODO
     */
    private void loadSelectedGame() {
        String selectedGame = savedGamesListView.getSelectionModel().getSelectedItem();
        if (selectedGame != null) {

            boolean confirmed = showCustomConfirmDialog(
                "Load Game",
                "Are you sure you want to load: " + selectedGame + "?\n\nAny unsaved progress will be lost."
            );

            if (confirmed) {

                showWoodenAlert(
                    "Game Loading",
                    "Loading saved game: " + selectedGame
                );



                Main.getViewManager().switchTo("/com/example/fxml/game_screen_page.fxml");
            }
        }
    }

    @FXML
    /**
     * TODO
     */
    private void deleteSelectedGame() {
        String selectedGame = savedGamesListView.getSelectionModel().getSelectedItem();
        if (selectedGame != null) {

            boolean confirmed = showCustomConfirmDialog(
                "Delete Saved Game",
                "Are you sure you want to delete the saved game:\n" + selectedGame + "?\n\nThis action cannot be undone."
            );

            if (confirmed) {

                savedGames.remove(selectedGame);


                if (savedGames.isEmpty()) {
                    loadBtn.setDisable(true);
                    deleteBtn.setDisable(true);
                }


                showWoodenAlert(
                    "Game Deleted",
                    "The selected game has been deleted."
                );
            }
        }
    }

    @FXML
    /**
     * TODO
     */
    private void goToHomePage() {
        Main.getViewManager().switchTo("/com/example/fxml/home_page.fxml");
    }


/**
 * TODO
 */
private boolean showCustomConfirmDialog(String title, String content) {

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initStyle(StageStyle.UNDECORATED);
    dialogStage.setTitle(title);


    dialogConfirmed = false;


    HBox titleBar = createTitleBar(dialogStage, title);


    VBox contentArea = new VBox(10);
    contentArea.setAlignment(Pos.CENTER);
    contentArea.setPadding(new Insets(20, 20, 20, 20));
    contentArea.setStyle("-fx-background-color: #5d4228;");


    Text contentText = new Text(content);
    contentText.setFont(Font.font("Segoe UI", 14));
    contentText.setFill(Color.web("#e8d9b5"));
    contentText.setWrappingWidth(350);


    HBox buttonBox = new HBox(20);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setPadding(new Insets(20, 0, 10, 0));
    buttonBox.setStyle("-fx-background-color: #5d4228;");


    Button okButton = new Button("OK");
    okButton.setPrefWidth(100);
    okButton.setPrefHeight(30);
    okButton.setStyle(BUTTON_NORMAL_STYLE);


    Image customCursorImage = new Image(getClass().getResourceAsStream("/com/example/assets/ui/01.png"));
    ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
    okButton.setCursor(customCursor);


    okButton.setOnMouseEntered(e -> okButton.setStyle(BUTTON_HOVER_STYLE));
    okButton.setOnMouseExited(e -> okButton.setStyle(BUTTON_NORMAL_STYLE));


    okButton.setOnAction(e -> {
        dialogConfirmed = true;
        dialogStage.close();
    });


    Button cancelButton = new Button("Cancel");
    cancelButton.setPrefWidth(100);
    cancelButton.setPrefHeight(30);
    cancelButton.setStyle(BUTTON_NORMAL_STYLE);


    cancelButton.setCursor(customCursor);


    cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(BUTTON_HOVER_STYLE));
    cancelButton.setOnMouseExited(e -> cancelButton.setStyle(BUTTON_NORMAL_STYLE));


    cancelButton.setOnAction(e -> {
        dialogConfirmed = false;
        dialogStage.close();
    });


    buttonBox.getChildren().addAll(okButton, cancelButton);


    contentArea.getChildren().addAll(contentText, buttonBox);


    VBox root = new VBox();
    root.getChildren().addAll(titleBar, contentArea);
    root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");


    root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));


    Scene dialogScene = new Scene(root, 400, 220);

    dialogScene.setFill(Color.web("#5d4228"));
    dialogStage.setScene(dialogScene);


    dialogScene.setCursor(customCursor);
    root.setCursor(customCursor);


    dialogStage.centerOnScreen();


    setupDraggableStage(titleBar, dialogStage);


    dialogStage.showAndWait();


    return dialogConfirmed;
}



/**
 * TODO
 */
private void showWoodenAlert(String title, String content) {

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initStyle(StageStyle.UNDECORATED);
    dialogStage.setTitle(title);


    HBox titleBar = createTitleBar(dialogStage, title);


    VBox contentArea = new VBox(10);
    contentArea.setAlignment(Pos.CENTER);
    contentArea.setPadding(new Insets(20, 20, 20, 20));
    contentArea.setStyle("-fx-background-color: #5d4228;");


    Text contentText = new Text(content);
    contentText.setFont(Font.font("Segoe UI", 14));
    contentText.setFill(Color.web("#e8d9b5"));
    contentText.setWrappingWidth(350);


    HBox buttonBox = new HBox();
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setPadding(new Insets(20, 0, 10, 0));
    buttonBox.setStyle("-fx-background-color: #5d4228;");


    Button okButton = new Button("OK");
    okButton.setPrefWidth(100);
    okButton.setPrefHeight(30);
    okButton.setStyle(BUTTON_NORMAL_STYLE);


    Image customCursorImage = new Image(getClass().getResourceAsStream("/com/example/assets/ui/01.png"));
    ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
    okButton.setCursor(customCursor);


    okButton.setOnMouseEntered(e -> okButton.setStyle(BUTTON_HOVER_STYLE));
    okButton.setOnMouseExited(e -> okButton.setStyle(BUTTON_NORMAL_STYLE));


    okButton.setOnAction(e -> dialogStage.close());


    buttonBox.getChildren().add(okButton);


    contentArea.getChildren().addAll(contentText, buttonBox);


    VBox root = new VBox();
    root.getChildren().addAll(titleBar, contentArea);
    root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");


    root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));


    Scene dialogScene = new Scene(root, 400, 200);

    dialogScene.setFill(Color.web("#5d4228"));
    dialogStage.setScene(dialogScene);


    dialogScene.setCursor(customCursor);
    root.setCursor(customCursor);


    dialogStage.centerOnScreen();


    setupDraggableStage(titleBar, dialogStage);


    dialogStage.showAndWait();
}


    /**
     * TODO
     */
    private HBox createTitleBar(Stage stage, String title) {

        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPrefHeight(25);
        titleBar.setStyle(TITLE_BAR_STYLE);
        titleBar.setPadding(new Insets(0, 5, 0, 10));


        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#e8d9b5"));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setAlignment(Pos.CENTER_LEFT);


        Button closeButton = new Button("×");
        closeButton.setStyle(BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;");
        closeButton.setOnAction(e -> stage.close());


        closeButton.setOnMouseEntered(e -> closeButton.setStyle(CLOSE_BUTTON_HOVER + "-fx-font-size: 16px;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(BUTTON_TRANSPARENT_STYLE + "-fx-font-size: 16px;"));


        Image customCursorImage = new Image(getClass().getResourceAsStream("/com/example/assets/ui/01.png"));
        ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
        closeButton.setCursor(customCursor);


        titleBar.getChildren().addAll(titleLabel, closeButton);

        return titleBar;
    }


    /**
     * TODO
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