
package com.fs.xml2json.type;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for FileTypeEnum.
 *
 * @author Anton
 * @since 1.2.0
 */
public class FileTypeEnumTest {

    @Test
    public void testValueOfCorrectValues() {
        FileTypeEnum type = FileTypeEnum.valueOf("XML");
        Assert.assertEquals(FileTypeEnum.XML, type);
        
        type = FileTypeEnum.valueOf("JSON");
        Assert.assertEquals(FileTypeEnum.JSON, type);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValueOfIncorrectValueXml() {
        FileTypeEnum.valueOf("xml");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValueOfIncorrectValueJson() {
        FileTypeEnum.valueOf("json");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValueOfIncorrectValue() {
        FileTypeEnum.valueOf("txt");
    }
}
