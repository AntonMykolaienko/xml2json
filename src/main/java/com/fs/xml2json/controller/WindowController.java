package com.fs.xml2json.controller;

import com.fs.xml2json.core.Config;
import com.fs.xml2json.listener.GuiFileReadListener;
import com.fs.xml2json.service.ConverterService;
import com.fs.xml2json.type.FileTypeEnum;
import com.fs.xml2json.type.UnsupportedFileType;
import com.fs.xml2json.util.ApplicationUtils;
import com.fs.xml2json.util.ConfigUtils;
import com.fs.xml2json.util.ConverterUtils;
import java.io.File;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public class WindowController implements Initializable {

    private static final ExtensionFilter EXTENSION_XML_OR_JSON = new ExtensionFilter("XML or JSON files",
            Arrays.asList("*.xml", "*.json"));

    private static final ExtensionFilter EXTENSION_XML = new ExtensionFilter("XML-files", "*.xml");
    private static final ExtensionFilter EXTENSION_JSON = new ExtensionFilter("Json-files", "*.json");

    private static final DecimalFormat df = new DecimalFormat("###");

    private static final Logger logger = LoggerFactory.getLogger(WindowController.class);

    
    @FXML
    Button inputBrowseBtn;
    @FXML
    Button outputBrowseBtn;
    @FXML
    Button startBtn;

    @FXML
    TextField inputPath;
    @FXML
    TextField outputPath;

    @FXML
    ProgressBar progressBar;
    @FXML
    Label progressValue;

    @FXML
    Label message;

    // bottom
    @FXML
    Hyperlink donate;
    @FXML
    Label version;

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
            Platform.runLater(() -> progressValue.setText(df.format((double) newValue * 100) + "%"))
        );

        String versionTxt = "Version: " + ApplicationUtils.getVersion() + "; ";
        version.setText(versionTxt);
        donate.setText("Donate");
        donate.setOnAction(e -> HostServicesProvider.INSTANCE.getHostServices().showDocument(Config.DONATE_LINK));
    }

    public void openBrowseDialogInput(ActionEvent event) {
        reset();
        FileChooser fileChooser = new FileChooser();
        if (null != path) {
            fileChooser.setInitialDirectory(new File(path));
        } else {
            path = ConfigUtils.readLastPath();
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
                logger.warn(ex.toString());
            }
            
            processedBytes.set(0);

            ConfigUtils.saveLastPath(selectedFile);
        }
    }

    public void openBrowseDialogOutput(ActionEvent event) {
        reset();        
        FileChooser fileChooser = new FileChooser();
        if (null != path) {
            fileChooser.setInitialDirectory(new File(path));
        } else {
            path = ConfigUtils.readLastPath();
            if (null != path) {
                fileChooser.setInitialDirectory(new File(path));
            }
        }

        if (null != inputPath) {
            if (inputPath.getText().endsWith(FileTypeEnum.XML.getExtension())) {
                fileChooser.getExtensionFilters().addAll(EXTENSION_JSON);
            } else if (inputPath.getText().endsWith(FileTypeEnum.JSON.getExtension())) {
                fileChooser.getExtensionFilters().addAll(EXTENSION_XML);
            }
        } else {
            fileChooser.getExtensionFilters().addAll(EXTENSION_XML_OR_JSON);
        }

        File selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            outputPath.setText(selectedFile.getAbsolutePath());
            ConfigUtils.saveLastPath(selectedFile);
        }
    }


    public void startConvertation(ActionEvent event) {
        if (inProgress.get()) { // handle cancel event
            Platform.runLater(() -> {
                isCanceled.compareAndSet(false, true);
                startBtn.setDisable(false);
                startBtn.setText(Config.START_BUTTON_START);

                enableOrDisableButtonsAndInputs(false);

                message.setText("Canceled");
            });

            // cancel converting task
            if (null != convertTask) {
                convertTask = null;
            }

            inProgress.set(false);
            isCanceled.compareAndSet(true, false);

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
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            
            Text text = new Text("File '" + outputPath.getText() + "' already exists, overwrite?");
            TextFlow textFlow = new TextFlow();
            textFlow.setTextAlignment(TextAlignment.CENTER);

            textFlow.getChildren().addAll(text);
            
            dialog.getDialogPane().setContent(textFlow);
            dialog.initOwner(((Node)event.getTarget()).getScene().getWindow());
            dialog.setTitle("File already exists");
            dialog.showAndWait();

            if (dialog.getResult() == ButtonType.YES) {
                logger.debug("Overwrite file");
            } else if (dialog.getResult() == ButtonType.NO) {
                logger.debug("Cancel overwrite file");
                return;
            }
        }

        isFailed.compareAndSet(true, false);
        isCanceled.compareAndSet(true, false);
        inProgress.set(true);
        startBtn.setText(Config.START_BUTTON_CANCEL);
        startBtn.setDisable(false);

        enableOrDisableButtonsAndInputs(true);

        processedBytes.set(0);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(processedBytes);
        reset();

        convertTask = new Task() {
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
                            startBtn.setText(Config.START_BUTTON_START);

                            enableOrDisableButtonsAndInputs(false);
                            
                            long seconds = sw.getTime(TimeUnit.SECONDS);
                            message.setText("Finished in " + String.format("%02d:%02d", seconds/60, seconds%60));
                            inProgress.compareAndSet(true, false);
                        });
                    }
                } finally {
                    if (!sw.isStarted()) {
                        try {
                            sw.stop();
                        } catch (Exception ex) {
                            logger.debug(ex.toString());    // not importatnt exception
                        }
                    }
                }

                return true;
            }

            @Override
            protected void failed() {
                super.failed();
                
                if (!isCanceled.get()) {
                    message.setText("Finished with errors");
                }
                isFailed.compareAndSet(false, true);
                inProgress.compareAndSet(true, false);
                startBtn.setText(Config.START_BUTTON_START);
                enableOrDisableButtonsAndInputs(false);
            }
        };

        new Thread(convertTask, "ConvertingThread").start();
    }

    private boolean isXml(TextField txtField) {
        return txtField.getText().toLowerCase().endsWith(FileTypeEnum.XML.getExtension());
    }

    private boolean isJson(TextField txtField) {
        return txtField.getText().toLowerCase().endsWith(FileTypeEnum.JSON.getExtension());
    }
    
    /**
     * Returns error message if exists, or null if all is OK.
     * 
     * @return error message or null;
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
    
    private void showErrors(ActionEvent event, String errorMessage) {
        Alert alert = new Alert(AlertType.ERROR, errorMessage, ButtonType.OK);
        alert.initOwner(((Node)event.getTarget()).getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);

        Text text = new Text(errorMessage);
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

        alert.setX(alert.getOwner().getX() + (alert.getOwner().getWidth() - alert.getDialogPane().getWidth())/2);
        alert.setY(alert.getOwner().getY() + 50);

        alert.showAndWait();
    }

    /**
     * Disables or enables buttons.
     *
     * @param isDisable true if want to disable buttons
     */
    private void enableOrDisableButtonsAndInputs(boolean disable) {
        inputPath.setDisable(disable);
        inputBrowseBtn.setDisable(disable);
        outputPath.setDisable(disable);
        outputBrowseBtn.setDisable(disable);
    }

    private void convertFile() {
        logger.info("Converting started");

        ConverterService service = new ConverterService();
        
        File inputFile = new File(inputPath.getText());
        File outputFile = new File(outputPath.getText());
        
        try {
            service.convert(inputFile, outputFile, new GuiFileReadListener(processedBytes, inputFile), isCanceled);
        } catch (Exception ex) {
            logger.error(ex.toString());
            throw new RuntimeException(ex);
        } 
    }

    /**
     * Sets empty string to message area.
     */
    private void reset() {
        message.setText("");
    }

}
