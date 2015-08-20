package com.temporaryteam.treenote.io.importers;

import org.jsoup.safety.Whitelist;

public enum HtmlImportMode {
    RELAXED("relaxed", Whitelist.relaxed()),
    ONLY_TEXT("only_text", Whitelist.none()),
    ORIGINAL("original", null),
    BASIC("basic", Whitelist.basic()),
    BASIC_WITH_IMAGES("basic_with_img", Whitelist.basicWithImages()),
    SIMPLE_TEXT("simple_text", Whitelist.simpleText());
    
    private final String name;
    private final Whitelist whitelist;

    private HtmlImportMode(String name, Whitelist whitelist) {
        this.name = name;
        this.whitelist = whitelist;
    }

    public String getName() {
        return this.name;
    }

    public Whitelist getWhitelist() {
        return this.whitelist;
    }
}
