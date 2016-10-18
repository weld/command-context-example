package org.jboss.weld.examples.command;

import static org.junit.Assert.assertEquals;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class CommandContextBeanTest {

    @Test
    public void testCommandContextActivatedManually() {

        try (WeldContainer container = new Weld()
                // Disable discovery completely
                .disableDiscovery()
                // Add command context implementation, decorator is enabled globally/automatically
                .packages(CommandContext.class)
                // Add bean classes manually
                .beanClasses(DummyService.class, IdService.class, TestCommand.class)
                // Add command extension manually so that we don't need to create META-INF/...
                .addExtension(new CommandExtension()).initialize()) {

            // Command cotext is activated/deactivated manually
            CommandContext ctx = container.select(CommandContext.class).get();

            try {
                ctx.activate();
                // Note that we actually don't even need to execute any command...

                // Use programmatic lookup to simulate @Inject IdService
                String id1 = container.select(IdService.class).get().getId();
                String id2 = container.select(IdService.class).get().getId();
                assertEquals(id1, id2);
            } finally {
                ctx.deactivate();
            }
        }

    }

}
