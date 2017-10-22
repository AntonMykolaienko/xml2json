
package com.fs.xml2json.util;

import com.fs.xml2json.core.Config;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static utils.
 *
 * @author Anton
 * @since 1.1.0
 */
public class ConfigUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    
    private static final Properties applicationProperties = new Properties();

    /**
     * Private constructor.
     */
    private ConfigUtils() {
    }
    
    /**
     * Reads stored path to file to convert and return this path or null if file with path not found.
     * 
     * @return path to file or null
     */
    public static String readLastPath() {
        if (applicationProperties.isEmpty()) {
            loadApplicationProperties();
        }
        
        String path = applicationProperties.getProperty(Config.LAST_DIRECTORY);
        if (null != path && !path.isEmpty()) {
            File f = new File(path);
            if (!f.exists()) {
                return null;
            }
        }
        return path;
    }
    
    /**
     * Saves files directory to Application config file.
     * 
     * @param path path to last opened file
     */
    public static void saveLastPath(File path) {
        if (applicationProperties.isEmpty()) {
            loadApplicationProperties();
        }
        String formattedPath = path.getParent().replace("\\", "\\\\");
        String oldValue = (String)applicationProperties.get(Config.LAST_DIRECTORY);
        if (null == oldValue || formattedPath.equalsIgnoreCase(oldValue)) {
            applicationProperties.put(Config.LAST_DIRECTORY, formattedPath);
            
            saveApplicationProperties();
        }
    }
    
    
    /**
     * Loads properties from application's config file.
     */
    private static void loadApplicationProperties() {
        if (applicationProperties.isEmpty()) {
            logger.debug("Application properties haven't been loaded yet...");
            String usersDir = System.getProperty("user.home");
            File appDir = new File(usersDir, Config.APPLICATION_FOLDER_NAME);
            if (appDir.exists()) {
                File configFile = new File(appDir, Config.APPLICATION_STORAGE_FILE_NAME);
                if (configFile.exists()) {
                    try (InputStream in = new FileInputStream(configFile)) {
                        applicationProperties.load(in);
                        logger.debug("Loaded {} properties", applicationProperties.size());
                    } catch (IOException ex) {
                        logger.error(ex.toString());
                    }
                }
            } else {
                appDir.mkdir();
            }
        }
    }
    
    private static void saveApplicationProperties() {
        String usersDir = System.getProperty("user.home");
        File appDir = new File(usersDir, Config.APPLICATION_FOLDER_NAME);
        File configFile = new File(appDir, Config.APPLICATION_STORAGE_FILE_NAME);
        if (!appDir.exists()) {
            appDir.mkdir();
        } 
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(configFile))) {
            StringBuilder sb = new StringBuilder();
            applicationProperties.entrySet().forEach(entry -> {
                if (null != entry.getKey() && !entry.getKey().toString().trim().isEmpty()) {
                    sb.append(entry.getKey().toString());
                    sb.append("=");
                    sb.append(entry.getValue().toString());                    
                    sb.append("\n");  
                }
            });
            
            os.write(sb.toString().getBytes());
            os.flush();
        } catch (IOException ex) {
            logger.error(ex.toString());
        }
    }
}
