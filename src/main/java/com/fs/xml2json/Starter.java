
package com.fs.xml2json;

import com.fs.xml2json.cli.ApplicationCommandLine;
import com.fs.xml2json.filter.CustomPatternFileFilter;
import com.fs.xml2json.listener.CmdFileReadListener;
import com.fs.xml2json.service.ConverterService;
import com.fs.xml2json.util.ApplicationUtils;
import com.fs.xml2json.util.ConverterUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
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

    
    private final String[] args;
    private final AtomicBoolean isCanceled = new AtomicBoolean(false);
    private ConverterService service;
    
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
    
    /**
     * Constructor.
     * 
     * @param args command line arguments
     */
    public Starter(String[] args) {
        this.args = args;
    }


    public void start() {
        logger.info("Starting Xml2Json converter (v." + ApplicationUtils.getVersion() + ")");

        ApplicationCommandLine.printHelp();

        try {
            ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
            
            if (cmd.isNoGuiEnabled()) {
                noGuiHandler(cmd);
            } else {
                GuiStarter.main(args);
            }
        } catch (ParseException | FileNotFoundException ex) {
            logger.error(ex.toString());
        } catch (IOException ex) {
            logger.debug(ex.toString());    // unimportant exception at this point
        }
    }
    
    /**
     * Performs converting files in batch mode without GUI.
     * 
     * @param cmd application argumants
     */
    private void noGuiHandler(ApplicationCommandLine cmd) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            CustomPatternFileFilter filter = new CustomPatternFileFilter(cmd.getPattern());
            long numberOfFiles = Stream.of(cmd.getSourceFolder().listFiles())
                    .filter(filter::accept).count();

            if (numberOfFiles > 0) {
                logger.info("Found {} files", numberOfFiles);
                service = new ConverterService();
                int numberOfProcessed = 0;
                for (File file : cmd.getSourceFolder().listFiles()) {
                    if (isCanceled.get()) {
                        break;
                    }
                    if (filter.accept(file)) {
                        processFile(file, br, cmd);
                        logger.info("Processed {} of {}", ++numberOfProcessed, numberOfFiles);
                    } else {
                        logger.debug("File '{}' will be skipped", file.getAbsolutePath());
                    }
                }
            } else {
                logger.info("No one file found for '{}' pattern", cmd.getPattern());
            }
        }
    }
    
    /**
     * Converts file <code>file</code> and stores converted content in <code>destinationFolder</code> 
     * with same name as source file.
     * 
     * @param file file which must be converted
     * @param br buffered reader
     * @param cmd application arguments
     */
    private void processFile(File file, BufferedReader br, ApplicationCommandLine cmd) 
            throws IOException {
        logger.debug("Start processing '{}'", file.getAbsolutePath());
        File convertedFile = ConverterUtils.getConvertedFile(file, cmd.getDestinationFolder());
        boolean isOverwrite = true;
        if (convertedFile.exists() && !cmd.isForceOverwrite()) {    // overwrite?
            isOverwrite = overwriteFile(br, convertedFile);
        }
        if (isOverwrite) {
            try {
                service.convert(file, convertedFile, new CmdFileReadListener(file), isCanceled);
            } catch (Exception ex) {
                logger.error(ex.toString());
            }
        }
    }
    
    private boolean overwriteFile(BufferedReader br, File destinationFile) throws IOException {
        boolean isOverwrite = false;
        System.out.print(String.format("%nFile '%s' already exists, overwrite? [y/n]: ", 
                                    destinationFile.getAbsolutePath()));

        boolean isCorrectAnswer = false;
        while (!isCorrectAnswer) {
            String answer = br.readLine();
            if (null != answer) {
                if (answer.trim().equalsIgnoreCase("Y")) {
                    isOverwrite = true;
                    isCorrectAnswer = true;
                } else if (answer.trim().equalsIgnoreCase("n")) {
                    isCorrectAnswer = true;
                } else {
                    System.out.print("Expected [y/n]: ");
                }
            }
        }
        
        return isOverwrite;
    }

    public void stop() {
        isCanceled.compareAndSet(false, true);
    }

}
