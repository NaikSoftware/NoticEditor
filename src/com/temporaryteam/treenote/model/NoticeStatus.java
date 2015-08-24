package com.temporaryteam.treenote.model;

import com.temporaryteam.treenote.Context;

/**
 * @author Naik
 */
public enum NoticeStatus {

    NORMAL(0), IMPORTANT(1);

    private final int id;

    NoticeStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        String key;
        switch (id) {
            case 1:
                key = "important";
                break;
            default:
                key = "normal";
        }
        return Context.getResources().getString(key);
    }

    public static NoticeStatus fromId(int id) {
        switch (id) {
            case 1: return IMPORTANT;
            default: return NORMAL;
        }
    }
}
