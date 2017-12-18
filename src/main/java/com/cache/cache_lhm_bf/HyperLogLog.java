/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm_bf;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import java.nio.charset.Charset;
import net.agkn.hll.HLL;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 *
 * @author Sixto Castro
 */
public class HyperLogLog {
    public static void main(String[] args) {
        int totals=100000;
        final int seed = 123456;
        int i =0;
        BloomFilter bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 
            15,0.01);
        HashFunction hash = Hashing.murmur3_128(seed);
        // data on which to calculate distinct count
        final Integer[] data = new Integer[]{1, 1, 2, 3, 4, 5, 6, 6, 
                6, 7, 7, 7, 7, 8, 10};
        final HLL hll = new HLL(13, 5); //number of bucket and bits per bucket
        ////
        final HLL hll2 = new HLL(13, 5);
        

        
        /////////////////////////
        /*for (int item : data) {
            i++;
            final long value = hash.newHasher().putInt(item).hash().asLong();
            System.out.println("value "+i+" "+value);
            hll.addRaw(value);
            
        }
        System.out.println("Distinct count="+ hll.cardinality());*/
        for (int item : data) {
            i++;
            if(bloomFilter.mightContain(item)){
                final long value = hash.newHasher().putInt(item).hash().asLong();
                System.out.println("value 1HLL#"+i+" "+value);
                hll.addRaw(value);
            } else{
                final long value = hash.newHasher().putInt(item).hash().asLong();
                System.out.println("value 2HLL#"+i+" "+value);
                hll2.addRaw(value);
                bloomFilter.put(value);
                
            }
            
           
            
        }
        System.out.println("Cardinalidad HLL1: "+hll.cardinality());
        System.out.println("Cardinalidad HLL2: "+hll2.cardinality());
        System.out.println("Cardinalidad Dif: "+ (hll2.cardinality()-hll.cardinality()));
        

    }
}