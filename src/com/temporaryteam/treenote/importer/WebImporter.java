package com.temporaryteam.treenote.importer;

import com.temporaryteam.treenote.io.IOUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Load page from Internet, insert scripts, styles, images directly to html.
 * @author Naik
 */
public class WebImporter {
	
	public static WebImporter from(String url_str) throws MalformedURLException, IOException {
		return new WebImporter(new URL(url_str).openStream());
	}
	
	private final InputStream stream;
	
	private WebImporter(InputStream stream) {
		this.stream = stream;
	}
	
	public String grab() throws IOException {
		String data = IOUtil.stringFromStream(stream);
		return data;
	}
}
