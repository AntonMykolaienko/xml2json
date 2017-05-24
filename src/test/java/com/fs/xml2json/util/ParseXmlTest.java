
package com.fs.xml2json.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Anton
 */
public class ParseXmlTest {
    
    @Test
    public void testParseXml() throws FileNotFoundException, XMLStreamException {
        XMLInputFactory f = XMLInputFactory.newFactory();
        File inputFile = new File(getClass().getResource("/SampleXml.xml").getFile());
        XMLStreamReader sr = f.createXMLStreamReader(new FileInputStream(inputFile));

        String propertyName = "";
        String propertyValue = "";
        String currentElement = "";
        
        XmlMapper mapper = new XmlMapper();
        while (sr.hasNext()) {
            int code = sr.next();
            switch (code) {
                case XMLStreamConstants.START_ELEMENT:
                    currentElement = sr.getLocalName();
                    System.out.println(currentElement);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    currentElement = sr.getLocalName();
                    System.out.println("/"+currentElement);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (currentElement.equalsIgnoreCase("element")) {
                        propertyName += sr.getText();
                    } else if (currentElement.equalsIgnoreCase("attribute")) {
                        propertyValue += sr.getText();
                    }
                    break;
                }
            
        }
        
        sr.close();

        Assert.assertTrue(true);
    }
    
}
