
package com.fs.xml2json.listener;

/**
 * Iterface for 
 *
 * @author Anton
 * @since 1.1.0
 */
public interface IFileReadListener {
    
    /**
     * Updates listener about number of bytes which have been read.
     * 
     * @param bytes processed bytes
     */
    void update(int bytes);
    
    /**
     * Updates listener about finishing file processing.
     */
    void finished();
}
