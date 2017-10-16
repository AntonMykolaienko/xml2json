
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
    private FileTypeEnum fileType;

    /**
     * Creates instance of Listener.
     * 
     * @param sourceFile source file
     */
    public AbstractFileReadListener(File sourceFile) {       
        this.fileType = FileTypeEnum.parseByFileName(sourceFile.getName());
        switch (fileType) {
            case XML:
                this.fileSize = sourceFile.length() * 2;
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
        if (delta > 0.01) {
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
        updateProgressInPercent(1.0d);
    }
    
    /**
     * Updates listener that 1% have been read (1%, 2%, 3%... 100%).
     * 
     * @param newValue incremental value
     */
    abstract void updateProgressInPercent(double newValue);
    
    
    /**
     * Returns <code>true</code> if file is XML, otherwise return <code>false</code>.
     * 
     * @return true if file is XML
     */
    protected final boolean isXml() {
        return fileType == FileTypeEnum.XML;
    }
}
