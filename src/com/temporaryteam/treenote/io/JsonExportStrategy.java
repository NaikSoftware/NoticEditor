package com.temporaryteam.treenote.io;

import com.temporaryteam.treenote.model.NoticeTree;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * Export notices to json.
 *
 * @author aNNiMON
 */
public class JsonExportStrategy extends ExportStrategy {

    @Override
    public void export(File file, NoticeTree tree) {
        try {
            JsonFormat.with(file).export(tree);
        } catch (IOException | JSONException e) {
            throw new ExportException(e);
        }
    }

}
