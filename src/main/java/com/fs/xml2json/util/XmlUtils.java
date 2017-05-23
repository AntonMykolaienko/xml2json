

package com.fs.xml2json.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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
        XMLStreamReader sr = null;
        try {
            XMLInputFactory f = XMLInputFactory.newFactory();
            sr = f.createXMLStreamReader(in);

            String propertyName = "";
            String propertyValue = "";
            String currentElement = "";

            Set<String> arrayKeys = new HashSet<>();
            Map<String, XmlNode> tree = new LinkedHashMap();
            
            XmlNode root = new XmlNode();
            
            String path = "/";
            XmlNode parentNode = root;
            int level = 0;
            while (sr.hasNext() && !isCanceled.get()) {
                int code = sr.next();
                switch (code) {
                    case XMLStreamConstants.START_ELEMENT:
                        currentElement = sr.getLocalName();
                        if (!path.endsWith("/")) {
                            path += "/";
                        }
                        path = path + currentElement.toLowerCase();
                        XmlNode node = tree.get(path);
                        if (null == node) { // node exists (array?)
                            level++;
//                            if (parentNode.occurrence > 1) {
//                                parentNode.occurrence = 1;
//                            }
                            node = new XmlNode(currentElement.toLowerCase());
                            if (parentNode.equals(tree.get(path.substring(0, path.lastIndexOf("/"))))) {
                                // same level
                                // TODO:
                            }
                            parentNode = tree.get(path.substring(0, path.lastIndexOf("/")));
                            if (null == parentNode) {
                                parentNode = root;
                            }
                            node.parentNode = parentNode;
                            //parentNode.nestedNode = node;
                            //parentNode = node;
                            tree.put(path, node);
                        } else {
                            node.occurrence += 1;
                            if (node.occurrence >= 2 && !arrayKeys.contains(path)) {
                                arrayKeys.add(path);
                            }
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        level--;
                        currentElement = sr.getLocalName();
                        if (path.endsWith(currentElement.toLowerCase())) {
                            XmlNode elementNode = tree.get(path);
                            if (null != elementNode && null != elementNode.nestedNode) {
                                elementNode.nestedNode = null;
                            }
                            path = path.substring(0, path.lastIndexOf("/"));
                        } else {
                            logger.warn("Expected [{}], but found [{}]", path.substring(path.lastIndexOf("/") + 1), 
                                    currentElement);
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (currentElement.equalsIgnoreCase("element")) {
                            propertyName += sr.getText();
                        } else if (currentElement.equalsIgnoreCase("attribute")) {
                            propertyValue += sr.getText();
                        }
                        break;
                    }

            }

            sr.close();
            
            // TODO: implement read
            tree.forEach((String key, XmlNode value) -> {
                System.out.println(key + " - " + value.occurrence);
            });
            
            System.out.println("========================");
            System.out.println("Arrays");
            arrayKeys.forEach(key -> {System.out.println(key);});
        } finally {
            if (null != sr) {
                sr.close();
            }
        }
        
        
        //return new ArrayList<>();
        return Arrays.asList("/root/channel", "/root/channel/formats/format");
        //return new ArrayList<>();
    }
    
    private static List<XmlNode> getObjectElements(XmlNode parentNode, XMLStreamReader sr, int level, 
            AtomicBoolean isCanceled) throws XMLStreamException {
        if (null == sr) {
            throw new IllegalArgumentException("XMLStreamReader cannot be null");
        }
        
        List<XmlNode> result = new ArrayList<>();
        String currentElement = "";
        while (sr.hasNext() && !isCanceled.get()) {
            int code = sr.next();
            switch (code) {
                case XMLStreamConstants.START_ELEMENT:
                    currentElement = sr.getLocalName();

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    level--;
                    currentElement = sr.getLocalName();
                    break;
                case XMLStreamConstants.CHARACTERS:
//                        if (currentElement.equalsIgnoreCase("element")) {
//                            propertyName += sr.getText();
//                        } else if (currentElement.equalsIgnoreCase("attribute")) {
//                            propertyValue += sr.getText();
//                        }
                    break;
                }

        }
        
        return result;
    }
    
    private static class XmlNode {
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
        
        @Override
        public String toString() {
            return String.format("{node=%s, nested=%s}", 
                    (null != parentNode ? parentNode.toString()+"/" : "/") + nodeName, 
                    (null != nestedNode ? nestedNode.toString() : "null"));
        }
    }

}
