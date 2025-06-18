package com.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

/**
 * Utility class for creating and displaying custom dialog windows with a wooden theme.
 */
public class DialogUtil {

    private static final String TITLE_BAR_STYLE = "-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 0 0 1 0;";
    private static final String BUTTON_STYLE = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
            "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold; " +
            "-fx-border-color: #8a673c; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * Displays a custom alert dialog with a wooden theme.
     *
     * @param title   The title of the dialog window.
     * @param content The content message to display in the dialog.
     */
    public static void showWoodenAlert(String title, String content) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setTitle(title);

        HBox titleBar = createTitleBar(dialogStage, title);

        VBox contentArea = new VBox(10);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #5d4228;");

        Text contentText = new Text(content);
        contentText.setFont(Font.font("Segoe UI", 14));
        contentText.setFill(Color.web("#e8d9b5"));
        contentText.setWrappingWidth(350);

        Button okButton = new Button("OK");
        okButton.setPrefWidth(100);
        okButton.setStyle(BUTTON_STYLE);
        okButton.setOnAction(e -> dialogStage.close());

        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        contentArea.getChildren().addAll(contentText, buttonBox);

        VBox root = new VBox(titleBar, contentArea);
        root.setStyle("-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 2;");
        root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.5)));

        Scene dialogScene = new Scene(root, 400, 200);
        dialogScene.setFill(Color.web("#5d4228"));

        try {
            Image cursorImage = new Image(DialogUtil.class.getResourceAsStream("/com/example/assets/ui/01.png"));
            ImageCursor customCursor = new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2);
            dialogScene.setCursor(customCursor);
            root.setCursor(customCursor);
        } catch (Exception ignored) {}

        dialogStage.setScene(dialogScene);
        dialogStage.centerOnScreen();
        setupDraggableStage(titleBar, dialogStage);
        dialogStage.showAndWait();
    }

    /**
     * Creates a draggable title bar for the dialog window.
     *
     * @param stage The stage associated with the dialog window.
     * @param title The title text to display in the title bar.
     * @return A {@link HBox} representing the title bar.
     */
    private static HBox createTitleBar(Stage stage, String title) {
        HBox titleBar = new HBox();
        titleBar.setAlignment(Pos.CENTER_RIGHT);
        titleBar.setPrefHeight(25);
        titleBar.setPadding(new Insets(0, 5, 0, 10));
        titleBar.setStyle(TITLE_BAR_STYLE);

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#e8d9b5"));
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Button closeButton = new Button("×");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-size: 16px;");
        closeButton.setOnAction(e -> stage.close());
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: #a05454; -fx-text-fill: #f5ead9; -fx-font-size: 16px;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-size: 16px;"));

        titleBar.getChildren().addAll(titleLabel, closeButton);
        return titleBar;
    }

    /**
     * Enables dragging functionality for the dialog window using the title bar.
     *
     * @param titleBar The {@link HBox} representing the title bar.
     * @param stage    The stage associated with the dialog window.
     */
    private static void setupDraggableStage(HBox titleBar, Stage stage) {
        final double[] offset = new double[2];
        titleBar.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - offset[0]);
            stage.setY(e.getScreenY() - offset[1]);
        });
    }
}
