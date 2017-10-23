
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

    /**
     * Creates instance of GuiFileReadListener.
     * 
     * @param processedBytes GUI object wich indicates progress (max 1.0)
     * @param sourceFile source file
     */
    public GuiFileReadListener(DoubleProperty processedBytes, File sourceFile) {
        super(sourceFile);
        this.processedBytes = processedBytes;
    }

    @Override
    void updateProgressInPercent(double newValue) {
        processedBytes.set(newValue); 
    }

}
