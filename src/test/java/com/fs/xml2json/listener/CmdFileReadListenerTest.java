
package com.fs.xml2json.listener;

import java.io.File;
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
    public void testCmdFileReadListener() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        Assert.assertTrue(sourceFile.exists());
        IFileReadListener listener = new CmdFileReadListener(sourceFile);
        
        listener.update((int) sourceFile.length() / (int) sourceFile.length() / 2);  // 50%
        listener.update((int) sourceFile.length() / (int) sourceFile.length());  // 100%
        
        listener.finished();
    }
}
