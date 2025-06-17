package com.example.utils;

import com.example.main.Main;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
 * Class MapEditorUtils
 */
public class MapEditorUtils {


    /**
     * TODO
     */
    public static final String BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#6b4c2e, #4e331f); " +
                                                     "-fx-text-fill: #e8d9b5; -fx-font-family: 'Segoe UI'; " +
                                                     "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                     "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                     "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * TODO
     */
    public static final String BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#7d5a3c, #5d4228); " +
                                                    "-fx-text-fill: #f5ead9; -fx-font-family: 'Segoe UI'; " +
                                                    "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                    "-fx-border-color: #a07748; -fx-border-width: 2; " +
                                                    "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * TODO
     */
    public static final String BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#422c17, #6b4c2e); " +
                                                      "-fx-text-fill: #d9c9a0; -fx-font-family: 'Segoe UI'; " +
                                                      "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                      "-fx-border-color: #8a673c; -fx-border-width: 2; " +
                                                      "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * TODO
     */
    public static final String OK_BUTTON_NORMAL_STYLE = "-fx-background-color: linear-gradient(#447240, #2e5a23); " +
                                                        "-fx-text-fill: #e8f4d9; -fx-font-family: 'Segoe UI'; " +
                                                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                        "-fx-border-color: #5d7542; -fx-border-width: 2; " +
                                                        "-fx-border-radius: 5; -fx-background-radius: 5;";

    /**
     * TODO
     */
    public static final String OK_BUTTON_HOVER_STYLE = "-fx-background-color: linear-gradient(#548e4f, #3b6e2c); " +
                                                       "-fx-text-fill: #f5ffe9; -fx-font-family: 'Segoe UI'; " +
                                                       "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                       "-fx-border-color: #6a894d; -fx-border-width: 2; " +
                                                       "-fx-border-radius: 5; -fx-background-radius: 5; ";

    /**
     * TODO
     */
    public static final String OK_BUTTON_PRESSED_STYLE = "-fx-background-color: linear-gradient(#2e5a23, #447240); " +
                                                         "-fx-text-fill: #d9f0c0; -fx-font-family: 'Segoe UI'; " +
                                                         "-fx-font-size: 14px; -fx-font-weight: bold; " +
                                                         "-fx-border-color: #5d7542; -fx-border-width: 2; " +
                                                         "-fx-border-radius: 5; -fx-background-radius: 5;";


    public static final String TITLE_BAR_STYLE = "-fx-background-color: #5d4228; -fx-border-color: #8a673c; -fx-border-width: 0 0 1 0;";
    public static final String BUTTON_TRANSPARENT_STYLE = "-fx-background-color: transparent; -fx-text-fill: #e8d9b5; -fx-font-weight: bold;";
    public static final String CLOSE_BUTTON_HOVER = "-fx-background-color: #a05454; -fx-text-fill: #f5ead9;";


    public static boolean dialogConfirmed = false;


    public static boolean suppressDialogs = false;


    /**
     * TODO
     */
    public static void showInfoAlert(String title, String content, Object controller) {
        if (suppressDialogs) return;


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
        okButton.setStyle(OK_BUTTON_NORMAL_STYLE);


        okButton.setOnMouseEntered(e -> okButton.setStyle(OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(OK_BUTTON_NORMAL_STYLE));


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


        Image customCursorImage = new Image(MapEditorUtils.class.getResourceAsStream("/com/example/assets/ui/01.png"));
        ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
        dialogScene.setCursor(customCursor);
        root.setCursor(customCursor);


        dialogStage.centerOnScreen();


        setupDraggableStage(titleBar, dialogStage);


        okButton.setCursor(customCursor);


        dialogStage.showAndWait();


        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }
    }

    /**
     * TODO
     */
    public static void showErrorAlert(String title, String header, String content, Object caller) {
        if (suppressDialogs) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20, 20, 10, 20));


        ImageView ribbonIcon = null;
        String ribbonPath = "/com/example/assets/ui/Ribbon_Red_3Slides.png";

        try {
            Image iconImage = new Image(caller.getClass().getResourceAsStream(ribbonPath));
            ribbonIcon = new ImageView(iconImage);
            ribbonIcon.setFitWidth(200);
            ribbonIcon.setFitHeight(40);
            ribbonIcon.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Could not load ribbon image for error alert: " + e.getMessage());
        }

        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C62828;");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setWrapText(true);

        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);

        if (ribbonIcon != null) {
            contentBox.getChildren().addAll(ribbonIcon, headerLabel, contentLabel);
        } else {
            contentBox.getChildren().addAll(headerLabel, contentLabel);
        }

        dialogPane.setStyle("-fx-background-color: #f4dede;");

        contentBox.setStyle("-fx-background-color: #fbeaea; -fx-background-radius: 5; " +
                "-fx-border-color: #c62828; -fx-border-width: 3; -fx-border-radius: 5;");

        dialogPane.setContent(contentBox);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(okButtonType);

        Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        okButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;");


        Image customCursorImage = new Image(MapEditorUtils.class.getResourceAsStream("/com/example/assets/ui/01.png"));
        ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
        okButton.setCursor(customCursor);

        okButton.setOnMouseEntered(e ->
                okButton.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold;")
        );
        okButton.setOnMouseExited(e ->
                okButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;")
        );


        dialog.getDialogPane().setCursor(customCursor);
        dialog.getDialogPane().getScene().setCursor(customCursor);

        dialog.showAndWait();


        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }
    }



    /**
     * TODO
     */
    public static boolean showCustomConfirmDialog(String title, String content, Object controller) {
        if (suppressDialogs) return true;


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
        okButton.setStyle(MapEditorUtils.OK_BUTTON_NORMAL_STYLE);


        Image customCursorImage = new Image(MapEditorUtils.class.getResourceAsStream("/com/example/assets/ui/01.png"));
        ImageCursor customCursor = new ImageCursor(customCursorImage, customCursorImage.getWidth() / 2, customCursorImage.getHeight() / 2);
        okButton.setCursor(customCursor);


        okButton.setOnMouseEntered(e -> okButton.setStyle(MapEditorUtils.OK_BUTTON_HOVER_STYLE));
        okButton.setOnMouseExited(e -> okButton.setStyle(MapEditorUtils.OK_BUTTON_NORMAL_STYLE));


        okButton.setOnAction(e -> {
            dialogConfirmed = true;
            dialogStage.close();
        });


        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(100);
        cancelButton.setPrefHeight(30);
        cancelButton.setStyle(MapEditorUtils.BUTTON_NORMAL_STYLE);


        cancelButton.setCursor(customCursor);


        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(MapEditorUtils.BUTTON_HOVER_STYLE));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(MapEditorUtils.BUTTON_NORMAL_STYLE));


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


        Scene dialogScene = new Scene(root, 400, 200);
        dialogScene.setFill(Color.web("#5d4228"));
        dialogStage.setScene(dialogScene);


        dialogScene.setCursor(customCursor);
        root.setCursor(customCursor);


        dialogStage.centerOnScreen();


        setupDraggableStage(titleBar, dialogStage);


        dialogStage.showAndWait();


        if (Main.getViewManager() != null && Main.getViewManager().getScene() != null && Main.getViewManager().getCustomCursor() != null) {
            Main.getViewManager().getScene().setCursor(Main.getViewManager().getCustomCursor());
        }

        return dialogConfirmed;
    }


    /**
     * TODO
     */
    public static HBox createTitleBar(Stage stage, String title) {

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


        titleBar.getChildren().addAll(titleLabel, closeButton);

        return titleBar;
    }


    /**
     * TODO
     */
    public static void setupDraggableStage(HBox titleBar, Stage stage) {
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


    /**
     * TODO
     */
    public static void animateButtonClick(Button button, ImageView imageView, String pressedImagePath, Object controller) {

        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    /**
     * TODO
     */
    public static void setSuppressDialogs(boolean b) {
        suppressDialogs = b;
    }
}