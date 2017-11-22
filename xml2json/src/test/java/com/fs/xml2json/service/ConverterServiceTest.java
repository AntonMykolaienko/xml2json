
package com.fs.xml2json.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fs.xml2json.listener.IFileReadListener;
import com.fs.xml2json.model.ComplexObject;
import com.fs.xml2json.model.SimpleObject;
import com.fs.xml2json.type.UnsupportedFileType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Tests for ConverterService.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ConverterServiceTest {
    
    List<File> filesToDelete = new ArrayList<>();
    File destinationFile;
    
    @After
    public void tearDown() {
        if (null != destinationFile && destinationFile.exists()) {
            deleteFilesAndDirs(destinationFile);
        }
        if (!filesToDelete.isEmpty()) {
            filesToDelete.forEach(file -> deleteFilesAndDirs(file));
        }
    }
    
    void deleteFilesAndDirs(File file) {
        if (file.isDirectory()) {
            // delete from directory all files
            Stream.of(file.listFiles()).forEach(f -> deleteFilesAndDirs(f));
            file.delete();
        } else {
            file.delete();
        }
    }

    @Test
    public void testConvertXmlToJson() throws IOException, XMLStreamException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.json");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
        
        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
        System.out.println("destinationFile = " + destinationFile.getAbsolutePath());
    }
    
    @Test
    public void testConvertXmlToJsonToNonExistingDirectory() throws IOException, XMLStreamException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        File nonExistingDirectory = new File(getTempDirectory(), "newDirectory");
        filesToDelete.add(nonExistingDirectory);
        destinationFile = new File(nonExistingDirectory, "newDirectory/ConvertedFile.json");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
        
        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
        System.out.println("destinationFile = " + destinationFile.getAbsolutePath());
    }
    
    @Test
    public void testConvertJsonToXml() throws IOException, XMLStreamException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleJson.json").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.xml");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
        
        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
        System.out.println("destinationFile = " + destinationFile.getAbsolutePath());
    }
    
    
    @Test(expected = XMLStreamException.class)
    public void testConvertCorruptedXmlToJson() throws IOException, XMLStreamException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("CorruptedXml.xml").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.json");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
    }
    
    
    @Test(expected = UnsupportedFileType.class)
    public void testTryConvertUnsupportedFile() throws IOException, XMLStreamException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("Unsupported.txt").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.json");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
    }
    
    
    @Test(expected = NullPointerException.class)
    public void testTryConvertWithNullListener() throws IOException, XMLStreamException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleJson.json").getFile());
        destinationFile = new File(getTempDirectory(), "ConvertedFile.xml");
        
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, null, isCanceled);
    }
    
    
    
    @Test
    public void testConvertXmlToJsonFullProcess() throws IOException, XMLStreamException {
        
        File tempDirectory = new File(getTempDirectory(), "xml2jsonSerialize");
        filesToDelete.add(tempDirectory);
        
        tempDirectory.mkdirs();
        
        ComplexObject actualObject = new ComplexObject();
        actualObject.setIntValue(555);
        actualObject.setText("Some Text in Complex Object");
        
        SimpleObject so = new SimpleObject();
        so.setBytePrimValue(Byte.valueOf("109"));
        so.setByteValue(Byte.valueOf("45"));
        so.setDoublePrimValue(457d);
        so.setDoubleValue(Double.parseDouble("256.12"));
        so.setFloatPrimValue(15.4f);
        so.setFloatValue(Float.parseFloat("852.45"));
        so.setIntegerValue(Integer.parseInt("400"));
        so.setIntValue(500);
        so.setLongPrimValue(32000000);
        so.setLongValue(Long.parseLong("4656053654"));
        so.setShortPrimValue((short)120);
        so.setShortValue(Short.parseShort("-99"));
        so.setSomeValue("Some text");
        
        actualObject.getListOfObjects().add(so);
        
        //2
        so = new SimpleObject();
        so.setBytePrimValue(Byte.valueOf("111"));
        so.setByteValue(Byte.valueOf("23"));
        so.setDoublePrimValue(3242d);
        so.setDoubleValue(Double.parseDouble("222.55"));
        so.setFloatPrimValue(2.2134f);
        so.setFloatValue(Float.parseFloat("876.44"));
        so.setIntegerValue(Integer.parseInt("456"));
        so.setIntValue(2223);
        so.setLongPrimValue(34534634);
        so.setLongValue(Long.parseLong("45856865"));
        so.setShortPrimValue((short)101);
        so.setShortValue(Short.parseShort("-44"));
        so.setSomeValue("Some text 2");
        
        actualObject.getListOfObjects().add(so);
        
        File sourceFile = new File(tempDirectory, "SerializedObject.xml");
        
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
        xmlMapper.setDefaultUseWrapper(false);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
            writer.write(xmlMapper.writeValueAsString(actualObject));
        }
        
        destinationFile = new File(tempDirectory, "SerializedObject.json");
        ConverterService service = new ConverterService();
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        service.convert(sourceFile, destinationFile, new CustomFileReadListener(), isCanceled);
        
        assertTrue(destinationFile.exists());
        assertTrue(destinationFile.length() > 0);
        
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        
        ComplexObject result = mapper.readValue(destinationFile, ComplexObject.class);
        
        assertNotNull(result);
    }
    
    
    private File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
    
    
    
    private class CustomFileReadListener implements IFileReadListener {

        @Override
        public void update(int bytes) {
            
        }

        @Override
        public void finished() {
            
        }
    }
}
