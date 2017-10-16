package com.fs.xml2json.listener;

import java.io.File;

/**
 * Specific listener for CMD. Will show convertation progress in command line.
 *
 * @author Anton
 * @since 1.2.0
 */
public class CmdFileReadListener extends AbstractFileReadListener {

    private double lastProgress;
    /**
     * Creates instance of Command line listener.
     *
     * @param sourceFile source file to read
     */
    public CmdFileReadListener(File sourceFile) {
        super(sourceFile);
    }

    @Override
    public void finished() {
        super.finished(); 
        if (lastProgress == 1.0) {
            System.out.print("\n");
        }
    }
    
    

    /**
     * Draws progress bar in command line.
     * 
     * @param newValue percent processed
     */
    @Override
    void updateProgressInPercent(double newValue) {
        lastProgress = newValue;
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
//        if (newValue == 1.0d) {
//            System.out.print("\n");
//        }
    }
}
