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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.examples.command.CommandContextImpl.InjectableCommandContext;
import org.jboss.weld.literal.DefaultLiteral;
import org.jboss.weld.util.collections.ImmutableSet;

/**
 * This portable extension registers the custom context and beans for {@link CommandContext} and {@link CommandExecution}. The first one might be used to
 * activate/deactivate the context and the second one represents a single Command execution.
 *
 * @author Martin Kouba
 */
class CommandExtension implements Extension {

    void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager) {

        final CommandContextImpl commandContext = new CommandContextImpl();

        // Register the command context
        event.addContext(commandContext);

        // Register the command context bean
        event.addBean(new Bean<CommandContext>() {

            @Override
            public CommandContext create(CreationalContext<CommandContext> creationalContext) {
                return new InjectableCommandContext(commandContext, beanManager);
            }

            @Override
            public void destroy(CommandContext instance, CreationalContext<CommandContext> creationalContext) {
            }

            @Override
            public Set<Type> getTypes() {
                return ImmutableSet.of(CommandContext.class);
            }

            @Override
            public Set<Annotation> getQualifiers() {
                return ImmutableSet.of(DefaultLiteral.INSTANCE);
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return Dependent.class;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Set<Class<? extends Annotation>> getStereotypes() {
                return Collections.emptySet();
            }

            @Override
            public boolean isAlternative() {
                return false;
            }

            @Override
            public Class<?> getBeanClass() {
                return CommandExtension.class;
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return Collections.emptySet();
            }

            @Override
            public boolean isNullable() {
                return false;
            }
        });

        // Register the CommandExecution bean
        event.addBean(new Bean<CommandExecution>() {

            @Override
            public CommandExecution create(CreationalContext<CommandExecution> creationalContext) {
                return commandContext.getCurrentCommandExecution();
            }

            @Override
            public void destroy(CommandExecution instance, CreationalContext<CommandExecution> creationalContext) {
            }

            @Override
            public Set<Type> getTypes() {
                return ImmutableSet.of(CommandExecution.class);
            }

            @Override
            public Set<Annotation> getQualifiers() {
                return ImmutableSet.of(DefaultLiteral.INSTANCE);
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return CommandScoped.class;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Set<Class<? extends Annotation>> getStereotypes() {
                return Collections.emptySet();
            }

            @Override
            public boolean isAlternative() {
                return false;
            }

            @Override
            public Class<?> getBeanClass() {
                return CommandExtension.class;
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return Collections.emptySet();
            }

            @Override
            public boolean isNullable() {
                return false;
            }
        });
    }

}
