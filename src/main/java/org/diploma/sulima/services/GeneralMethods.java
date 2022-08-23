package org.diploma.sulima.services;

import java.net.URI;
import java.net.URISyntaxException;

public class GeneralMethods {

    public static void sleep(long delay) {

        try {

            Thread.sleep(delay);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getSiteFromUri(String uri) {

        URI uriNew = getURI(uri);

        return uriNew == null ? null : uriNew.getScheme() + "://" + uriNew.getHost();
    }

    public static URI getURI(String uri) {

        URI uriNew = null;

        try {

            uriNew = new URI(uri);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uriNew;
    }

    public static String getUrn(String link) {

        URI urn = getURI(link);

        String query = urn.getQuery();

        if (query == null) {

            query = "";

        } else {
            query = "?" + query;
        }

        return urn.getPath() + query;
    }
}
