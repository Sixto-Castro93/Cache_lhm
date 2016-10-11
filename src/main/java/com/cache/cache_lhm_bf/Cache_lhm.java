/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm_bf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 *
 * @author Sixto
 */
public class Cache_lhm {
    
    private static String value_default = "abcdeabcdeabcdeabacdeabcdeabcdeabcdeabcdeabcdeabcde";
    
    public static void main(String[] args) throws IOException{//args[0]->delimitador..args[1]->columna del id
                                                              //args[2]->1:LRUCache, 2:LRUCache with Bloom Filter
                                                              //args[3]-> cache tamaño...args[4]-> probability p
        int total_access = 0;
        int total_hits = 0;
        int capacity = Integer.parseInt(args[3]);//4988059->100%
        double p = Double.parseDouble(args[4]);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        LRUCache_BloomFilter cache_bf;
        LRUCache cache;
        String line = null;
        String opcion;
        float hit_rate = 0;
        if(Integer.parseInt(args[2]) == 1){     //opcion 1 = LRU Cache
            opcion = "LRU Cache";
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
            
        }else{      //opcion 2 = LRU Cache with Bloom Filter
            opcion = "LRU Cache with Bloom Filter";
            cache_bf = new LRUCache_BloomFilter(capacity, p);
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
        
        System.out.println(opcion);
        System.out.printf("Number of accessed file:  %d \n", total_access);
        System.out.printf("Number of hits:  %d \n", total_hits);
        hit_rate = (float)total_hits / total_access;
        System.out.printf("Hit rate %.7f \n", hit_rate);
        //System.out.println(cache_bf.bloomFilter.expectedFpp());
       
        
    }
    
    public static void print_lhm (LRUCache cache){
        for(Map.Entry<String, String> e : cache.entrySet()) {
            System.out.println(e.getKey());
        }
        System.out.printf("Size %d\n", cache.size());
    }
    
    
}
