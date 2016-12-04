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
 * @author cesar17
 */
public class LRUCache2 extends LinkedHashMap<String, String>{
    public int capacity;
    
   
    public LRUCache2(int capacity) {
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
            if(WTinyLFU.slru.firstAccessLRU.size() == WTinyLFU.slru.firstAccessLRU.capacity){
            //if(WTinyLFU.data.size()> WTinyLFU.totalCapacity){
                //WTinyLFU.data.remove(entry.getKey().toString());
                Entry eldestFA= WTinyLFU.slru.firstAccessLRU.entrySet().iterator().next();
                int freqVictim = WTinyLFU.frequency(eldestFA.getKey().toString());//eldest element from probation cache
                int freqAdmitted = WTinyLFU.frequency(entry.getKey().toString());//eldest element from window cache
                //System.out.println("Frecuencia victima: "+freqVictim+" Victima: "+eldestFA.getKey().toString());
                //System.out.println("Frecuencia admitted: "+freqAdmitted+" Admitted: "+entry.getKey().toString());
                if(freqVictim>freqAdmitted){
                    //System.out.println("Victima tiene mayor freq:"+ " " +eldestFA.getKey().toString());
                    return true;
                }
                else{
                    WTinyLFU.slru.firstAccessLRU.put(entry.getKey().toString(), entry.getValue().toString());
                    //System.out.println("Admitted tiene mayor freq:"+ " " +entry.getKey().toString());
                    return true;
                }
                    
               
            }
            WTinyLFU.slru.firstAccessLRU.put(entry.getKey().toString(), entry.getValue().toString());
            return true;
        }
        else
            return false;
    }
}
