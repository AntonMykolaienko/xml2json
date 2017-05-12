package com.fs.xml2json.io;

import java.io.IOException;
import java.io.InputStream;
import javafx.beans.property.DoubleProperty;

/**
 * Wrapper for input stream to indicate progress.
 * <p>Used to update progress bar.
 *
 * @author Anton
 * @since 1.0.0
 */
public class WrappedInputStream extends InputStream {

    private final InputStream input;
    private final DoubleProperty processedBytes;
    private final long fileBytes;
    private long bytesRead = 0;

    /**
     * Constructor.
     * 
     * @param input source input stream
     * @param processedBytes updatable pramaeter
     * @param fileBytes number of bytes of source file
     */
    public WrappedInputStream(InputStream input, DoubleProperty processedBytes, long fileBytes) {
        
        this.input = input;
        this.processedBytes = processedBytes;
        this.fileBytes = fileBytes;
    }

    @Override
    public int read() throws IOException {
        int b = input.read();

        if (-1 == b) {
            processedBytes.set(1.0);
            bytesRead = 0;
        }

        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytes = input.read(b, off, len);

        if (-1 == bytes) {
            processedBytes.set(1.0);
            bytesRead = 0;
        }

        bytesRead += bytes;
        double delta = (double) bytesRead / (double) fileBytes;
        if (delta > 0.01) {
            processedBytes.set(processedBytes.get() + delta);
            bytesRead = 0;
        }

        return bytes;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public int available() throws IOException {
        return input.available();
    }

}
