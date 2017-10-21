
package com.fs.xml2json.filter;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for CustomPatternFileFilter.
 *
 * @author Anton
 * @since 1.2.0
 */
public class CustomPatternFileFilterTest {

    
    @Test
    public void testFilteringXmlByPattern() {
        String pattern = "*.xml";
        File file = new File("SomeFileWithXml.xml");
        CustomPatternFileFilter filter = new CustomPatternFileFilter(pattern);
        
        Assert.assertTrue(filter.accept(file));
    }
    
    @Test
    public void testFilteringXmlByPatternFalse() {
        String pattern = "*.xml";
        File file = new File("SomeFileWithXml.json");
        CustomPatternFileFilter filter = new CustomPatternFileFilter(pattern);
        
        Assert.assertFalse(filter.accept(file));
    }
    
    @Test
    public void testFilteringXmlByPartialFileNamePattern() {
        String pattern = "*File*.xml";
        File file = new File("SomeFileWithXml.xml");
        CustomPatternFileFilter filter = new CustomPatternFileFilter(pattern);
        
        Assert.assertTrue(filter.accept(file));
    }
    
    @Test
    public void testFilteringXmlByPartialFileNamePatternFalse() {
        String pattern = "*Fle*.xml";
        File file = new File("SomeFileWithXml.xml");
        CustomPatternFileFilter filter = new CustomPatternFileFilter(pattern);
        
        Assert.assertFalse(filter.accept(file));
    }
    
    @Test
    public void testFilteringXmlByPartialFileNamePatternCaseInsensitive() {
        String pattern = "*File*.xml";
        File file = new File("Some FileWithXml.XML");
        CustomPatternFileFilter filter = new CustomPatternFileFilter(pattern);
        
        Assert.assertTrue(filter.accept(file));
    }
    
    
    
    @Test
    public void testFilteringJsonByPattern() {
        String pattern = "*.json";
        File file = new File("SomeFileWithJson.json");
        CustomPatternFileFilter filter = new CustomPatternFileFilter(pattern);
        
        Assert.assertTrue(filter.accept(file));
    }
    
    @Test
    public void testFilteringUnsupportedExtension() {
        String pattern = "*.txt";
        File file = new File("SomeFileWithJson.txt");
        CustomPatternFileFilter filter = new CustomPatternFileFilter(pattern);
        
        Assert.assertFalse(filter.accept(file));
    }
}
