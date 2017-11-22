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

package com.fs.xml2json.cli;

import com.fs.xml2json.core.Config;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specific class which responsible for parsing input arguments and validation.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ApplicationCommandLine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationCommandLine.class);

    private static final Boolean DEFAULT_FORCE_OVERWRITE = false;
    private static final String PARAMETER_MISSING_TEPLATE = "Parameter '--%s' is not set";
    private static final String PARAM_SIGN = "--";
    /**
     * Size of help text.
     */
    private static final int HELP_WINDOW_SIZE = 120;
    private static final Options OPTIONS = new Options();

    static {
        OPTIONS.addOption("g", Config.PAR_NO_GUI, false,
                "Flag to start Application without GUI in command line mode");
        OPTIONS.addOption("s", Config.PAR_SOURCE_FOLDER, true,
                "Path to folder with files to convert\n\tExample: C:\\temp\\input");
        OPTIONS.addOption("d", Config.PAR_DESTINATION_FOLDER, true,
                "Path to folder for converted files\n\tExample: C:\\temp\\output");
        OPTIONS.addOption("p", Config.PAR_SOURCE_FILE_PATTERN, true,
                "Pattern for filtering input files\n\tExample: *.json");
        OPTIONS.addOption("o", Config.PAR_FORCE_OVERWRITE, false,
                "Force overwrite existing converted files (Default: false)");
    }

    private final CommandLine cmd;

    // source file or folder with files to convert.
    private File sourcePath = null;
    // path to destination folder, where converted files must be stored
    private File destinationPath = null;
    // pattern for filtering files
    private String pattern = null;
    // flag to overwrite existing files
    private Boolean forceOverwrite;

    /**
     * Private constructor.
     *
     * @param cmd parsed command line
     */
    private ApplicationCommandLine(CommandLine cmd) {
        this.cmd = cmd;
    }

    /**
     * Parses application's input arguments and returns {@link ApplicationCommandLine} with parsed values.
     *
     * @param args command line arguments
     * @return {@link ApplicationCommandLine}
     * @throws java.text.ParseException if there are any problems encountered
     * while parsing the command line tokens.
     * @throws FileNotFoundException if source folder not found (only for NoGUI mode)
     */
    public static ApplicationCommandLine parse(String[] args) throws java.text.ParseException, FileNotFoundException {
        CommandLineParser parser = new DefaultParser();

        ApplicationCommandLine cmd = null;
        try {
            cmd = new ApplicationCommandLine(parser.parse(OPTIONS, args, true));
            cmd.checkInputParameters();
        } catch (ParseException ex) {
            throw new java.text.ParseException(ex.getMessage(), 0);
        }

        return cmd;
    }

    /**
     * Prints description for application's parameters and examples.
     */
    public static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(HELP_WINDOW_SIZE, "\n  java -jar xml2json.jar "
                + PARAM_SIGN + Config.PAR_NO_GUI + " "
                + PARAM_SIGN + Config.PAR_SOURCE_FOLDER + "=[path_to_source_folder] "
                + PARAM_SIGN + Config.PAR_DESTINATION_FOLDER + "=[path_to_destination_folder] "
                + PARAM_SIGN + Config.PAR_SOURCE_FILE_PATTERN + "=[*.json|*.xml]",
                ", where:",
                OPTIONS,
                "example:\n  java -jar xml2json.jar "
                + PARAM_SIGN + Config.PAR_NO_GUI + " "
                + PARAM_SIGN + Config.PAR_SOURCE_FOLDER + "=C:\\temp\\input "
                + PARAM_SIGN + Config.PAR_DESTINATION_FOLDER + "=C:\\temp\\output "
                + PARAM_SIGN + Config.PAR_SOURCE_FILE_PATTERN + "=*.json\n\n");
    }

    /**
     * Checks input parameters and throws exception if mandatory values are missing.
     * <p>Creates Destination directories.
     * @throws FileNotFoundException if source folder is not exist
     */
    private void checkInputParameters() throws FileNotFoundException {
        boolean isNoGuiEnabled = cmd.hasOption(Config.PAR_NO_GUI);
        String sourceFolderTxt = cmd.getOptionValue(Config.PAR_SOURCE_FOLDER);
        String destinationFolderTxt = cmd.getOptionValue(Config.PAR_DESTINATION_FOLDER);
        String patternTxt = cmd.getOptionValue(Config.PAR_SOURCE_FILE_PATTERN);

        if (isNoGuiEnabled) {
            if (null == sourceFolderTxt) {
                throw new IllegalArgumentException(String.format(PARAMETER_MISSING_TEPLATE,
                        Config.PAR_SOURCE_FOLDER));
            }
            if (null == destinationFolderTxt) {
                throw new IllegalArgumentException(String.format(PARAMETER_MISSING_TEPLATE,
                        Config.PAR_DESTINATION_FOLDER));
            }
            if (null == patternTxt) {
                throw new IllegalArgumentException(String.format(PARAMETER_MISSING_TEPLATE,
                        Config.PAR_SOURCE_FILE_PATTERN));
            }
            File sourceFolder = new File(sourceFolderTxt);
            if (sourceFolder.exists() && sourceFolder.isFile()) {
                throw new IllegalArgumentException(String.format("Parameter '%s' should point "
                        + "to Directory not to File", Config.PAR_SOURCE_FILE_PATTERN));
            }
            if (!sourceFolder.exists()) {
                throw new FileNotFoundException("Directory '" + sourceFolderTxt + "' not found, nothing to convert...");
            } else if (sourceFolder.list().length == 0) {
                LOGGER.info("Source directory ('{}') is empty, nothing to convert", sourceFolderTxt);
            }

            File destinationFolder = new File(destinationFolderTxt);
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }
        }
    }

    /**
     * Returns <code>true</code> if application started with <code>nogui</code> flag.
     *
     * @return <code>true</code> or <code>false</code>
     */
    public boolean isNoGuiEnabled() {
        return cmd.hasOption(Config.PAR_NO_GUI);
    }

    /**
     * Returns link to source File (or Folder with files) to convert.
     *
     * @return link to File (or Folder with files) to convert
     */
    public File getSourceFolder() {
        if (null == sourcePath && isNoGuiEnabled()) {
            sourcePath = new File(cmd.getOptionValue(Config.PAR_SOURCE_FOLDER));
        }

        return sourcePath;
    }

    /**
     * Returns link to Folder where converted files must be stored.
     *
     * @return link to Folder
     */
    public File getDestinationFolder() {
        if (null == destinationPath && isNoGuiEnabled()) {
            destinationPath = new File(cmd.getOptionValue(Config.PAR_DESTINATION_FOLDER));
        }

        return destinationPath;
    }

    /**
     * Returns pattern for filtering files in source folder.
     *
     * @return pattern value
     */
    public String getPattern() {
        if (null == pattern && isNoGuiEnabled()) {
            pattern = cmd.getOptionValue(Config.PAR_SOURCE_FILE_PATTERN);
        }

        return pattern;
    }

    /**
     * Returns <code>true</code> if existing files must be overwritten at Destination folder,
     * othervise returns <code>false</code> (Default value).
     *
     * @return <code>true</code> or <code>false</code>
     */
    public boolean isForceOverwrite() {
        if (null == forceOverwrite) {
            forceOverwrite = cmd.hasOption(Config.PAR_FORCE_OVERWRITE) || DEFAULT_FORCE_OVERWRITE;
        }

        return forceOverwrite;
    }
}
