
package com.fs.xml2json.controller;

/**
 * Iterface for 
 *
 * @author Anton
 * @since 1.1.0
 */
public interface IFileReadUpdater {
    
    /**
     * Updates listener about number of bytes which have been read.
     * 
     * @param bytes processed bytes
     */
    void update(int bytes);
}
