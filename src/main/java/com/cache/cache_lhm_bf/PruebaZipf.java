/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cache.cache_lhm_bf;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.nio.charset.Charset;
import net.agkn.hll.HLL;

/**
 *
 * @author Sixto Castro
 */
public class PruebaZipf {
    public static void main(String[] args) {
        // convert object into funnel - specific to google implementation
        int totals=100000;
        int numKeysZipfian = 1000000;
        double ZipfConf = 0.7;
        int total_access = 0;
        ZipfianGenerator zipf = new ZipfianGenerator(numKeysZipfian, ZipfConf);
        BloomFilter<CharSequence> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 
            532000000,0.01);
        //ZipfianGenerator zipf = new ZipfianGenerator(50, ZipfConf);
        //BloomFilter<CharSequence> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 
        //    532000000,0.01);
        final HLL hll = new HLL(17, 5); //number of bucket and bits per bucket 13 y 5
        ////
        final HLL hll2 = new HLL(17, 5);
        final int seed = 123456;
        int i =0;
        int num_access = 532000000;//20;//500;//
        HashFunction hash = Hashing.murmur3_128(seed);
        // data on which to calculate distinct count
        final Integer[] data = new Integer[]{1, 1, 2, 3, 4, 5, 6, 6, 
                6, 7, 7, 7, 7, 8, 10};
        
        /*for (int item : data) {
            i++;
            if(bloomFilter.mightContain(Integer.toString(item))){
                System.out.println("value 1HLL#"+i+": "+item);
                hll.addRaw(item);
            } else{
                System.out.println("value 2HLL#"+i+": "+item);
                hll2.addRaw(item);
                bloomFilter.put(Integer.toString(item));
                
            }
              
        }*/
         while( num_access > 0 ) {
            num_access--; 
            i++;
            total_access = total_access + 1;
            long rnd = zipf.nextValue();
             //System.out.println("Item "+i+": "+rnd);
            if(bloomFilter.mightContain(Long.toString(rnd))){
                //System.out.println("value 1HLL#"+i+": "+rnd);
                hll.addRaw(rnd);
            } else{
                //System.out.println("value 2HLL#"+i+": "+rnd);
                hll2.addRaw(rnd);
                bloomFilter.put(Long.toString(rnd));
                
            }
         }
        System.out.println("Total accesos: "+total_access); 
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
