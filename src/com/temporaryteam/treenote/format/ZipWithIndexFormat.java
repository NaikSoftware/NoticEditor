package com.temporaryteam.treenote.format;

import com.temporaryteam.treenote.Context;
import com.temporaryteam.treenote.io.IOUtil;
import com.temporaryteam.treenote.model.Attached;
import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import com.temporaryteam.treenote.view.InputPasswordDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.temporaryteam.treenote.format.JsonFields.*;

/**
 * Document format that stores to zip archive with index.json.
 *
 * @author aNNiMON, NaikSoftware
 */
public class ZipWithIndexFormat {

    private static final String INDEX_JSON = "index.json";

    private static final String BRANCH_PREFIX = "branch_";
    private static final String NOTE_PREFIX = "note_";

    private static final InputPasswordDialog inputPassDialog = new InputPasswordDialog();

    public static ZipWithIndexFormat with(File file) throws ZipException {
        return new ZipWithIndexFormat(file);
    }

    public static ZipWithIndexFormat with(File file, String password) throws ZipException {
        return new ZipWithIndexFormat(file, password);
    }

    private final Set<String> paths;
    private ZipFile currZip;
    private ZipFile tempZip;
    private ZipParameters parameters;

    private ZipWithIndexFormat(File file) throws ZipException {
        currZip = new ZipFile(file);
        paths = new HashSet<>();
        parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        parameters.setSourceExternalStream(true);
        if (currZip.isValidZipFile() && currZip.isEncrypted()) {

        }
    }

    private ZipWithIndexFormat(File file, String password) throws ZipException {
        this(file);
        setupEncryption(password);
    }

    private void setupEncryption(String password) {
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
        parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        parameters.setPassword(password);
    }

    public NoticeTree importDocument() throws IOException, JSONException, ZipException {
        String indexContent = readFile(INDEX_JSON);
        if (indexContent == null || indexContent.isEmpty()) {
            throw new IOException("Invalid file format");
        }

        JSONObject index = new JSONObject(indexContent);
        return new NoticeTree(readNotices("", index));
    }

    private String readFile(String path) throws IOException, ZipException {
        FileHeader header = currZip.getFileHeader(path);
        if (header == null) return "";
        return IOUtil.stringFromStream(currZip.getInputStream(header));
    }

    private NoticeTreeItem readNotices(String dir, JSONObject index) throws IOException, JSONException, ZipException {
        final String title = index.getString(KEY_TITLE);
        final String filename = index.getString(KEY_FILENAME);
        final int status = index.optInt(KEY_STATUS, NoticeTreeItem.STATUS_NORMAL);
        final String dirPrefix = index.has(KEY_CHILDS) ? BRANCH_PREFIX : NOTE_PREFIX;

        final String newDir = dir + dirPrefix + filename + "/";
        if (index.has(KEY_CHILDS)) {
            JSONArray childs = index.getJSONArray(KEY_CHILDS);
            NoticeTreeItem branch = new NoticeTreeItem(title);
            for (int i = 0; i < childs.length(); i++) {
                branch.addChild(readNotices(newDir, childs.getJSONObject(i)));
            }
            return branch;
        } else {
            // ../note_filename/filename.md
            final String mdPath = newDir + filename + ".md";
            NoticeTreeItem item = new NoticeTreeItem(title, readFile(mdPath), status);
            JSONArray attaches = index.getJSONArray(KEY_ATTACHES);
            for (int i = 0; i < attaches.length(); i++) {
                JSONObject attach = attaches.getJSONObject(i);
                item.addAttach(new Attached(Attached.State.ATTACHED,
                        attach.getString(KEY_ATTACH_PATH), attach.getString(KEY_ATTACH_NAME)));

            }
            return item;
        }
    }

    public void save(NoticeTree tree) throws IOException, JSONException, ZipException {
        File tmp = File.createTempFile("treenote_", ".zip");
        tmp.delete(); // delete for creating new empty ZipFile in tmp directory
        tempZip = new ZipFile(tmp);
        JSONObject index = new JSONObject();
        writeNoticesAndFillIndex("", tree.getRoot(), index);
        storeFile(INDEX_JSON, IOUtil.toStream(index.toString()), tempZip);
        currZip.getFile().delete();
        tempZip.getFile().renameTo(currZip.getFile());
    }

    private void storeFile(String path, InputStream stream, ZipFile zip) throws IOException, ZipException {
        parameters.setFileNameInZip(path);
        zip.addStream(stream, parameters);
        stream.close();
    }

    private void writeNoticesAndFillIndex(String dir, NoticeTreeItem item, JSONObject index) throws IOException, JSONException, ZipException {
        final String title = item.getTitle();
        final String dirPrefix = item.isBranch() ? BRANCH_PREFIX : NOTE_PREFIX;

        String filename = getUniqueName(dir + dirPrefix, IOUtil.sanitizeFilename(title));
        String newDir = dir + dirPrefix + filename;

        index.put(KEY_TITLE, title);
        index.put(KEY_FILENAME, filename);

        if (item.isBranch()) {
            // ../branch_filename
            JSONArray jsonArray = new JSONArray();
            for (TreeItem<String> object : item.getChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) object;

                JSONObject indexEntry = new JSONObject();
                writeNoticesAndFillIndex(newDir + "/", child, indexEntry);
                jsonArray.put(indexEntry);
            }
            index.put(KEY_CHILDS, jsonArray);
        } else {
            // ../note_filename/filename.md
            String noticePath = newDir + getUniqueName(newDir, "/" + filename + ".md");
            storeFile(noticePath, IOUtil.toStream(item.getContent()), tempZip);
            index.put(KEY_STATUS, item.getStatus());
            index.put(KEY_ATTACHES, saveAttaches(item.getAttaches(), newDir));
        }
    }

    private JSONArray saveAttaches(List<Attached> attachedList, String path) throws JSONException, ZipException, IOException {
        JSONArray array = new JSONArray();
        for (Attached attached : attachedList) {
            JSONObject jsonAttach = new JSONObject();
            InputStream inputStream = null;
            switch (attached.getState()) {
                case ATTACHED: // get from zip
                    inputStream = currZip.getInputStream(currZip.getFileHeader(attached.getPath()));
                    break;
                case NEW: // get from filesystem
                    inputStream = new FileInputStream(attached.getPath());
                    break;
            }
            if (inputStream != null) {
                String attachPath = path + "/" + getUniqueName(path, IOUtil.sanitizeFilename(attached.getName()));
                if (attached.getState() == Attached.State.NEW) {
                    attached.newPath(attachPath); // set new path (after saving changes)
                    attached.changeState(Attached.State.ATTACHED);
                }
                jsonAttach.put(KEY_ATTACH_NAME, attached.getName());
                jsonAttach.put(KEY_ATTACH_PATH, attachPath);
                storeFile(attachPath, inputStream, tempZip);
                array.put(jsonAttach);
            }
        }
        return array;
    }

    private String getUniqueName(String path, String prefName) {
        int counter = 1;
        while (paths.contains(path + prefName)) {
            prefName = String.format("%s_(%d)", prefName, counter++);
        }
        paths.add(path + prefName);
        return prefName;
    }

}
