
package com.fs.xml2json.listener;

import com.fs.xml2json.io.WrappedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for CmdFileReadListener.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class CmdFileReadListenerTest {

    
    @Test
    public void testCmdFileReadListenerForXml() throws IOException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        Assert.assertTrue(sourceFile.exists());
        AtomicBoolean isFinishCalled = new AtomicBoolean(false);
        AtomicLong bytesRead = new AtomicLong(0);
        AtomicInteger numberOfCallFinish = new AtomicInteger(0);
        IFileReadListener listener = new CustomCmdFileReadListener(isFinishCalled, numberOfCallFinish, 
                bytesRead, sourceFile);
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        byte[] buffer = new byte[128];
        // determine arrays
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.read(buffer, 0, buffer.length) > 0) {
            }
        }
        
        // second read for convertation
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.read(buffer, 0, buffer.length) > 0) {
            }
        }
        Assert.assertEquals(sourceFile.length() * 2, bytesRead.get());
        Assert.assertTrue(isFinishCalled.get());
        Assert.assertEquals(1, numberOfCallFinish.get());
    }
    
    @Test
    public void testCmdFileReadListenerForXmlReadByteMethod() throws IOException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        Assert.assertTrue(sourceFile.exists());
        AtomicBoolean isFinishCalled = new AtomicBoolean(false);
        AtomicLong bytesRead = new AtomicLong(0);
        AtomicInteger numberOfCallFinish = new AtomicInteger(0);
        IFileReadListener listener = new CustomCmdFileReadListener(isFinishCalled, numberOfCallFinish, 
                bytesRead, sourceFile);
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        // determine arrays
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.read() > 0) {
            }
        }
        // second read for convertation
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.read() > 0) {
            }
        }
        Assert.assertEquals(sourceFile.length() * 2, bytesRead.get());
        Assert.assertTrue(isFinishCalled.get());
        Assert.assertEquals(1, numberOfCallFinish.get());
    }
    
    
    
    @Test
    public void testCmdFileReadListenerForJson() throws IOException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleJson.json").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        Assert.assertTrue(sourceFile.exists());
        AtomicBoolean isFinishCalled = new AtomicBoolean(false);
        AtomicLong bytesRead = new AtomicLong(0);
        AtomicInteger numberOfCallFinish = new AtomicInteger(0);
        IFileReadListener listener = new CustomCmdFileReadListener(isFinishCalled, numberOfCallFinish, 
                bytesRead, sourceFile);
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        byte[] buffer = new byte[128];
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.read(buffer, 0, buffer.length) > 0) {
            }
        }
        Assert.assertEquals(sourceFile.length(), bytesRead.get());
        Assert.assertTrue(isFinishCalled.get());
        Assert.assertEquals(1, numberOfCallFinish.get());
    }
    
    private class CustomCmdFileReadListener extends CmdFileReadListener {
        AtomicBoolean isFinishedCalled;
        AtomicInteger numberOfCallFinish;
        AtomicLong bytesRead;
        int numberOfReads = 1;

        public CustomCmdFileReadListener(AtomicBoolean isFinishedCalled, AtomicInteger numberOfCallFinish, 
                AtomicLong bytesRead, File sourceFile) {
            super(sourceFile);
            this.isFinishedCalled = isFinishedCalled;
            this.numberOfCallFinish = numberOfCallFinish;
            this.bytesRead = bytesRead;
            if (sourceFile.getName().toLowerCase().endsWith(".xml")) {
                numberOfReads += 1;
            }
        }

        @Override
        public void update(int bytes) {
            super.update(bytes); 
            bytesRead.addAndGet(bytes);
        }
        
        

        @Override
        public void finished() {
            super.finished();
            if (0 == --numberOfReads) {
                isFinishedCalled.set(true);
                numberOfCallFinish.incrementAndGet();
            }
        }
        
        
    }
}
