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
}
