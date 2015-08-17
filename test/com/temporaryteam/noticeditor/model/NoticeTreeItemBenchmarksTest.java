package com.temporaryteam.noticeditor.model;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.temporaryteam.treenote.io.DocumentFormat;
import com.temporaryteam.treenote.io.ExportStrategyHolder;
import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.File;
import java.io.IOException;

import static java.lang.System.out;

public class NoticeTreeItemBenchmarksTest {

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    private static final int NESTING_LEVEL = 6000;
    private static NoticeTreeItem root;
    private static NoticeTree tree;

    @BeforeClass
    public static void beforeClass() {
        root = new NoticeTreeItem("root");
        tree = new NoticeTree(root);
        NoticeTreeItem branch = root;
        for (int i = 0; i < NESTING_LEVEL; i++) {
            NoticeTreeItem node = new NoticeTreeItem("branch " + i);
            tree.addItem(node, branch);
            branch = node;
        }
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 1)
    @Test
    public void testJsonExport() throws JSONException, IOException {
        DocumentFormat.save(File.createTempFile("test", ".json"), tree, ExportStrategyHolder.JSON,
                (error) -> {
                    if (error == null) out.println("Export to JSON complete");
                    else out.println("Export to JSON error: " + error);
                    return null;
                });
    }
}
