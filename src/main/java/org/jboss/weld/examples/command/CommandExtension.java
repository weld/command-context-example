/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.examples.command;

import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.examples.command.CommandContextImpl.InjectableCommandContext;

/**
 * This portable extension registers the custom context and beans for {@link CommandContext} and {@link CommandExecution}. The
 * first one might be used to activate/deactivate the context and the second one represents a single Command execution.
 *
 * @author Martin Kouba
 */
class CommandExtension implements Extension {

    void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager) {

        final CommandContextImpl commandContext = new CommandContextImpl();

        // Register the command context
        event.addContext(commandContext);

        // Register the command context bean using CDI 2 configurators API
        event.addBean()
            .addType(CommandContext.class)
            .createWith(ctx -> new InjectableCommandContext(commandContext, beanManager))
            .addQualifier(Default.Literal.INSTANCE)
            .scope(Dependent.class)
            .beanClass(CommandExtension.class);

        // Register the CommandExecution bean using CDI 2 configurators API
        event.addBean()
            .createWith(ctx -> commandContext.getCurrentCommandExecution())
            .addType(CommandExecution.class)
            .addQualifier(Default.Literal.INSTANCE)
            .scope(CommandScoped.class)
            .beanClass(CommandExtension.class);
    }

}
