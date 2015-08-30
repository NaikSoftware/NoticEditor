package com.temporaryteam.treenote.view;

import com.temporaryteam.treenote.Context;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * File and directory chooser dialog
 *
 * @author aNNiMON, Naik
 */
public class Chooser {

    public static final ExtensionFilter SUPPORTED = new ExtensionFilter("Supported Files", "*.zip", "*.txt", "*.md", "*.htm", "*.html", "*.json");
    public static final ExtensionFilter JSON = new ExtensionFilter("Json Files", "*.json");
    public static final ExtensionFilter ZIP = new ExtensionFilter("Zip Files", "*.zip");
    public static final ExtensionFilter ENC_ZIP = new ExtensionFilter("Zip Encrypted Files", "*.enc.zip");
    public static final ExtensionFilter ALL = new ExtensionFilter("All Files", "*.*");

    public static final List<ExtensionFilter> EXPORT_EXTENSIONS = new ArrayList<ExtensionFilter>() {{
        add(ZIP);
        add(ENC_ZIP);
        add(JSON);
    }};

    private static FileChooser fileChooser;
    private static DirectoryChooser directoryChooser;

    private static File lastDirectory;
    private static ExtensionFilter lastSelectedExtensionFilter;

    public static Chooser file() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
        }
        return new Chooser(true);
    }

    public static Chooser directory() {
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select folder to save");
        }
        return new Chooser(false);
    }

    public static void setInitialDirectory(File directory) {
        Chooser.lastDirectory = directory;
    }

    public static void setLastSelectedExtensionFilter(ExtensionFilter filter) {
        lastSelectedExtensionFilter = filter;
    }

    public static ExtensionFilter getLastSelectedExtensionFilter() {
        return lastSelectedExtensionFilter;
    }

    private boolean fileChooseMode;
    private boolean openMode;

    protected Chooser() {} // for tests

    private Chooser(boolean fileChooseMode) {
        this.fileChooseMode = fileChooseMode;
        openMode = true;
    }

    public Chooser open() {
        openMode = true;
        return this;
    }

    public Chooser save() {
        openMode = false;
        return this;
    }

    public Chooser filter(ExtensionFilter... filters) {
        return filter(Arrays.asList(filters));
    }

    public Chooser filter(List<ExtensionFilter> filters) {
        if (fileChooseMode) {
            fileChooser.getExtensionFilters().setAll(filters);
        }
        return this;
    }

    public Chooser title(String title) {
        if (fileChooseMode) {
            fileChooser.setTitle(title);
        } else {
            directoryChooser.setTitle(title);
        }
        return this;
    }

    public File show() {
        Window window = Context.getPrimaryStage();
        // Set initial directory from last session
        if (lastDirectory != null && lastDirectory.isDirectory() && lastDirectory.exists()) {
            if (fileChooseMode) {
                fileChooser.setInitialDirectory(lastDirectory);
            } else {
                directoryChooser.setInitialDirectory(lastDirectory);
            }
        }

        File result;
        if (fileChooseMode) {
            if (openMode) {
                result = fileChooser.showOpenDialog(window);
            } else {
                result = fileChooser.showSaveDialog(window);
            }
            // Save last directory and selected extension filter
            if (result != null) lastDirectory = result.getParentFile();
            lastSelectedExtensionFilter = correctFilter(result, fileChooser.getSelectedExtensionFilter());
        } else {
            result = directoryChooser.showDialog(window);
            lastDirectory = result;
        }

        return result;
    }

    /* Change filter, if user input other file extension */
    protected static ExtensionFilter correctFilter(File file, ExtensionFilter selectedFilter) {
        return EXPORT_EXTENSIONS.stream()
                .filter(extFilter -> selectedFilter == extFilter || matchExt(file, extFilter))
                .findFirst()
                .orElse(null);
    }

    protected static boolean matchExt(File file, ExtensionFilter filter) {
        return filter.getExtensions().stream()
                .map(ext -> ext.substring(1))
                .filter(file.getName().toLowerCase()::endsWith)
                .findFirst()
                .isPresent();
    }
}
