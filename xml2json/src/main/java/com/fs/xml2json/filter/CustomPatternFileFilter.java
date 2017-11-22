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

package com.fs.xml2json.filter;

import com.fs.xml2json.type.FileTypeEnum;
import java.io.File;
import java.io.FileFilter;

/**
 * Custom filter for files.
 * <p>Performs filtering by supported extensions (.json and .xml) and then by custom pattern.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class CustomPatternFileFilter implements FileFilter {

    private final String pattern;

    /**
     * Creates custom filter for desired pattern.
     *
     * @param pattern pattern for file names
     */
    public CustomPatternFileFilter(String pattern) {
        this.pattern = pattern.replace("*", ".*?").toLowerCase();
    }


    /**
     * Returns {@code true} if filename matches pattern, otherwise returns {@code false}.
     *
     * @param file file to check
     * @return true if filename matches pattern
     */
    @Override
    public boolean accept(File file) {
        String fileNameInLowerCase = file.getName().toLowerCase();

        FileTypeEnum fileType = FileTypeEnum.parseByFileName(fileNameInLowerCase);

        return (null != fileType) && fileNameInLowerCase.matches(pattern);
    }

}
