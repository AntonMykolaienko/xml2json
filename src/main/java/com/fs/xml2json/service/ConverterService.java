/**
 * Copyright © 2016-2017 Anton Mykolaienko. All rights reserved. Contacts: <amykolaienko@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

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
import com.fs.xml2json.type.UnsupportedFileType;
import java.io.FileNotFoundException;
import java.util.Objects;

/**
 * Service which responsible for file conversion from JSON to XML and vise versa.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ConverterService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConverterService.class);
    
    private static final String UNSUPPORTED_FILE_TYPE_TEMPLATE = "Unsupported file type: '%s'";
    
    /**
     * Converts file from XML to JSON or vise versa and returns link to converted file.
     * 
     * @param sourceFile file to convert
     * @param outputFile output file 
     * @param listener read listener
     * @param isCanceled flag to stop process
     * @return converted file
     * @throws IOException if an I/O error occurs
     * @throws XMLStreamException if cannot create XML/JSON writer
     */
    public File convert(File sourceFile, File outputFile, IFileReadListener listener, AtomicBoolean isCanceled) 
            throws IOException, XMLStreamException {
        StopWatch sw = new StopWatch();
        
        Objects.requireNonNull(listener, "Listener must be not null");

        FileTypeEnum inputFileType = FileTypeEnum.parseByFileName(sourceFile.getName());
        
        if (null == inputFileType) {
            throw new UnsupportedFileType(String.format(UNSUPPORTED_FILE_TYPE_TEMPLATE, sourceFile.getName()));
        }
        
        File parentFolder = outputFile.getParentFile();
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
        
        try (InputStream input = getWrappedInputStream(sourceFile, listener, isCanceled); 
                OutputStream output = getOutputStream(outputFile)) {
            
            sw.start();

            // converter config
            JsonXMLConfig config = createConfig(inputFileType);

            // Create reader.
            XMLEventReader reader = createReader(config, inputFileType, input);
            // Create writer.
            XMLEventWriter writer = createWriter(config, sourceFile, output, isCanceled, listener);
            
            // Copy events from reader to writer.
            writer.add(reader);

            listener.finished();
                
            writer.flush();
            writer.close();
            reader.close();
        } catch (XMLStreamException ex) {
            throw new XMLStreamException(ex.getMessage());
        } finally {
            logger.info("Taken time: {}", sw);
            sw.stop();
        }
        
        return outputFile;
    }

    /**
     * Returns wrapped input stream.
     * 
     * @param sourceFile file to read
     * @param listener progress listener
     * @param isCanceled variable for canceling process
     * @return wrapped input stream 
     * @throws FileNotFoundException if file not found
     */
    private InputStream getWrappedInputStream(File sourceFile, IFileReadListener listener, 
            AtomicBoolean isCanceled) throws FileNotFoundException {
        return new WrappedInputStream(new BufferedInputStream(new FileInputStream(sourceFile)), 
                listener, isCanceled);
    }
    
    /**
     * Returns output stream.
     * 
     * @param outputFile file for output
     * @return output stream
     * @throws IOException if an I/O error occurs
     */
    private OutputStream getOutputStream(File outputFile) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(outputFile.toPath(),
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));
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
                throw new UnsupportedFileType(String.format(UNSUPPORTED_FILE_TYPE_TEMPLATE, inputFileType.toString()));
        }
    }
    
    
    private XMLEventReader createReader(JsonXMLConfig config, FileTypeEnum inputFileType, InputStream input) 
            throws XMLStreamException {
        if (inputFileType == FileTypeEnum.XML) {
            return XMLInputFactory.newInstance().createXMLEventReader(input);
        } else {    // json
            return new JsonXMLInputFactory(config).createXMLEventReader(input);
        }
    }

    /**
     * Creates writer based on source file.
     * <p>Is source file is XML, then arrays will be determined first.
     * 
     * @param config converter config
     * @param sourceFile file to convert
     * @param output output stream
     * @param isCanceled object for canceling process
     * @param listener progress listener
     * @return file writer
     * @throws XMLStreamException if cannot create writer
     * @throws IOException if an I/O error occurs or if source file not found
     */
    private XMLEventWriter createWriter(JsonXMLConfig config, File sourceFile, OutputStream output,
            AtomicBoolean isCanceled, IFileReadListener listener) 
            throws XMLStreamException, IOException {
        
        FileTypeEnum inputFileType = FileTypeEnum.parseByFileName(sourceFile.getName());
        if (inputFileType == FileTypeEnum.XML) {
            XMLEventWriter sourceWriter = new JsonXMLOutputFactory(config).createXMLEventWriter(output);
            try (InputStream input = getWrappedInputStream(sourceFile, listener, isCanceled)) {
                List<String> fileArrays = XmlUtils.determineArrays(input);
                return new XMLMultipleEventWriter(sourceWriter, true, fileArrays.toArray(new String[]{}));
            }
        } else { // json
            return new PrettyXMLEventWriter(XMLOutputFactory.newInstance().createXMLEventWriter(output));
        }
    }
}
