
package com.fs.xml2json.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    
    /**
     * Private constructor.
     */
    private XmlUtils() {
    }

    /**
     * Returns a list of paths of all arrays in XML.
     * 
     * @param in input stream (will be closed at the end)
     * @param isCanceled flag to indicate cancelation
     * @return list of paths of arrays in xml or empty list of arrays not found
     * @throws XMLStreamException 
     */
    public static List<String> determineArrays(InputStream in, AtomicBoolean isCanceled) throws XMLStreamException {
        Set<String> arrayKeys = new HashSet<>();
        XMLStreamReader sr = null;
        try {
            XMLInputFactory f = XMLInputFactory.newFactory();
            sr = f.createXMLStreamReader(in);
            
            XmlUtils.getObjectElements(null, sr, new AtomicInteger(0), isCanceled, arrayKeys);
            
            if (logger.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                arrayKeys.forEach(key -> sb.append(sb.length() > 0 ? "\n" : "").append(key));
                
                logger.trace("Found arrays:\n{}", sb.toString());
            }            
        } finally {
            if (null != sr) {
                sr.close();
            }
        }

        return new ArrayList<>(arrayKeys);
    }
    
    private static void getObjectElements(XmlNode parentNode, XMLStreamReader sr, AtomicInteger level, 
            AtomicBoolean isCanceled, Set<String> arrayKeys) throws XMLStreamException {
        
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
                    if (null == parentNode) {
                        parentNode = node;
                    } else {
                        node.parentNode = parentNode;
                        String nodeFullPath = node.getFullPath();
                        XmlNode elementNode = parentNode.nestedNode.get(nodeFullPath);
                        if (null == elementNode) {
                            parentNode.nestedNode.put(nodeFullPath, node);
                        } else {
                            elementNode.occurrence++;
                            String elementNodeFullPath = elementNode.getFullPath();
                            if (elementNode.occurrence > 1 && null != arrayKeys 
                                    && !arrayKeys.contains(elementNodeFullPath)) {
                                arrayKeys.add(elementNodeFullPath);
                            }
                        }
                    }
                    getObjectElements(node, sr, elementLevel, isCanceled, arrayKeys);

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    currentElement = sr.getLocalName();
                    elementLevel.decrementAndGet();
                    
                    if (parentNode.nodeName.equals(currentElement)) {
                        levelFinished = true;
                    }
                    
                    break;
                case XMLStreamConstants.CHARACTERS:
                    break;
                    
                default: // nothing to do
                    break;
            }
        }
    }
    
    private static class XmlNode {
        private String nodeName;
        private int occurrence = 1;
        private XmlNode parentNode;
        private Map<String, XmlNode> nestedNode = new LinkedHashMap<>();

        XmlNode(String nodeName) {
            this.nodeName = nodeName;
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
