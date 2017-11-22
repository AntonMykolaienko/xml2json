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

/**
 * Uncategorized utility methods.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public final class ApplicationUtils {

    /**
     * Literal value of unknown version.
     */
    public static final String UNNOWN_VERSION = "unknown";

    /**
     * Private constructor.
     */
    private ApplicationUtils() {
    }

    /**
     * Returns application version from Manifest-file.
     *
     * @return string with application version
     */
    public static final String getVersion() {
        String version = getVersionFromManifest();
        if (null == version || version.isEmpty()) {
            version = UNNOWN_VERSION;
        }
        return version;
    }

    /**
     * Returns application version from Manifest.
     *
     * @return application versions
     */
    private static String getVersionFromManifest() {
        return ApplicationUtils.class.getPackage().getImplementationVersion();
    }

}
