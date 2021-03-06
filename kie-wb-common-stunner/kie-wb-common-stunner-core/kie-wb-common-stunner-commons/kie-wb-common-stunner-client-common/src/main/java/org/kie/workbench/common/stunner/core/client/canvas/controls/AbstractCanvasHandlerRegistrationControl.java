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

package org.kie.workbench.common.stunner.core.client.canvas.controls;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractCanvasHandlerRegistrationControl extends AbstractCanvasHandlerControl
        implements CanvasRegistationControl<AbstractCanvasHandler, Element> {

    private final Map<String, ViewHandler<?>> handlers = new HashMap<>();

    public void update(final Element element) {
        // Do nothing by default.
    }

    protected void registerHandler(final String uuid,
                                   final ViewHandler<?> handler) {
        handlers.put(uuid,
                     handler);
    }

    @Override
    protected void doDisable() {
        deregisterAll();
    }

    public void deregisterAll() {
        new HashSet<>(handlers.keySet())
                .stream()
                .forEach(this::deregister);
        handlers.clear();
    }

    @Override
    public void deregister(final Element element) {
        deregister(element.getUUID());
    }

    protected void deregister(final String uuid) {
        final Shape shape = canvasHandler.getCanvas().getShape(uuid);
        final ViewHandler<?> handler = handlers.get(uuid);
        doDeregisterHandler(shape,
                            handler);
    }

    private void doDeregisterHandler(final Shape shape,
                                     final ViewHandler<?> handler) {
        if (null != shape && null != handler) {
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
            hasEventHandlers.removeHandler(handler);
            handlers.remove(shape.getUUID());
        }
    }

    protected boolean checkEventContext(final AbstractCanvasHandlerEvent canvasHandlerEvent) {
        final CanvasHandler _canvasHandler = canvasHandlerEvent.getCanvasHandler();
        return canvasHandler != null && canvasHandler.equals(_canvasHandler);
    }
}
