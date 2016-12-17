/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm_bf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Sixto Castro
 */
public class Cache_lhm {
    
    private static String value_default = "abcdeabcdeabcdeabacdeabcdeabcdeabcdeabcdeabcdeabcde";
    
    public static void main(String[] args) throws IOException{//args[0]->delimiter..args[1]->id column
                                                              //args[2]->1:LRUCache, 2:LRUCacheWithBloomFilter, 3:Segmented Cache
                                                              //args[3]-> cache capacity
        int length_args = args.length;                                                      
        int total_access = 0;
        int total_hits = 0;
        int replacement_policy = Integer.parseInt(args[2]);
        int capacity = (int)Double.parseDouble(args[3]);//4988059->100%
        double p = 0.0;
        double percentage = 0.0;
        double percentage2 = 0.0;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        LRUCache_BloomFilter cache_bf;
        SegmentedLRUCache segmented_cache;
        SegmentedLRUCache2 segmented_cache2=null;
        WTinyLFU wLFU = null;
        LRUCache cache;
        String line = null;
        String option = null;
        float hit_rate = 0;
        HashSet<Long> uniqueKeys = new HashSet<Long>();
        Random rand = new Random();
        int numKeysZipfian = 1000000;
        double ZipfConf = 0.7;
        ZipfianGenerator zipf = new ZipfianGenerator(numKeysZipfian, ZipfConf);

        
        if(replacement_policy == 1){     //opcion 1 = LRU Cache 
            option = "LRU Cache";
            cache = new LRUCache(capacity);
            while( (line = input.readLine()) != null ) {   
                total_access = total_access + 1;
                String file_id = line.split(args[0])[Integer.parseInt(args[1])];
                
                if (cache.get(file_id) == null){
                    cache.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
            
        }
        if(replacement_policy == 2)
        {      //opcion 2 = LRU Cache with Bloom Filter.....args[4]-> probability p
            option = "LRU Cache with Bloom Filter";
            if(length_args == 5){
                p = Double.parseDouble(args[4]);
                System.out.println("Probability p: " + p);
                cache_bf = new LRUCache_BloomFilter(capacity, p);
            }else
                cache_bf = new LRUCache_BloomFilter(capacity);
            
            while( (line = input.readLine()) != null ) {   
                total_access = total_access + 1;
                String file_id = line.split(args[0])[Integer.parseInt(args[1])];
                
                if (cache_bf.get(file_id) == null){
                    cache_bf.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }        
        }
        if(replacement_policy == 3){ //opcion 3: SLRU
            option = "Segmented LRU Cache";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            segmented_cache = new SegmentedLRUCache(capacity, percentage, percentage2);
            System.out.println("Principal cache capacity: "+segmented_cache.capacity);
            System.out.println("First access LRU cache capacity: "+segmented_cache.firstAccessLRU.capacity);
            while( (line = input.readLine()) != null ) {   
                total_access = total_access + 1;
                String file_id = line.split(args[0])[Integer.parseInt(args[1])];
                
                if (segmented_cache.get(file_id) == null){
                    segmented_cache.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
        }
        if(replacement_policy == 4){ //opcion 4: SLRU
            option = "Segmented LRU Cache 2";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            segmented_cache2 = new SegmentedLRUCache2(capacity, percentage, percentage2);
            System.out.println("Principal cache capacity: "+segmented_cache2.capacity);
            System.out.println("First access LRU cache capacity: "+segmented_cache2.firstAccessLRU.capacity);
            while( (line = input.readLine()) != null ) {   
                total_access = total_access + 1;
                String file_id = line.split(args[0])[Integer.parseInt(args[1])];
                
                if (segmented_cache2.get(file_id) == null){
                    segmented_cache2.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
//            System.out.println("Lista items en First Access LRU");
//            for (Map.Entry<String, String> entry : segmented_cache2.firstAccessLRU.entrySet()) {
//                System.out.println(entry.getKey()+" : "+entry.getValue());
//            }
        }
        if(replacement_policy == 5){ //opcion 5: WTiny LFU
            option = "WTiny LFU";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            wLFU = new WTinyLFU(capacity, percentage, 0.3, 0.7, percentage2);//0.3 0.7(protected and first access cache percentage):DreamWork / youtube trace
            System.out.println("Main cache capacity: "+(capacity*percentage));
            System.out.println("Window cache capacity: "+WTinyLFU.windowCacheCapacity);
            while( (line = input.readLine()) != null ) {   
                total_access = total_access + 1;
                String file_id = line.split(args[0])[Integer.parseInt(args[1])];
                
                if (wLFU.get(file_id) == null){
                    wLFU.increment(file_id);
                    wLFU.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
//            System.out.println("Lista items en First Access LRU");
//            for (Map.Entry<String, String> entry : WTinyLFU.slru.firstAccessLRU.entrySet()) {
//                System.out.println(entry.getKey()+" : "+entry.getValue());
//            }
        }
        
        if(replacement_policy == 6){     //opcion 1 = LRU Cache 
            option = "LRU Cache - zipfian";
            cache = new LRUCache(capacity);
            //int num_access=30;
            int num_access=(20*32*capacity)+(capacity*10000);
            int i=0;
            while( num_access > 0 ) {
                num_access--; 
                i++;
                total_access = total_access + 1;
                long rnd = zipf.nextValue();
                //System.out.println("rand number " +i+ ": "+rnd);
                uniqueKeys.add(rnd);
                String file_id = Long.toString(rnd);
                if (cache.get(file_id) == null){
                    cache.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
            /*System.out.println("unique keys: ");
            Object[] array = uniqueKeys.toArray();
            for (int j = 0; j< array.length; j++) {
                System.out.println(array[j]);
            }*/
            System.out.println("Unique keys: " + uniqueKeys.size());
            
        }
        if(replacement_policy == 7){     
            option = "Segmented LRU Cache 2 - zipfian";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            segmented_cache2 = new SegmentedLRUCache2(capacity, percentage, percentage2);
            System.out.println("Principal cache capacity: "+segmented_cache2.capacity);
            System.out.println("First access LRU cache capacity: "+segmented_cache2.firstAccessLRU.capacity);
            int num_access=(20*32*capacity)+(capacity*10000);
            int i=0;
            while( num_access > 0 ) {
                num_access--; 
                i++;
                total_access = total_access + 1;
                long rnd = zipf.nextValue();
                //System.out.println("rand number " +i+ ": "+rnd);
                uniqueKeys.add(rnd);
                String file_id = Long.toString(rnd);
                if (segmented_cache2.get(file_id) == null){
                    segmented_cache2.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            
            }
            
            System.out.println("Unique keys: " + uniqueKeys.size());
            
        }
        
        if(replacement_policy == 8){ 
            option = "WTiny LFU - Zipfian";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            wLFU = new WTinyLFU(capacity, percentage, 0.7, 0.3, percentage2);//0.3 0.7(protected and first access cache percentage):DreamWork / youtube trace
            System.out.println("Main cache capacity: "+(capacity*percentage));
            System.out.println("Window cache capacity: "+WTinyLFU.windowCacheCapacity);
            int num_access=(20*32*capacity)+(capacity*10000);
            int i=0;
            while( num_access > 0 ) {
                num_access--; 
                i++;
                total_access = total_access + 1;
                long rnd = zipf.nextValue();
                //System.out.println("rand number " +i+ ": "+rnd);
                uniqueKeys.add(rnd);
                String file_id = Long.toString(rnd);
                
                if (wLFU.get(file_id) == null){
                    wLFU.increment(file_id);
                    wLFU.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }

        }
        
        System.out.println(option);
        System.out.printf("Number of accessed file:  %d \n", total_access);
        System.out.printf("Number of hits:  %d \n", total_hits);
        hit_rate = (float)total_hits / total_access;
        System.out.printf("Hit rate %.7f \n", hit_rate);
        long warmUpPeriod = 20*32*capacity;
        double warm_hit_rate = (float)(total_hits - warmUpPeriod) / (total_access - warmUpPeriod);
        System.out.printf("Warm hit rate %.7f \n", warm_hit_rate);
     }   
                
        
    
    
    public static void print_lhm (LRUCache cache){
        for(Map.Entry<String, String> e : cache.entrySet()) {
            System.out.println(e.getKey());
        }
        System.out.printf("Size %d\n", cache.size());
    }
    
    
}
