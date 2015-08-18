package com.temporaryteam.treenote.io.export;

import com.temporaryteam.treenote.format.ZipWithIndexFormat;
import com.temporaryteam.treenote.model.NoticeTree;

import java.io.File;

/**
 * @author Naik
 */
public class ZipExporter extends Exporter {

    private final File file;

    public ZipExporter(File file, NoticeTree tree) {
        super(tree);
        this.file = file;
    }

    @Override
    void export() throws ExportException {
        try {
            ZipWithIndexFormat.with(file).save(getTree());
        } catch (Exception e) {
            throw new ExportException(e);
        }
    }
}
