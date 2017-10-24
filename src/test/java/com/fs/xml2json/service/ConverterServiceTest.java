
package com.fs.xml2json.service;

import com.fs.xml2json.listener.IFileReadListener;
import com.fs.xml2json.type.UnsupportedFileType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for ConverterService.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ConverterServiceTest {
    
    List<File> filesToDelete = new ArrayList<>();
    File destinationFile;
    
    @After
    public void tearDown() {
        if (null != destinationFile && destinationFile.exists()) {
            deleteFilesAndDirs(destinationFile);
        }
        if (!filesToDelete.isEmpty()) {
            filesToDelete.forEach(file -> deleteFilesAndDirs(file));
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

    @Test
    public void testConvertXmlToJson() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.json");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
        
        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
        System.out.println("destinationFile = " + destinationFile.getAbsolutePath());
    }
    
    @Test
    public void testConvertXmlToJsonToNonExistingDirectory() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        File nonExistingDirectory = new File(getTempDirectory(), "newDirectory");
        filesToDelete.add(nonExistingDirectory);
        destinationFile = new File(nonExistingDirectory, "newDirectory/ConvertedFile.json");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
        
        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
        System.out.println("destinationFile = " + destinationFile.getAbsolutePath());
    }
    
    @Test
    public void testConvertJsonToXml() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleJson.json").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.xml");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
        
        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
        System.out.println("destinationFile = " + destinationFile.getAbsolutePath());
    }
    
    
    @Test(expected = RuntimeException.class)
    public void testConvertCorruptedXmlToJson() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("CorruptedXml.xml").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.json");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
    }
    
    
    @Test(expected = UnsupportedFileType.class)
    public void testTryConvertUnsupportedFile() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("Unsupported.txt").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.json");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
    }
    
    
    @Test(expected = AssertionError.class)
    public void testTryConvertWithNullListener() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleJson.json").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.xml");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, null, isCanceled);
    }
    
    private File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
    
    
    
    private class CustomFileReadListener implements IFileReadListener {

        @Override
        public void update(int bytes) {
            
        }

        @Override
        public void finished() {
            
        }
    }
}
