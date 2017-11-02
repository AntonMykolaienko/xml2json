package com.fs.xml2json;

import com.fs.xml2json.cli.ApplicationCommandLine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for Starter.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
@RunWith(PowerMockRunner.class)
public class CMDStarterMockTest {
    
    private static final String DESTINATION_FOLDER = "destination";
    private File destinationFolder;
    private final List<File> filesToDelete = new ArrayList<>();
    
    //BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
    
    
    @Before
    public void setUp() {
        destinationFolder = getDestinationDirectory();
        if (null != destinationFolder && destinationFolder.exists()) {
            deleteFilesAndDirs(destinationFolder);
        }
    }
    
    @After
    public void tearDown() {
        if (null != destinationFolder && destinationFolder.exists()) {
            deleteFilesAndDirs(destinationFolder);
        }
        filesToDelete.forEach(file -> deleteFilesAndDirs(file));
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
    @PrepareForTest({Starter.class, LoggerFactory.class})
    public void testStartNoGuiAndAnswerNo() throws FileNotFoundException, IOException, Exception {
        File sourceFolder = new File("src/test/resources/xml");
        
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        BDDMockito.when(bufferedReader.readLine()).thenReturn("r").thenReturn("n");
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.xml"};

        PowerMockito.mockStatic(LoggerFactory.class);
        Logger logger = PowerMockito.mock(Logger.class);
        PowerMockito.when(LoggerFactory.getLogger(Mockito.any(Class.class))).thenReturn(logger);

        // first run
        Starter starter = Mockito.spy(new Starter(args));
        starter.start();
        
        Assert.assertTrue(destinationFolder.exists());
        Assert.assertEquals(2, destinationFolder.listFiles().length);
        Stream.of(destinationFolder.listFiles()).forEach(file -> Assert.assertTrue(file.length() > 0));
        
        Stream.of(destinationFolder.listFiles()).forEach(file -> {
            try (FileChannel outChan = new FileOutputStream(file, true).getChannel()) {
                outChan.truncate(0);
            } catch (IOException ex) {}
        });
        
        Starter starterClass = Mockito.spy(new Starter(args));
        PowerMockito.whenNew(BufferedReader.class).withAnyArguments().thenReturn(bufferedReader);
        
        starterClass.start();       
        
        Assert.assertEquals(2, destinationFolder.listFiles().length);
        Stream.of(destinationFolder.listFiles()).forEach(file -> Assert.assertTrue(file.length() == 0));
    }
    
    
    @Test
    @PrepareForTest({Starter.class, LoggerFactory.class})
    public void testStartNoGuiAndAnswerYes() throws FileNotFoundException, IOException, Exception {
        File sourceFolder = new File("src/test/resources/xml");
        
        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        BDDMockito.when(bufferedReader.readLine()).thenReturn("r").thenReturn("Y");
        
        System.out.println("SourceFolder: " + sourceFolder.getAbsolutePath());
        Assert.assertTrue(sourceFolder.exists());
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.xml"};
        
        PowerMockito.mockStatic(LoggerFactory.class);
        Logger logger = PowerMockito.mock(Logger.class);
        PowerMockito.when(LoggerFactory.getLogger(Mockito.any(Class.class))).thenReturn(logger);
        
        Starter starter = Mockito.spy(new Starter(args));
        starter.start();
        
        Assert.assertTrue(destinationFolder.exists());
        Assert.assertEquals(2, destinationFolder.listFiles().length);
        Stream.of(destinationFolder.listFiles()).forEach(file -> Assert.assertTrue(file.length() > 0));
        
        Stream.of(destinationFolder.listFiles()).forEach(file -> {
            try (FileChannel outChan = new FileOutputStream(file, true).getChannel()) {
                outChan.truncate(0);
            } catch (IOException ex) {}
        });
        
//        Starter starterClass = Mockito.mock(Starter.class);
        //InputStream in = Mockito.mock(InputStream.class);
//        BufferedInputStream in = Mockito.mock(BufferedInputStream.class);
//        InputStreamReader inr = Mockito.mock(InputStreamReader.class);
//        PowerMockito.whenNew(Starter.class).withArguments(args).thenReturn(starterClass);
//        PowerMockito.whenNew(InputStreamReader.class).withArguments(in).thenReturn(inr);
//        PowerMockito.whenNew(BufferedReader.class).withArguments(inr).thenReturn(bufferedReader);
//        PowerMockito.when(bufferedReader.readLine()).thenReturn("r").thenReturn("Y");
//            //Whitebox.invokeMethod(starterClass, "start");
        
        
        Starter secondStarter = Mockito.spy(new Starter(args));
        PowerMockito.whenNew(BufferedReader.class).withAnyArguments().thenReturn(bufferedReader);
        
//        //Starter.main(args);
        secondStarter.start();
        
        Assert.assertEquals(2, destinationFolder.listFiles().length);
        Stream.of(destinationFolder.listFiles()).forEach(file -> Assert.assertTrue(file.length() > 0));
    }
    
    
    @Test
    @PrepareForTest({Starter.class, GuiStarter.class, LoggerFactory.class})
    public void testStartWithFakeGui() throws Exception {
        String[] args = new String[]{};
        //given
        PowerMockito.mockStatic(LoggerFactory.class);
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(LoggerFactory.getLogger(Mockito.any(Class.class))).thenReturn(logger);
        
        Starter starter = Mockito.spy(new Starter(args));
        PowerMockito.mockStatic(Starter.class);
        PowerMockito.mockStatic(GuiStarter.class);
                
        //PowerMockito.doThrow(new ParseException("Some parse exception", 0)).when(GuiStarter.class, "main", 
        PowerMockito.doNothing().when(GuiStarter.class, "main", 
                Mockito.any());
        

        //PowerMockito.verifyStatic(Starter.class);
        starter.start();

//        //when
//        sut.execute();
//
//        //then
//        PowerMockito.verifyStatic();
//        DriverManager.getConnection(...);
    }
    
    
    @Test
    @PrepareForTest({Starter.class, LoggerFactory.class, ApplicationCommandLine.class})
    public void testCallStartWithParseException() throws Exception {
        String[] args = new String[]{"--noGui"};
        //given
        PowerMockito.mockStatic(LoggerFactory.class);
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(LoggerFactory.getLogger(Mockito.any(Class.class))).thenReturn(logger);
        
        Starter starter = Mockito.spy(new Starter(args));
        PowerMockito.mockStatic(Starter.class);
        
        PowerMockito.mockStatic(ApplicationCommandLine.class);
        PowerMockito.doThrow(new ParseException("Some parse exception", 0)).when(ApplicationCommandLine.class, "parse", 
                Mockito.any());

        starter.start();
    }
    
    @Test
    @PrepareForTest({Starter.class, LoggerFactory.class, ApplicationCommandLine.class})
    public void testCallStartWithFileNotFoundException() throws Exception {
        String[] args = new String[]{"--noGui"};
        //given
        PowerMockito.mockStatic(LoggerFactory.class);
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(LoggerFactory.getLogger(Mockito.any(Class.class))).thenReturn(logger);
        
        Starter starter = Mockito.spy(new Starter(args));
        PowerMockito.mockStatic(Starter.class);
        
        PowerMockito.mockStatic(ApplicationCommandLine.class);
        PowerMockito.doThrow(new FileNotFoundException("File is missing")).when(ApplicationCommandLine.class, "parse", 
                Mockito.any());

        starter.start();
    }
    
    @Test
    @PrepareForTest({Starter.class, LoggerFactory.class})
    public void testCallStartWithIOException() throws Exception {
        File sourceFolder = new File("src/test/resources/json");
        
        String[] args = new String[]{"--noGui", "--sourceFolder", sourceFolder.getAbsolutePath(), 
            "--destinationFolder", destinationFolder.getAbsolutePath(), "--pattern", "*.json",
            "--overwrite"};
        
        //given
        PowerMockito.mockStatic(LoggerFactory.class);
        Logger logger = Mockito.mock(Logger.class);
        Mockito.when(LoggerFactory.getLogger(Mockito.any(Class.class))).thenReturn(logger);

        Starter starter = PowerMockito.spy(new Starter(args));
        PowerMockito.mockStatic(Starter.class);
        
        ApplicationCommandLine cmd = ApplicationCommandLine.parse(args);
        
        PowerMockito.doThrow(new IOException("Some IO exception")).when(starter, "noGuiHandler", 
                Mockito.any(cmd.getClass()));

        starter.start();
    }
    
    
    private File getDestinationDirectory() {
        return new File(getTempDirectory(), DESTINATION_FOLDER);
    }
    
    private File getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
}