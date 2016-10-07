package org.jboss.weld.examples.commandcontext;

import static org.junit.Assert.assertEquals;

import javax.enterprise.inject.spi.Unmanaged;
import javax.enterprise.inject.spi.Unmanaged.UnmanagedInstance;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class CommandContextTest {

    @Test
    public void testCommandDecorator() {

        try (WeldContainer container = new Weld().disableDiscovery().packages(CommandContext.class)
                .beanClasses(DummyService.class, IdService.class, LogIdCommand.class).addExtension(new CommandExtension()).initialize()) {

            // Command is a bean - cotext is activated/deactivated by decorator
            container.select(LogIdCommand.class).get().execute();
        }

    }

    @Test
    public void testCommandExecutor() {

        try (WeldContainer container = new Weld().disableDiscovery().packages(CommandContext.class)
                .beanClasses(DummyService.class, IdService.class, LogIdCommand.class).addExtension(new CommandExtension()).initialize()) {

            // Command is a non-contextual instance
            UnmanagedInstance<LogIdCommand> command = new Unmanaged<>(LogIdCommand.class).newInstance();
            // Command cotext is activated/deactivated by executor
            container.select(CommandExecutor.class).get().execute(command.produce().inject().postConstruct().get());
        }
    }

    @Test
    public void testCommandContextActivatedManually() {

        try (WeldContainer container = new Weld().disableDiscovery().packages(CommandContext.class)
                .beanClasses(DummyService.class, IdService.class, LogIdCommand.class).addExtension(new CommandExtension()).initialize()) {

            // Command cotext is activated/deactivated manually
            CommandContext ctx = container.select(CommandExtension.class).get().getCommandContext();
            ctx.activate();
            String id = null;
            for (int i = 0; i < 10; i++) {
                if (id != null) {
                    assertEquals(id, container.select(IdService.class).get().getId());
                } else {
                    id = container.select(IdService.class).get().getId();
                }
            }
            ctx.deactivate();
        }
    }

}
