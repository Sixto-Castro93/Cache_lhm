/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm;
import com.cache.cache_lhm.LRUCache;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 *
 * @author cesar17
 */
public class Cache_lhm {

    /**
     * @param args the command line arguments
     */
    private static String value_default = "abcdeabcdeabcdeabacdeabcdeabcdeabcdeabcdeabcdeabcde";
    
    public static void main(String[] args) throws IOException{
        int total_access = 0;
        int total_hits = 0;
        int capacity = 228177;
//        int capacity = 5;
        float hit_rate = 0;
        LRUCache cache = new LRUCache(capacity);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String line = null;  
        while( (line = input.readLine()) != null ) {   
            total_access = total_access + 1;
            String file_id = line.split(",")[1];
            if (cache.get(file_id) == null){
                cache.set(file_id, Cache_lhm.value_default);
            }
            else{
                total_hits =  total_hits + 1;
                cache.set(file_id, Cache_lhm.value_default);
            }
//            System.out.printf("***Current state %d***\n", total_access);
//            print_lhm(cache);
//            System.out.println("***************");
        }
        System.out.printf("Number of accessed file:  %d \n", total_access);
        System.out.printf("Number of hits:  %d \n", total_hits);
        hit_rate = (float)total_hits / total_access;
        System.out.printf("Hit rate %.7f \n", hit_rate);
    }
    
    public static void print_lhm (LRUCache cache){
        for(Map.Entry<String, String> e : cache.entrySet()) {
            System.out.println(e.getKey());
        }
        System.out.printf("Size %d\n", cache.size());
    }
    
}
