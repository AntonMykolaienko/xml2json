
package com.fs.xml2json.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests for ApplicationUtils class.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationUtils.class)
public class ApplicationUtilsTest {


    @Test
    public void testApplicationVersion() {
        Assert.assertEquals(ApplicationUtils.UNNOWN_VERSION, ApplicationUtils.getVersion());
    }
    
    
    @Test
    public void getImplementationVersion() throws Exception {
        PowerMockito.spy(ApplicationUtils.class);
        PowerMockito.doReturn("1.0.0").when(ApplicationUtils.class, "getVersionFromManifest");

        String s = ApplicationUtils.getVersion();
        
        // PowerMock, verify
        PowerMockito.verifyStatic(Package.class);
        
        // Assert
        Assert.assertEquals("1.0.0", s);
    }

    
}
