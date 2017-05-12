
package com.fs.xml2json;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command-line interface.
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public class Starter {
    
    private static final String XML_EXTENSION = ".xml";
    private static final String JSON_EXTENSION = ".json";
    
    private static final Logger logger = LoggerFactory.getLogger(Starter.class);
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please run with filepath to json- or xml- file");
        }
        String filePath = args[0];
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("File '" + filePath + "' not found");
        } else if (file.isDirectory()) {
            throw new IllegalArgumentException("Mentioned path point to directory while expected File");
        }
        
        Starter converter = new Starter();
        if (filePath.toLowerCase().endsWith(JSON_EXTENSION)) {
            converter.convertFileJsonToXml(file.toURI());
        } else if (filePath.toLowerCase().endsWith(XML_EXTENSION)) {
            converter.convertFileXmlToJson(file.toURI());
        } else {
            throw new IllegalArgumentException("Mentioned path point to not supported file type. Only JSON and XML files supported.");
        }
    }
    
    
    public Starter() {
    }
    
    
    public void convertFileJsonToXml(URI uri) {
        String fileNameWithExtension = new File(uri).getName();
        
        //FileMetrics metrics = new FileMetrics();
        
        StopWatch sw = new StopWatch();
        
        try (InputStream input = new BufferedInputStream(new FileInputStream(new File(uri)))) {
            OutputStream output = new FileOutputStream(new File(new File(uri).getParentFile(), 
                fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."))+".xml"));
            
            sw.start();
            /*
            * If the <code>multiplePI</code> property is
            * set to <code>true</code>, the StAXON reader will generate
            * <code>&lt;xml-multiple&gt;</code> processing instructions
            * which would be copied to the XML output.
            * These can be used by StAXON when converting back to JSON
            * to trigger array starts.
            * Set to <code>false</code> if you don't need to go back to JSON.
            */
            JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).build();
            try {
                /*
                * Create reader (JSON).
                */
                XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);

               /*
                * Create writer (XML).
                */
                XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);
                writer = new PrettyXMLEventWriter(writer); // format output

               /*
                * Copy events from reader to writer.
                */
                writer.add(reader);
                
                /*
                * Close reader/writer.
                */
                reader.close();
                writer.close();
            } catch (XMLStreamException e) {
                logger.error(e.toString());
            } finally {
               /*
                * As per StAX specification, XMLStreamReader/Writer.close() doesn't close
                * the underlying stream.
                */
                output.close();
                input.close();
           }
        } catch (IOException ex) {
            logger.error(ex.toString());
        } finally {
            sw.stop();
        }
        
        //logger.info("Metrics for '{}':\n{}", new File(uri).getAbsolutePath(), metrics.toString());
        logger.info("Time taken: {}", sw.toString());
    }

    
    public void convertFileXmlToJson(URI uri) {
        String fileNameWithExtension = new File(uri).getName();
        
        //FileMetrics metrics = new FileMetrics();
        
        StopWatch sw = new StopWatch();
        
        /*
         * If we want to insert JSON array boundaries for multiple elements,
         * we need to set the <code>autoArray</code> property.
         * If our XML source was decorated with <code>&lt;?xml-multiple?&gt;</code>
         * processing instructions, we'd set the <code>multiplePI</code>
         * property instead.
         * With the <code>autoPrimitive</code> property set, element text gets
         * automatically converted to JSON primitives (number, boolean, null).
         */
        JsonXMLConfig config = new JsonXMLConfigBuilder()
            .autoArray(true)
            .autoPrimitive(true)
            .prettyPrint(true)
            .build();
        
        try (InputStream input = new BufferedInputStream(new FileInputStream(new File(uri)))) {
            OutputStream output = new FileOutputStream(new File(new File(uri).getParentFile(), 
                fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."))+".json"));
            
            sw.start();
            /*
             * Create reader (XML).
             */
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);

            /*
             * Create writer (JSON).
             */
            XMLEventWriter writer = new JsonXMLOutputFactory(config).createXMLEventWriter(output);

            /*
             * Copy events from reader to writer.
             */
            writer.add(reader);

            /*
             * Close reader/writer.
             */
            reader.close();
            writer.close();
        } catch (IOException | XMLStreamException ex) {
            logger.error(ex.toString());
        } finally {
            sw.stop();
        }
    }
}
