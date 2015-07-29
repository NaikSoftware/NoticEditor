package com.temporaryteam.noticeditor.lngs;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

import java.util.List;

/**
 * @author kalterfive
 */
public class Template {

	public static void main(String[] args) throws Exception {
		create(args[0]).parse();
	}

	private static Template create(String path) throws IOException {
		final List<String> source = Files.readAllLines(Paths.get(path));
		return new Template(source);
	}

	private final List<String> source;

	private Template(List<String> source) {
		this.source = source;
	}

	public void parse() {
		source.forEach(System.out::println);
	}
}
