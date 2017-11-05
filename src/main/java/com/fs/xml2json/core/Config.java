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

/**
 * Constants.
 *
 * @author Anton
 * @since 1.0.0
 */
public final class Config {
    
    private Config() {
    }

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
