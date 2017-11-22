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

package com.fs.xml2json.util;

import com.fs.xml2json.type.FileTypeEnum;
import com.fs.xml2json.type.UnsupportedFileType;
import java.io.File;

/**
 * Various utility methods.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public final class ConverterUtils {

    /**
     * Private constructor.
     */
    private ConverterUtils() {
    }

    /**
     * Returns link to converted file with same name as source file, but with
     * another extension (xml -> json, json -> xml).
     * <p>Note: only .json and .xml supported, caller method should pass only these files.
     * Supported extensions can be found in {@link FileTypeEnum}.
     *
     * @param sourceFile file to convert
     * @param destinationFolder path to destination folder
     * @return link to converted file
     */
    public static File getConvertedFile(File sourceFile, File destinationFolder) {
        FileTypeEnum fileType = FileTypeEnum.parseByFileName(sourceFile.getName());
        String convertedFileName = "";
        String fileNameWithoutExtension = sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf('.'));
        switch (fileType) {
            case JSON:
                convertedFileName = fileNameWithoutExtension + FileTypeEnum.XML.getExtension();
                break;
            case XML:
                convertedFileName = fileNameWithoutExtension + FileTypeEnum.JSON.getExtension();
                break;
            default:
                throw new UnsupportedFileType("Unsupported file's extension");
        }

        return new File(destinationFolder, convertedFileName);
    }

}
