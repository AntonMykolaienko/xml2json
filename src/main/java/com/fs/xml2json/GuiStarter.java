/**
 * Copyright © 2016-2017 Anton Mykolaienko. All rights reserved. Contacts: <amykolaienko@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
public class GuiStarter extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuiStarter.class);

    /**
     * Maximum window height.
     */
    private static final int MAX_HEIGHT = 230;
    /**
     * Minimum window height.
     */
    private static final int MIN_HEIGHT = 230;
    /**
     * Minimum window width.
     */
    private static final int MIN_WIDTH = 400;

    @Override
    public void start(Stage primaryStage) {
        try {
            HostServicesProvider.INSTANCE.init(getHostServices());
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Window.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Xml <-> Json Converter");
            primaryStage.setScene(scene);

            primaryStage.setMaxHeight(MAX_HEIGHT);
            primaryStage.setMinHeight(MIN_HEIGHT);

            primaryStage.setMinWidth(MIN_WIDTH);

            primaryStage.getIcons().add(new Image("favicon.png"));

            primaryStage.setOnCloseRequest(event -> LOGGER.info("Shutting down GUI ..."));

            primaryStage.show();
        } catch (IOException ex) {
            LOGGER.error(ex.toString());
        }
    }

    /**
     * Main method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
