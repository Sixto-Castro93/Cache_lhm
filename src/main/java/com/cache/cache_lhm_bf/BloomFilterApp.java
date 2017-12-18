/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm_bf;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import java.nio.charset.Charset;
import java.util.Random;
import net.agkn.hll.HLL;

/**
 *
 * @author Sixto Castro
 */
public class BloomFilterApp {
    static Random random = new Random();

    public static void main(String[] args) {
        // convert object into funnel - specific to google implementation
        int totals=100000;
        BloomFilter<CharSequence> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 
            15,0.01);

        final HLL hll = new HLL(13, 5); //number of bucket and bits per bucket
        ////
        final HLL hll2 = new HLL(13, 5);
        final int seed = 123456;
        int i =0;
        HashFunction hash = Hashing.murmur3_128(seed);
        // data on which to calculate distinct count
        final Integer[] data = new Integer[]{1, 1, 2, 3, 4, 5, 6, 6, 
                6, 7, 7, 7, 7, 8, 10};
        
        for (int item : data) {
            i++;
            if(bloomFilter.mightContain(Integer.toString(item))){
                System.out.println("value 1HLL#"+i+": "+item);
                hll.addRaw(item);
            } else{
                System.out.println("value 2HLL#"+i+": "+item);
                hll2.addRaw(item);
                bloomFilter.put(Integer.toString(item));
                
            }
            
           
            
        }
        System.out.println("Cardinalidad HLL1: "+hll.cardinality());
        System.out.println("Cardinalidad HLL2: "+hll2.cardinality());
        System.out.println("Cardinalidad Dif: "+ (hll2.cardinality()-hll.cardinality()));
        // check if key exist in bloom filter
        /*String key = "key100";
        assert (bloomFilter.mightContain(key)) == true;
        System.out.println((bloomFilter.mightContain(key)) == true);
        String key2 = "key5";
        assert (bloomFilter.mightContain(key2) == false);
        System.out.println((bloomFilter.mightContain(key2)) == false);*/
        
        
    }
}