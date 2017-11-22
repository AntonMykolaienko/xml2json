/**
 * Copyright © 2016-2017 Anton Mykolaienko. All rights reserved. Contacts: <amykolaienko@gmail.com>
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

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application properties.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ApplicationProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationProperties.class);

    private PropertiesLoader loader;
    private boolean isInitialized = false;
    private final Properties properties = new Properties();


    /**
     * Creates instance of Application properties.
     *
     * @param loader properties loader
     */
    public ApplicationProperties(PropertiesLoader loader) {
        if (null == loader) {
            throw new IllegalArgumentException("Properties loader cannot be null");
        }
        this.loader = loader;
    }


    /**
     * Returns path to last opened folder.
     *
     * @return path to folder or null if path not exist or value is empty
     */
    public String getLastOpenedPath() {
        if (!isInitialized) {
            loadProperties();
        }

        String path = properties.getProperty(Config.LAST_DIRECTORY);
        if (null == path || path.trim().isEmpty()) {
            return null;
        } else {
            File f = new File(path);
            if (!f.exists()) {
                return null;
            }
        }

        return path;
    }

    /**
     * Saves file directory to Application config file.
     * <p>Will be used parent File as last directory.
     *
     * @param path path to last opened file
     */
    public void saveLastOpenedPath(File path) {
        if (!isInitialized) {
            loadProperties();
        }
        String formattedPath = path.getParent();
        String oldValue = (String) properties.get(Config.LAST_DIRECTORY);
        if (null == oldValue || !formattedPath.equalsIgnoreCase(oldValue)) {
            properties.put(Config.LAST_DIRECTORY, formattedPath);

            loader.saveProperties(properties);
        }
    }

    /**
     * Sets loader.
     *
     * @param loader properties loader
     */
    public void setPropertiesLoader(PropertiesLoader loader) {
        if (loader == null) {
            throw new IllegalArgumentException("Properties loader cannot be null");
        }
        this.loader = loader;
    }


    /**
     * Loads properties from file to current instance of Application properties.
     */
    private void loadProperties() {
        try {
            Properties props = loader.load();
            props.forEach(properties::put);
        } catch (IOException ex) {
            LOGGER.error(ex.toString());
        }
        isInitialized = true;
    }

}
