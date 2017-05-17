package com.fs.xml2json.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Abstract controller for all controllers.
 *
 * @author Anton
 * @since 1.0.0
 */
public abstract class AbstractController {

    private static final String UNNOWN_VERSION = "unknown";

    /**
     * Returns application version from Manifest-file.
     *
     * @return string with application version
     */
    protected final String getVersion() {
        String version = AbstractController.class.getPackage().getImplementationVersion();
        if (null == version || version.isEmpty()) {
            version = UNNOWN_VERSION;
        }
        return version;
    }

    private void showCustomAlert(Stage owner, String textMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, textMsg, ButtonType.OK);
        alert.initOwner(owner);
        alert.initModality(Modality.WINDOW_MODAL);

        Text text = new Text(textMsg);
        TextFlow textFlow = new TextFlow();
        textFlow.setTextAlignment(TextAlignment.CENTER);

        textFlow.getChildren().addAll(text);

        alert.getDialogPane().setContent(textFlow);
        alert.getDialogPane().setMinHeight(100);
        alert.getDialogPane().setMinWidth(300);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setHeader(new GridPane());
        dialogPane.setGraphic(null);
        dialogPane.setCenterShape(true);

        //alert.setX(alert.getOwner().getX() + (alert.getOwner().getWidth() - alert.getWidth())/2);
        alert.setX(alert.getOwner().getX() + (alert.getOwner().getWidth() - alert.getDialogPane().getWidth())/2);
        alert.setY(alert.getOwner().getY() + 50);

        alert.showAndWait();
    }
}
