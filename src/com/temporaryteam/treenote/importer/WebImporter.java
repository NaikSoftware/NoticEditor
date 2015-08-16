package com.temporaryteam.treenote.importer;

import java.io.*;
import java.net.URL;
import javafx.application.Platform;
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * Load page from Internet, insert scripts, styles, images directly to html.
 * @author Naik
 */
public class WebImporter {
	
	public enum Mode {
		RELAXED, ONLY_TEXT, ORIGINAL, BASIC, BASIC_WITH_IMAGES, SIMPLE_TEXT
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
	
	public void grab(final Callback<Object, Void> calback) {
		new Thread(() -> {
			try {
				InputStream stream = new URL(url).openStream();
				readHTML(stream);
				clear();
				Platform.runLater(() -> calback.call(html));
			} catch (Exception ex) {
				Platform.runLater(() -> calback.call(ex));
			}
		}).start();
	}
	
	private void readHTML(InputStream stream) throws IOException {
		final StringBuilder result = new StringBuilder();
		try (Reader isr = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(isr)) {
			String line;
			while ( (line = reader.readLine()) != null ) {
				result.append(line.trim()).append(System.lineSeparator());
			}
		}
		html = result.toString();
	}
	
	private void clear() {
		Whitelist whitelist = Whitelist.basic();
		switch (mode) {
			case ONLY_TEXT: whitelist = Whitelist.none(); break;
			case BASIC_WITH_IMAGES: whitelist = Whitelist.basicWithImages(); break;
			case RELAXED: whitelist = Whitelist.relaxed(); break;
			case SIMPLE_TEXT: whitelist = Whitelist.simpleText(); break;
			case ORIGINAL: return;
		}
		html = Jsoup.clean(html, addr, whitelist, new Document.OutputSettings().indentAmount(0));
	}
	
}
