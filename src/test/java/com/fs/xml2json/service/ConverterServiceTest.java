
package com.fs.xml2json.service;

import com.fs.xml2json.listener.IFileReadListener;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
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
    
    File destinationFile;
    
    @After
    public void tearDown() {
        if (null != destinationFile && destinationFile.exists()) {
            if (destinationFile.isDirectory()) {
                // delete from directory all files
            } else {
                destinationFile.delete();
            }
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
