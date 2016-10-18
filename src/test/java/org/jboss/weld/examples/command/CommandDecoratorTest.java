package org.jboss.weld.examples.command;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class CommandDecoratorTest {

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
                .addExtension(new CommandExtension())
                .initialize()) {

            // Command is a bean - cotext is activated/deactivated by decorator
            container.select(TestCommand.class).get().execute();
        }

    }

}
