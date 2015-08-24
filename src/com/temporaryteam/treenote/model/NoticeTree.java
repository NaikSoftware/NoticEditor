package com.temporaryteam.treenote.model;

import javafx.event.EventType;
import javafx.scene.control.TreeItem;

public class NoticeTree {

    private final NoticeTreeItem root;

    public NoticeTree(NoticeTreeItem root) {
        this.root = root;
    }

    public NoticeTreeItem getRoot() {
        return root;
    }

    /**
     * @param item   to add
     * @param parent if null, item will be added to root item.
     */
    public void addItem(NoticeTreeItem item, NoticeTreeItem parent) {
        if (parent == null) {
            parent = root;
        } else if (parent.isLeaf()) {
            parent = (NoticeTreeItem) parent.getParent();
        }
        parent.addChild(item);
        parent.setExpanded(true);
    }

    public void removeItem(NoticeTreeItem item) {
        if (item == null) return;
        ((NoticeTreeItem)item.getParent()).removeChild(item);
    }

    public static EventType documentChangedEvent() {
        return DOCUMENT_CHANGED_EVENT;
    }

    private static final EventType DOCUMENT_CHANGED_EVENT = new EventType(
            TreeItem.valueChangedEvent(), "DocumentChangedEvent");

}
