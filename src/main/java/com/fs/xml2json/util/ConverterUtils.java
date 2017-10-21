
package com.fs.xml2json.util;

import com.fs.xml2json.type.FileTypeEnum;
import java.io.File;

/**
 * Various utility methods.
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
public class ConverterUtils {

    /**
     * Returns link to converted file with same name as source file, but with 
     * another extension (xml -> json, json -> xml).
     * <p>Note: only .json and .xml supported, caller method should pass only these files.
     * Supported extensions can be found in {@link FileTypeEnum}
     * 
     * @param sourceFile file to convert
     * @param destinationFolder path to destination folder
     * @return link to converted file
     */
    public static File getConvertedFile(File sourceFile, File destinationFolder) {
        FileTypeEnum fileType = FileTypeEnum.parseByFileName(sourceFile.getName());
        String convertedFileName = "";
        String fileNameWithoutExtension = sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf("."));
        switch (fileType) {
            case JSON:
                convertedFileName = fileNameWithoutExtension + FileTypeEnum.XML.getExtension();
                break;
            case XML:
                convertedFileName = fileNameWithoutExtension + FileTypeEnum.JSON.getExtension();
                break;
            default:
                throw new RuntimeException("Unsupported file's extension");
        }
        
        return new File(destinationFolder, convertedFileName);
    }
    
}
