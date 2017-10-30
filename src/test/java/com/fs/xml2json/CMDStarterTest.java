package com.fs.xml2json;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Starter.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class CMDStarterTest {
    
    private static final String DESTINATION_FOLDER = "destination";
    private File destinationFolder;
    private final List<File> filesToDelete = new ArrayList<>();
    
    //BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
    
    
    @Before
    public void setUp() {
        destinationFolder = getDestinationDirectory();
        if (null != destinationFolder && destinationFolder.exists()) {
            deleteFilesAndDirs(destinationFolder);
        }
    }
    
    @After
    public void tearDown() {
        if (null != destinationFolder && destinationFolder.exists()) {
            deleteFilesAndDirs(destinationFolder);
        }
        filesToDelete.forEach(file -> deleteFilesAndDirs(file));
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
    public void testStartNoGuiWithoutMandatoryArguments() {
        String[] args = new String[]{"--noGui"};
        Starter.main(args);
    }
    
    @Test
    public void testStartNoGuiAndSourceFolderNotExists() throws FileNotFoundException {
        String[] args = new String[]{"--noGui", "--sourceFolder", "/SomeFolderName/", 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.json"};
        Starter.main(args);
    }
    
    @Test
    public void testStartNoGuiConvertFewXmls() throws FileNotFoundException {
        File sourceFolder = new File("src/test/resources/xml");
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.xml"};
        Starter.main(args);
        
        Assert.assertTrue(destinationFolder.exists());
        Assert.assertEquals(2, destinationFolder.listFiles().length);
        Stream.of(destinationFolder.listFiles()).forEach(file -> Assert.assertTrue(file.length() > 0));
    }
    
    @Test
    public void testStartNoGuiConvertFewXmlsAndIncorrectPattern() throws FileNotFoundException {
        File sourceFolder = new File("src/test/resources/xml");
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.json"};
        Starter.main(args);
        
        Assert.assertTrue(destinationFolder.exists());
        Assert.assertEquals(0, destinationFolder.listFiles().length);
    }
    
    
    @Test
    public void testStartNoGuiConvertFilesFromEmptyFolder() throws FileNotFoundException {
        File sourceFolder = new File(getTempDirectory(), "emptyFolder");
        sourceFolder.mkdirs();
        filesToDelete.add(sourceFolder);
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.xml"};
        Starter.main(args);
        
        Assert.assertTrue(destinationFolder.exists());
        Assert.assertEquals(0, destinationFolder.listFiles().length);
    }
    
    @Test
    public void testStartNoGuiConvertCorruptedFile() throws FileNotFoundException {
        File sourceFolder = new File("src/test/resources/");
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "corruptedxml.xml"};
        Starter.main(args);
        
        Assert.assertTrue(destinationFolder.exists());
        Assert.assertEquals(1, destinationFolder.listFiles().length); // file will be created but it will be corrupted
        File file = destinationFolder.listFiles()[0];
        Assert.assertEquals(0, file.length()); 
        Assert.assertTrue("corruptedxml.json".equalsIgnoreCase(file.getName()));
    }
    
    
    @Test
    public void testStartNoGuiConvertFewJsonTwice() throws FileNotFoundException {
        File sourceFolder = new File("src/test/resources/json");
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.json",
            "--overwrite"};
        Starter.main(args);
        
        Assert.assertTrue(destinationFolder.exists());
        Assert.assertEquals(2, destinationFolder.listFiles().length);
        Stream.of(destinationFolder.listFiles()).forEach(file -> Assert.assertTrue(file.length() > 0));
        
        Starter.main(args);
    }
    
    @Test
    public void testStartNoGuiAndCancelIsTrue() throws FileNotFoundException, NoSuchFieldException, 
            IllegalArgumentException, IllegalAccessException {
        File sourceFolder = new File("src/test/resources/xml");
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.xml"};
        Starter starter = new Starter(args);
        
        Field field = starter.getClass().getDeclaredField("isCanceled");
        Assert.assertNotNull(field);
        field.setAccessible(true);
        
        AtomicBoolean value = (AtomicBoolean)field.get(starter);
        value.set(true);
        
        starter.start();
        
        Assert.assertTrue(destinationFolder.exists());
        Assert.assertEquals(0, destinationFolder.listFiles().length);
    }

    
    private File getDestinationDirectory() {
        return new File(getTempDirectory(), DESTINATION_FOLDER);
    }
    
    private File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
}