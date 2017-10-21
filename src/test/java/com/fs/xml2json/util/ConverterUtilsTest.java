
package com.fs.xml2json.util;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for ConverterUtils.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ConverterUtilsTest {

    @Test
    public void testGetConvertedFileForJson() {
        File sourceFile = new File("someJsonFile.json");
        File outputDirectory = new File(".");
        
        File outFile = ConverterUtils.getConvertedFile(sourceFile, outputDirectory);
        
        Assert.assertEquals("someJsonFile.xml", outFile.getName());
    }
    
    @Test
    public void testGetConvertedFileForXml() {
        File sourceFile = new File("someFile.xml");
        File outputDirectory = new File(".");
        
        File outFile = ConverterUtils.getConvertedFile(sourceFile, outputDirectory);
        
        Assert.assertEquals("someFile.json", outFile.getName());
    }
    
    @Test(expected = NullPointerException.class)
    public void testGetConvertedFileWithUnsupportedFile() {
        File sourceFile = new File("someIncorrectFile.txt");
        File outputDirectory = new File(".");
        
        ConverterUtils.getConvertedFile(sourceFile, outputDirectory);
    }
}
