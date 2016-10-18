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

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import javax.inject.Inject;

/**
 *
 * @author Martin Kouba
 */
public class TestCommand implements Command {

    private static final Logger LOGGER = Logger.getLogger(TestCommand.class.getName());

    @Inject
    private CommandExecution commandExecution;

    @Inject
    private IdService idService;

    @Inject
    private DummyService dummyService;

    @Override
    public void execute() {
        // Dummy service puts id from IdService into execution attributes
        dummyService.doSomeDummyLogic();
        assertEquals(commandExecution.getAttributes().get("id"), idService.getId());
        LOGGER.info("Executed at " + commandExecution.getStartedAt() + ", attributes: " + commandExecution.getAttributes());
    }

}
