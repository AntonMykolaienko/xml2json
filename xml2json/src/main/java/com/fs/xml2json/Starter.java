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
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command-line interface.
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public class Starter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);


    private final String[] args;
    private final AtomicBoolean isCanceled = new AtomicBoolean(false);
    private ConverterService service;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Starter starter = new Starter(args);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down application starter ...");
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

    /**
     * Starts application.
     */
    public final void start() {
        LOGGER.info("Starting Xml2Json converter (v." + ApplicationUtils.getVersion() + ")");

        ApplicationCommandLine.printHelp();

        try {
            ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);

            if (cmd.isNoGuiEnabled()) {
                noGuiHandler(cmd);
            } else {
                GuiStarter.main(args);
            }
        } catch (ParseException | FileNotFoundException ex) {
            LOGGER.error(ex.toString());
        } catch (IOException ex) {
            LOGGER.debug(ex.toString());    // unimportant exception at this point
        }
    }

    /**
     * Performs converting files in batch mode without GUI.
     *
     * @param cmd application argumants
     * @throws IOException nested exception from {@link #overwriteFile(java.io.File)}
     */
    private void noGuiHandler(ApplicationCommandLine cmd) throws IOException {
        CustomPatternFileFilter filter = new CustomPatternFileFilter(cmd.getPattern());
        long numberOfFiles = Stream.of(cmd.getSourceFolder().listFiles())
                .filter(filter::accept).count();

        if (numberOfFiles > 0) {
            LOGGER.info("Found {} files", numberOfFiles);
            service = new ConverterService();
            int numberOfProcessed = 0;
            for (File file : cmd.getSourceFolder().listFiles()) {
                if (isCanceled.get()) {
                    break;
                }
                if (filter.accept(file)) {
                    processFile(file, cmd);
                    LOGGER.info("Processed {} of {}", ++numberOfProcessed, numberOfFiles);
                } else {
                    LOGGER.debug("File '{}' will be skipped", file.getAbsolutePath());
                }
            }
        } else {
            LOGGER.info("No one file found for '{}' pattern", cmd.getPattern());
        }
    }

    /**
     * Converts file <code>file</code> and stores converted content in <code>destinationFolder</code>
     * with same name as source file.
     *
     * @param file file which must be converted
     * @param cmd application arguments
     * @throws IOException when {@link #overwriteFile(java.io.File)} throw exception
     */
    private void processFile(File file, ApplicationCommandLine cmd)
            throws IOException {
        LOGGER.debug("Start processing '{}'", file.getAbsolutePath());
        File convertedFile = ConverterUtils.getConvertedFile(file, cmd.getDestinationFolder());
        boolean isOverwrite = true;
        if (convertedFile.exists() && !cmd.isForceOverwrite()) {    // overwrite?
            isOverwrite = overwriteFile(convertedFile);
        }
        if (isOverwrite) {
            try {
                service.convert(file, convertedFile, new CmdFileReadListener(file), isCanceled);
            } catch (IOException | XMLStreamException ex) {
                LOGGER.error(ex.toString());
            }
        }
    }

    /**
     * Parses input text (expected "Y" or "n") and returns <code>true</code> if file needs to be overwritten.
     *
     * @param destinationFile destination file
     * @return <code>true</code> if need to overwrite file, otherwise return <code>false</code>
     * @throws IOException if some errors occurs in buffered reader
     */
    private boolean overwriteFile(File destinationFile) throws IOException {
        boolean isOverwrite = false;
        System.out.print(String.format("%nFile '%s' already exists, overwrite? [y/n]: ",
                                    destinationFile.getAbsolutePath()));

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
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
        }

        return isOverwrite;
    }

    /**
     * Stops application and release resources.
     */
    public final void stop() {
        isCanceled.compareAndSet(false, true);
    }

}
