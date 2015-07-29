package com.temporaryteam.noticeditor.lngs;

/**
 * Стандартные цвета пока будут временно в программе хранится
 * Потом будет перенесено в конфиги
 *
 * @author kalterfive
 */
public class DefaultColors {

	private static final int RED   = 0xFF0000;
	private static final int GREEN = 0x00FF00;
	private static final int BLUE  = 0x0000FF;
	private static final int WHITE = 0xFFFFFF;
	private static final int BLACK = 0x000000;

	public static int forText() {
		return RED;
	}

	public static int forWord() {
		return GREEN;
	}

	public static int forNumber() {
		return BLUE:
	}

	public static int forForeground() {
		return WHITE;
	}

	public static int forBackground() {
		return BLACK;
	}
}
