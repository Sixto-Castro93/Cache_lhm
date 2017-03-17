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
import java.util.concurrent.ThreadLocalRandom;

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
        WTinyLFU_LRU wLFU2 = null;
        LRUCache cache;
        String line = null;
        String option = null;
        float hit_rate = 0;
        HashSet<Long> uniqueKeys = new HashSet<Long>();
        Random rand = new Random();
        int numKeysZipfian = 1000000;
        double ZipfConf = 0.7;
        ZipfianGenerator zipf = new ZipfianGenerator(numKeysZipfian, ZipfConf);
        boolean flag_malicious = false;
        
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
            //wLFU = new WTinyLFU(capacity, percentage, 0.6, 0.4, percentage2);//0.3 0.7(protected and first access cache percentage):DreamWork / youtube trace
            wLFU = new WTinyLFU(capacity, percentage, 0.3, 0.7, percentage2);
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
        
        if(replacement_policy == 6){      
            option = "LRU Cache - zipfian";
            cache = new LRUCache(capacity);
            int cont = 0;
            int cont_malicious=0;
            int num_access=(20*32*capacity)+(capacity*10000);
            double malicious_requests=0.0;
            int option2;//option2==0->Zipfian Distribution...option2==1-> Zipfian Distribution with malicious requests
            try{
                malicious_requests = Double.parseDouble(args[6]);
                option2=1;
                
            }catch(Exception e){
                option2 = 0;
            }
            
            if(option2 == 0){//"LRU Cache - zipfian"
                while( num_access > 0 ) {
                    num_access--; 
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
            }
            else{//"LRU Cache - zipfian with malicious requests"
                option = option + " with malicious requests";
                //double malicious_requests = Double.parseDouble(args[6]);//35466667;//35466666.67; 
                double percent_malic_req = malicious_requests/num_access;
                System.out.println(percent_malic_req);
                int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
                System.out.println(x);
                long rnd;
                while( num_access > 0 ) {
                    num_access--; 
                    cont++;
                    total_access = total_access + 1;
                    if((cont % x) == 0){
                        rnd = ThreadLocalRandom.current().nextLong(0,1000000);
                        //System.out.println("Malicioso: "+rnd);
                        cont_malicious++;
                    }
                    else{
                        rnd = zipf.nextValue();
                        //System.out.println("zipf: "+rnd);
                    }

                    uniqueKeys.add(rnd);
                    String file_id = Long.toString(rnd);

                    if (cache.get(file_id) == null){
                        cache.set(file_id, Cache_lhm.value_default);
                    }
                    else{
                        total_hits =  total_hits + 1;
                    }
                }
            }
            
            /*System.out.println("unique keys: ");
            Object[] array = uniqueKeys.toArray();
            for (int j = 0; j< array.length; j++) {
                System.out.println(array[j]);
            }*/
            System.out.println("cont_malicious: "+cont_malicious);
            System.out.println("Unique keys: " + uniqueKeys.size());
            
        }
        if(replacement_policy == 7){     
            option = "Segmented LRU Cache 2 - zipfian";
            int cont = 0;
            int cont_malicious=0;
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            segmented_cache2 = new SegmentedLRUCache2(capacity, percentage, percentage2);
            System.out.println("Principal cache capacity: "+segmented_cache2.capacity);
            System.out.println("First access LRU cache capacity: "+segmented_cache2.firstAccessLRU.capacity);
            int num_access=(20*32*capacity)+(capacity*10000);
            int i=0;
            double malicious_requests=0.0;
            int option2;//option2==0->Zipfian Distribution...option2==1-> Zipfian Distribution with malicious requests
            try{
                malicious_requests = Double.parseDouble(args[6]);
                option2=1;
                
            }catch(Exception e){
                option2 = 0;
            }
            
            if(option2 == 0){//Segmented LRU Cache 2 - zipfian
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
            }
            else{//Segmented LRU Cache 2 - zipfian with malicious requests
                option = option + " with malicious requests";
                //double malicious_requests = Double.parseDouble(args[6]);//35466667;//35466666.67; 
                double percent_malic_req = malicious_requests/num_access;
                System.out.println(percent_malic_req);
                int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
                System.out.println(x);
                long rnd;
                while( num_access > 0 ) {
                    num_access--; 
                    cont++;
                    total_access = total_access + 1;
                    if((cont % x) == 0){
                        rnd = ThreadLocalRandom.current().nextLong(0,1000000);
                        //System.out.println("Malicioso: "+rnd);
                        cont_malicious++;
                    }
                    else{
                        rnd = zipf.nextValue();
                        //System.out.println("zipf: "+rnd);
                    }

                    uniqueKeys.add(rnd);
                    String file_id = Long.toString(rnd);

                    if (segmented_cache2.get(file_id) == null){
                        segmented_cache2.set(file_id, Cache_lhm.value_default);
                    }
                    else{
                        total_hits =  total_hits + 1;
                    }
                }
            }
                
            
            System.out.println("cont_malicious: "+cont_malicious);
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
            int cont = 0;
            int cont_malicious=0;
            double malicious_requests=0.0;
            int option2;//option2==0->Zipfian Distribution...option2==1-> Zipfian Distribution with malicious requests
            try{
                malicious_requests = Double.parseDouble(args[6]);
                option2=1;
                
            }catch(Exception e){
                option2 = 0;
            }
            
            if(option2 == 0){
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
            else{//"WTiny LFU - Zipfian with malicious requests"
                option = option + " with malicious requests";
                //malicious_requests = Double.parseDouble(args[6]);//35466667;//35466666.67; 
                double percent_malic_req = malicious_requests/num_access;
                System.out.println(percent_malic_req);
                int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
                System.out.println(x);
                long rnd;
                while( num_access > 0 ) {
                    num_access--; 
                    cont++;
                    total_access = total_access + 1;
                    if((cont % x) == 0){
                        rnd = ThreadLocalRandom.current().nextLong(0,1000000);
                        //System.out.println("Malicioso: "+rnd);
                        cont_malicious++;
                    }
                    else{
                        rnd = zipf.nextValue();
                        //System.out.println("zipf: "+rnd);
                    }

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
            System.out.println("cont_malicious: "+cont_malicious);
            System.out.println("Unique keys: " + uniqueKeys.size());
            

        }
        
        
        if(replacement_policy == 9){ //double malicious requests->WTiny LFU 
            option = "WTiny LFU - Zipfian";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            wLFU = new WTinyLFU(capacity, percentage, 0.7, 0.3, percentage2);//0.3 0.7(protected and first access cache percentage):DreamWork / youtube trace
            System.out.println("Main cache capacity: "+(capacity*percentage));
            System.out.println("Window cache capacity: "+WTinyLFU.windowCacheCapacity);
            int num_access=(20*32*capacity)+(capacity*10000);
            int i=0;
            int cont = 0;
            int cont_malicious=0;
            double malicious_requests=0.0;
            int option2;//option2==0->Zipfian Distribution...option2==1-> Zipfian Distribution with malicious requests
            try{
                malicious_requests = Double.parseDouble(args[6]);
                option2=1;
                
            }catch(Exception e){
                option2 = 0;
            }
            
            if(option2 == 1){//"WTiny LFU - Zipfian with malicious requests"
                option = option + " with double malicious requests";
                //malicious_requests = Double.parseDouble(args[6]);//35466667;//35466666.67; 
                double percent_malic_req = malicious_requests/num_access;
                System.out.println(percent_malic_req);
                int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
                System.out.println(x);
                long rnd;
                int j;
                int iter;
                while( num_access > 0 ) {
                    num_access--; 
                    cont++;
                    total_access = total_access + 1;
                    if((cont % x) == 0){
                        flag_malicious = true;
                        rnd = ThreadLocalRandom.current().nextLong(0,1000000);
                        //System.out.println("Malicioso: "+rnd);
                        cont_malicious = cont_malicious+2;
                    }
                    else{
                        rnd = zipf.nextValue();
                        //System.out.println("zipf: "+rnd);
                    }

                    uniqueKeys.add(rnd);
                    String file_id = Long.toString(rnd);
                    if(flag_malicious==false)
                        iter=1;
                    else{
                        iter=2;
                        num_access--;
                    }
                    for(j=0; j<iter; j++){
                        //System.out.println(file_id+" iter:"+ j);
                        if (wLFU.get(file_id) == null){
                            wLFU.increment(file_id);
                            wLFU.set(file_id, Cache_lhm.value_default);
                        }
                        else{
                            if(iter==2)
                                total_hits = total_hits;
                            else
                                total_hits =  total_hits + 1;
                        }
                    }
                    flag_malicious = false;
                }
            }
            System.out.println("cont_malicious: "+cont_malicious);
            System.out.println("Unique keys: " + uniqueKeys.size());
            

        }
        
        
        if(replacement_policy == 10){//double malicious requests->LRU       
            option = "LRU Cache - zipfian";
            cache = new LRUCache(capacity);
            int cont = 0;
            int cont_malicious=0;
            int num_access=(20*32*capacity)+(capacity*10000);
            double malicious_requests=0.0;
            int option2;//option2==0->Zipfian Distribution...option2==1-> Zipfian Distribution with malicious requests
            try{
                malicious_requests = Double.parseDouble(args[6]);
                option2=1;
                
            }catch(Exception e){
                option2 = 0;
            }
            
            if(option2 == 1){//"LRU Cache - zipfian with malicious requests"
                option = option + " with double malicious requests";
                //double malicious_requests = Double.parseDouble(args[6]);//35466667;//35466666.67; 
                double percent_malic_req = malicious_requests/num_access;
                System.out.println(percent_malic_req);
                int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
                System.out.println(x);
                long rnd;
                int iter, j;
                while( num_access > 0 ) {
                    num_access--; 
                    cont++;
                    total_access = total_access + 1;
                    if((cont % x) == 0){
                        flag_malicious = true;
                        rnd = ThreadLocalRandom.current().nextLong(0,1000000);
                        //System.out.println("Malicioso: "+rnd);
                        cont_malicious = cont_malicious+2;
                    }
                    else{
                        rnd = zipf.nextValue();
                        //System.out.println("zipf: "+rnd);
                    }
                    
                    
                    uniqueKeys.add(rnd);
                    String file_id = Long.toString(rnd);
                    if(flag_malicious==false)
                        iter=1;
                    else{
                        iter=2;
                        num_access--;
                    }
                    for(j=0; j<iter; j++){
                        if (cache.get(file_id) == null){
                        cache.set(file_id, Cache_lhm.value_default);
                        }
                        else{
                            if(iter==2)
                                total_hits = total_hits;
                            else
                                total_hits =  total_hits + 1;
                        }
                    }
                    flag_malicious = false;
                    
                }
            }
            
            /*System.out.println("unique keys: ");
            Object[] array = uniqueKeys.toArray();
            for (int j = 0; j< array.length; j++) {
                System.out.println(array[j]);
            }*/
            System.out.println("cont_malicious: "+cont_malicious);
            System.out.println("Unique keys: " + uniqueKeys.size());
            
        }
        if(replacement_policy == 11){//double malicious requests->SLRU2     
            option = "Segmented LRU Cache 2 - zipfian";
            int cont = 0;
            int cont_malicious=0;
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            segmented_cache2 = new SegmentedLRUCache2(capacity, percentage, percentage2);
            System.out.println("Principal cache capacity: "+segmented_cache2.capacity);
            System.out.println("First access LRU cache capacity: "+segmented_cache2.firstAccessLRU.capacity);
            int num_access=(20*32*capacity)+(capacity*10000);
            int i=0;
            double malicious_requests=0.0;
            int option2;//option2==0->Zipfian Distribution...option2==1-> Zipfian Distribution with malicious requests
            try{
                malicious_requests = Double.parseDouble(args[6]);
                option2=1;
                
            }catch(Exception e){
                option2 = 0;
            }
            
            if(option2 == 1){//Segmented LRU Cache 2 - zipfian with malicious requests
                option = option + " with double malicious requests";
                //double malicious_requests = Double.parseDouble(args[6]);//35466667;//35466666.67; 
                double percent_malic_req = malicious_requests/num_access;
                System.out.println(percent_malic_req);
                int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
                System.out.println(x);
                long rnd;
                int iter, j;
                while( num_access > 0 ) {
                    num_access--; 
                    cont++;
                    total_access = total_access + 1;
                    if((cont % x) == 0){
                        flag_malicious = true;
                        rnd = ThreadLocalRandom.current().nextLong(0,1000000);
                        //System.out.println("Malicioso: "+rnd);
                        cont_malicious = cont_malicious+2;
                    }
                    else{
                        rnd = zipf.nextValue();
                        //System.out.println("zipf: "+rnd);
                    }

                    uniqueKeys.add(rnd);
                    String file_id = Long.toString(rnd);
                    if(flag_malicious==false)
                        iter=1;
                    else{
                        iter=2;
                        num_access--;
                    }
                    for(j=0; j<iter; j++){
                       if (segmented_cache2.get(file_id) == null){
                        segmented_cache2.set(file_id, Cache_lhm.value_default);
                        }
                        else{
                            if(iter==2)
                                total_hits = total_hits;
                            else
                                total_hits =  total_hits + 1;
                        }
                    }
                    flag_malicious = false;
                    
                }
            }
                
            
            System.out.println("cont_malicious: "+cont_malicious);
            System.out.println("Unique keys: " + uniqueKeys.size());
            
        }
        
        if(replacement_policy == 12){ 
            option = "WTinyLFU: LRU & Window Cache - Zipfian";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity: LRU Cache
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of Window Cache
            wLFU2 = new WTinyLFU_LRU(capacity, percentage, percentage2);//0.3 0.7(protected and first access cache percentage):DreamWork / youtube trace
            System.out.println("Main cache capacity: "+(capacity*percentage));
            System.out.println("Window cache capacity: "+WTinyLFU_LRU.windowCacheCapacity);
            int num_access=(20*32*capacity)+(capacity*10000);
            int i=0;
            int cont = 0;
            int cont_malicious=0;
            double malicious_requests=0.0;
            int option2;//option2==0->Zipfian Distribution...option2==1-> Zipfian Distribution with malicious requests
            try{
                malicious_requests = Double.parseDouble(args[6]);
                option2=1;
                
            }catch(Exception e){
                option2 = 0;
            }
            
            if(option2 == 0){
                while( num_access > 0 ) {
                    num_access--; 
                    i++;
                    total_access = total_access + 1;
                    long rnd = zipf.nextValue();
                    //System.out.println("rand number " +i+ ": "+rnd);
                    uniqueKeys.add(rnd);
                    String file_id = Long.toString(rnd);

                    if (wLFU2.get(file_id) == null){
                        wLFU2.increment(file_id);
                        wLFU2.set(file_id, Cache_lhm.value_default);
                    }
                    else{
                        total_hits =  total_hits + 1;
                    }
                }
            }
            else{//"WTiny LFU - Zipfian with malicious requests"
                option = option + " with malicious requests";
                //malicious_requests = Double.parseDouble(args[6]);//35466667;//35466666.67; 
                double percent_malic_req = malicious_requests/num_access;
                System.out.println(percent_malic_req);
                int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
                System.out.println(x);
                long rnd;
                while( num_access > 0 ) {
                    num_access--; 
                    cont++;
                    total_access = total_access + 1;
                    if((cont % x) == 0){
                        rnd = ThreadLocalRandom.current().nextLong(0,1000000);
                        //System.out.println("Malicioso: "+rnd);
                        cont_malicious++;
                    }
                    else{
                        rnd = zipf.nextValue();
                        //System.out.println("zipf: "+rnd);
                    }

                    uniqueKeys.add(rnd);
                    String file_id = Long.toString(rnd);

                    if (wLFU2.get(file_id) == null){
                        wLFU2.increment(file_id);
                        wLFU2.set(file_id, Cache_lhm.value_default);
                    }
                    else{
                        total_hits =  total_hits + 1;
                    }
                }
            }
            System.out.println("cont_malicious: "+cont_malicious);
            System.out.println("Unique keys: " + uniqueKeys.size());
            

        }
        
        
        if(replacement_policy == 13){ //double malicious requests->WTiny LFU 
            option = "WTinyLFU: LRU & Window Cache - Zipfian";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            wLFU2 = new WTinyLFU_LRU(capacity, percentage, percentage2);//0.3 0.7(protected and first access cache percentage):DreamWork / youtube trace
            System.out.println("Main cache capacity: "+(capacity*percentage));
            System.out.println("Window cache capacity: "+WTinyLFU_LRU.windowCacheCapacity);
            int num_access=(20*32*capacity)+(capacity*10000);
            int i=0;
            int cont = 0;
            int cont_malicious=0;
            double malicious_requests=0.0;
            int option2;//option2==0->Zipfian Distribution...option2==1-> Zipfian Distribution with malicious requests
            try{
                malicious_requests = Double.parseDouble(args[6]);
                option2=1;
                
            }catch(Exception e){
                option2 = 0;
            }
            
            if(option2==1){//"WTiny LFU - Zipfian with malicious requests"
                option = option + " with double malicious requests";
                //malicious_requests = Double.parseDouble(args[6]);//35466667;//35466666.67; 
                double percent_malic_req = malicious_requests/num_access;
                System.out.println(percent_malic_req);
                int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
                System.out.println(x);
                long rnd;
                int j;
                int iter;
                while( num_access > 0 ) {
                    num_access--; 
                    cont++;
                    total_access = total_access + 1;
                    if((cont % x) == 0){
                        flag_malicious = true;
                        rnd = ThreadLocalRandom.current().nextLong(0,1000000);
                        //System.out.println("Malicioso: "+rnd);
                        cont_malicious = cont_malicious+2;
                    }
                    else{
                        rnd = zipf.nextValue();
                        //System.out.println("zipf: "+rnd);
                    }

                    uniqueKeys.add(rnd);
                    String file_id = Long.toString(rnd);
                    if(flag_malicious==false)
                        iter=1;
                    else{
                        iter=2;
                        num_access--;
                    }
                    for(j=0; j<iter; j++){
                        //System.out.println(file_id+" iter:"+ j);
                        if (wLFU2.get(file_id) == null){
                            wLFU2.increment(file_id);
                            wLFU2.set(file_id, Cache_lhm.value_default);
                        }
                        else{
                            if(iter==2)
                                total_hits = total_hits;
                            else
                                total_hits =  total_hits + 1;
                        }
                    }
                    flag_malicious = false;
                }
            }
            System.out.println("cont_malicious: "+cont_malicious);
            System.out.println("Unique keys: " + uniqueKeys.size());
            

        }
        
        if(replacement_policy == 14){ //opcion 14: WTiny LFU_LRU->using Yahoo Trace and malicious requests
            option = "WTiny_LFU (LRU-> main cache) using Trace with malicious requests";
            int num_access=Integer.parseInt(args[6]);//TOTAL requests of the Trace
            int cont = 0;
            int numRequestTrace = 0;
            int cont_malicious=0;
            double malicious_requests=0.0;
            String file_id="";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            wLFU2 = new WTinyLFU_LRU(capacity, percentage, percentage2);
            malicious_requests = Double.parseDouble(args[7]);
            System.out.println("Main cache capacity: "+(capacity*percentage));
            System.out.println("Window cache capacity: "+WTinyLFU_LRU.windowCacheCapacity);
            
            double percent_malic_req = malicious_requests/num_access;
            System.out.println(percent_malic_req);
            int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
            System.out.println(x);
            long rnd=0;
            while( num_access > 0 ) {
                num_access--; 
                cont++;
                total_access = total_access + 1;
                if((cont % x) == 0){
                    file_id = Long.toString(ThreadLocalRandom.current().nextLong(0,1000000));
                    //System.out.println("Malicioso: "+file_id);
                    cont_malicious++;
                }
                else{
                    if((line = input.readLine()) != null){
                        file_id = line.split(args[0])[Integer.parseInt(args[1])];
                        numRequestTrace++;
                        //System.out.println("Trace item: "+file_id);
                    }
                    
                }

                //uniqueKeys.add(rnd);
                

                if (wLFU2.get(file_id) == null){
                    wLFU2.increment(file_id);
                    wLFU2.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
            System.out.println("Pedidos Maliciosos: "+cont_malicious);
            System.out.println("Peticiones trace: "+numRequestTrace);

        }
        
        if(replacement_policy == 15){ 
            option = "WTiny_LFU (SLRU2-> main cache) using Trace with malicious requests";
            int num_access=Integer.parseInt(args[6]);//TOTAL requests of the Trace
            int cont = 0;
            int numRequestTrace = 0;
            int cont_malicious=0;
            double malicious_requests=0.0;
            String file_id="";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            //wLFU = new WTinyLFU(capacity, percentage, 0.7, 0.3, percentage2);//Yahoo trace
            wLFU = new WTinyLFU(capacity, percentage, 0.3, 0.7, percentage2);//Youtube Trace
            
            malicious_requests = Double.parseDouble(args[7]);
            System.out.println("Main cache capacity: "+(capacity*percentage));
            System.out.println("Window cache capacity: "+WTinyLFU.windowCacheCapacity);
            
            double percent_malic_req = malicious_requests/num_access;
            System.out.println(percent_malic_req);
            int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
            System.out.println(x);
            long rnd=0;
            while( num_access > 0 ) {
                num_access--; 
                cont++;
                total_access = total_access + 1;
                if((cont % x) == 0){
                    file_id = Long.toString(ThreadLocalRandom.current().nextLong(0,1000000));
                    //System.out.println("Malicioso: "+file_id);
                    cont_malicious++;
                }
                else{
                    if((line = input.readLine()) != null){
                        file_id = line.split(args[0])[Integer.parseInt(args[1])];
                        numRequestTrace++;
                        //System.out.println("Trace item: "+file_id);
                    }
                    
                }

                
                if (wLFU.get(file_id) == null){
                    wLFU.increment(file_id);
                    wLFU.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
            System.out.println("Pedidos Maliciosos: "+cont_malicious);
            System.out.println("Peticiones trace: "+numRequestTrace);

        }
        
        if(replacement_policy == 16){ 
            option = "Segmented LRU using Trace with malicious requests";
            int num_access=Integer.parseInt(args[6]);//TOTAL requests of the Trace
            int cont = 0;
            int numRequestTrace = 0;
            int cont_malicious=0;
            double malicious_requests=0.0;
            String file_id="";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            segmented_cache2 = new SegmentedLRUCache2(capacity, percentage, percentage2);
            
            System.out.println("Principal cache capacity: "+segmented_cache2.capacity);
            System.out.println("First access LRU cache capacity: "+segmented_cache2.firstAccessLRU.capacity);
            malicious_requests = Double.parseDouble(args[7]);
            
            
            double percent_malic_req = malicious_requests/num_access;
            System.out.println(percent_malic_req);
            int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
            System.out.println(x);
            long rnd=0;
            while( num_access > 0 ) {
                num_access--; 
                cont++;
                total_access = total_access + 1;
                if((cont % x) == 0){
                    file_id = Long.toString(ThreadLocalRandom.current().nextLong(0,1000000));
                    //System.out.println("Malicioso: "+file_id);
                    cont_malicious++;
                }
                else{
                    if((line = input.readLine()) != null){
                        file_id = line.split(args[0])[Integer.parseInt(args[1])];
                        numRequestTrace++;
                        //System.out.println("Trace item: "+file_id);
                    }
                    
                }

                //uniqueKeys.add(rnd);
                

                if (segmented_cache2.get(file_id) == null){
                    segmented_cache2.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
            System.out.println("Pedidos Maliciosos: "+cont_malicious);
            System.out.println("Peticiones trace: "+numRequestTrace);

        }
        
        if(replacement_policy == 17){ 
            option = "LRU using Trace with malicious requests";
            int num_access=Integer.parseInt(args[6]);//TOTAL requests of the Trace
            int cont = 0;
            int numRequestTrace = 0;
            int cont_malicious=0;
            double malicious_requests=0.0;
            String file_id="";
            cache = new LRUCache(capacity);
            System.out.println("Principal cache capacity: "+cache.capacity);
            malicious_requests = Double.parseDouble(args[7]);
            
            
            double percent_malic_req = malicious_requests/num_access;
            System.out.println(percent_malic_req);
            int x = (int) Math.round(1/percent_malic_req);//(int)(1/percent_malic_req); //cada cuanto se envia un pedido malicioso
            System.out.println(x);
            long rnd=0;
            while( num_access > 0 ) {
                num_access--; 
                cont++;
                total_access = total_access + 1;
                if((cont % x) == 0){
                    file_id = Long.toString(ThreadLocalRandom.current().nextLong(0,1000000));
                    //System.out.println("Malicioso: "+file_id);
                    cont_malicious++;
                }
                else{
                    if((line = input.readLine()) != null){
                        file_id = line.split(args[0])[Integer.parseInt(args[1])];
                        numRequestTrace++;
                        //System.out.println("Trace item: "+file_id);
                    }
                    
                }

                //uniqueKeys.add(rnd);
                

                if (cache.get(file_id) == null){
                    cache.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }
            System.out.println("Pedidos Maliciosos: "+cont_malicious);
            System.out.println("Peticiones trace: "+numRequestTrace);

        }
        
        if(replacement_policy == 18){ //opcion 5: WTiny LFU
            option = "WTiny LFU-LRU";
            percentage = Double.parseDouble(args[4]);//args[4]-> percentage of Principal Cache capacity
            percentage2 = Double.parseDouble(args[5]);//args[5]->percentage of First Access LRU Cache capacity
            //wLFU = new WTinyLFU(capacity, percentage, 0.6, 0.4, percentage2);//0.3 0.7(protected and first access cache percentage):DreamWork / youtube trace
            wLFU2 = new WTinyLFU_LRU(capacity, percentage, percentage2);
            System.out.println("Main cache capacity: "+(capacity*percentage));
            System.out.println("Window cache capacity: "+WTinyLFU_LRU.windowCacheCapacity);
            while( (line = input.readLine()) != null ) {   
                total_access = total_access + 1;
                String file_id = line.split(args[0])[Integer.parseInt(args[1])];
                
                if (wLFU2.get(file_id) == null){
                    wLFU2.increment(file_id);
                    wLFU2.set(file_id, Cache_lhm.value_default);
                }
                else{
                    total_hits =  total_hits + 1;
                }
            }

        }
        
        System.out.println(option);
        System.out.printf("Number of accessed file:  %d \n", total_access);
        System.out.printf("Number of hits:  %d \n", total_hits);
        hit_rate = (float)  total_hits / total_access;
        System.out.printf("Hit rate %.7f \n", hit_rate);
        //long warmUpPeriod = 2*capacity;//20*32*capacity;
        long warmUpPeriod = 20*32*capacity;
        System.out.println("warmUpPeriod: "+warmUpPeriod);
        System.out.println("total_hits - warmUpPeriod: "+(total_hits - warmUpPeriod));
        System.out.println("total_access - warmUpPeriod: "+(total_access - warmUpPeriod));
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
