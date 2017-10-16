
package com.fs.xml2json;

import com.fs.xml2json.core.Config;
import com.fs.xml2json.filter.CustomPatternFileFilter;
import com.fs.xml2json.listener.CmdFileReadListener;
import com.fs.xml2json.service.ConverterService;
import com.fs.xml2json.type.FileTypeEnum;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command-line interface.
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public class Starter {
    
    private static final Logger logger = LoggerFactory.getLogger(Starter.class);
    
    
    private static final Options options = new Options();
    
    static {
        options.addOption("g", Config.PAR_NO_GUI, false, 
                "Flag to start Application without GUI in command line mode");
        options.addOption("s", Config.PAR_SOURCE_FOLDER, true, 
                "Path to folder with files to convert\n\tExample: C:\\temp\\input");
        options.addOption("d", Config.PAR_DESTINATION_FOLDER, true, 
                "Path to folder for converted files\n\tExample: C:\\temp\\output");
        options.addOption("p", Config.PAR_SOURCE_FILE_PATTERN, true, 
                "Pattern for filtering input files\n\tExample: *.json");
        options.addOption("o", Config.PAR_FORCE_OVERWRITE, false, 
                "Force overwrite existing converted files (Default: false)");
    }

    
    private final String[] args;
    private final AtomicBoolean isCanceled = new AtomicBoolean(false);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Starter starter = new Starter(args);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down application starter ...");
            starter.stop();
        }, "ShutdownThread"));

        starter.start();
        
        
    }
    
    
    public Starter(String[] args) {
        this.args = args;
    }
    
    
    public void start() {
        logger.info("Starting Xml2Json converter (v." + getApplicationVersion() + ")");
        
        printHelp();
        
        CommandLineParser parser = new DefaultParser();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            CommandLine cmd = parser.parse(options, args, true);
            
            if (cmd.hasOption(Config.PAR_NO_GUI)) {
                String sourceFolderTxt = cmd.getOptionValue(Config.PAR_SOURCE_FOLDER);
                String destinationFolderTxt = cmd.getOptionValue(Config.PAR_DESTINATION_FOLDER);
                String patternTxt = cmd.getOptionValue(Config.PAR_SOURCE_FILE_PATTERN);
                boolean forceOverwrite = cmd.hasOption(Config.PAR_FORCE_OVERWRITE);
                
                // adjust heap-size
                setHeapMaxSize();
                
                checkInputParameters(cmd);
                
                File sourceFolder = new File(sourceFolderTxt);
                File destinationFolder = new File(destinationFolderTxt);
                
                CustomPatternFileFilter filter = new CustomPatternFileFilter(patternTxt);
                int numberOfFiles = 0;
                for (File file : sourceFolder.listFiles()) {
                    if (filter.accept(file)) {
                        numberOfFiles++;
                    }
                }
                if (numberOfFiles > 0) {
                    logger.info("Found {} files", numberOfFiles);
                    ConverterService service = new ConverterService();
                    int numberOfProcessed = 0;
                    for (File file : sourceFolder.listFiles()) {
                        if (isCanceled.get()) {
                            break;
                        }
                        if (filter.accept(file)) {
                            //logger.info("Start processing '{}'", file.getAbsolutePath());
                            File convertedFile = getConvertedFile(file, destinationFolder);
                            boolean skip = false;
                            if (convertedFile.exists() && !forceOverwrite) {
                                skip = true;
                                // overwrite ?
                                System.out.print(String.format("\nFile '%s' already exists, overwrite? [y/n]: ", 
                                        convertedFile.getAbsolutePath()));
                                
                                boolean isCorrectAnswer = false;
                                while (!isCorrectAnswer) {
                                    String answer = br.readLine();
                                    if (null != answer) {
                                        if (answer.trim().equalsIgnoreCase("Y")) {
                                            skip = false;
                                            isCorrectAnswer = true;
                                        } else if (answer.trim().equalsIgnoreCase("n")) {
                                            isCorrectAnswer = true;
                                        } else {
                                            System.out.print("Expected [y/n]: ");
                                        }
                                    }
                                }
                            }
                            try {
                                if (!skip) {
                                    service.convert(file, convertedFile, new CmdFileReadListener(file), isCanceled);
                                }
                            } catch (Exception ex) {
                                logger.error(ex.toString());
                            } finally {
                                numberOfProcessed++;
                            }
                            logger.info("Processed {} of {}", numberOfProcessed, numberOfFiles);
                        } else {
                            logger.debug("File '{}' will be skipped", file.getAbsolutePath());
                        }
                    }
                } else {
                    logger.info("No one file found for '{}' pattern", patternTxt);
                }
            } else {
                GuiStarter.main(args);
            }
        } catch (ParseException  ex) {
            logger.error(ex.toString());
        } catch (IOException ex) {
        }
    }
    
    public void stop() {
        isCanceled.compareAndSet(false, true);
    }
    
    /**
     * Prints description for application's parameters and examples.
     */
    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(120, "\n  java -jar xml2json.jar --" + Config.PAR_NO_GUI + " "
                + "--" + Config.PAR_SOURCE_FOLDER + "=[path_to_source_folder] "
                + "--" + Config.PAR_DESTINATION_FOLDER + "=[path_to_destination_folder] "
                + "--" + Config.PAR_SOURCE_FILE_PATTERN + "=[*.json|*.xml]", 
                ", where:", 
                options, 
                "example:\n  java -jar xml2json.jar --" + Config.PAR_NO_GUI + " "
                + "--"+Config.PAR_SOURCE_FOLDER + "=C:\\temp\\input "
                + "--" + Config.PAR_DESTINATION_FOLDER + "=C:\\temp\\output "
                + "--" + Config.PAR_SOURCE_FILE_PATTERN + "=*.json\n\n");
    }
    
    /**
     * Checks input parameters and throws exception if mandatory values are missing.
     * Creates Destination directories.
     * 
     * @param cmd command line
     */
    private void checkInputParameters(CommandLine cmd) {
        String sourceFolderTxt = cmd.getOptionValue(Config.PAR_SOURCE_FOLDER);
        String destinationFolderTxt = cmd.getOptionValue(Config.PAR_DESTINATION_FOLDER);
        String patternTxt = cmd.getOptionValue(Config.PAR_SOURCE_FILE_PATTERN);

        if (null == sourceFolderTxt) {
            throw new IllegalArgumentException("Parameter '--" + Config.PAR_SOURCE_FOLDER + "' not set");
        }
        if (null == destinationFolderTxt) {
            throw new IllegalArgumentException("Parameter '--" + Config.PAR_DESTINATION_FOLDER + "' not set");
        }
        if (null == patternTxt) {
            throw new IllegalArgumentException("Parameter '--" + Config.PAR_SOURCE_FILE_PATTERN + "' not set");
        }
        File sourceFolder = new File(sourceFolderTxt);
        if (!sourceFolder.exists()) {
            throw new RuntimeException("Directory '" + sourceFolderTxt + "' not found, nothing to convert...");
        } else if (sourceFolder.list().length == 0) {
            logger.info("Source directory ('{}') is empty, nothing to convert", sourceFolderTxt);
        }
        
        File destinationFolder = new File(destinationFolderTxt);
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }
    }
    
    /**
     * Returns link to converted file with same name as source file, but with 
     * another extension (xml -> json, json -> xml).
     * 
     * @param sourceFile file to convert
     * @param destinationFolder path to destination folder
     * @return link to converted file
     */
    private File getConvertedFile(File sourceFile, File destinationFolder) {
        FileTypeEnum fileType = FileTypeEnum.parseByFileName(sourceFile.getName());
        if (null == fileType) {
            return null;
        }
        String convertedFileName = "";
        String fileNameWithoutExtension = sourceFile.getName()
                .substring(0, sourceFile.getName().lastIndexOf("."));
        switch (fileType) {
            case JSON:
                convertedFileName = fileNameWithoutExtension + FileTypeEnum.XML.getExtension();
                break;
            case XML:
                convertedFileName = fileNameWithoutExtension + FileTypeEnum.JSON.getExtension();
                break;
            default:
                throw new RuntimeException("Unsupported file's extension");
        }
        
        return new File(destinationFolder, convertedFileName);
    }
    
    /**
     * Returns application's version from POM-file.
     *
     * @return application's version
     */
    private String getApplicationVersion() {
        String version = Starter.class.getPackage().getImplementationVersion();
        if (null == version) {
            version = "unknown";
        }
        return version;
    }
    
    private void setHeapMaxSize() {
        // TODO: implement me
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        mxBean.getName();
        //MonitoredHost
    }
    
}
