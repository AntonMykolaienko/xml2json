/**
 * Copyright © 2016-2017 Anton Mykolaienko. All rights reserved. Contacts: <amykolaienko@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.fs.xml2json.controller;

import javafx.application.Application;
import javafx.application.HostServices;

/**
 * Provider for HostServices.
 *
 * @author Anton
 * @since 1.1.0
 */
public class HostServicesProvider {

    /**
     * Singleton instance of {@link HostServicesProvider}.
     */
    public static final HostServicesProvider INSTANCE = new HostServicesProvider();

    /**
     * Real HostServices.
     */
    private HostServices hostServices;

    /**
     * Initialize class with value.
     *
     * @param hostServices instance of {@link HostServices} from {@link Application}
     */
    public void init(HostServices hostServices) {
        if (this.hostServices != null) {
            throw new IllegalStateException("Host services already initialized");
        }
        this.hostServices = hostServices;
    }

    /**
     * Returns instance of {@link HostServices}.
     *
     * @return {@link HostServices}
     */
    public HostServices getHostServices() {
        if (hostServices == null) {
            throw new IllegalStateException("Host services not initialized");
        }
        return hostServices;
    }
}
