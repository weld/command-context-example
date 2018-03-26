package org.jboss.weld.examples.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.proxy.WeldClientProxy;
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
                .beanClasses(DummyService.class, IdService.class)
                // Add command extension manually so that we don't need to create META-INF/...
                .addExtension(new CommandExtension()).initialize()) {

            // Command cotext is activated/deactivated manually
            CommandContext ctx = container.select(CommandContext.class).get();

            try {
                ctx.activate();
                // Note that we actually don't even need to execute any command...

                // Use programmatic lookup to simulate @Inject IdService
                // Unwrap the client proxy so that we can test whether the instance was destroyed correctly
                IdService idService1 = container.select(IdService.class).get();
                IdService underlyingIdService1 = (IdService) ((WeldClientProxy) idService1).getMetadata().getContextualInstance();
                String id1 = idService1.getId();
                String id2 = container.select(IdService.class).get().getId();
                assertEquals(id1, id2);

                container.destroy(idService1);
                assertTrue(underlyingIdService1.isDestroyed());

            } finally {
                ctx.deactivate();
            }
        }

    }

}
