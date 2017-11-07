/**
 * Copyright © 2016-2017 Anton Mykolaienko. All rights reserved. Contacts: <amykolaienko@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public final class XmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

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
     * @throws XMLStreamException if exception occurs in
     * {@link #getObjectElements(XmlUtils.XmlNode, XMLStreamReader, LongAdder, Set)}
     */
    public static List<String> determineArrays(InputStream in) throws XMLStreamException {
        Set<String> arrayKeys = new HashSet<>();
        XMLStreamReader sr = null;
        try {
            XMLInputFactory f = XMLInputFactory.newFactory();
            sr = f.createXMLStreamReader(in);

            getObjectElements(null, sr, new LongAdder(), arrayKeys);

            if (LOGGER.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                arrayKeys.forEach(key -> sb.append(sb.length() > 0 ? "\n" : "").append(key));

                LOGGER.trace("Found arrays:\n{}", sb.toString());
            }
        } finally {
            if (null != sr) {
                sr.close();
            }
        }

        return new ArrayList<>(arrayKeys);
    }

    /**
     * Counts arrays and populates {@code arrayKeys}.
     *
     * @param parentNode root or parent node
     * @param sr stream reader
     * @param level nesting level
     * @param arrayKeys kays to hold counters
     * @throws XMLStreamException if exception occurs in XMLStreamReader
     */
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

    /**
     * Xml node.
     */
    private static class XmlNode {
        private String nodeName;
        private int occurrence = 1;
        private final XmlNode parentNode;
        private final String fullPath;
        private Map<String, XmlNode> nestedNode = new LinkedHashMap<>();

        /**
         * Constructor.
         *
         * @param nodeName name of current node
         * @param parentNode parrent node
         */
        XmlNode(String nodeName, XmlNode parentNode) {
            this.nodeName = nodeName.toLowerCase();
            this.parentNode = parentNode;
            if (null != parentNode) {
                fullPath = parentNode.getFullPath().toLowerCase() + DELIM + nodeName;
            } else {
                fullPath = DELIM + nodeName;
            }
        }

        /**
         * Returns full path to current node like /root/element/someAnotherElement.
         *
         * @return full name for current node
         */
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
