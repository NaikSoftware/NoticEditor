package com.temporaryteam.treenote.importer;

import com.temporaryteam.treenote.io.IOUtil;
import java.io.InputStream;
import java.net.URL;
import javafx.application.Platform;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Load page from Internet, insert scripts, styles, images directly to html.
 * @author Naik
 */
public class WebImporter {
	
	public static WebImporter from(String url) {
		return new WebImporter(url);
	}
	
	private final String url;
	
	private WebImporter(String url) {
		this.url = url;
	}
	
	public void grab(final Callback<Pair<Exception, String>, Void> calback) {
		new Thread(() -> {
			try {
				InputStream stream = new URL(url).openStream();
				String data = IOUtil.stringFromStream(stream);
				Platform.runLater(() -> calback.call(new Pair<>(null, data)));
			} catch (Exception ex) {
				Platform.runLater(() -> calback.call(new Pair<>(ex, null)));
			}
		}).start();
	}
}
