package com.temporaryteam.treenote.io.importers;

import com.temporaryteam.treenote.Context;
import javafx.application.Platform;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.*;
import java.net.URL;

/**
 * Load page from Internet, insert scripts, styles, images directly to html.
 *
 * @author Naik
 */
public class WebImporter {

    public enum Mode {
        RELAXED("relaxed", Whitelist.relaxed()),
        ONLY_TEXT("only_text", Whitelist.none()),
        ORIGINAL("original", null),
        BASIC("basic", Whitelist.basic()),
        BASIC_WITH_IMAGES("basic_with_img", Whitelist.basicWithImages()),
        SIMPLE_TEXT("simple_text", Whitelist.simpleText());

        private final String name;
        private final Whitelist whitelist;

        Mode(String name_id, Whitelist whitelist) {
            this.name = Context.getResources().getString(name_id);
            this.whitelist = whitelist;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public WebImporter(Mode mode) {
        this.mode = mode;
    }

    private String origHtml;
    private String lastUrl;
    private Mode mode;

    public void newMode(Mode mode) {
        this.mode = mode;
    }

    public void grab(String url, final ImportCallback callback) {
        new Thread(() -> {
            try (InputStream stream = new URL(url).openStream()) {
                if (origHtml == null || lastUrl != url) {
                    origHtml = readHTML(stream);
                    lastUrl = url;
                }
                String html = clearHTML(origHtml, url);
                Platform.runLater(() -> callback.call(html, null));
            } catch (Exception ex) {
                Platform.runLater(() -> callback.call(null, ex));
            }
        }).start();
    }

    private String readHTML(InputStream stream) throws IOException {
        final StringBuilder result = new StringBuilder();
        try (Reader isr = new InputStreamReader(stream, "UTF-8");
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line.trim()).append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    private String clearHTML(String html, String url) {
        if (mode.whitelist == null) return origHtml;
        return Jsoup.clean(html, getBaseUrl(url), mode.whitelist);
    }

    private String getBaseUrl(String fullUrl) {
        String baseUrl = fullUrl;
        int endProtocol = fullUrl.indexOf("://") + 3;
        int end = fullUrl.lastIndexOf("/", endProtocol);
        if (endProtocol > 0) {
            baseUrl = fullUrl.substring(0, end > 0 ? end : baseUrl.length());
        }
        return baseUrl;
    }

}
