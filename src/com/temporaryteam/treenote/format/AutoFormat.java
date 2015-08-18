package com.temporaryteam.treenote.format;

import com.temporaryteam.treenote.format.JsonFormat;
import com.temporaryteam.treenote.format.ZipWithIndexFormat;
import com.temporaryteam.treenote.model.NoticeTree;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * Provides common operations with document.
 *
 * @author Naik
 */
public final class AutoFormat {

    public static NoticeTree open(File file) throws JSONException, IOException {
        try {
            return ZipWithIndexFormat.with(file).importDocument();
        } catch (ZipException | IOException | JSONException e) {
            return JsonFormat.with(file).importDocument();
        }
    }
}
