
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
    public void testCmdFileReadListenerForXml() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleXml.xml").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        Assert.assertTrue(sourceFile.exists());
        IFileReadListener listener = new CmdFileReadListener(sourceFile);
        
        // read bytes
        listener.update((int) sourceFile.length() / 2);  // 25%
        listener.update((int) sourceFile.length() / 2);  // 50%
        listener.update((int) sourceFile.length() / 2);  // 75%
        listener.update((int) sourceFile.length() / 2);  // 100%
        
        listener.finished();
    }
    
    @Test
    public void testCmdFileReadListenerForJson() {
        File sourceFile = new File(this.getClass().getClassLoader().getResource("SampleJson.json").getFile());
        System.out.println(sourceFile.getAbsolutePath());
        Assert.assertTrue(sourceFile.exists());
        IFileReadListener listener = new CmdFileReadListener(sourceFile);
        
        listener.update((int) sourceFile.length() / 2);  // 50%
        listener.update((int) sourceFile.length() / 2);  // 100%
        
        listener.finished();
    }
}
