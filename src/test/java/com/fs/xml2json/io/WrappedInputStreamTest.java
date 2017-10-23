
package com.fs.xml2json.io;

import com.fs.xml2json.listener.IFileReadListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for WrappedInputStream.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class WrappedInputStreamTest {
    
    @Test
    public void testAvailableMethod() throws IOException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        assertTrue(sourceFile.exists());
        
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        AtomicLong bytesRead = new AtomicLong(0);
        AtomicLong bytesSendToListener = new AtomicLong(0);
        IFileReadListener listener = new CustomFileReadListener(bytesSendToListener);
        
        byte[] buffer = new byte[128];
        // determine arrays
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.available() > 0) {
                bytesRead.addAndGet(in.read(buffer, 0, buffer.length));
            }
        }
        // second read for convertation
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.available() > 0) {
                bytesRead.addAndGet(in.read(buffer, 0, buffer.length));
            }
        }
        assertEquals(sourceFile.length() * 2, bytesRead.get());
        assertEquals(sourceFile.length() * 2, bytesSendToListener.get());
    }
    
    @Test
    public void testCancelRead() throws IOException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        assertTrue(sourceFile.exists());
        
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        AtomicLong bytesRead = new AtomicLong(0);
        AtomicLong bytesSendToListener = new AtomicLong(0);
        IFileReadListener listener = new CustomFileReadListener(bytesSendToListener);
        
        int bytesToReadBeforeCancel = (int)sourceFile.length()/2;
        
        // determine arrays
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.available() > 0) {
                if (bytesToReadBeforeCancel == bytesRead.get()) {
                    isCanceled.compareAndSet(false, true);
                }
                if (in.read() > 0) {
                    bytesRead.incrementAndGet();
                }
            }
        }
        // second read for convertation
        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.available() > 0) {
                if (bytesToReadBeforeCancel == bytesRead.get()) {
                    isCanceled.compareAndSet(false, true);
                }
                if (in.read() > 0) {
                    bytesRead.incrementAndGet();
                }
            }
        }
        assertEquals(bytesToReadBeforeCancel, bytesRead.get());
        assertEquals(bytesToReadBeforeCancel, bytesSendToListener.get());
    }
    
    @Test
    public void testCancelReadBuffer() throws IOException {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleJson.json").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        assertTrue(sourceFile.exists());
        
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        AtomicLong bytesRead = new AtomicLong(0);
        AtomicLong bytesSendToListener = new AtomicLong(0);
        IFileReadListener listener = new CustomFileReadListener(bytesSendToListener);
        
        int bytesToReadBeforeCancel = (int)sourceFile.length()/2;
        byte[] buffer = new byte[128];

        try (InputStream in = new WrappedInputStream(new FileInputStream(sourceFile), listener, isCanceled)){
            while (in.available() > 0) {
                if (bytesToReadBeforeCancel == bytesRead.get()) {
                    isCanceled.compareAndSet(false, true);
                }
                int bytesToRead = (bytesRead.get() + buffer.length) >= bytesToReadBeforeCancel 
                        ? (bytesToReadBeforeCancel - bytesRead.intValue()) : buffer.length;
                int bytes = in.read(buffer, 0, bytesToRead);
                if (bytes > 0) {
                    bytesRead.addAndGet(bytes);
                }
            }
        }

        assertEquals(bytesToReadBeforeCancel, bytesRead.get());
        assertEquals(bytesToReadBeforeCancel, bytesSendToListener.get());
    }
    
    private class CustomFileReadListener implements IFileReadListener {

        private final AtomicLong bytesSendToListener;

        public CustomFileReadListener(AtomicLong bytesSendToListener) {
            this.bytesSendToListener = bytesSendToListener;
        }
        
        @Override
        public void update(int bytes) {
            bytesSendToListener.addAndGet(bytes);
        }

        @Override
        public void finished() {
        }
    }
}
