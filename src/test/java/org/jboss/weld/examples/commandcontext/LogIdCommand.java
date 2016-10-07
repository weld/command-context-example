package org.jboss.weld.examples.commandcontext;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

/**
 *
 * @author Martin Kouba
 */
public class LogIdCommand implements Command {

    @Inject
    private IdService idService;

    @Inject
    private DummyService dummyService;

    @Override
    public void execute() {
        assertEquals(idService.getId(), dummyService.getId());
        System.out.println(idService.getId());
    }

}
