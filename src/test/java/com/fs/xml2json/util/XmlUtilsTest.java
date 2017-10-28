
package com.fs.xml2json.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Anton
 */
public class XmlUtilsTest {


    @Test
    public void testParseXml() throws FileNotFoundException, XMLStreamException {
        XMLInputFactory f = XMLInputFactory.newFactory();
        File inputFile = new File(getClass().getResource("/SampleXml.xml").getFile());
        XMLStreamReader sr = f.createXMLStreamReader(new FileInputStream(inputFile));

        InputStream in = new FileInputStream(inputFile);
        List<String> arrays = XmlUtils.determineArrays(in);
        
        Assert.assertFalse(arrays.isEmpty());
        Assert.assertEquals(2, arrays.size());
        Assert.assertEquals("/root/channel", arrays.get(0));
        Assert.assertEquals("/root/channel/formats/format", arrays.get(1));
                
        sr.close();
    }
    
}
