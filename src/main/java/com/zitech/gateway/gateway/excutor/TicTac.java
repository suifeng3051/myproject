package com.zitech.gateway.gateway.excutor;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TicTac {

    private static Logger logger = LoggerFactory.getLogger(TicTac.class);

    private Map<String, Entry> entryMap = new HashMap<>();

    public void tic(String name) {
        if (entryMap.get(name) != null) {
            logger.error(name + " already exists");
            return;
        }
        entryMap.put(name, new Entry(System.nanoTime(), 0));
    }

    public void tac(String name) {
        Entry entry = entryMap.get(name);
        if (entry == null) {
            logger.error(name + " does not exist");
            return;
        }
        entry.setEnd(System.nanoTime());
    }

    public Map<String, Entry> getEntryMap() {
        return entryMap;
    }

    public Entry get(String name) {
        return entryMap.get(name);
    }

    public long elapsed(String name) {
        Entry entry = entryMap.get(name);
        if (entry != null)
            return entry.getElapsed();
        return -1;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(entryMap);
    }

    public class Entry {
        private long start;
        private long end;

        public Entry(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long getElapsed() {
            if (end == 0 || start == 0) {
                return -1;
            }
            return TimeUnit.NANOSECONDS.toMillis(end - start);
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

}
