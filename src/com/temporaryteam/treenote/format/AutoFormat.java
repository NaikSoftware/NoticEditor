package com.temporaryteam.treenote.format;

import com.temporaryteam.treenote.Context;
import com.temporaryteam.treenote.io.export.Exporter;
import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.view.Chooser;
import com.temporaryteam.treenote.view.SimpleAlert;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Provides common operations with document.
 *
 * @author Naik
 */
public final class AutoFormat {

    public static NoticeTree open(File file) throws JSONException, IOException, ZipException, FileEncryptedException {
        try {
            // Try open as zip
            Chooser.setLastSelectedExtensionFilter(Chooser.ZIP);
            return ZipWithIndexFormat.with(file).importDocument();
        } catch (FileEncryptedException e) {
            // Ok, try encrypted zip
            Chooser.setLastSelectedExtensionFilter(Chooser.ENC_ZIP);
            ResourceBundle res = Context.getResources();
            SimpleAlert.input(res.getString("input_pass"), "").ifPresent(Exporter.ENCRYPTED_ZIP::setPassword);
            return ZipWithIndexFormat.with(file).importDocument(Exporter.ENCRYPTED_ZIP.getPassword());
        } catch (ZipException | IOException | JSONException e) {
            // Mmm, all hope that this file in json format
            Chooser.setLastSelectedExtensionFilter(Chooser.JSON);
            return JsonFormat.with(file).importDocument();
        }
    }
}
