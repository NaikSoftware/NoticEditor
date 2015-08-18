package com.temporaryteam.treenote.io.export;

import com.temporaryteam.treenote.format.JsonFormat;
import com.temporaryteam.treenote.model.NoticeTree;

import java.io.File;

/**
 * @author Naik
 */
public class JsonExporter extends Exporter {

    private final File file;

    public JsonExporter(File file, NoticeTree tree) {
        super(tree);
        this.file = file;
    }

    @Override
    void export() throws ExportException {
        try {
            JsonFormat.with(file).save(getTree());
        } catch (Exception e) {
            throw new ExportException(e);
        }
    }
}
