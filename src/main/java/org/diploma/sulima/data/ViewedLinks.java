package org.diploma.sulima.data;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ViewedLinks {

    private static Set<String> viewed;

    public static void addUrl(String url) {
        viewed.add(url);
    }

    public static boolean containsUrl(String url) {
        return viewed.contains(url);
    }

    public static void newViewed() {
        viewed = ConcurrentHashMap.newKeySet();
    }

    public static Set<String> getViewed() {
        return viewed;
    }
}
