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

	public void parse() throws TemplateException {
		for (String line : source) {
			System.out.println(validation(line));
		}
	}

	private final String validation(String line) throws TemplateException {
		line = line.replaceAll("\t+", " ")
				.replaceAll(" +", " ")
				.trim();

		System.out.println(line + " " + line.indexOf("W"));
		if (!isOneChar(' ', line)){
			throw new TemplateException("line.indexOf(\" \") != 1");
		}

		return line;
	}

	private final boolean isOneChar(char ch, String string) {
		int count = 0;
		int length = string.length();
		for (int i = 0; i < length; i++) {
			if ((string.charAt(i) == ch) && (++count > 1)) {
				return false;
			}
		}

		return true;
	}
}
