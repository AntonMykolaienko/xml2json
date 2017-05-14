
package com.fs.xml2json.controller;

/**
 * Abstract controller for all controllers.
 *
 * @author Anton
 * @since 1.0.0
 */
public abstract class AbstractController {
    
    private static final String UNNOWN_VERSION = "unknown";

    protected final String getVersion() {
        String version = AbstractController.class.getPackage().getImplementationVersion();
        if (null == version || version.isEmpty()) {
            version = UNNOWN_VERSION;
        }
        return version;
    }
}
