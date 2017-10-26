package com.fs.xml2json;

import java.io.File;
import java.io.FileNotFoundException;
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
    
    @Before
    public void setUp() {
        destinationFolder = getDestinationDirectory();
    }
    
    @After
    public void tearDown() {
        if (null != destinationFolder && destinationFolder.exists()) {
            deleteFilesAndDirs(destinationFolder);
        }
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
        File sourceFolder = new File("src/test/resources/emptyFolder");
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.xml"};
        Starter.main(args);
        
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