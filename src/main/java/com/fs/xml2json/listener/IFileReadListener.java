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

/**
 * Iterface for file's read - action listeners.
 *
 * @author Anton
 * @since 1.1.0
 */
public interface IFileReadListener {

    /**
     * Updates listener about number of bytes which have been read.
     *
     * @param bytes processed bytes
     */
    void update(int bytes);

    /**
     * Updates listener about finishing file processing.
     */
    void finished();
}
