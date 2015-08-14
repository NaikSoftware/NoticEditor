package com.temporaryteam.treenote.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.scene.control.TreeItem;

/**
 * Model representation of notice. Contains notice data or branch data
 *
 * @author naik, setser, annimon, kalter
 */
public class NoticeTreeItem extends TreeItem<String> {

	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_IMPORTANT = 1;
	
	private String title;
	private ObservableList<TreeItem<String>> childs;
	private String content;
	private int status;
	private final ObservableList<Attached> attaches = FXCollections.observableArrayList();
	private final FilteredList<Attached> filteredAttaches = new FilteredList<>(attaches);

	/**
	 * Create branch node on tree.
	 *
	 * @param title
	 */
	public NoticeTreeItem(String title) {
		this(title, null, 0);
	}

	/**
	 * Create leaf node on tree.
	 *
	 * @param title
	 * @param content
	 * @param status
	 */
	public NoticeTreeItem(String title, String content, int status) {
		super(title);
		this.title = title;
		this.content = content;
		this.status = status;
		childs = getChildren();
	}

	/**
	 * Do not use this directly. For adding exists {@link  NoticeTree#addItem}
	 *
	 * @param item
	 */
	public void addChild(NoticeTreeItem item) {
		childs.add(item);
	}

	/**
	 * Shortcut for {@code !isBranch()}.
	 *
	 * @return
	 */
	@Override
	public boolean isLeaf() {
		return content != null;
	}

	/**
	 * @return true if content == null
	 */
	public boolean isBranch() {
		return content == null;
	}

	/**
	 * @return notice content or null if its a branch
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Content will be changed only when is a leaf node.
	 *
	 * @param content
	 */
	public void changeContent(String content) {
		if (isLeaf()) {
			this.content = content;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		setValue(title);
		this.title = title;
	}

	public void setStatus(int status) {
		this.status = status;
		Event.fireEvent(this, new TreeModificationEvent(childrenModificationEvent(), this));
	}

	public int getStatus() {
		return status;
	}
	
	public void addAttach(Attached attached) {
		if (isLeaf()) {
			attaches.add(attached);
		}
	}

	public ObservableList<Attached> getAttaches() {
		return attaches;
	}
	
	public FilteredList<Attached> getAttachesForDisplay() {
		return filteredAttaches;
	}

}
