/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm_bf;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 *
 * @author Sixto
 */
public class LRUCache_BloomFilter extends LinkedHashMap<String, String>{
    private int capacity;
    double fpp = 0.03; // desired false positive probability
    int expectedInsertions = 54600645; //500;
    BloomFilter<CharSequence> bloomFilter;
    double p;
    
    public LRUCache_BloomFilter(int capacity) {
        this(capacity, 0);
    }
    
    public LRUCache_BloomFilter(int capacity, double p) {
        super(capacity+1, 1.0f, true);  // for access order
        this.capacity = capacity;
        this.p = 0;
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 
            expectedInsertions,fpp);
    }
   
    public String get(String key) {
        if(super.get(key) == null)
            return null;
        else
            return ((String) super.get(key));
    }
   
    public void set(String key, String value) {
        if(this.bloomFilter.mightContain(key)){
            super.put(key, value);
        } else{
            // First time we are seing this key
            // Ignore key but add it to bloom filter 
            // so that we know that we have already seen it.
            this.bloomFilter.put(key);
            
            // With some probability p, we will also add the
            // element to the cache.
            double sample = rnd.nextDouble();
            if (sample < p)
                super(key, value);
        }
    }
    
    
    @Override
    protected boolean removeEldestEntry(Entry entry) {
        return (size()> this.capacity);
    }
    
}
