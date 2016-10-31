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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.BeanManager;

/**
 * {@link CommandContext} implementation.
 *
 * @author Martin Kouba
 * @see CommandScoped
 */
@Vetoed
class CommandContextImpl implements CommandContext {

    private static final Logger LOGGER = Logger.getLogger(CommandContextImpl.class.getName());

    // It's a normal scope so there may be no more than one mapped instance per contextual type per thread
    private final ThreadLocal<Map<Contextual<?>, ContextualInstance<?>>> currentContext = new ThreadLocal<>();

    private final ThreadLocal<CommandExecution> currentCommandExecution = new ThreadLocal<>();

    public Class<? extends Annotation> getScope() {
        return CommandScoped.class;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Map<Contextual<?>, ContextualInstance<?>> ctx = currentContext.get();

        if (ctx == null) {
            // Thread local not set - context is not active!
            throw new ContextNotActiveException();
        }

        ContextualInstance<T> instance = (ContextualInstance<T>) ctx.get(contextual);

        if (instance == null && creationalContext != null) {
            // Bean instance does not exist - create one if we have CreationalContext
            instance = new ContextualInstance<T>(contextual.create(creationalContext), creationalContext, contextual);
            ctx.put(contextual, instance);
        }

        return instance != null ? instance.get() : null;
    }

    public <T> T get(Contextual<T> contextual) {
        return get(contextual, null);
    }

    public boolean isActive() {
        return currentContext.get() != null;
    }

    public void destroy(Contextual<?> contextual) {
        Map<Contextual<?>, ContextualInstance<?>> ctx = currentContext.get();
        if (ctx == null) {
            return;
        }
        ctx.remove(contextual);
    }

    public void activate() {
        currentContext.set(new HashMap<>());
        currentCommandExecution.set(new CommandExecution());
    }

    public void deactivate() {
        Map<Contextual<?>, ContextualInstance<?>> ctx = currentContext.get();
        if (ctx == null) {
            return;
        }
        for (ContextualInstance<?> instance : ctx.values()) {
            try {
                instance.destroy();
            } catch (Exception e) {
                LOGGER.warning("Unable to destroy instance" + instance.get() + " for bean: " + instance.getContextual());
            }
        }
        ctx.clear();
        currentContext.remove();
        currentCommandExecution.remove();
    }

    CommandExecution getCurrentCommandExecution() {
        return currentCommandExecution.get();
    }

    /**
     * We use this injectable version to detect the original "activator", so that we can skip deactivation during {@link #deactivate()} if needed.
     *
     * @author Martin Kouba
     *
     */
    static final class InjectableCommandContext implements CommandContext {

        private static final Logger LOGGER = Logger.getLogger(InjectableCommandContext.class.getName());

        private final CommandContext delegate;

        private final BeanManager beanManager;

        private boolean isActivator;

        /**
         *
         * @param delegate
         * @param beanManager
         */
        InjectableCommandContext(CommandContext delegate, BeanManager beanManager) {
            this.delegate = delegate;
            this.beanManager = beanManager;
            this.isActivator = false;
        }

        @Override
        public void destroy(Contextual<?> contextual) {
            delegate.destroy(contextual);
        }

        @Override
        public Class<? extends Annotation> getScope() {
            return delegate.getScope();
        }

        @Override
        public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
            return delegate.get(contextual);
        }

        @Override
        public <T> T get(Contextual<T> contextual) {
            return delegate.get(contextual);
        }

        @Override
        public boolean isActive() {
            return delegate.isActive();
        }

        @Override
        public void activate() {
            try {
                beanManager.getContext(delegate.getScope());
                LOGGER.info("Command context already active");
            } catch (ContextNotActiveException e) {
                // Only activate the context if not already active
                delegate.activate();
                isActivator = true;
            }
        }

        @Override
        public void deactivate() {
            if (isActivator) {
                delegate.deactivate();
            } else {
                LOGGER.info("Command context not activated by this bean");
            }
        }

    }

    /**
     * This wrapper allows to create and destroy a bean instance properly.
     *
     * @author Martin Kouba
     *
     * @param <T>
     */
    static final class ContextualInstance<T> {

        private final T value;

        private final CreationalContext<T> creationalContext;

        private final Contextual<T> contextual;

        /**
         *
         * @param instance
         * @param creationalContext
         * @param contextual
         */
        ContextualInstance(T instance, CreationalContext<T> creationalContext, Contextual<T> contextual) {
            this.value = instance;
            this.creationalContext = creationalContext;
            this.contextual = contextual;
        }

        T get() {
            return value;
        }

        Contextual<T> getContextual() {
            return contextual;
        }

        void destroy() {
            contextual.destroy(value, creationalContext);
        }

    }

}
