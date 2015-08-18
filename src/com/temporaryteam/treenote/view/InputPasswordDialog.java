package com.temporaryteam.treenote.view;

import com.temporaryteam.treenote.Context;
import javafx.scene.control.TextInputDialog;

/**
 * @author Naik
 */
public class InputPasswordDialog extends TextInputDialog {

    public InputPasswordDialog() {
        initOwner(Context.getPrimaryStage());
        setHeaderText(Context.getResources().getString("input_pass"));
    }

    public void setPassword(String password) {
        getEditor().setText(password);
    }
}
