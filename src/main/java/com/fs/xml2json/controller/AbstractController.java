package com.fs.xml2json.controller;

import javafx.stage.Stage;

/**
 * Abstract controller for all controllers.
 *
 * @author Anton
 * @since 1.0.0
 */
public abstract class AbstractController {

    private static final String UNNOWN_VERSION = "unknown";

    /**
     * Returns application version from Manifest-file.
     *
     * @return string with application version
     */
    protected final String getVersion() {
        String version = AbstractController.class.getPackage().getImplementationVersion();
        if (null == version || version.isEmpty()) {
            version = UNNOWN_VERSION;
        }
        return version;
    }

    private void showCustomAlert(Stage owner, String text) {
        
    }
}
