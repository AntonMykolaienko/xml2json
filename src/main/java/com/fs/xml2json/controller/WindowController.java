package com.fs.xml2json.controller;

import com.fs.xml2json.core.Config;
import com.fs.xml2json.io.WrappedInputStream;
import com.fs.xml2json.type.FileTypeEnum;
import com.fs.xml2json.util.ConfigUtils;
import com.fs.xml2json.util.XmlUtils;
import com.sun.javafx.stage.StageHelper;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.json.util.XMLMultipleEventWriter;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public class WindowController extends AbstractController implements Initializable {

    private static final ExtensionFilter EXTENSION_XML_OR_JSON = new ExtensionFilter("XML or JSON files",
            Arrays.asList("*.xml", "*.json"));

    private static final ExtensionFilter EXTENSION_XML = new ExtensionFilter("XML-files", "*.xml");
    private static final ExtensionFilter EXTENSION_JSON = new ExtensionFilter("Json-files", "*.json");
    private static final String XML = ".xml";
    private static final String JSON = ".json";

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
    private FileTypeEnum inputFileType;
    private final DoubleProperty processedBytes = new SimpleDoubleProperty(0);
    private final AtomicBoolean inProgress = new AtomicBoolean(false);
    private final AtomicBoolean isFailed = new AtomicBoolean(false);
    private final AtomicBoolean isCanceled = new AtomicBoolean(false);

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        processedBytes.addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
            Platform.runLater(() -> {
                progressValue.setText(df.format((double) newValue * 100) + "%");
            });
        });

        String versionTxt = "Version: " + getVersion() + "; ";
        version.setText(versionTxt);
        donate.setText("Donate");
        donate.setOnAction((ActionEvent e) -> {
            HostServicesProvider.INSTANCE.getHostServices().showDocument(Config.DONATE_LINK);
        });
        
        // TODO: check updates
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
            outputPath.setText(createOutputFilePath(selectedFile));
            processedBytes.set(0);

            inputFileType = FileTypeEnum.parseByFileName(selectedFile.getName());
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
            if (inputPath.getText().endsWith(XML)) {
                fileChooser.getExtensionFilters().addAll(EXTENSION_JSON);
            } else if (inputPath.getText().endsWith(JSON)) {
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

    /**
     * Creates fullpath for destination file based on source file (Replaces
     * extension to opposite).
     *
     * @param inputFile file to convert
     * @return full path for destination file
     */
    private String createOutputFilePath(File inputFile) {
        String fileNameWithoutExtension = inputFile.getName().substring(0, inputFile.getName().lastIndexOf("."));

        if (inputFile.getName().endsWith(XML)) {
            return inputFile.getParentFile() + File.separator + fileNameWithoutExtension + JSON;
        } else if (inputFile.getName().endsWith(JSON)) {
            return inputFile.getParentFile() + File.separator + fileNameWithoutExtension + XML;
        }

        return null;
    }

    public void startConvertation(ActionEvent event) {
        if (inProgress.get()) { // handle cancel event
            Platform.runLater(() -> {
                isCanceled.compareAndSet(false, true);
                startBtn.setDisable(false);
                startBtn.setText(Config.START_BUTTON__START);

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
        boolean hasErrors = false;
        String errorMessage = "";
        if (null == inputPath.getText() || inputPath.getText().isEmpty()) {
            hasErrors = true;
            errorMessage = "Select \"Source file\"";
        }
        if (!hasErrors && (null == outputPath.getText() || outputPath.getText().isEmpty())) {
            hasErrors = true;
            errorMessage = "Select \"Output file\"";
        }
        if (!hasErrors && ((isXml(inputPath) && isXml(outputPath)) || (isJson(inputPath) && isJson(outputPath)))) {
            hasErrors = true;
            errorMessage = "Only xml->json or json->xml convert supported";
        }

        // show errors
        if (hasErrors) {
            Alert alert = new Alert(AlertType.ERROR, errorMessage, ButtonType.OK);
            alert.initOwner(StageHelper.getStages().get(0));
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
            dialog.initOwner(StageHelper.getStages().get(0));
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
        startBtn.setText(Config.START_BUTTON__CANCEL);
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
                            startBtn.setText(Config.START_BUTTON__START);

                            enableOrDisableButtonsAndInputs(false);

                            String timePattern = "%02d:%02d";
                            long seconds = sw.getTime(TimeUnit.SECONDS);
                            message.setText("Finished in " + String.format(timePattern, seconds/60, seconds%60));
                        });
                    }
                } finally {
                    if (!sw.isStarted()) {
                        try {
                            sw.stop();
                        } catch (Exception ex) {}
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
                startBtn.setText(Config.START_BUTTON__START);
                enableOrDisableButtonsAndInputs(false);
            }
        };

        new Thread(convertTask, "ConvertingThread").start();
    }

    private boolean isXml(TextField txtField) {
        return txtField.getText().toLowerCase().endsWith(XML);
    }

    private boolean isJson(TextField txtField) {
        return txtField.getText().toLowerCase().endsWith(JSON);
    }

    /**
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

        File inputFile = new File(inputPath.getText());
        File outputFile;

        StopWatch sw = new StopWatch();

        InputStream input = null;
        OutputStream output = null;
        XMLEventReader reader = null;
        XMLEventWriter writer = null;
        try {
            long fileSizeInBytes = inputFile.length();
            
            if (isXml(inputPath)) {
                fileSizeInBytes *= 2;
            }

            input = new WrappedInputStream(new BufferedInputStream(new FileInputStream(inputFile)),
                    processedBytes, fileSizeInBytes, isCanceled);

            JsonXMLConfig config = createConfig();

            outputFile = new File(outputPath.getText());
            output = new BufferedOutputStream(Files.newOutputStream(outputFile.toPath(),
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));

            sw.start();


            // Create reader.
            reader = createReader(config, input);
            // Create writer.
            writer = createWriter(config, output);

            // if source is xml then read file first and determine arrays
            if (isXml(inputPath)) {
                InputStream input2 = null;
                try {
                    input2 = new WrappedInputStream(new BufferedInputStream(new FileInputStream(inputFile)),
                            processedBytes, inputFile.length()*2, isCanceled);
                    List<String> fileArrays = XmlUtils.determineArrays(input2, isCanceled);
                    writer = new XMLMultipleEventWriter(writer, true, fileArrays.toArray(new String[]{}));
                } finally {
                    if (null != input2) {
                        try {
                            input2.close();
                        } catch (IOException ex) {}
                    }
                }
            }
            
            // Copy events from reader to writer.
            writer.add(reader);

            // Close reader/writer.
            reader.close();
            writer.close();

            processedBytes.set(1.0);
            inProgress.compareAndSet(true, false);
        } catch (Exception ex) {
            logger.error(ex.toString());
            throw new RuntimeException(ex);
        } finally {
            logger.info("Taken time: {}", sw);
            sw.stop();
            if (null != reader) {
                try {
                    reader.close();
                } catch (XMLStreamException ex) {
                    logger.error(ex.toString());
                }
            }
            if (null != writer) {
                try {
                    writer.close();
                } catch (XMLStreamException ex) {
                    logger.error(ex.toString());
                }
            }
            if (null != input) {
                try {
                    input.close();
                } catch (IOException ex) {
                    logger.error(ex.toString());
                }
            }
            if (null != output) {
                try {
                    output.close();
                } catch (IOException ex) {
                    logger.error(ex.toString());
                }
            }
        }
    }

    
    private JsonXMLConfig createConfig() {
        switch (inputFileType) {
            case JSON:
                /*
                * If the <code>multiplePI</code> property is
                * set to <code>true</code>, the StAXON reader will generate
                * <code>&lt;xml-multiple&gt;</code> processing instructions
                * which would be copied to the XML output.
                * These can be used by StAXON when converting back to JSON
                * to trigger array starts.
                * Set to <code>false</code> if you don't need to go back to JSON.
                 */
                return new JsonXMLConfigBuilder()
                        .multiplePI(false)
                        .build();
            case XML:
                /*
                * If we want to insert JSON array boundaries for multiple elements,
                * we need to set the <code>autoArray</code> property.
                * If our XML source was decorated with <code>&lt;?xml-multiple?&gt;</code>
                * processing instructions, we'd set the <code>multiplePI</code>
                * property instead.
                * With the <code>autoPrimitive</code> property set, element text gets
                * automatically converted to JSON primitives (number, boolean, null).
                 */
                return new JsonXMLConfigBuilder()
                        .autoArray(false)   //  if set to true then memory usage will increase
                        .autoPrimitive(true)
                        .prettyPrint(true)
                        .build();
            default:
                throw new RuntimeException("Unsupported file type: " + inputFileType.toString());
        }
    }

    private XMLEventReader createReader(JsonXMLConfig config, InputStream input) throws XMLStreamException {
        if (inputFileType == FileTypeEnum.XML) {
            return XMLInputFactory.newInstance().createXMLEventReader(input);
        } else if (inputFileType == FileTypeEnum.JSON) {
            return new JsonXMLInputFactory(config).createXMLEventReader(input);
        }

        throw new IllegalArgumentException("Unsupported file type: " + inputFileType);
    }

    private XMLEventWriter createWriter(JsonXMLConfig config, OutputStream output) throws XMLStreamException {
        if (inputFileType == FileTypeEnum.XML) {
            return new JsonXMLOutputFactory(config).createXMLEventWriter(output);
        } else if (inputFileType == FileTypeEnum.JSON) {
            return new PrettyXMLEventWriter(XMLOutputFactory.newInstance().createXMLEventWriter(output));
        }

        throw new IllegalArgumentException("Unsupported file type: " + inputFileType);
    }

    private void reset() {
        message.setText("");
    }

}
