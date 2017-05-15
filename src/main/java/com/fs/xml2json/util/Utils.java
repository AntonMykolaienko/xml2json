
package com.fs.xml2json.util;

import com.fs.xml2json.core.Config;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static utils.
 *
 * @author Anton
 * @since 1.1.0
 */
public class Utils {
    
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * Reads stored path to file to convert and return this path or null if file with path not found.
     * 
     * @return path to file or null
     */
    public static String readLastPath() {
        String usersDir = System.getProperty("user.home");
        File appDir = new File(usersDir, Config.APPLICATION_FOLDER_NAME);
        if (appDir.exists()) {
            File configFile = new File(appDir, Config.APPLICATION_STORAGE_FILE_NAME);
            if (configFile.exists()) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(configFile));
                    String line;
                    while (null != (line = br.readLine())) {
                        return line;
                    }
                } catch (IOException ex) {
                    logger.error(ex.toString());
                } finally {
                    if (null != br) {
                        try {
                            br.close();
                        } catch (IOException ex) {}
                    }
                }
            }
        } else {
            appDir.mkdir();
        }
        
        return null;
    }
    
    
    /**
     * Saves files directory to Application config file.
     * 
     * @param path path to last opened file
     */
    public static void saveLastPath(File path) {
        String usersDir = System.getProperty("user.home");
        File appDir = new File(usersDir, Config.APPLICATION_FOLDER_NAME);
        File configFile = new File(appDir, Config.APPLICATION_STORAGE_FILE_NAME);
        if (!appDir.exists()) {
            appDir.mkdir();
        } 
        OutputStream os = null;
        try {
            os = new FileOutputStream(configFile);
            os.write(path.getParent().getBytes());
            os.flush();
        } catch (IOException ex) {
            logger.error(ex.toString());
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException ex) {}
            }
        }
    }
}
