package org.jboss.weld.examples.command;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

/**
 * Represents a single command execution state.
 * <p>
 * It's not thread-safe.
 *
 * @author Martin Kouba
 */
@Vetoed
public class CommandExecution {

    private Date startedAt;

    private Map<String, Object> attributes;

    CommandExecution() {
        this.startedAt = new Date();
        this.attributes = new HashMap<>();
    }

    Date getStartedAt() {
        return startedAt;
    }

    Map<String, Object> getAttributes() {
        return attributes;
    }

}
