package com.temporaryteam.treenote.model;

/**
 * Represent attached file in notice.
 * @author Naik
 */
public class Attached {
	
	public static enum State {
		NEW, ATTACHED, REMOVED
	}
	
	private State state;
	private final String path;
	
	public Attached(State state, String path) {
		this.state = state;
		this.path = path;
	}
	
	public State getState() {
		return state;
	}

	public void changeState(State state) {
		this.state = state;
	}
	
	public String getPath() {
		return path;
	}
	
}
