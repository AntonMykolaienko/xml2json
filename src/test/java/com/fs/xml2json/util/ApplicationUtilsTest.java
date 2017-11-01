
package com.fs.xml2json.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
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

    @Test
    public void getImplementationVersionEmptyValue() throws Exception {
        PowerMockito.spy(ApplicationUtils.class);
        PowerMockito.doReturn("").when(ApplicationUtils.class, "getVersionFromManifest");

        String s = ApplicationUtils.getVersion();
        
        // PowerMock, verify
        PowerMockito.verifyStatic(Package.class);
        
        // Assert
        Assert.assertEquals(ApplicationUtils.UNNOWN_VERSION, s);
    }
    
    
    @Test
    public void testCallDefaultPrivateConstructor() throws NoSuchMethodException, InstantiationException, 
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<ApplicationUtils> c = ApplicationUtils.class.getDeclaredConstructor();
        c.setAccessible(true);
        ApplicationUtils inst = c.newInstance();
        
        Assert.assertNotNull(inst);
    }
}
