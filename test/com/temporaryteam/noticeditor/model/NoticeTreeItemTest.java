package com.temporaryteam.noticeditor.model;

import com.temporaryteam.treenote.model.NoticeStatus;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoticeTreeItemTest {

    @Test
    public void testBranch() {
        NoticeTreeItem branch = new NoticeTreeItem("branch");
        assertTrue(branch.isBranch());
        assertFalse(branch.isLeaf());
        assertEquals(0, branch.getChildren().size());

        branch.addChild(new NoticeTreeItem("branch"));
        branch.addChild(new NoticeTreeItem("leaf", "content", NoticeStatus.NORMAL));
        assertEquals(2, branch.getChildren().size());

        assertEquals("leaf", ((NoticeTreeItem) branch.getChildren().get(1)).getTitle());
    }

    @Test
    public void testLeaf() {
        NoticeTreeItem leaf = new NoticeTreeItem("leaf", "content", NoticeStatus.NORMAL);
        assertEquals("leaf", leaf.getTitle());
        assertFalse(leaf.isBranch());
        assertTrue(leaf.isLeaf());
        assertEquals(0, leaf.getChildren().size());
    }

    @Test
    public void testAddChild() {
        NoticeTreeItem branch = new NoticeTreeItem("branch");
        assertEquals(0, branch.getChildren().size());
        for (int i = 0; i < 10; i++) {
            branch.addChild(null);
        }
        assertEquals(10, branch.getChildren().size());
    }

    @Test
    public void testChangeContent() {
        NoticeTreeItem branch = new NoticeTreeItem("branch");
        assertNull(branch.getContent());
        branch.changeContent("test");
        assertNull(branch.getContent());

        NoticeTreeItem leaf = new NoticeTreeItem("leaf", "content", NoticeStatus.NORMAL);
        assertEquals("content", leaf.getContent());
        leaf.changeContent("test");
        assertEquals("test", leaf.getContent());
    }

}
