
package com.fs.xml2json;

import com.fs.xml2json.core.Config;
import java.io.File;
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
        options.addOption("o", Config.PAR_DESTINATION_FOLDER, true, "Path to folder for converted files\n\tExample: C:\\temp\\output");
        options.addOption("p", Config.PAR_SOURCE_FILE_PATTERN, true, "Pattern for filtering input files\n\tExample: *.json ");
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
                String sourceFolder = cmd.getOptionValue(Config.PAR_SOURCE_FOLDER);
                String destinationFolder = cmd.getOptionValue(Config.PAR_DESTINATION_FOLDER);
                String pattern = cmd.getOptionValue(Config.PAR_SOURCE_FILE_PATTERN);
                if (null == sourceFolder) {
                    throw new IllegalArgumentException("Parameter '--" + Config.PAR_SOURCE_FOLDER+ "' not set");
                }
                if (null == destinationFolder) {
                    throw new IllegalArgumentException("Parameter '--" + Config.PAR_DESTINATION_FOLDER+ "' not set");
                }
                File file = new File(sourceFolder);
                if (!file.exists()) {
                    throw new RuntimeException("File '" + sourceFolder + "' not found");
                } else if (!file.isDirectory()) {
                    throw new IllegalArgumentException("Mentioned path point to File while expected Directory");
                }
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
