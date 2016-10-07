package org.jboss.weld.examples.commandcontext;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 *
 * @author Martin Kouba
 */
@Dependent
public class DummyService {

    @Inject
    private IdService foo;

    String getId() {
        return foo.getId();
    }

}
