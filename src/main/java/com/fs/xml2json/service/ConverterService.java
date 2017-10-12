
package com.fs.xml2json.service;

import com.fs.xml2json.controller.IFileReadUpdater;
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
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service which responsible for file conversion from JSON to XML and vise versa.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ConverterService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);
    
    
    /**
     * Converts file from XML to JSON or vise versa
     * 
     * @param sourceFile file to convert
     * @param outputFile output file 
     * @param listener read listener
     * @return converted file
     */
    public File convert(File sourceFile, File outputFile, IFileReadUpdater listener) {
        
        // TODO: implement me
        
        logger.info("Started converting ...");


        StopWatch sw = new StopWatch();

        InputStream input = null;
        OutputStream output = null;
        XMLEventReader reader = null;
        XMLEventWriter writer = null;
        FileTypeEnum inputFileType;
        try {
            long fileSizeInBytes = sourceFile.length();
            inputFileType = FileTypeEnum.parseByFileName(sourceFile.getName());
            
            if (isXml(sourceFile)) {
                fileSizeInBytes *= 2;
            }

            // XXX: uncomment this
//            input = new WrappedInputStream(new BufferedInputStream(new FileInputStream(sourceFile)),
//                    processedBytes, fileSizeInBytes, isCanceled);

            JsonXMLConfig config = createConfig(inputFileType);

            output = new BufferedOutputStream(Files.newOutputStream(outputFile.toPath(),
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));

            sw.start();


            // Create reader.
            reader = createReader(config, inputFileType, input);
            // Create writer.
            writer = createWriter(config, inputFileType, output);

            // if source is xml then read file first and determine arrays
            if (isXml(sourceFile)) {
                InputStream input2 = null;
                try {
                    //XXX: uncomment this
//                    input2 = new WrappedInputStream(new BufferedInputStream(new FileInputStream(sourceFile)),
//                            processedBytes, fileSizeInBytes, isCanceled);
//                    List<String> fileArrays = XmlUtils.determineArrays(input2, isCanceled);
//                    writer = new XMLMultipleEventWriter(writer, true, fileArrays.toArray(new String[]{}));
                } finally {
                    if (null != input2) {
                        try {
                            input2.close();
                        } catch (IOException ex) {}
                    }
                }
            }
            
            // Copy events from reader to writer.
            writer.add(reader);

            // XXX: uncomment this
//            processedBytes.set(1.0);
//            inProgress.compareAndSet(true, false);
            if (null != listener) {
                listener.finished();
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
            throw new RuntimeException(ex);
        } finally {
            logger.info("Taken time: {}", sw);
            sw.stop();
            if (null != reader) {
                try {
                    reader.close();
                } catch (XMLStreamException ex) {
                    logger.error(ex.toString());
                }
            }
            if (null != writer) {
                try {
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
        
        return null;
    }
    
    
    private boolean isXml(File file) {
        return file.getAbsolutePath().toLowerCase().endsWith(FileTypeEnum.XML.getExtension());
    }

    private boolean isJson(File file) {
        return file.getAbsolutePath().toLowerCase().endsWith(FileTypeEnum.JSON.getExtension());
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

    private XMLEventWriter createWriter(JsonXMLConfig config, FileTypeEnum inputFileType, OutputStream output) 
            throws XMLStreamException {
        if (inputFileType == FileTypeEnum.XML) {
            return new JsonXMLOutputFactory(config).createXMLEventWriter(output);
        } else if (inputFileType == FileTypeEnum.JSON) {
            return new PrettyXMLEventWriter(XMLOutputFactory.newInstance().createXMLEventWriter(output));
        }

        throw new IllegalArgumentException("Unsupported file type: " + inputFileType);
    }
}
