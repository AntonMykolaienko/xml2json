
package com.fs.xml2json.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Custom filter for files.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class CustomPatternFileFilter implements FileFilter {
    
    private final String pattern;

    /**
     * Creates custom filter for desired pattern.
     * 
     * @param pattern pattern for file names
     */
    public CustomPatternFileFilter(String pattern) {
        this.pattern = pattern.replace("*", ".*?").toLowerCase();
    }

    
    @Override
    public boolean accept(File file) {        
        return file.getName().toLowerCase().matches(pattern);
    }

}
