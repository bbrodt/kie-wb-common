/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.Session;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.RedoCommandHandler;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class RedoSessionCommand extends AbstractClientSessionCommand<AbstractClientFullSession> {

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final RedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandHandler;

    protected RedoSessionCommand() {
        this(null,
             null);
    }

    @Inject
    public RedoSessionCommand(final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final RedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandHandler) {
        super(false);
        this.redoCommandHandler = redoCommandHandler;
        this.sessionCommandManager = sessionCommandManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void execute(final Callback<T> callback) {
        checkNotNull("callback",
                     callback);
        final CommandResult<?> result = redoCommandHandler.execute(getSession().getCanvasHandler(),
                                                                   sessionCommandManager);
        checkState();
        callback.onSuccess((T) result);
    }

    @Override
    public void unbind() {
        super.unbind();
        redoCommandHandler.clear();
    }

    @SuppressWarnings("unchecked")
    void onCommandExecuted(final @Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        checkNotNull("commandExecutedEvent",
                     commandExecutedEvent);
        if (null != commandExecutedEvent.getCommand()) {
            redoCommandHandler.onCommandExecuted(commandExecutedEvent.getCommand());
        }
        checkState();
    }

    @SuppressWarnings("unchecked")
    void onCommandUndoExecuted(final @Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent) {
        checkNotNull("commandUndoExecutedEvent",
                     commandUndoExecutedEvent);
        if (null != commandUndoExecutedEvent.getCommand()) {
            redoCommandHandler.onUndoCommandExecuted(commandUndoExecutedEvent.getCommand());
        }
        checkState();
    }

    private void checkState() {
        setEnabled(null != getSession() && redoCommandHandler.isEnabled());
        fire();
    }
}
