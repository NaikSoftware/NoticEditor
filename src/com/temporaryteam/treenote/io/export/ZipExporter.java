package com.temporaryteam.treenote.io.export;

import com.temporaryteam.treenote.format.ZipWithIndexFormat;
import com.temporaryteam.treenote.model.NoticeTree;

import java.io.File;

/**
 * @author Naik
 */
public class ZipExporter extends Exporter {

    private File file;

    public ZipExporter setup(File file, NoticeTree tree) {
        setup(tree);
        this.file = file;
        return this;
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
