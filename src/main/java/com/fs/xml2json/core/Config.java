
package com.fs.xml2json.core;

/**
 * Constants.
 *
 * @author Anton
 * @since 1.0.0
 */
public class Config {
    
    /**
     * Name for Start button to start convert process.
     */
    public static final String START_BUTTON__START  = "Start convert";
    /**
     * Button's name when convert process have been started.
     */
    public static final String START_BUTTON__CANCEL = "Cancel";
    
    /**
     * Folder's name where all configuration files will be stored.
     */
    public static final String APPLICATION_FOLDER_NAME = ".xml2json";
    /**
     * File name where all application configs will be stored.
     */
    public static final String APPLICATION_STORAGE_FILE_NAME = "config.txt";
 
    /**
     * Link for Donate on PayPal.
     */
    public static final String DONATE_LINK = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=33R3LMBMX3R96";
    
    
    /**
     * Property name in config file which will ocntain last opened directory.
     */
    public static final String LAST_DIRECTORY = "browse.lastDirectory";
}
