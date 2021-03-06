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
