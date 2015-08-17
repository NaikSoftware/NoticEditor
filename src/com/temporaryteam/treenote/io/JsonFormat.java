package com.temporaryteam.treenote.io;

import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import javafx.scene.control.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static com.temporaryteam.treenote.io.JsonFields.*;

/**
 * @author Naik
 */
public class JsonFormat {

    public static JsonFormat with(File file) {
        return new JsonFormat(file);
    }

    private final File file;

    private JsonFormat(File file) {
        this.file = file;
    }

    public NoticeTree importDocument() throws IOException, JSONException {
        JSONObject json = new JSONObject(IOUtil.readContent(file));
        return new NoticeTree(jsonToTree(json));
    }

    private NoticeTreeItem jsonToTree(JSONObject json) throws JSONException {
        NoticeTreeItem item = new NoticeTreeItem(json.getString(KEY_TITLE), json.optString(KEY_CONTENT, null),
                json.optInt(KEY_STATUS, NoticeTreeItem.STATUS_NORMAL));
        JSONArray arr = json.getJSONArray(KEY_CHILDS);
        for (int i = 0; i < arr.length(); i++) {
            item.addChild(jsonToTree(arr.getJSONObject(i)));
        }
        return item;
    }

    public void export(NoticeTree tree) throws JSONException, IOException {
        JSONObject json = new JSONObject();
        treeToJson(tree.getRoot(), json);
        file.delete();
        IOUtil.writeJson(file, json);
    }

    private void treeToJson(NoticeTreeItem item, JSONObject json) throws JSONException {
        json.put(KEY_TITLE, item.getTitle());
        if (item.isBranch()) {
            JSONArray childs = new JSONArray();
            for (TreeItem<String> object : item.getChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) object;
                JSONObject entry = new JSONObject();
                treeToJson(child, entry);
                childs.put(entry);
            }
            json.put(KEY_CHILDS, childs);
        } else {
            json.put(KEY_STATUS, item.getStatus());
            json.put(KEY_CONTENT, item.getContent());
        }
    }

}
