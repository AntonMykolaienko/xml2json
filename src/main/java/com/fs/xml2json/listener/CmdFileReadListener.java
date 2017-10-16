package com.fs.xml2json.listener;

import java.io.File;

/**
 * Specific listener for CMD. Will show convertation progress in command line.
 *
 * @author Anton
 * @since 1.2.0
 */
public class CmdFileReadListener extends AbstractFileReadListener {

    // helper variable for XML, when we need to read file twice
    private int numberOfReads = 1;
    
    /**
     * Creates instance of Command line listener.
     *
     * @param sourceFile source file to read
     */
    public CmdFileReadListener(File sourceFile) {
        super(sourceFile);
        if (isXml()) {
            numberOfReads += 1;
        }
    }

    @Override
    public void finished() {
        numberOfReads--;
        if (0 == numberOfReads) {
            progressPercentage(1.0d);
            System.out.print("\n");
        }
    }

    @Override
    void updateProgressInPercent(double newValue) {
        progressPercentage(newValue);
    }

    
    /**
     * Draws progress bar in command line.
     * 
     * @param newValue percent processed
     */
    private void progressPercentage(double newValue) {
        int maxBareSize = 100; // number of chars for progress bar
        int percentDone = (int) (100 * newValue) * (maxBareSize / 100 /*100 percent*/);
        char defaultChar = ' ';
        String icon = "=";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("[");
        for (int i = 0; i < percentDone; i++) {
            if (percentDone < 100 && i == percentDone-1) {
                bareDone.append('>');
            } else {
                bareDone.append(icon);
            }
        }
        
        String bareRemain = bare.substring(percentDone, bare.length());
        System.out.print("\r" + bareDone + bareRemain + " " + percentDone + "%");
        if (newValue == 1.0d) {
            System.out.print("\n");
        }
    }
}
