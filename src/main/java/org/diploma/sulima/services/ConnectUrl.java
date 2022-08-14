package org.diploma.sulima.services;

import lombok.Data;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Data
public class ConnectUrl {

    private Document doc;
    private int responseStatus;
    private String userAgent;

    public ConnectUrl() {
        this.userAgent = IndexingBuilder.getUserAgent();
    }

    public ConnectUrl(String userAgent) {
        this.userAgent = userAgent;
    }

    public ConnectUrl getConnectUrl(String url) {
        try {
            doc = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .referrer("http://www.google.com")
                    .get();
        } catch (HttpStatusException httpEx) {
            responseStatus = httpEx.getStatusCode();
        } catch (IOException ex) {
//            ex.printStackTrace();
            responseStatus = 404;
        }
        return this;
    }

    public static ConnectUrl buildConnectUrl() {
        return new ConnectUrl();
    }

    public static ConnectUrl buildConnectUrl(String userAgent) {
        return new ConnectUrl(userAgent);
    }
}
