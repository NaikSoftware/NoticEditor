package com.temporaryteam.treenote.io.importers;

import com.temporaryteam.treenote.Context;
import javafx.application.Platform;
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    public static WebImporter from(String url) {
        return new WebImporter(url);
    }

    private final String url;
    private String addr;
    private String html;
    private Mode mode;

    private WebImporter(String url) {
        this.url = url;
        this.addr = url;
        int endProtocol = url.indexOf("://") + 3;
        int end = url.lastIndexOf("/");
        if (endProtocol > 0) {
            addr = url.substring(0, end > endProtocol ? end : addr.length());
        }
    }

    public WebImporter mode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public void grab(final Callback<Object, Void> callback) {
        new Thread(() -> {
            try {
                InputStream stream = new URL(url).openStream();
                readHTML(stream);
                clear();
                Platform.runLater(() -> callback.call(html));
            } catch (Exception ex) {
                Platform.runLater(() -> callback.call(ex));
            }
        }).start();
    }

    private void readHTML(InputStream stream) throws IOException {
        final StringBuilder result = new StringBuilder();
        try (Reader isr = new InputStreamReader(stream);
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line.trim()).append(System.lineSeparator());
            }
        }
        html = result.toString();
    }

    private void clear() {
        if (mode.whitelist == null) return;
        html = Jsoup.clean(html, addr, mode.whitelist);
    }

}
