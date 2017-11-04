/*
 * Copyright 2017 Anton Mykolaienko.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fs.xml2json.controller;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for HostServicesProvider.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class HostServicesProviderTest extends Application {

    @Test(expected = IllegalStateException.class)
    public void testNotInitializedInstance() {
        HostServicesProvider.INSTANCE.getHostServices();
    }
    
    @Test
    public void testInstance() {
        HostServicesProvider.INSTANCE.init(getHostServices());
        HostServices instance = HostServicesProvider.INSTANCE.getHostServices();
        
        Assert.assertNotNull(instance);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testInitInitializedInstance() {
        HostServicesProvider.INSTANCE.init(getHostServices());
        HostServicesProvider.INSTANCE.init(getHostServices());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    }
    
}
