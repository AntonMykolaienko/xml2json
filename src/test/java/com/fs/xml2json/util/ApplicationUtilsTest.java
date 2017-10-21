
package com.fs.xml2json.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for ApplicationUtils class.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ApplicationUtilsTest {


    @Test
    public void testApplicationVersion() {
        Assert.assertEquals(ApplicationUtils.UNNOWN_VERSION, ApplicationUtils.getVersion());
    }
}
