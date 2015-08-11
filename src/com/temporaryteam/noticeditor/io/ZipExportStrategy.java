package com.temporaryteam.noticeditor.io;

import com.temporaryteam.noticeditor.model.NoticeTree;
import java.io.File;
import net.lingala.zip4j.exception.ZipException;

/**
 * Export document to zip archive with index.json.
 * @author aNNiMON
 */
public class ZipExportStrategy implements ExportStrategy {
	
	@Override
	public void export(File file, NoticeTree notice) {
		try {
			ZipWithIndexFormat.with(file).exportAsync(notice);
		} catch (ZipException e) {
			throw new ExportException(e);
		}
	}
}
