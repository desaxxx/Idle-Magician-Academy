package org.nandayo.Manager;

import java.util.*;

public class ListManager {

    private final List<String> list = new ArrayList<>();

    public ListManager() {
    }
    public ListManager(List<String> list) {
        this.list.addAll(list);
    }

    // ADD
    public ListManager add(String... strings) {
        list.addAll(Arrays.asList(strings));
        return this;
    }
    public ListManager addAll(List<String> strings) {
        list.addAll(strings);
        return this;
    }
    public ListManager add(String string) {
        list.add(string);
        return this;
    }

    // RESULT
    public List<String> result() {
        return list;
    }

    /*
    public <K,V> MapManager createMap(Map<K,V> map) {
        return new MapManager(map);
    }

    public class MapManager<K,V> {

        private Map<K,V> map = new HashMap<>();

        public MapManager(Map<K,V> map) {
            this.map = map;
        }

        public V get(K key) {
            return map.get(key);
        }
        public MapManager<K,V> addToList(K key, String text, String replacer1, String replacer2) {
            V value = map.get(key);
            list.add(text.replaceFirst(replacer1, key.toString()).replaceFirst(replacer2, value.toString()));
            return this;
        }
    }
     */
}
