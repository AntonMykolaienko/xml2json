
package com.fs.xml2json.util;

/**
 * Uncategorized utility methods.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ApplicationUtils {

    public static final String UNNOWN_VERSION = "unknown";
    
    
    private ApplicationUtils() {
    }

    /**
     * Returns application version from Manifest-file.
     *
     * @return string with application version
     */
    public static final String getVersion() {
        String version = ApplicationUtils.class.getPackage().getImplementationVersion();
        if (null == version || version.isEmpty()) {
            version = UNNOWN_VERSION;
        }
        return version;
    }
}
