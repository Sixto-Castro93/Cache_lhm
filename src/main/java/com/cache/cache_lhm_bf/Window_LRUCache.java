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
public class Window_LRUCache extends LinkedHashMap<String, String>{//>Window Cache
    public int capacity;
    
   
    public Window_LRUCache(int capacity) {
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
        if(size()> this.capacity){
            if(WTinyLFU_LRU.lru.size() == WTinyLFU_LRU.lru.capacity){
                Entry eldestLRU= WTinyLFU_LRU.lru.entrySet().iterator().next();
                int freqVictim = WTinyLFU_LRU.frequency(eldestLRU.getKey().toString());//eldest element from probation cache
                int freqAdmitted = WTinyLFU_LRU.frequency(entry.getKey().toString());//eldest element from window cache
                //System.out.println("Frecuencia victima: "+freqVictim+" Victima: "+eldestLRU.getKey().toString());
                //System.out.println("Frecuencia admitted: "+freqAdmitted+" Admitted: "+entry.getKey().toString());
                if(freqVictim>freqAdmitted){
                    //System.out.println("Victima tiene mayor freq:"+ " " +eldestLRU.getKey().toString());
                    return true;
                }
                if(freqVictim<freqAdmitted){
                    WTinyLFU_LRU.lru.put(entry.getKey().toString(), entry.getValue().toString());
                    //System.out.println("Admitted tiene mayor freq:"+ " " +entry.getKey().toString());
                    return true;
                }
                    
               
            }
            WTinyLFU_LRU.lru.put(entry.getKey().toString(), entry.getValue().toString());
            return true;
        }
        else
            return false;
    }
}
