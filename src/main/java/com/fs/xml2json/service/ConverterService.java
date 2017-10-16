
package com.fs.xml2json.service;

import com.fs.xml2json.io.WrappedInputStream;
import com.fs.xml2json.type.FileTypeEnum;
import com.fs.xml2json.util.XmlUtils;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.json.util.XMLMultipleEventWriter;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fs.xml2json.listener.IFileReadListener;
import java.io.FileNotFoundException;

/**
 * Service which responsible for file conversion from JSON to XML and vise versa.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ConverterService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);
    
    
    /**
     * Converts file from XML to JSON or vise versa and returns link to converted file.
     * 
     * @param sourceFile file to convert
     * @param outputFile output file 
     * @param listener read listener
     * @param isCanceled flag to stop process
     * @return converted file
     */
    public File convert(File sourceFile, File outputFile, IFileReadListener listener, AtomicBoolean isCanceled) {
        StopWatch sw = new StopWatch();

        InputStream input = null;
        OutputStream output = null;
        XMLEventReader reader = null;
        XMLEventWriter writer = null;
        FileTypeEnum inputFileType;
        try {
            inputFileType = FileTypeEnum.parseByFileName(sourceFile.getName());

            input = new WrappedInputStream(new BufferedInputStream(new FileInputStream(sourceFile)),
                    listener, isCanceled);

            File parentFolder = outputFile.getParentFile();
            if (!parentFolder.exists()) {
                parentFolder.mkdirs();
            }
            
            output = new BufferedOutputStream(Files.newOutputStream(outputFile.toPath(),
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));

            sw.start();

            // converter config
            JsonXMLConfig config = createConfig(inputFileType);

            // Create reader.
            reader = createReader(config, inputFileType, input);
            // Create writer.
            writer = createWriter(config, sourceFile, output, isCanceled, listener);
            
            // Copy events from reader to writer.
            writer.add(reader);

            if (null != listener) {
                listener.finished();
            }
        } catch (IOException | XMLStreamException ex) {
            logger.error(ex.toString());
            throw new RuntimeException(ex);
        } finally {
            logger.info("Taken time: {}", sw);
            if (sw.isStarted()) {
                sw.stop();
            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (XMLStreamException ex) {
                    logger.error(ex.toString());
                }
            }
            if (null != writer) {
                try {
                    writer.flush();
                    writer.close();
                } catch (XMLStreamException ex) {
                    logger.error(ex.toString());
                }
            }
            if (null != input) {
                try {
                    input.close();
                } catch (IOException ex) {
                    logger.error(ex.toString());
                }
            }
            if (null != output) {
                try {
                    output.close();
                } catch (IOException ex) {
                    logger.error(ex.toString());
                }
            }
        }
        
        return outputFile;
    }

    
    private JsonXMLConfig createConfig(FileTypeEnum inputFileType) {
        switch (inputFileType) {
            case JSON:
                /*
                * If the <code>multiplePI</code> property is
                * set to <code>true</code>, the StAXON reader will generate
                * <code>&lt;xml-multiple&gt;</code> processing instructions
                * which would be copied to the XML output.
                * These can be used by StAXON when converting back to JSON
                * to trigger array starts.
                * Set to <code>false</code> if you don't need to go back to JSON.
                 */
                return new JsonXMLConfigBuilder()
                        .multiplePI(false)
                        .build();
            case XML:
                /*
                * If we want to insert JSON array boundaries for multiple elements,
                * we need to set the <code>autoArray</code> property.
                * If our XML source was decorated with <code>&lt;?xml-multiple?&gt;</code>
                * processing instructions, we'd set the <code>multiplePI</code>
                * property instead.
                * With the <code>autoPrimitive</code> property set, element text gets
                * automatically converted to JSON primitives (number, boolean, null).
                 */
                return new JsonXMLConfigBuilder()
                        .autoArray(false)   //  if set to true then memory usage will increase
                        .autoPrimitive(true)
                        .prettyPrint(true)
                        .build();
            default:
                throw new RuntimeException("Unsupported file type: " + inputFileType.toString());
        }
    }
    
    
    private XMLEventReader createReader(JsonXMLConfig config, FileTypeEnum inputFileType, InputStream input) 
            throws XMLStreamException {
        if (inputFileType == FileTypeEnum.XML) {
            return XMLInputFactory.newInstance().createXMLEventReader(input);
        } else if (inputFileType == FileTypeEnum.JSON) {
            return new JsonXMLInputFactory(config).createXMLEventReader(input);
        }

        throw new IllegalArgumentException("Unsupported file type: " + inputFileType);
    }

    private XMLEventWriter createWriter(JsonXMLConfig config, File sourceFile, OutputStream output,
            AtomicBoolean isCanceled, IFileReadListener listener) 
            throws XMLStreamException, FileNotFoundException, IOException {
        FileTypeEnum inputFileType = FileTypeEnum.parseByFileName(sourceFile.getName());
        if (inputFileType == FileTypeEnum.XML) {
            XMLEventWriter sourceWriter = new JsonXMLOutputFactory(config).createXMLEventWriter(output);
            try (InputStream input = new WrappedInputStream(new BufferedInputStream(new FileInputStream(sourceFile)),
                            listener, isCanceled)) {
                List<String> fileArrays = XmlUtils.determineArrays(input, isCanceled);
                return new XMLMultipleEventWriter(sourceWriter, true, fileArrays.toArray(new String[]{}));
            }
        } else if (inputFileType == FileTypeEnum.JSON) {
            return new PrettyXMLEventWriter(XMLOutputFactory.newInstance().createXMLEventWriter(output));
        }

        throw new IllegalArgumentException("Unsupported file type: " + inputFileType);
    }
}
