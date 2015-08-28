package com.temporaryteam.treenote.model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    private String title;
    private ObservableList<TreeItem<String>> childs;
    private String content;
    private ObjectProperty<NoticeStatus> statusProperty;

    private final ObservableList<Attached> attaches = FXCollections.observableArrayList(
            attached -> new Observable[]{attached.stateProperty()});

    private final FilteredList<Attached> filteredAttaches = attaches.filtered(
            attached -> attached.getState() != Attached.State.REMOVED);

    /**
     * Create branch node on tree.
     *
     * @param title
     */
    public NoticeTreeItem(String title) {
        this(title, null, null);
    }

    /**
     * Create leaf node on tree.
     *
     * @param title
     * @param content
     * @param status
     */
    public NoticeTreeItem(String title, String content, NoticeStatus status) {
        super(title);
        this.title = title;
        this.content = content;
        childs = getChildren();
        attaches.addListener((Observable o) -> fireChanges());
        statusProperty = new SimpleObjectProperty<NoticeStatus>(status) {
            @Override
            protected void invalidated() {
                requestUpdateTreeView();
                fireChanges();
            }
        };
    }

    /**
     * Do not use this directly. For adding exists {@link  NoticeTree#addItem(NoticeTreeItem, NoticeTreeItem)}
     *
     * @param item
     */
    public void addChild(NoticeTreeItem item) {
        childs.add(item);
        fireChanges();
    }

    /**
     * Do not use this directly. For removing exists {@link  NoticeTree#removeItem(NoticeTreeItem)} Item}
     *
     * @param item
     */
    public void removeChild(NoticeTreeItem item) {
        childs.remove(item);
        fireChanges();
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
            fireChanges();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        setValue(title);
        this.title = title;
        fireChanges();
    }

    public void setStatus(NoticeStatus status) {
        statusProperty.set(status);
    }

    public NoticeStatus getStatus() {
        return statusProperty.get();
    }

    public ObjectProperty<NoticeStatus> statusProperty() {
        return statusProperty;
    }

    public void addAttach(Attached attached) {
        if (isLeaf()) {
            attaches.add(attached);
        }
    }

    public void removeAttach(Attached attached) {
        if (attached.getState() == Attached.State.ATTACHED) {
            attached.changeState(Attached.State.REMOVED);
        } else {
            attaches.remove(attached);
        }
    }

    private void fireChanges() {
        Event.fireEvent(this, new TreeModificationEvent(NoticeTree.documentChangedEvent(), this));
    }

    private void requestUpdateTreeView() {
        Event.fireEvent(this, new TreeModificationEvent(TreeItem.valueChangedEvent(), this));
    }

    public ObservableList<Attached> getAttaches() {
        return attaches;
    }

    public FilteredList<Attached> getAttachesForDisplay() {
        return filteredAttaches;
    }

}
