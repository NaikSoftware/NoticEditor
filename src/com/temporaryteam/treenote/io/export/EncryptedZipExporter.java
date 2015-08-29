package com.temporaryteam.treenote.io.export;

import com.temporaryteam.treenote.format.ZipWithIndexFormat;
import com.temporaryteam.treenote.model.NoticeTree;

import java.io.File;

/**
 * @author Naik
 */
public class EncryptedZipExporter extends Exporter {

    private File file;
    private String password;

    public EncryptedZipExporter setup(File file, NoticeTree tree) {
        setup(tree);
        this.file = file;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    void export() throws ExportException {
        try {
            ZipWithIndexFormat.with(file, password).save(getTree());
        } catch (Exception e) {
            throw new ExportException(e);
        }
    }

    public File getLastFile() {
        return file;
    }
}
