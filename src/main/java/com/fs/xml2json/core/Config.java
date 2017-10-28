
package com.fs.xml2json.core;

/**
 * Constants.
 *
 * @author Anton
 * @since 1.0.0
 */
public final class Config {

    /**
     * Name for Start button to start convert process.
     */
    public static final String START_BUTTON_START  = "Start convert";
    /**
     * Button's name when convert process have been started.
     */
    public static final String START_BUTTON_CANCEL = "Cancel";

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

    
    /******************************************************/
    /**************** Input parameter names ***************/
    /******************************************************/
    
    /**
     * Parameter name for Source folder.
     */
    public static final String PAR_SOURCE_FOLDER = "sourceFolder";
    /**
     * Parameter name for Destination folder.
     */
    public static final String PAR_DESTINATION_FOLDER = "destinationFolder";
    /**
     * Parameter name for file pattern
     */
    public static final String PAR_SOURCE_FILE_PATTERN = "pattern";
    /**
     * Parameter name for disabling GUI.
     */
    public static final String PAR_NO_GUI = "noGui";
    /**
     * Parameter name for overwriting converted files.
     */
    public static final String PAR_FORCE_OVERWRITE = "overwrite";
}
