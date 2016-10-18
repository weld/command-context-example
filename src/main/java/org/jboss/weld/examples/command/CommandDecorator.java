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

import static javax.interceptor.Interceptor.Priority.LIBRARY_BEFORE;

import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

/**
 * This decorator might be used to activate/deactive command context for commands which are also CDI beans.
 *
 * @author Martin Kouba
 */
@Priority(LIBRARY_BEFORE)
@Decorator
abstract class CommandDecorator implements Command {

    @Inject
    @Delegate
    private Command delegate;

    private final CommandContext commandContext;

    @Inject
    CommandDecorator(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    @Override
    public void execute() {
        try {
            commandContext.activate();
            delegate.execute();
        } finally {
            commandContext.deactivate();
        }
    }

}
