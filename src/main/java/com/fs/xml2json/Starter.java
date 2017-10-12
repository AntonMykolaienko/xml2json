
package com.fs.xml2json;

import com.fs.xml2json.core.Config;
import com.fs.xml2json.filter.CustomPatternFileFilter;
import java.io.File;
import java.util.regex.Pattern;
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
        options.addOption("g", Config.PAR_NO_GUI, false, "Flag to start Application without GUI in command line mode");
        options.addOption("s", Config.PAR_SOURCE_FOLDER, true, "Path to folder with files\n\tExample: C:\\temp\\input");
        options.addOption("d", Config.PAR_DESTINATION_FOLDER, true, "Path to folder for converted files\n\tExample: C:\\temp\\output");
        options.addOption("p", Config.PAR_SOURCE_FILE_PATTERN, true, "Pattern for filtering input files\n\tExample: *.json");
        options.addOption("o", Config.PAR_FORCE_OVERWRITE, false, "Force overwrite existing converted files");
    }

    
    private String[] args;
    
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
        
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Xml2json Converter", options);
        System.out.println("Example:\n"
                + "\tjava -jar xml2json.jar --" + Config.PAR_NO_GUI + " "
                + "--"+Config.PAR_SOURCE_FILE_PATTERN + "=C:\\temp\\input "
                + "--" + Config.PAR_DESTINATION_FOLDER + "=C:\\temp\\output "
                + "--" + Config.PAR_SOURCE_FILE_PATTERN + "=*.json "
                );
        
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args, true);
            
            if (cmd.hasOption(Config.PAR_NO_GUI)) {
                String sourceFolderTxt = cmd.getOptionValue(Config.PAR_SOURCE_FOLDER);
                String destinationFolder = cmd.getOptionValue(Config.PAR_DESTINATION_FOLDER);
                String patternTxt = cmd.getOptionValue(Config.PAR_SOURCE_FILE_PATTERN);
                String overwrite = cmd.getOptionValue(Config.PAR_FORCE_OVERWRITE);
                if (null == sourceFolderTxt) {
                    throw new IllegalArgumentException("Parameter '--" + Config.PAR_SOURCE_FOLDER + "' not set");
                }
                if (null == destinationFolder) {
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
                
                CustomPatternFileFilter filter = new CustomPatternFileFilter(patternTxt);
                for (File file : sourceFolder.listFiles()) {
                    //if (pattern.matcher(file.getName().matches(patternTxt)).matches()) {
                    if (filter.accept(file)) {
                        logger.info("Start processing '{}'", file.getAbsolutePath());
                    } else {
                        logger.debug("File '{}' will be skipped", file.getAbsolutePath());
                    }
                }
                // TODO: call ConverterService
//                if (filePath.toLowerCase().endsWith(JSON_EXTENSION)) {
//                    convertFileJsonToXml(file.toURI());
//                } else if (filePath.toLowerCase().endsWith(XML_EXTENSION)) {
//                    convertFileXmlToJson(file.toURI());
//                } else {
//                    throw new IllegalArgumentException("Mentioned path point to not supported file type. Only JSON and XML files supported.");
//                }
            } else {
                GuiStarter.main(args);
            }
        } catch (ParseException ex) {
            logger.error(ex.toString());
        }
    }
    
    public void stop() {
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
    
}
