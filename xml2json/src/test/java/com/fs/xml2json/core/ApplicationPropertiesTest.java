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
 * Test for ApplicationProperties.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ApplicationPropertiesTest {
    
    private static final String DESTINATION_FOLDER = "ApplicationPropertiesFolder";
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
    
    
    

    @Test(expected = IllegalArgumentException.class)
    public void testApplicationPropertiesWithNullLoader() {
        ApplicationProperties props = new ApplicationProperties(null);
        Assert.assertNull(props);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testApplicationPropertiesSetNullLoader() {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        ApplicationProperties props = new ApplicationProperties(new PropertiesLoader(propsFile));
        Assert.assertNotNull(props);
        
        props.setPropertiesLoader(null);
    }
    
    @Test
    public void testApplicationPropertiesSetNotNullLoader() {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        ApplicationProperties props = new ApplicationProperties(new PropertiesLoader(propsFile));
        Assert.assertNotNull(props);
        
        props.setPropertiesLoader(loader);
    }
    
    @Test
    public void testGetPropertiesAndPathNotExists() {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        ApplicationProperties props = new ApplicationProperties(new PropertiesLoader(propsFile));
        Assert.assertNotNull(props);
        
        String path = props.getLastOpenedPath();
        Assert.assertNull(path);
        
        path = props.getLastOpenedPath();
        Assert.assertNull(path);
    }
    
    
    @Test
    public void testGetPropertiesAndPathIsEmpty() throws IOException {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        ApplicationProperties appProps = new ApplicationProperties(loader);
        
        Properties props = loader.load();
        props.put(Config.LAST_DIRECTORY, "");
        loader.saveProperties(props);
        
        String path = appProps.getLastOpenedPath();
        Assert.assertNull(path);
    }
    
    @Test
    public void testGetPropertiesAndPathWithOnlySpaces() throws IOException {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        ApplicationProperties appProps = new ApplicationProperties(loader);
        
        Properties props = loader.load();
        props.put(Config.LAST_DIRECTORY, "   ");
        loader.saveProperties(props);
        
        String path = appProps.getLastOpenedPath();
        Assert.assertNull(path);
    }
    
    @Test
    public void testGetPropertiesAndPathIsNotExist() throws IOException {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        ApplicationProperties appProps = new ApplicationProperties(loader);
        
        Properties props = loader.load();
        props.put(Config.LAST_DIRECTORY, 
                new File(propsFile.getParentFile(), "someNameFolder/file.txt").getAbsolutePath());
        loader.saveProperties(props);
        
        String path = appProps.getLastOpenedPath();
        Assert.assertNull(path);
    }
    
    @Test
    public void testGetPropertiesAndCorrectPath() throws IOException {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        ApplicationProperties appProps = new ApplicationProperties(loader);
        
        appProps.saveLastOpenedPath(propsFile);
        
        String path = appProps.getLastOpenedPath();
        Assert.assertEquals(propsFile.getParentFile().getAbsolutePath(), path);
    }
    
    @Test
    public void testSaveLastPathTwice() throws IOException {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        ApplicationProperties appProps = new ApplicationProperties(loader);
        
        appProps.saveLastOpenedPath(new File(propsFile.getParentFile(), "someNameFolder/file.txt"));
        
        String path = appProps.getLastOpenedPath();
        Assert.assertNull(path);
        
        appProps.saveLastOpenedPath(propsFile);
        
        path = appProps.getLastOpenedPath();
        Assert.assertEquals(propsFile.getParentFile().getAbsolutePath(), path);
    }
    
    @Test
    public void testSaveLastPathTwiceSameValue() throws IOException {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        ApplicationProperties appProps = new ApplicationProperties(loader);
        
        appProps.saveLastOpenedPath(new File(propsFile.getParentFile(), "someNameFolder/file.txt"));
        
        String path = appProps.getLastOpenedPath();
        Assert.assertNull(path);
        
        appProps.saveLastOpenedPath(propsFile);
        // second save 
        appProps.saveLastOpenedPath(propsFile);
        
        path = appProps.getLastOpenedPath();
        Assert.assertEquals(propsFile.getParentFile().getAbsolutePath(), path);
    }
    
    @Test
    public void testGetLastPathWithTabCharacter() throws IOException {
        File propsFile = new File(getDestinationDirectory(), TEST_FILE);
        
        PropertiesLoader loader = new PropertiesLoader(propsFile);
        ApplicationProperties appProps = new ApplicationProperties(loader);
        
        File lastOpenedFile = new File(propsFile.getParentFile(), File.separator + "newFolder" 
                + File.separator + "testfile.txt");
        
        if (!lastOpenedFile.getParentFile().exists()) {
            lastOpenedFile.getParentFile().mkdirs();
            filesToDelete.add(lastOpenedFile.getParentFile());
        }
        
        appProps.saveLastOpenedPath(lastOpenedFile.getAbsoluteFile());
        
        String path = appProps.getLastOpenedPath();
        Assert.assertNotNull(path);
        Assert.assertEquals(lastOpenedFile.getParentFile().getAbsolutePath(), path);
    }
    
    
    @Test
    public void testLoadWithException() throws IOException {
        PropertiesLoader loader = Mockito.mock(PropertiesLoader.class);
        BDDMockito.when(loader.load()).thenThrow(new IOException());

        ApplicationProperties appProps = Mockito.spy(new ApplicationProperties(loader));
        String path = appProps.getLastOpenedPath();
        Assert.assertNull(path);
    }
    
    
    
    
    private File getDestinationDirectory() {
        return new File(getTempDirectory(), DESTINATION_FOLDER);
    }
    
    private File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
}
