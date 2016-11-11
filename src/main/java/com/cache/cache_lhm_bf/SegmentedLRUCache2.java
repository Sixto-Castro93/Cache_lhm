/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm_bf;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 *
 * @author Sixto Castro
 */
public class SegmentedLRUCache2 extends LinkedHashMap<String, String>{
    public int capacity;
    public LRUCache firstAccessLRU;
   
    public SegmentedLRUCache2(int capacity, double percentage, double percentage2) {
        super((int)((capacity+1)*percentage), 1.0f, true);  // for access order
        this.capacity = (int)(capacity*percentage); 
        firstAccessLRU = new LRUCache((int)((capacity+1)*percentage2)); 
        
    }
   
    public String get(String key) {
        if(super.get(key) == null) {
            String value = this.firstAccessLRU.get(key);
            if (value == null)
               return null;
            this.firstAccessLRU.remove(key);
            super.put(key, value);
            return value;
        } else {
            return ((String) super.get(key));
        }
    }
   
    public void set(String key, String value) {
        if (super.get(key) == null && this.firstAccessLRU.get(key) == null)
            this.firstAccessLRU.put(key, value);
        else {
            super.remove(key);
            this.firstAccessLRU.remove(key);
            this.firstAccessLRU.put(key, value);
        }
    }
    
    @Override
    protected boolean removeEldestEntry(Entry entry) {
/*        Entry eldest= super.entrySet().iterator().next();
        firstAccessLRU.put(eldest.getKey().toString(), eldest.getValue().toString());
        System.out.println(eldest.getKey().toString()+" "+eldest.getValue().toString());*/
        
        if (size()> this.capacity){
            firstAccessLRU.put(entry.getKey().toString(), entry.getValue().toString());
            //System.out.println(entry.getKey().toString()+" "+entry.getValue().toString());
            return true;
        }
        else
            return false;
    }
}
