package com.temporaryteam.treenote.model;

public class NoticeTree {

	private final NoticeTreeItem root;

	public NoticeTree(NoticeTreeItem root) {
		this.root = root;
	}

	public NoticeTreeItem getRoot() {
		return root;
	}

	/**
	 * @param item to add
	 * @param parent if null, item will be added to root item.
	 */
	public void addItem(NoticeTreeItem item, NoticeTreeItem parent) {
		if (parent == null) {
			parent = root;
		} else if (parent.isLeaf()) {
			parent = (NoticeTreeItem) parent.getParent();
		}
		parent.getChildren().add(item);
		parent.setExpanded(true);
	}

	public void removeItem(NoticeTreeItem item) {
		if (item == null) return;
		item.getParent().getChildren().remove(item);
	}

}
