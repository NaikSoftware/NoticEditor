package com.temporaryteam.noticeditor.lngs;

/**
 * Test-class for Template.java
 *
 * @author kalterfive
 */
public class TestTemplate {

	public static void testWord(Template t) {
		System.out.println(t.isWord("qw32"));
		System.out.println(t.isWord("qerq"));
		System.out.println(t.isWord("2342sa"));
		System.out.println(t.isWord("q$#"));
	}

	public static void testNumber(Template t) {
		// dec
		System.out.println("12" + t.isNumber("12"));
		System.out.println("0" + t.isNumber("0"));
		System.out.println("012" + t.isNumber("012"));
		System.out.println("lol" + t.isNumber("lol"));

		// bin
		System.out.println("0b12" + t.isNumber("0b12"));
		System.out.println("0b0" + t.isNumber("0b0"));
		System.out.println("0b012" + t.isNumber("0b012"));
		System.out.println("0blol" + t.isNumber("0blol"));
		System.out.println("0b00001" + t.isNumber("0b00001"));

		// oct
		System.out.println("012" + t.isNumber("012"));
		System.out.println("00" + t.isNumber("00"));
		System.out.println("0012" + t.isNumber("0012"));
		System.out.println("08" + t.isNumber("08"));
		System.out.println("0lol" + t.isNumber("0lol"));
		System.out.println("000001" + t.isNumber("000001"));

		// hex
		System.out.println("0x12" + t.isNumber("0x12"));
		System.out.println("0x0" + t.isNumber("0x0"));
		System.out.println("0x012" + t.isNumber("0x012"));
		System.out.println("0xaAz" + t.isNumber("0xaAz"));
		System.out.println("0xaaF" + t.isNumber("0xaaF"));
		System.out.println("0x00001" + t.isNumber("0x00001"));
	}
}

// да простят меня боги за мои велосипеды
