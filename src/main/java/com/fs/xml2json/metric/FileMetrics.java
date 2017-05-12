
package com.fs.xml2json.metric;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Contains counters for arrays, objects etc. in file.
 *
 * @author  Anton Mykolaienko
 * @since   1.0.0
 */
public class FileMetrics {

    private long arrays = 0;
    private long objects = 0;
    
    private final Map<String, AtomicLong> objectsCounter;

    public FileMetrics() {
        this.objectsCounter = new HashMap<>();
    }
    
    
    

    public long getArrays() {
        return arrays;
    }

    public void incrementArrays() {
        this.arrays++;
    }

    public long getObjects() {
        return objects;
    }

    public void incrementObjects() {
        this.objects++;
    }
    
    public void incrementObjectCounter(String objectName) {
        if (null == objectName) {
            return;
        }
        AtomicLong counter = objectsCounter.computeIfAbsent(objectName, key -> objectsCounter.put(key, new AtomicLong(0)));
        counter.incrementAndGet();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        objectsCounter.entrySet().stream().forEach((entry) -> {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(entry.getKey()).append(" - ").append(entry.getValue().get());
        });
        
        return String.format("Metrics: arrays=%d, objects=%d, objectsCounters={%s}", arrays, objects, sb.toString());
    }
}
