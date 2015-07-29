package com.temporaryteam.noticeditor.lngs;

/**
 * @author kalterfive
 */
public class Token {

	private final String text;
	private final TokenType type;
	private final int color;

	public Token(String text, TokenType type, int color) {
		this.text = text;
		this.type = type;
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public TokenType getType() {
		return type;
	}

	public int getColor() {
		return color;
	}
}
