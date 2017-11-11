/**
 * Copyright © 2017 Anton Mykolaienko. All rights reserved. Contacts: <amykolaienko@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fs.xml2json.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for loading properties from file and storing properties to file.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class PropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);

    private final File propertiesFile;

    /**
     * Creates instance of {@link PropertiesLoader}.
     *
     * @param propertiesFile file with properties
     */
    public PropertiesLoader(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    /**
     * Reads source file with properties and returns {@link Properties}
     * object with application properties.
     *
     * @return application properties
     * @throws FileNotFoundException if properties file not exist
     * @throws IOException if I/O exceptions occurs in FileInputStream
     */
    public Properties load() throws IOException {
        Properties properties = new Properties();

        LOGGER.debug("Loading properties from {}", propertiesFile.getAbsolutePath());

        if (propertiesFile.exists()) {
            try (InputStream in = new FileInputStream(propertiesFile)) {
                properties.load(in);
                LOGGER.debug("Loaded {} properties", properties.size());
            }
        } else {
            throw new FileNotFoundException("File '" + propertiesFile.getAbsolutePath() + "' is not exist");
        }

        return properties;
    }


    /**
     * Save properties to file.
     *
     * @param properties application properties to save
     */
    public void saveProperties(Properties properties) {
        File parentFolder = propertiesFile.getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(propertiesFile))) {
            StringBuilder sb = new StringBuilder();
            properties.entrySet().forEach(entry -> {
                if (!entry.getKey().toString().trim().isEmpty()) {
                    sb.append(entry.getKey().toString());
                    sb.append("=");
                    sb.append(entry.getValue().toString().replace("\\", "\\\\"));
                    sb.append("\n");
                }
            });

            os.write(sb.toString().getBytes());
            os.flush();
        } catch (IOException ex) {
            LOGGER.error(ex.toString());
        }
    }

}
