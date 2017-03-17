/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm_bf;

import java.util.HashMap;


/**
 *
 * @author Sixto Castro
 */
public final class WTinyLFU {
    public static SegmentedLRUCache2 slru;
    public LRUCache2 windowCache;
    public static HashMap<String, String> data;
    static int windowCacheCapacity;
    static int totalCapacity;
    private static final String value_default = "abcdeabcdeabcdeabacdeabcdeabcdeabcdeabcdeabcdeabcde";
    static final long RESET_MASK = 0x7777777777777777L;
    static final long ONE_MASK = 0x1111111111111111L;
    static final long[] SEED = new long[] { // A mixture of seeds from FNV-1a, CityHash, and Murmur3
      0xc3a5c85c97cb3127L, 0xb492b66fbe98f273L, 0x9ae16a3b2f90404fL, 0xcbf29ce484222325L};
    int sampleSize;
    static int tableMask;
    static long[] table;
    int size;
    
    
    public WTinyLFU(int capacity, double percentMainCache, double percentPC, double percentFA, double percentWC){ //PC->Protected Cache, FA->First access Cache
        int mainCacheCapacity = (int) (capacity*percentMainCache);                                              //WC->Window Cache  
        windowCacheCapacity = (int)(capacity*percentWC);
        this.totalCapacity = capacity;
        this.slru = new SegmentedLRUCache2(mainCacheCapacity, percentPC, percentFA);
        this.windowCache = new LRUCache2(windowCacheCapacity);
        //WTinyLFU.data = new HashMap<>();
        ensureCapacity(capacity);
        
    }
    
    public String get(String key){
        
        if(windowCache.get(key)!=null){
            //System.out.println("item en Wc: "+key);
            increment(key);
            return key;
        }
        if(slru.get(key) == null) {//Search key in the main cache: Protected and First Access Cache
            String value = slru.firstAccessLRU.get(key);
            if (value == null)
                return null;
            slru.firstAccessLRU.remove(key);
            //System.out.println("item en Probation cache: "+key);
            slru.put(key, value);
            increment(key);
            return value;

        }else{
            increment(key);
            //System.out.println("item en Protected cache: "+key);
            return key;
        }
            
        
    }
    
    public void set(String key, String value) {
        windowCache.put(key, value);
//        if(!data.containsKey(key)){
//            data.put(key, value_default);
//            
//        }
        
    }
    
    public void ensureCapacity(long maximumSize) {
    //Caffeine.requireArgument(maximumSize >= 0);
        int maximum = (int) Math.min(maximumSize, Integer.MAX_VALUE >>> 1);
        if ((table != null) && (table.length >= maximum)) {
          return;
        }

        table = new long[(maximum == 0) ? 1 : ceilingNextPowerOfTwo(maximum)];
        tableMask = Math.max(0, table.length - 1);
        sampleSize = (maximumSize == 0) ? 10 : (10 * maximum);
        if (sampleSize <= 0) {
          sampleSize = Integer.MAX_VALUE;
        }
        size = 0;
    }
    
    
    
    public static int frequency(String key) {
        if (isNotInitialized()) {
            return 0;
        }
        int hash = spread(key.hashCode());
        int start = (hash & 3) << 2;
        int frequency = Integer.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
          int index = indexOf(hash, i);
          int count = (int) ((table[index] >>> ((start + i) << 2)) & 0xfL);
          frequency = Math.min(frequency, count);
        }
        return frequency;
    }
    
    public static boolean isNotInitialized() {
        return (table == null);
    }
    
    static int spread(int x) {
        x = ((x >>> 16) ^ x) * 0x45d9f3b;
        //x = ((x >>> 16) ^ x) * randomSeed;
        return (x >>> 16) ^ x;
    }
    
    public void increment(String key) {
        if (isNotInitialized()) {
          return;
        }

        int hash = spread(key.hashCode());
        int start = (hash & 3) << 2;

        // Loop unrolling improves throughput by 5m ops/s
        int index0 = indexOf(hash, 0);
        int index1 = indexOf(hash, 1);
        int index2 = indexOf(hash, 2);
        int index3 = indexOf(hash, 3);

        boolean added = incrementAt(index0, start);
        added |= incrementAt(index1, start + 1);
        added |= incrementAt(index2, start + 2);
        added |= incrementAt(index3, start + 3);

        if (added && (++size == sampleSize)) {
          reset();
        }
    }
    
   /**
   * Returns the table index for the counter at the specified depth.
   *
   * @param item the element's hash
   * @param i the counter depth
   * @return the table index
   */
    static int indexOf(int item, int i) {
        long hash = SEED[i] * item;
        hash += hash >> 32;
        return ((int) hash) & tableMask;
    }
   
  
  /** Reduces every counter by half of its original value. */
    void reset() {
        int count = 0;
        for (int i = 0; i < table.length; i++) {
            count += Long.bitCount(table[i] & ONE_MASK);
            table[i] = (table[i] >>> 1) & RESET_MASK;
        }
        size = (size >>> 1) - (count >>> 2);
    }

     
   /**
   * Increments the specified counter by 1 if it is not already at the maximum value (15).
   *
   * @param i the table index (16 counters)
   * @param j the counter to increment
   * @return if incremented
   */
    boolean incrementAt(int i, int j) {
        int offset = j << 2;
        long mask = (0xfL << offset);
        if ((table[i] & mask) != mask) {
          table[i] += (1L << offset);
          return true;
        }
        return false;
    }

    
    static int ceilingNextPowerOfTwo(int x) {
    // From Hacker's Delight, Chapter 3, Harry S. Warren Jr.
        return 1 << (Integer.SIZE - Integer.numberOfLeadingZeros(x - 1));
    }    
     
        
}
