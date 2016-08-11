/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 *
 * @author cesar17
 */
public class LRUCache extends LinkedHashMap<String, String>{
    private int capacity;
   
    public LRUCache(int capacity) {
        super(capacity+1, 1.0f, true);  // for access order
        this.capacity = capacity;
    }
   
    public String get(String key) {
        if(super.get(key) == null)
            return null;
        else
            return ((String) super.get(key));
    }
   
    public void set(String key, String value) {
        super.put(key, value);
    }
    
    @Override
    protected boolean removeEldestEntry(Entry entry) {
        return (size() > this.capacity);
    }
}
