package org.dev.velostack.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
@Component
public class CacheStore {
    private  static class CacheEntry{
        Object value;
        long expiryTime;
        CacheEntry(Object value, long ttl) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + ttl;
        }
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }


    }
    private  final ConcurrentHashMap<String ,CacheEntry> cache= new ConcurrentHashMap<>();

    public  Object get(String key){
        CacheEntry entry    =cache.get(key);
        if(entry==null) return  null;
        if(entry.isExpired()) {
            cache.remove(key);
            return  null;
        }
        return  entry.value;

    }
    public void put(String key,Object value,long ttl){
        cache.put(key
                ,new CacheEntry(value, ttl) );
    }

    public boolean contains(String key) {
        return get(key) != null;
    }


}
