

package com.fs.xml2json.controller;

import javafx.application.HostServices;

/**
 * Provider for HostServices.
 *
 * @author Anton
 * @since 1.1.0
 */
public class HostServicesProvider {

    public static final HostServicesProvider INSTANCE = new HostServicesProvider();

    private HostServices hostServices ;
    
    public void init(HostServices hostServices) {
        if (this.hostServices != null) {
            throw new IllegalStateException("Host services already initialized");
        }
        this.hostServices = hostServices ;
    }
    public HostServices getHostServices() {
        if (hostServices == null) {
            throw new IllegalStateException("Host services not initialized");
        }
        return hostServices ;
    }
}
