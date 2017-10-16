
package com.fs.xml2json.listener;

import java.io.File;
import javafx.beans.property.DoubleProperty;

/**
 * Specific listener for GUI. Will update convertation progress in graphical interface.
 *
 * @author Anton
 * @since 1.2.0
 */
public class GuiFileReadListener extends AbstractFileReadListener {

    private final DoubleProperty processedBytes;
    // helper variable for XML, when we need to read file twice
    private int numberOfReads = 1;

    /**
     * Creates instance of GuiFileReadListener.
     * 
     * @param processedBytes giu object wich indicates progress
     * @param sourceFile source file
     */
    public GuiFileReadListener(DoubleProperty processedBytes, File sourceFile) {
        super(sourceFile);
        this.processedBytes = processedBytes;
        if (isXml()) {
            numberOfReads += 1;
        }
    }


    @Override
    public void finished() {
        numberOfReads--;
        if (0 == numberOfReads) {
            processedBytes.set(1.0);
        }
    }

    @Override
    void updateProgressInPercent(double newValue) {
        processedBytes.set(newValue); 
    }

}
