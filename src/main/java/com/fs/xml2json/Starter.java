
package com.fs.xml2json;

import com.fs.xml2json.cli.ApplicationCommandLine;
import com.fs.xml2json.filter.CustomPatternFileFilter;
import com.fs.xml2json.listener.CmdFileReadListener;
import com.fs.xml2json.listener.IFileReadListener;
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

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
            
            if (cmd.isNoGuiEnabled()) {
                CustomPatternFileFilter filter = new CustomPatternFileFilter(cmd.getPattern());
                long numberOfFiles = Stream.of(cmd.getSourceFolder().listFiles())
                        .filter(file -> filter.accept(file))
                        .count();

                if (numberOfFiles > 0) {
                    logger.info("Found {} files", numberOfFiles);
                    service = new ConverterService();
                    int numberOfProcessed = 0;
                    for (File file : cmd.getSourceFolder().listFiles()) {
                        if (isCanceled.get()) {
                            break;
                        }
                        if (filter.accept(file)) {
                            logger.debug("Start processing '{}'", file.getAbsolutePath());
                            File convertedFile = ConverterUtils.getConvertedFile(file, cmd.getDestinationFolder());
                            boolean skip = false;
                            if (convertedFile.exists() && !cmd.isForceOverwrite()) {
                                skip = true;
                                // overwrite ?
                                System.out.print(String.format("%nFile '%s' already exists, overwrite? [y/n]: ", 
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
                            if (!skip) {
                                converFile(file, convertedFile, new CmdFileReadListener(file));
                            }
                            logger.info("Processed {} of {}", ++numberOfProcessed, numberOfFiles);
                        } else {
                            logger.debug("File '{}' will be skipped", file.getAbsolutePath());
                        }
                    }
                } else {
                    logger.info("No one file found for '{}' pattern", cmd.getPattern());
                }
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
     * Converts file <code>fileToConvert</code> and stores converted content in <code>convertedFile</code>
     * 
     * @param fileToConvert file which must be converted
     * @param convertedFile destination file
     * @param listener progress listener
     */
    private void converFile(File fileToConvert, File convertedFile, IFileReadListener listener) {
        try {
            service.convert(fileToConvert, convertedFile, listener, isCanceled);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }

    public void stop() {
        isCanceled.compareAndSet(false, true);
    }

}
