package com.temporaryteam.treenote.io.export;

import com.temporaryteam.treenote.io.IOUtil;
import com.temporaryteam.treenote.model.NoticeTree;
import com.temporaryteam.treenote.model.NoticeTreeItem;
import javafx.scene.control.TreeItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Export notices to html.
 *
 * @author Naik, aNNiMON
 */
public class HtmlExporter extends Exporter {

    private PegDownProcessor processor;
    private File destDir;
    private Map<NoticeTreeItem, String> filenames;

    public void setProcessor(PegDownProcessor processor) {
        this.processor = processor;
    }

    public HtmlExporter setup(File destDir, NoticeTree tree) {
        setup(tree);
        this.destDir = destDir;
        return this;
    }

    @Override
    void export() {
        filenames = new HashMap<>();
        try {
            exportToHtmlPages(getTree().getRoot(), destDir, "index");
        } catch (IOException ioe) {
            throw new ExportException(ioe);
        }
    }

    /**
     * Save item as HTML pages. Root item will be saved to index.html
     *
     * @param item     node to recursively save
     * @param dir      directory to save
     * @param filename name of the file without extension
     */
    private void exportToHtmlPages(NoticeTreeItem item, File dir, String filename) throws IOException {
        Document doc = Jsoup.parse(getClass().getResourceAsStream("/resources/export_template.html"), null, "");
        generatePage(item, doc);
        File file = new File(dir, filename + ".html");
        IOUtil.writeContent(file, doc.outerHtml());
        if (item.isBranch()) {
            for (TreeItem<String> obj : item.getChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) obj;
                exportToHtmlPages(child, dir, generateFilename(child));
            }
        }
    }

    private void generatePage(NoticeTreeItem note, Document doc) {
        doc.title(note.getTitle());
        doc.select("#notice_title").first().text(note.getTitle());
        Element data = doc.select("#content").first();
        if (note.isBranch()) {
            Element list = doc.createElement("div").addClass("list-group");
            for (TreeItem<String> treeItem : note.getChildren()) {
                NoticeTreeItem child = (NoticeTreeItem) treeItem;
                Element item = doc.createElement("div").addClass("list-group-item");
                generateIcon(child, item);
                item.appendElement("a").attr("href", generateFilename(child) + ".html")
                        .text(child.getTitle())
                        .appendElement("br");
                list.appendChild(item);
            }
            data.appendChild(list);
        } else {
            data.html(processor.markdownToHtml(note.getContent()));
        }
    }

    private void generateIcon(NoticeTreeItem child, Element item) {
        if (child.isBranch()) {
            item.appendElement("span").addClass("glyphicon glyphicon-folder-open");
        } else {
            switch (child.getStatus()) {
                case IMPORTANT:
                    item.appendElement("span").addClass("glyphicon glyphicon-pushpin important");
                    break;
                default:
                    item.appendElement("span").addClass("glyphicon glyphicon-pushpin normal");
            }
        }
    }

    private String generateFilename(NoticeTreeItem item) {
        if (filenames.containsKey(item)) {
            return filenames.get(item);
        }

        String filename = IOUtil.sanitizeFilename(item.getTitle());
        if (filenames.containsValue(filename)) {
            // solve collision
            int counter = 1;
            String newFileName = filename;
            while (filenames.containsValue(newFileName)) {
                newFileName = String.format("%s_(%d)", filename, counter++);
            }
            filename = newFileName;
        }
        filenames.put(item, filename);
        return filename;
    }
}
