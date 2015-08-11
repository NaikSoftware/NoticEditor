package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;

/**
 * Export document to zip archive with index.json.
 *
 * @author aNNiMON
 */
public class ZipExportStrategy extends ExportStrategy {

	@Override
	public void export(File file, NoticeTree tree) {
		try {
			ZipWithIndexFormat.with(file).export(tree);
		} catch (ZipException | JSONException | IOException e) {
			throw new ExportException(e);
		}
	}

}
