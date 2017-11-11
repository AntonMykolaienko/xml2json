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

package com.fs.xml2json.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

/**
 * Tests for PropertiesLoader
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class PropertiesLoaderTest {
    
    private static final String DESTINATION_FOLDER = "LoaderPropertiesFolder";
    private static final String TEST_FILE = "testProps.txt";
    
    private final List<File> filesToDelete = new ArrayList<>();
    
    @Before
    public void setUp() throws IOException {
        File dir = getDestinationDirectory();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        filesToDelete.add(dir);
        
        Properties props = new Properties();
        props.put("IntegerKey", String.valueOf(124545));
        props.put("StringKey", "StringValue");
        props.put("ShortKey", String.valueOf(127));
        props.put("BinaryKey", String.valueOf(0b1111_1100));
        
        try (OutputStream os = new FileOutputStream(new File(dir, TEST_FILE))) {
            props.store(os, "");
        }
    }
    
    @After
    public void tearDown() {
        filesToDelete.forEach(this::deleteFilesAndDirs);
    }
    
    void deleteFilesAndDirs(File file) {
        if (file.isDirectory()) {
            // delete from directory all files
            Stream.of(file.listFiles()).forEach(f -> deleteFilesAndDirs(f));
            file.delete();
        } else {
            file.delete();
        }
    }
    

    
    @Test(expected = FileNotFoundException.class)
    public void testLoadFromNonExistingFile() throws IOException {
        PropertiesLoader loader = new PropertiesLoader(new File("someProperties.txt"));
        loader.load();
    }
    
    @Test
    public void testLoad() throws IOException {
        PropertiesLoader loader = new PropertiesLoader(getPropertiesFile());
        
        Properties props = loader.load();
        Assert.assertEquals(4, props.size());
    }
    
    @Test
    public void testLoadUpdateAndSave() throws IOException {
        PropertiesLoader loader = new PropertiesLoader(getPropertiesFile());
        
        Properties props = loader.load();
        Assert.assertEquals(4, props.size());
        
        props.put("SomeKey", "value");
        
        loader.saveProperties(props);
        
        Properties newProps = loader.load();
        Assert.assertEquals(5, newProps.size());
    }
    
    @Test
    public void testLoadAddEmptyKeyAndSave() throws IOException {
        PropertiesLoader loader = new PropertiesLoader(getPropertiesFile());
        
        Properties props = loader.load();
        Assert.assertEquals(4, props.size());
        
        props.put("", "value");
        
        loader.saveProperties(props);
        
        Properties newProps = loader.load();
        Assert.assertEquals(4, newProps.size());
    }
    
    
    @Test
    public void testSavePropertiesToNotExistenceFolder() throws IOException {
        File propsFile = new File(getDestinationDirectory(), "foldername" + File.separator + TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        
        Properties props = new Properties();
        props.put("SomeKey", "value");
        
        loader.saveProperties(props);
        
        Properties newProps = loader.load();
        Assert.assertEquals(1, newProps.size());
    }
    
    
    @Test(expected = FileNotFoundException.class)
    public void testLoadFileNotExists() throws IOException {
        File propsFile = new File(getDestinationDirectory(), "someFolder" + TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);

        loader.load();
    }
    
    @Test
    public void testSaveWithIOException() throws IOException {
        Properties props = Mockito.spy(new Properties());
        BDDMockito.doThrow(IOException.class).when(props).entrySet();
        
        PropertiesLoader loader = Mockito.spy(new PropertiesLoader(getPropertiesFile()));
        
        loader.load().forEach((key, value) -> props.put(key, value));
        Assert.assertEquals(4, props.size());      

        props.put("SomeKey", "value");
        
        
        loader.saveProperties(props);
        
        Properties newProps = loader.load();
        Assert.assertEquals(0, newProps.size());    // maybe this is not very good 
    }
    
    
    private File getPropertiesFile() {
        return new File(getDestinationDirectory(), TEST_FILE);
    }
    
    private File getDestinationDirectory() {
        return new File(getTempDirectory(), DESTINATION_FOLDER);
    }
    
    private File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
}
