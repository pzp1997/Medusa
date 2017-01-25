package com.palmerpaul.Shared;

import java.util.regex.Pattern;

/**
 * A protocol is used to process incoming messages. Subclasses must implement the
 * process(String). ServerProtocol and ClientProtocol are both children of this class.
 * All {@code SocketConnection} objects have a protocol.
 * 
 * @author palmerpa
 */
public abstract class Protocol {
    
    /* Commands use whitespace to divide themselves into parts.
     * This regular expression is stored for efficiency, as compiling
     * it is somewhat expensive and it needs to be used many times per second.
     */
    private static final Pattern commandDelimiter = Pattern.compile("\\s+");

    protected final GameModel model;
    
    /**
     * Creates a new Protocol object
     * @param model The state of the application; used in {@code process(String)}
     * to determine what action to take or what response to send; can be an alias
     * if state should be shared between multiple protocols / connections.
     */
    public Protocol(GameModel model) {
        this.model = model;
    }
    
    /**
     * Processes an incoming message.
     * @param line An incoming message
     */
    protected abstract void process(String line);
    
    /**
     * Convenience method for breaking up an incoming message into "tokens".
     * @param s An incoming message
     * @return The tokens of the message stored as a String[]
     */
    protected static String[] parseTokens(String s) {
        String[] tokens = commandDelimiter.split(s);
        return tokens;
    }
}
