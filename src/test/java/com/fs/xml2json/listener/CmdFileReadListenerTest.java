
package com.fs.xml2json.listener;

import com.fs.xml2json.io.WrappedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
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
        IFileReadListener listener = new CustomCmdFileReadListener(isFinishCalled, sourceFile);
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        
        byte[] buffer = new byte[128];
        // determine arrays
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.available() > 0) {
                int bytesToRead = in.available() > buffer.length ? buffer.length : in.available();
                in.read(buffer, 0, bytesToRead);
            }
        }
        
        // second read for convertation
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.available() > 0) {
                int bytesToRead = in.available() > buffer.length ? buffer.length : in.available();
                in.read(buffer, 0, bytesToRead);
            }
        }
        
        Assert.assertTrue(isFinishCalled.get());
    }
    
    
    
//    @Test
//    public void testCmdFileReadListenerForJson() {
//        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleJson.json").getFile());
//        System.out.println(sourceFile.getAbsolutePath());
//        Assert.assertTrue(sourceFile.exists());
//        IFileReadListener listener = new CmdFileReadListener(sourceFile);
//        
//        listener.update((int) sourceFile.length() / 2);  // 50%
//        listener.update((int) sourceFile.length() / 2);  // 100%
//        
//        listener.finished();
//    }
    
    private class CustomCmdFileReadListener extends CmdFileReadListener {
        AtomicBoolean isFinishedCalled;

        public CustomCmdFileReadListener(AtomicBoolean isFinishedCalled, File sourceFile) {
            super(sourceFile);
            this.isFinishedCalled = isFinishedCalled;
        }

        @Override
        public void finished() {
            super.finished(); 
            isFinishedCalled.set(true);
        }
        
        
    }
}
