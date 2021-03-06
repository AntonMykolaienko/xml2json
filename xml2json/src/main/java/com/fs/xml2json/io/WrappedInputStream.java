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

package com.fs.xml2json.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import com.fs.xml2json.listener.IFileReadListener;

/**
 * Wrapper for input stream to indicate progress.
 * <p>Used to update progress bar.
 *
 * @author Anton
 * @since 1.0.0
 */
public class WrappedInputStream extends InputStream {

    private final InputStream input;
    private final IFileReadListener listener;
    private final AtomicBoolean isCancel;

    /**
     * Constructor.
     *
     * @param input source input stream
     * @param listener read listener
     * @param isCancel flag which indicates that process have been canceled
     */
    public WrappedInputStream(InputStream input, IFileReadListener listener,
            AtomicBoolean isCancel) {
        this.input = input;
        this.listener = listener;
        this.isCancel = isCancel;
    }

    @Override
    public int read() throws IOException {
        if (isCancel.get()) {
            return -1;
        }

        int b = input.read();
        if (-1 == b) {
            listener.finished();
        } else {
            listener.update(1);
        }

        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (isCancel.get()) {
            return -1;
        }

        int bytes = input.read(b, off, len);
        if (-1 == bytes) {
            listener.finished();
        } else {
            listener.update(bytes);
        }

        return bytes;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public int available() throws IOException {
        if (isCancel.get()) {
            return -1;
        }
        return input.available();
    }

}
