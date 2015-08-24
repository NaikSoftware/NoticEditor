package com.temporaryteam.treenote.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represent attached file in notice.
 *
 * @author Naik
 */
public class Attached {

    public static enum State {
        NEW, ATTACHED, REMOVED
    }

    private final ObjectProperty<State> stateProperty = new SimpleObjectProperty<>();
    private String path;
    private final String name;

    public Attached(State state, String path, String name) {
        this.path = path;
        this.name = name;
        changeState(state);
    }

    public ObjectProperty<State> stateProperty() {
        return stateProperty;
    }

    public State getState() {
        return stateProperty.get();
    }

    public final void changeState(State state) {
        stateProperty.set(state);
    }

    public void newPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
