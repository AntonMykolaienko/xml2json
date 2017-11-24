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

package com.fs.xml2json.controller;

import com.fs.xml2json.core.ApplicationProperties;
import static com.fs.xml2json.core.Config.APPLICATION_FOLDER_NAME;
import static com.fs.xml2json.core.Config.APPLICATION_STORAGE_FILE_NAME;
import static com.fs.xml2json.core.Config.DONATE_LINK;
import static com.fs.xml2json.core.Config.START_BUTTON_CANCEL;
import static com.fs.xml2json.core.Config.START_BUTTON_START;
import com.fs.xml2json.core.PropertiesLoader;
import com.fs.xml2json.listener.GuiFileReadListener;
import com.fs.xml2json.service.ConverterService;
import com.fs.xml2json.type.FileTypeEnum;
import static com.fs.xml2json.type.FileTypeEnum.JSON;
import static com.fs.xml2json.type.FileTypeEnum.XML;
import com.fs.xml2json.type.UnsupportedFileType;
import com.fs.xml2json.util.ApplicationUtils;
import com.fs.xml2json.util.ConverterUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.ERROR;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.control.ButtonType.YES;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import static javafx.scene.text.TextAlignment.CENTER;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import static javafx.stage.Modality.WINDOW_MODAL;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class.
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public class WindowController implements Initializable {

    private static final ExtensionFilter EXTENSION_XML_OR_JSON = new ExtensionFilter("XML or JSON files",
            Arrays.asList("*.xml", "*.json"));

    private static final ExtensionFilter EXTENSION_XML = new ExtensionFilter("XML-files", "*.xml");
    private static final ExtensionFilter EXTENSION_JSON = new ExtensionFilter("Json-files", "*.json");

    /**
     * Alert pane min height.
     */
    private static final int ALERT_PANE_MIN_HEIGHT = 100;
    /**
     * Alert pane min width.
     */
    private static final int ALERT_PANE_MIN_WIDTH = 300;
    /**
     * Allert pane offset for Y axis.
     */
    private static final int ALERT_PANE_Y_OFFSET = 50;
    /**
     * 100%.
     */
    private static final int ONE_HUNDRED_PERCENTS = 100;
    /**
     * Number of seconds in minute.
     */
    private static final int NUMBER_OF_SECONDS = 60;

    private static final DecimalFormat DF = new DecimalFormat("###");

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowController.class);

    private ApplicationProperties applicationProperties;


    @FXML
    private Button inputBrowseBtn;
    @FXML
    private Button outputBrowseBtn;
    @FXML
    private Button startBtn;

    @FXML
    private TextField inputPath;
    @FXML
    private TextField outputPath;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressValue;

    @FXML
    private Label message;

    // bottom
    @FXML
    private Hyperlink donate;
    @FXML
    private Label version;

    private Task convertTask = null;
    private String path;
    private final DoubleProperty processedBytes = new SimpleDoubleProperty(0);
    private final AtomicBoolean inProgress = new AtomicBoolean(false);
    private final AtomicBoolean isFailed = new AtomicBoolean(false);
    private final AtomicBoolean isCanceled = new AtomicBoolean(false);

    /**
     * Initializes the controller class.
     *
     * @param url url
     * @param rb resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        processedBytes.addListener((observable, oldValue, newValue) ->
            Platform.runLater(() -> progressValue.setText(DF.format((double) newValue * ONE_HUNDRED_PERCENTS) + "%"))
        );

        String versionTxt = "Version: " + ApplicationUtils.getVersion() + "; ";
        version.setText(versionTxt);
        donate.setText("Donate");
        donate.setOnAction(e -> HostServicesProvider.INSTANCE.getHostServices().showDocument(DONATE_LINK));

        String usersDir = System.getProperty("user.home");
        File appDir = new File(usersDir, APPLICATION_FOLDER_NAME);
        File configFile = new File(appDir, APPLICATION_STORAGE_FILE_NAME);
        applicationProperties = new ApplicationProperties(new PropertiesLoader(configFile));
    }

    /**
     * Opens dialog pane to choose source file.
     *
     * @param event event
     */
    public void openBrowseDialogInput(ActionEvent event) {
        reset();
        FileChooser fileChooser = new FileChooser();
        if (null != path) {
            fileChooser.setInitialDirectory(new File(path));
        } else {
            path = applicationProperties.getLastOpenedPath();
            if (null != path) {
                fileChooser.setInitialDirectory(new File(path));
            }
        }

        fileChooser.getExtensionFilters().addAll(EXTENSION_XML_OR_JSON);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (null != selectedFile) {
            path = selectedFile.getParent();

            inputPath.setText(selectedFile.getAbsolutePath());

            try {
                File outputFile = ConverterUtils.getConvertedFile(selectedFile, selectedFile.getParentFile());
                outputPath.setText(outputFile.getAbsolutePath());
            } catch (UnsupportedFileType ex) {
                LOGGER.warn(ex.toString());
            }

            processedBytes.set(0);

            applicationProperties.saveLastOpenedPath(selectedFile);
        }
    }

    /**
     * Opens dialog pane to choose destination folder.
     *
     * @param event event
     */
    public void openBrowseDialogOutput(ActionEvent event) {
        reset();
        FileChooser fileChooser = new FileChooser();
        if (null != path) {
            fileChooser.setInitialDirectory(new File(path));
        } else {
            path = applicationProperties.getLastOpenedPath();
            if (null != path) {
                fileChooser.setInitialDirectory(new File(path));
            }
        }

        if (null != inputPath) {
            if (inputPath.getText().endsWith(XML.getExtension())) {
                fileChooser.getExtensionFilters().addAll(EXTENSION_JSON);
            } else if (inputPath.getText().endsWith(JSON.getExtension())) {
                fileChooser.getExtensionFilters().addAll(EXTENSION_XML);
            }
        } else {
            fileChooser.getExtensionFilters().addAll(EXTENSION_XML_OR_JSON);
        }

        File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            outputPath.setText(selectedFile.getAbsolutePath());
            applicationProperties.saveLastOpenedPath(selectedFile);
        }
    }


    /**
     * Handler for Start button.
     *
     * @param event event
     */
    public void startConvertation(ActionEvent event) {
        if (inProgress.get()) { // handle cancel event
            handleCancelEvent();

            return;
        }

        // check all values
        String errorMessage = checkInputParameters();
        // show errors
        if (null != errorMessage) {
            showErrors(event, errorMessage);
            return;
        }

        File output = new File(outputPath.getText());
        if (output.exists()) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().getButtonTypes().addAll(YES, NO);

            Text text = new Text("File '" + outputPath.getText() + "' already exists, overwrite?");
            TextFlow textFlow = new TextFlow();
            textFlow.setTextAlignment(CENTER);

            textFlow.getChildren().addAll(text);

            dialog.getDialogPane().setContent(textFlow);
            dialog.initOwner(((Node) event.getTarget()).getScene().getWindow());
            dialog.setTitle("File already exists");
            dialog.showAndWait();

            if (dialog.getResult() == YES) {
                LOGGER.debug("Overwrite file");
            } else if (dialog.getResult() == NO) {
                LOGGER.debug("Cancel overwrite file");
                return;
            }
        }

        isFailed.compareAndSet(true, false);
        isCanceled.compareAndSet(true, false);
        inProgress.set(true);
        startBtn.setText(START_BUTTON_CANCEL);
        startBtn.setDisable(false);

        enableOrDisableButtonsAndInputs(true);

        processedBytes.set(0);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(processedBytes);
        reset();

        convertTask = createConvertionTask();

        new Thread(convertTask, "ConvertingThread").start();
    }


    /**
     * Special method for handling Cancel.
     */
    private void handleCancelEvent() {
        Platform.runLater(() -> {
            isCanceled.compareAndSet(false, true);
            startBtn.setDisable(false);
            startBtn.setText(START_BUTTON_START);

            enableOrDisableButtonsAndInputs(false);

            message.setText("Canceled");
        });

        // cancel converting task
        if (null != convertTask) {
            convertTask = null;
        }

        inProgress.set(false);
        isCanceled.compareAndSet(true, false);
    }

    /**
     * Convertion task.
     *
     * @return {@link Task}
     */
    private Task createConvertionTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                // start convertation
                StopWatch sw = new StopWatch();

                sw.start();
                try {
                    convertFile();

                    if (!isCanceled.get() && !isFailed.get()) {
                        Platform.runLater(() -> {
                            startBtn.setDisable(false);
                            startBtn.setText(START_BUTTON_START);

                            enableOrDisableButtonsAndInputs(false);

                            long seconds = sw.getTime(TimeUnit.SECONDS);
                            message.setText("Finished in " + String.format("%02d:%02d", seconds / NUMBER_OF_SECONDS,
                                    seconds % NUMBER_OF_SECONDS));
                            inProgress.compareAndSet(true, false);
                        });
                    }
                } finally {
                    if (!sw.isStarted()) {
                        try {
                            sw.stop();
                        } catch (Exception ex) {
                            LOGGER.debug(ex.toString());    // not importatnt exception
                        }
                    }
                }

                return true;
            }

            @Override
            protected void failed() {
                super.failed();

                if (!isCanceled.get()) {
                    message.setText("Finished with errors. Please check logs.");
                }
                isFailed.compareAndSet(false, true);
                inProgress.compareAndSet(true, false);
                startBtn.setText(START_BUTTON_START);
                enableOrDisableButtonsAndInputs(false);
            }
        };
    }

    /**
     * Returns <code>true</code> if input file ends with {@link FileTypeEnum.XML} extension,
     * otherwise returns <code>false</code>.
     *
     * @param txtField input text field
     * @return <code>true</code> if file is XML, otherwise <code>false</code>
     */
    private boolean isXml(TextField txtField) {
        return txtField.getText().toLowerCase().endsWith(XML.getExtension());
    }

    /**
     * Returns <code>true</code> if input file ends with {@link FileTypeEnum.JSON} extension,
     * otherwise returns <code>false</code>.
     *
     * @param txtField input text field
     * @return <code>true</code> if file is JSON, otherwise <code>false</code>
     */
    private boolean isJson(TextField txtField) {
        return txtField.getText().toLowerCase().endsWith(JSON.getExtension());
    }

    /**
     * Returns error message if exists, or null if all is OK.
     *
     * @return error message or null
     */
    private String checkInputParameters() {
        // check all values
        if (null == inputPath.getText() || inputPath.getText().isEmpty()) {
            return "Select \"Source file\"";
        }
        if (null == outputPath.getText() || outputPath.getText().isEmpty()) {
            return "Select \"Output file\"";
        }
        if ((isXml(inputPath) && isXml(outputPath)) || (isJson(inputPath) && isJson(outputPath))) {
            return "Only xml->json or json->xml convert supported";
        }
        if (null != inputPath.getText() && !(new File(inputPath.getText())).exists()) {
            return "\"Source file\" not exists";
        }

        return null;
    }

    /**
     * Displays pane with Exception or with Warnings.
     *
     * @param event javaFx event
     * @param errorMessage message to display
     */
    private void showErrors(ActionEvent event, String errorMessage) {
        Alert alert = new Alert(ERROR, errorMessage, OK);
        alert.initOwner(((Node) event.getTarget()).getScene().getWindow());
        alert.initModality(WINDOW_MODAL);

        Text text = new Text(errorMessage);
        TextFlow textFlow = new TextFlow();
        textFlow.setTextAlignment(CENTER);

        textFlow.getChildren().addAll(text);

        alert.getDialogPane().setContent(textFlow);
        alert.getDialogPane().setMinHeight(ALERT_PANE_MIN_HEIGHT);
        alert.getDialogPane().setMinWidth(ALERT_PANE_MIN_WIDTH);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setHeader(new GridPane());
        dialogPane.setGraphic(null);
        dialogPane.setCenterShape(true);

        alert.setX(alert.getOwner().getX() + (alert.getOwner().getWidth() - alert.getDialogPane().getWidth()) / 2);
        alert.setY(alert.getOwner().getY() + ALERT_PANE_Y_OFFSET);

        alert.showAndWait();
    }

    /**
     * Disables or enables buttons.
     *
     * @param disable true if want to disable buttons
     */
    private void enableOrDisableButtonsAndInputs(boolean disable) {
        inputPath.setDisable(disable);
        inputBrowseBtn.setDisable(disable);
        outputPath.setDisable(disable);
        outputBrowseBtn.setDisable(disable);
    }

    /**
     * Converts source file to output file.
     *
     * @throws IOException IO exceptions from ConverterService
     * @throws XMLStreamException if XML file have incorrect structure
     */
    private void convertFile() throws IOException, XMLStreamException {
        LOGGER.info("Converting started");

        ConverterService service = new ConverterService();

        File inputFile = new File(inputPath.getText());
        File outputFile = new File(outputPath.getText());

        try {
            service.convert(inputFile, outputFile, new GuiFileReadListener(processedBytes, inputFile), isCanceled);
        } catch (IOException | XMLStreamException ex) {
            LOGGER.error(ex.toString());
            throw ex;
        }
    }

    /**
     * Sets empty string to message area.
     */
    private void reset() {
        message.setText("");
    }

}
