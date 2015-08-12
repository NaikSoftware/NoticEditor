package com.temporaryteam.noticeditor.model;

import com.temporaryteam.treenote.model.NoticeTreeItem;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.temporaryteam.treenote.io.*;
import com.temporaryteam.treenote.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import org.json.JSONException;
import org.junit.*;
import org.junit.rules.TestRule;
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
				new SaveListener() {

					@Override
					public void onComplete() {
						out.println("Export to JSON complete");
					}

					@Override
					public void onError(ExportException ex) {
						out.println("Export to JSON error: " + ex);
					}
				});
	}
}
