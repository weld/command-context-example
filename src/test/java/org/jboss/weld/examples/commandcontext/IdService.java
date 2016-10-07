package org.jboss.weld.examples.commandcontext;

import java.util.UUID;

import javax.annotation.PostConstruct;

/**
 *
 * @author Martin Kouba
 */
@CommandScoped
public class IdService {

    private String id;

    @PostConstruct
    void init() {
        id = UUID.randomUUID().toString();
    }

    String getId() {
        return id;
    }

}
