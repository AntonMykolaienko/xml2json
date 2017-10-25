
package com.fs.xml2json.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Anton Mykolaienko
 * @since 1.2.0
 */
@JsonRootName(value = "ComplexObject")
public class ComplexObject {
    
    private String text;
    private Integer intValue;
    private final List<SimpleObject> listOfObjects = new ArrayList<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public List<SimpleObject> getListOfObjects() {
        return listOfObjects;
    } 
    
}
