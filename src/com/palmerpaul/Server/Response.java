package com.palmerpaul.Server;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.palmerpaul.Shared.Command;

class Response {

    private String msg;
    private Set<String> recipients;

    public Response(String msg, Set<String> recipients) {
        this.msg = msg;

        if (recipients == null) {
            Set<String> empty = Collections.emptySet();
            this.recipients = empty;
        } else {
            this.recipients = recipients;
        }
    }

    public Response(String msg, String recipient) {
        this(msg, recipient == null ? null : Collections.singleton(recipient));
    }

    public Response(Command cmd, Set<String> recipients) {
        this(cmd.toString(), recipients);
    }

    public Response(Command cmd, String recipient) {
        this(cmd.toString(), recipient);
    }

    public String getMessage() {
        return msg;
    }

    // Intentionally breaks encapsulation
    public Collection<String> getRecipients() {
        return recipients;
    }

    public void send() {
        ServerMain.server.sendResponse(this);
    }

}
