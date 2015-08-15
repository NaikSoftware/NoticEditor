package com.temporaryteam.treenote.io;

import com.temporaryteam.treenote.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import javafx.util.Callback;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;

/**
 * Provides common operations with document.
 * @author aNNiMON
 */
public final class DocumentFormat {

	public static NoticeTree open(File file) throws JSONException, IOException {
		try {
			return ZipWithIndexFormat.with(file).importDocument();
		} catch (ZipException | IOException | JSONException e) {
			e.printStackTrace();
			return JsonFormat.with(file).importDocument();
		}
	}
	
	public static void save(File file, NoticeTree tree, ExportStrategy strategy, Callback<Exception, Void> callback) {
		strategy.exportAsync(file, tree, callback);
	}
}
