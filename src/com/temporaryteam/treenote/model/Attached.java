package com.temporaryteam.treenote.model;

/**
 * Represent attached file in notice.
 *
 * @author Naik
 */
public class Attached {

    public static enum State {
        NEW, ATTACHED, REMOVED
    }

    private State state;
    private final String path;
    private final String name;

    public Attached(State state, String path, String name) {
        this.state = state;
        this.path = path;
        this.name = name;
    }

    public State getState() {
        return state;
    }

    public void changeState(State state) {
        this.state = state;
        // Generate some event for update filtered list?
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
