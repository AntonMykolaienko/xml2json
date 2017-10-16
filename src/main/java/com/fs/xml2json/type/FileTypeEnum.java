
package com.fs.xml2json.type;

/**
 * File types.
 *
 * @author Anton Mykolaienko
 * @since 1.0.0
 */
public enum FileTypeEnum {
    
    XML(".xml")     ,
    
    JSON(".json")   ;
    
    private final String extension;

    private FileTypeEnum(String extension) {
        this.extension = extension;
    }
    
    
    /**
     * Returns file's extension with dot (example: .json, .xml)
     * 
     * @return file's extension
     */
    public String getExtension() {
        return extension;
    }
    
    /**
     * Return file's type (XML or JSON) or return <code>null</code> if not XML and not JSON.
     * 
     * @param fileName file's name
     * @return fyles type or null if cannot determine
     */
    public static final FileTypeEnum parseByFileName(String fileName) {
        for (FileTypeEnum type : FileTypeEnum.values()) {
            if (fileName.toLowerCase().endsWith(type.extension)) {
                return type;
            }
        }
        
        return null;
    }
}
