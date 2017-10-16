
package com.fs.xml2json.listener;

import com.fs.xml2json.type.FileTypeEnum;
import java.io.File;

/**
 * Abstract class for common logic all specific listeners.
 *
 * @author Anton
 * @since 1.2.0
 */
public abstract class AbstractFileReadListener implements IFileReadListener {
    
    private long readBytes;
    private long buffer;
    private final long fileSize;
    
    // helper variable for XML, when we need to read file twice
    private int numberOfReads = 1;

    /**
     * Creates instance of Listener.
     * 
     * @param sourceFile source file
     */
    public AbstractFileReadListener(File sourceFile) {       
        FileTypeEnum fileType = FileTypeEnum.parseByFileName(sourceFile.getName());
        switch (fileType) {
            case XML:
                this.fileSize = sourceFile.length() * 2;
                this.numberOfReads += 1;    // XML file will be read twice (first time - for determining arrays)
                break;
            default:
                this.fileSize = sourceFile.length();
        }
    }
    
    /**
     * Will update listener each 1% of data.
     * 
     * @param bytes read bytes from input stream, must be grather than -1
     */
    @Override
    public void update(int bytes) {
        buffer += bytes;
        double delta = (double) buffer / (double) fileSize;
        if (delta >= 0.01) {
            readBytes += buffer;
            updateProgressInPercent((double) readBytes / (double) fileSize);
            buffer = 0;
        }
    }
    
    /**
     * Sets 100%.
     */
    @Override
    public void finished() {
        numberOfReads--;
        if (numberOfReads == 0) {
            if (buffer > 0) {
                readBytes += buffer;
                updateProgressInPercent((double) readBytes / (double) fileSize);
                buffer = 0;
            }
        }
    }
    
    /**
     * Updates listener that 1% have been read (1%, 2%, 3%... 100%).
     * 
     * @param newValue incremental value
     */
    abstract void updateProgressInPercent(double newValue);
    
}
