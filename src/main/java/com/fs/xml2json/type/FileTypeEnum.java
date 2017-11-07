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

package com.fs.xml2json.type;

/**
 * File types.
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public enum FileTypeEnum {

    /**
     * XML type.
     */
    XML(".xml"),

    /**
     * JSON type.
     */
    JSON(".json");

    private final String extension;

    /**
     * Default constructor for enum.
     *
     * @param extension extension for file type
     */
    FileTypeEnum(String extension) {
        this.extension = extension;
    }


    /**
     * Returns file's extension with dot (example: .json, .xml).
     *
     * @return file's extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Return file's type (XML or JSON) or return <code>null</code> if not XML and not JSON.
     *
     * @param fileName file's name
     * @return fyles type or null if cannot determine
     */
    public static final FileTypeEnum parseByFileName(String fileName) {
        for (FileTypeEnum type : FileTypeEnum.values()) {
            if (fileName.toLowerCase().endsWith(type.extension)) {
                return type;
            }
        }

        return null;
    }
}
