
package com.fs.xml2json.util;

import com.fs.xml2json.util.XmlUtils.XmlNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
        
        System.out.println("===========");
        
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        List<XmlNode> list = XmlUtils.getObjectElements(null, sr, new AtomicInteger(0), isCanceled, null);
        
        Assert.assertNotNull(list);
//        Assert.assertEquals(1, list.size());
//        Assert.assertEquals("/root", list.get(0).getFullPath());
                
        sr.close();
    }
    
}
