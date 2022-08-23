package org.diploma.sulima.data;

import org.diploma.sulima.services.GeneralMethods;

import java.util.concurrent.ConcurrentHashMap;

public class Intervals {

    private static ConcurrentHashMap<String, Long> siteTime;
    private static String site;
    private static final long requestDelay = 150;

    public static long getSleep(String uri) {

        long timePassed = 0;

        site = GeneralMethods.getSiteFromUri(uri);

        timePassed = getTimeSetNewTime();

        return timePassed >= requestDelay ? 0 : requestDelay - timePassed;
    }

    private static long getTimeSetNewTime() {

        long timeBegin = 0;

        long timeEnd = System.currentTimeMillis();

        if (siteTime.containsKey(site)) {
            timeBegin = siteTime.get(site);
        }

        siteTime.put(site, timeEnd);

        return timeEnd - timeBegin;
    }

    public static void newSiteTime() {
        siteTime = new ConcurrentHashMap<>();
    }
}
