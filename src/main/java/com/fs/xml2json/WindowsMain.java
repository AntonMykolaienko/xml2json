
package com.fs.xml2json;

import com.fs.xml2json.controller.HostServicesProvider;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI-version.
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public class WindowsMain extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(WindowsMain.class);

    
    @Override
    public void start(Stage primaryStage) {
        try {
            HostServicesProvider.INSTANCE.init(getHostServices());
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Window.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Xml <-> Json Converter");
            primaryStage.setScene(scene);
            
            primaryStage.setMaxHeight(230);
            primaryStage.setMinHeight(230);
            
            primaryStage.setMinWidth(400);

            primaryStage.getIcons().add(new Image("favicon.png"));
            
            primaryStage.setOnCloseRequest(event -> {
                logger.info("Shuting down application...");
            });

            primaryStage.show();
        } catch (IOException ex) {
            logger.error(ex.toString());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
