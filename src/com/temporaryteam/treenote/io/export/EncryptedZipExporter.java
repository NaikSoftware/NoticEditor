package com.temporaryteam.treenote.io.export;

import com.temporaryteam.treenote.format.ZipWithIndexFormat;
import com.temporaryteam.treenote.model.NoticeTree;

import java.io.File;

/**
 * @author Naik
 */
public class EncryptedZipExporter extends ZipExporter {

    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    void export() throws ExportException {
        try {
            ZipWithIndexFormat.with(getFile(), password).save(getTree());
        } catch (Exception e) {
            throw new ExportException(e);
        }
    }
}
