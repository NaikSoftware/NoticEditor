package com.temporaryteam.noticeditor.lngs;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kalterfive
 */
public class Template {

	public static void main(String[] args) throws Exception {
		final Template t = create(args[0]);
		t.parse();
		TestTemplate.testNumber(t);
	}

	private static Template create(String path) throws IOException {
		final List<String> source = Files.readAllLines(Paths.get(path));
		return new Template(source);
	}

	private final List<String> source;
	private final Map<TokenType, String> regexps;

	private Template(List<String> source) {
		this.source = source;
		this.regexps = new HashMap<>();
	}

	public void parse() throws TemplateException {
		for (String line : source) {
			final String[] ln = validation(line).split(" ");
			final String regexp = ln[0];
			final String type = ln[1];
			switch (type) {
				case "WORD":
					regexps.put(TokenType.WORD, regexp);
					break;

				case "NUMBER":
					regexps.put(TokenType.NUMBER, regexp);
					break;

				case "TEXT":
					regexps.put(TokenType.TEXT, regexp);
					break;

				default:
					throw new TemplateException("");
			}
		}
	}

	public boolean isWord(String word) {
		final String regex = regexps.get(TokenType.WORD);
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(word);
		return matcher.matches();
	}

	public boolean isText(String text) {
		final String regex = regexps.get(TokenType.TEXT);
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(text);
		return matcher.matches();
	}	

	public boolean isNumber(String number) {
		final String regex = regexps.get(TokenType.NUMBER);
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(number);
		return matcher.matches();
	}

	private final String validation(String line) throws TemplateException {
		line = line.replaceAll("\t+", " ")
				.replaceAll(" +", " ")
				.trim();

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
