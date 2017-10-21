
package com.fs.xml2json.type;

/**
 * Custom exception for unsupported file types.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class UnsupportedFileType extends RuntimeException {

    /**
     * Creates instance of {@link UnsupportedFileType}
     * 
     * @param message custom message
     */
    public UnsupportedFileType(String message) {
        super(message);
    }

}
