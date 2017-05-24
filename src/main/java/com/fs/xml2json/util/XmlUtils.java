

package com.fs.xml2json.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analyze source xml and tries to determine arrays.
 *
 * @author Anton
 * @since 1.1.0
 */
public class XmlUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);
    

    public static List<String> determineArrays(InputStream in, AtomicBoolean isCanceled) throws XMLStreamException {
        Set<String> arrayKeys = new HashSet<>();
        XMLStreamReader sr = null;
        try {
            XMLInputFactory f = XMLInputFactory.newFactory();
            sr = f.createXMLStreamReader(in);
            
            XmlUtils.getObjectElements(null, sr, new AtomicInteger(0), isCanceled, arrayKeys);
            
            System.out.println("========================");
            System.out.println("Arrays");
            arrayKeys.forEach(key -> {System.out.println(key);});
        } finally {
            if (null != sr) {
                sr.close();
            }
        }
        
        
        return new ArrayList<>(arrayKeys);
        //return Arrays.asList("/root/channel", "/root/channel/formats/format");
        //return new ArrayList<>();
    }
    
    public static List<XmlNode> getObjectElements(XmlNode parentNode, XMLStreamReader sr, AtomicInteger level, 
            AtomicBoolean isCanceled, Set<String> arrayKeys) throws XMLStreamException {
        if (null == sr) {
            throw new IllegalArgumentException("XMLStreamReader cannot be null");
        }
        
        List<XmlNode> result = new ArrayList<>();
        String currentElement;
        AtomicInteger elementLevel = level;
        XmlNode node;
        boolean levelFinished = false;
        while (sr.hasNext() && !isCanceled.get() && !levelFinished) {
            int code = sr.next();
            switch (code) {
                case XMLStreamConstants.START_ELEMENT:
                    currentElement = sr.getLocalName();
                    elementLevel.incrementAndGet();
                    node = new XmlNode(currentElement);
                    //result.add(node);
                    if (null == parentNode) {
                        parentNode = node;
                    } else {
                        node.parentNode = parentNode;
                        XmlNode elementNode = parentNode.nestedNode.get(node.getFullPath());
                        if (null == elementNode) {
                            parentNode.nestedNode.put(node.getFullPath(), node);
                        } else {
                            elementNode.occurrence++;
                            if (elementNode.occurrence > 1) {
                                if (null != arrayKeys && !arrayKeys.contains(elementNode.getFullPath())) {
                                    arrayKeys.add(elementNode.getFullPath());
                                }
//                                System.out.println("array: " + elementNode.getFullPath());
                            }
                        }
                    }
                    
//                    System.out.println("level - " + elementLevel + ", name - " + currentElement + ", parent - "+parentNode.getFullPath());
                    getObjectElements(node, sr, elementLevel, isCanceled, arrayKeys);

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    currentElement = sr.getLocalName();
//                    System.out.println("level - " + elementLevel + ", name - /" + currentElement + ", parent - "+parentNode.parentNode.getFullPath());
                    elementLevel.decrementAndGet();
                    
                    if (parentNode.nodeName.equals(currentElement)) {
                        levelFinished = true;
                    }
                    
                    break;
                case XMLStreamConstants.CHARACTERS:
                    
                    //break;
                }

        }
        
        return result;
    }
    
    public static class XmlNode {
        private String nodeName;
        private int occurrence = 1;
        private XmlNode parentNode;
        private Map<String, XmlNode> nestedNode = new LinkedHashMap<>();
        XmlNode() {}
        XmlNode(String nodeName) {
            this.nodeName = nodeName;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        public String getNodeName() {
            return nodeName;
        }

        public int getOccurrence() {
            return occurrence;
        }

        public XmlNode getParentNode() {
            return parentNode;
        }

        public Map<String, XmlNode> getNestedNode() {
            return nestedNode;
        }
        
        

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final XmlNode other = (XmlNode) obj;
            if (this.occurrence != other.occurrence) {
                return false;
            }
            if (!Objects.equals(this.nodeName, other.nodeName)) {
                return false;
            }
            return Objects.equals(this.parentNode, other.parentNode);
        }
        
        public String getFullPath() {
            if (null != parentNode) {
                return parentNode.getFullPath() + "/" + nodeName;
            }
            return "/" + nodeName;
        }
        
        @Override
        public String toString() {
            return String.format("{node=%s, nested=%s}", 
                    (null != parentNode ? parentNode.toString()+"/" : "/") + nodeName, 
                    (null != nestedNode ? nestedNode.toString() : "null"));
        }
    }

}
