
package com.fs.xml2json.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for ApplicationCommandLine.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ApplicationCommandLineTest {
    
    private static final String DESTINATION_FOLDER_PATH = "destination/";
    
    @After
    public void tearDown() {
        File destinationFolder = new File(DESTINATION_FOLDER_PATH);
        if (destinationFolder.exists()) {
            if (destinationFolder.listFiles().length > 0) {
                deleteFiles(destinationFolder);
            }
            destinationFolder.delete();
        }
    }
    
    private void deleteFiles(File rootFolder) {
        Stream.of(rootFolder.listFiles()).forEach(file -> {
            if (file.isDirectory()) {
                deleteFiles(file);
            } else {
                file.delete();
            }
        });
    }

    @Test
    public void testParseNoInputs() throws ParseException, FileNotFoundException {
        String[] args = new String[]{};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertFalse(cmd.isNoGuiEnabled());
        Assert.assertTrue(null == cmd.getSourceFolder());
        Assert.assertTrue(null == cmd.getDestinationFolder());
        Assert.assertTrue(null == cmd.getPattern());
        Assert.assertFalse(cmd.isForceOverwrite());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParseNoGuiAndNoSourceFolder() throws ParseException, FileNotFoundException {
        String[] args = new String[]{"--noGui"};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertTrue(cmd.isNoGuiEnabled());
        cmd.getSourceFolder();
    }
    
    @Test(expected = ParseException.class)
    public void testParseNoGuiAndSourceFolderWithoutValue() throws ParseException, FileNotFoundException {
        String[] args = new String[]{"--noGui", "--sourceFolder",  
            "--destinationFolder", DESTINATION_FOLDER_PATH, "--pattern", "*.json"};
        ApplicationCommandLine.parse(args);
    }
    
    @Test(expected = FileNotFoundException.class)
    public void testParseNoGuiAndSourceFolderNotExists() throws ParseException, FileNotFoundException {
        String[] args = new String[]{"--noGui", "--sourceFolder", "/SomeFolderName/", 
            "--destinationFolder", DESTINATION_FOLDER_PATH, "--pattern", "*.json"};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertTrue(cmd.isNoGuiEnabled());
        cmd.getSourceFolder();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParseNoGuiAndNoDestinationFolder() throws ParseException, FileNotFoundException {
        String[] args = new String[]{"--noGui", "--sourceFolder", "."};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertTrue(cmd.isNoGuiEnabled());
        Assert.assertFalse(null == cmd.getSourceFolder());
        Assert.assertEquals(new File("."), cmd.getSourceFolder());
        cmd.getDestinationFolder();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParseNoGuiAndNoPattern() throws ParseException, FileNotFoundException {
        File destinationFolder = new File(DESTINATION_FOLDER_PATH);
        
        String[] args = new String[]{"--noGui", "--sourceFolder", ".", "--destinationFolder", DESTINATION_FOLDER_PATH};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertTrue(cmd.isNoGuiEnabled());
        Assert.assertFalse(null == cmd.getSourceFolder());
        Assert.assertFalse(null == cmd.getDestinationFolder());
        Assert.assertEquals(destinationFolder, cmd.getDestinationFolder());
        cmd.getPattern();
    }
    
    @Test
    public void testParseNoGuiAndForceOverwriteFalse() throws ParseException, FileNotFoundException {
        String[] args = new String[]{"--noGui", "--sourceFolder", ".", "--destinationFolder", DESTINATION_FOLDER_PATH,
            "--pattern", "*.json"};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertTrue(cmd.isNoGuiEnabled());
        Assert.assertFalse(null == cmd.getSourceFolder());
        Assert.assertFalse(null == cmd.getDestinationFolder());
        Assert.assertFalse(null == cmd.getPattern());
        Assert.assertEquals("*.json", cmd.getPattern());
        Assert.assertFalse(cmd.isForceOverwrite());
    }
    
    @Test
    public void testParseNoGuiAndDoubleCallForceOverwrite() throws ParseException, FileNotFoundException {
        String[] args = new String[]{"--noGui", "--sourceFolder", ".", "--destinationFolder", DESTINATION_FOLDER_PATH,
            "--pattern", "*.json"};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertTrue(cmd.isNoGuiEnabled());
        Assert.assertFalse(null == cmd.getSourceFolder());
        Assert.assertFalse(null == cmd.getDestinationFolder());
        Assert.assertFalse(null == cmd.getPattern());
        Assert.assertEquals("*.json", cmd.getPattern());
        Assert.assertFalse(cmd.isForceOverwrite());
        Assert.assertFalse(cmd.isForceOverwrite()); // second call for checking cache
    }
    
    @Test
    public void testParseNoGuiAndForceOverwrite() throws ParseException, FileNotFoundException {
        String[] args = new String[]{"--noGui", "--sourceFolder", ".", "--destinationFolder", DESTINATION_FOLDER_PATH,
            "--pattern", "*.json", "--overwrite"};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertTrue(cmd.isNoGuiEnabled());
        Assert.assertFalse(null == cmd.getSourceFolder());
        Assert.assertFalse(null == cmd.getDestinationFolder());
        Assert.assertFalse(null == cmd.getPattern());
        Assert.assertEquals("*.json", cmd.getPattern());
        Assert.assertTrue(cmd.isForceOverwrite());
        Assert.assertTrue(cmd.isForceOverwrite()); // second call for checking cache
    }
    
    @Test
    public void testParseNoGuiAndEmptySourceFolder() throws ParseException, FileNotFoundException {
        String sourceFolderPath = "SomeFolderName/";
        File sourceFolder = new File(sourceFolderPath);
        
        Assert.assertTrue(sourceFolder.mkdirs());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolderPath, 
            "--destinationFolder", DESTINATION_FOLDER_PATH, "--pattern", "*.json"};
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        Assert.assertTrue(cmd.isNoGuiEnabled());
        cmd.getSourceFolder();
        
        Assert.assertTrue(sourceFolder.delete());
    }

    @Test
    public void testPrintHelp() {
        ApplicationCommandLine.printHelp();
    }
}
