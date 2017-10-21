
package com.fs.xml2json.filter;

import com.fs.xml2json.type.FileTypeEnum;
import java.io.File;
import java.io.FileFilter;

/**
 * Custom filter for files. 
 * <p>Performs filtering by supported extensions (.json and .xml) and then by custom pattern.
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
        String fileNameInLowerCase = file.getName().toLowerCase();
        
        FileTypeEnum fileType = FileTypeEnum.parseByFileName(fileNameInLowerCase);
        
        return (null != fileType) && fileNameInLowerCase.matches(pattern);
    }

}
