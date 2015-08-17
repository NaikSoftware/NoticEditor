package com.temporaryteam.treenote.io;

import com.temporaryteam.treenote.model.NoticeTree;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

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
