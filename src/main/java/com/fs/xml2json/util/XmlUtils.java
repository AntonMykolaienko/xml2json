
package com.fs.xml2json.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
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
    
    private static final String DELIM = "/";
    
    /**
     * Private constructor.
     */
    private XmlUtils() {
    }

    /**
     * Returns a list of paths of all arrays in XML.
     * 
     * @param in input stream (will be closed at the end)
     * @return list of paths of arrays in xml or empty list of arrays not found
     * @throws XMLStreamException 
     */
    public static List<String> determineArrays(InputStream in) throws XMLStreamException {
        Set<String> arrayKeys = new HashSet<>();
        XMLStreamReader sr = null;
        try {
            XMLInputFactory f = XMLInputFactory.newFactory();
            sr = f.createXMLStreamReader(in);
            
            XmlUtils.getObjectElements(null, sr, new LongAdder(), arrayKeys);
            
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
    
    private static void getObjectElements(XmlNode parentNode, XMLStreamReader sr, LongAdder level, 
            Set<String> arrayKeys) throws XMLStreamException {
        XmlNode node;
        boolean levelFinished = false;
        while (sr.hasNext() && !levelFinished) {
            switch (sr.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    level.increment();
                    node = new XmlNode(sr.getLocalName(), parentNode);
                    if (null == parentNode) {
                        parentNode = node;
                    } else {
                        XmlNode elementNode = parentNode.nestedNode.get(node.getFullPath());
                        if (null == elementNode) {
                            parentNode.nestedNode.put(node.getFullPath(), node);
                        } else {
                            if (++elementNode.occurrence > 1 && !arrayKeys.contains(elementNode.getFullPath())) {
                                arrayKeys.add(elementNode.getFullPath());
                            }
                        }
                    }
                    getObjectElements(node, sr, level, arrayKeys);

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    level.decrement();
                    if (parentNode.nodeName.equalsIgnoreCase(sr.getLocalName())) {
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
        private final XmlNode parentNode;
        private final String fullPath;
        private Map<String, XmlNode> nestedNode = new LinkedHashMap<>();

        XmlNode(String nodeName, XmlNode parentNode) {
            this.nodeName = nodeName.toLowerCase();
            this.parentNode = parentNode;
            if (null != parentNode) {
                fullPath = parentNode.getFullPath().toLowerCase() + DELIM + nodeName;
            } else {
                fullPath = DELIM + nodeName;
            }
        }

        public String getFullPath() {
            return fullPath;
        }
        
        @Override
        public String toString() {
            return String.format("{node=%s, nested=%s}", 
                    (null != parentNode ? parentNode.toString() + DELIM : DELIM) + nodeName, 
                    (null != nestedNode ? nestedNode.toString() : "null"));
        }
    }

}
