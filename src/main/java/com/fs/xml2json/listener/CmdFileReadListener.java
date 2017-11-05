/**
 * Copyright © 2016-2017 Anton Mykolaienko. All rights reserved. Contacts: <amykolaienko@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package com.fs.xml2json.listener;

import java.io.File;

/**
 * Specific listener for CMD. Will show convertation progress in command line.
 *
 * @author Anton
 * @since 1.2.0
 */
public class CmdFileReadListener extends AbstractFileReadListener {

    /**
     * Creates instance of Command line listener.
     *
     * @param sourceFile source file to read
     */
    public CmdFileReadListener(File sourceFile) {
        super(sourceFile);
    }

    /**
     * Draws progress bar in command line.
     * 
     * @param newValue percent processed
     */
    @Override
    void updateProgressInPercent(double newValue) {
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
        if ((int)(newValue*100) == 100) {
            System.out.print("\n");
        }
    }
}
