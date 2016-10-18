package org.jboss.weld.examples.command;

import static org.junit.Assert.assertEquals;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class CommandExecutorTest {

    @Test
    public void testCommandDecorator() {

        try (WeldContainer container = new Weld()
                // Disable discovery completely
                .disableDiscovery()
                // Add command context implementation, decorator is enabled globally/automatically
                .packages(CommandContext.class)
                // Add bean classes manually
                .beanClasses(DummyService.class, IdService.class, TestCommand.class)
                // Add command extension manually so that we don't need to create META-INF/...
                .addExtension(new CommandExtension()).initialize()) {

            // Execute non-CDI bean command - context is activated/deactivated by executor
            CommandExecutor executor = container.select(CommandExecutor.class).get();
            executor.execute(() -> {
                assertEquals(container.select(IdService.class).get().getId(), container.select(IdService.class).get().getId());
            });
        }

    }

}
