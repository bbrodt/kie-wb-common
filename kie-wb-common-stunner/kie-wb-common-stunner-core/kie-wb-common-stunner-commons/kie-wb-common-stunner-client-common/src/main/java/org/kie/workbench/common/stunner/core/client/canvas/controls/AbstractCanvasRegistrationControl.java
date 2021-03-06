/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.Map;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;

public abstract class AbstractCanvasRegistrationControl extends AbstractCanvasControl
        implements CanvasRegistationControl<AbstractCanvas, Shape> {

    private final Map<String, ViewHandler<?>> handlers = new HashMap<>();

    protected void registerHandler(final String uuid,
                                   final ViewHandler<?> handler) {
        handlers.put(uuid,
                     handler);
    }

    @Override
    protected void doDisable() {
        // De-register all drag handlers.
        for (Map.Entry<String, ViewHandler<?>> entry : handlers.entrySet()) {
            final String uuid = entry.getKey();
            final Shape shape = canvas.getShape(uuid);
            final ViewHandler<?> handler = entry.getValue();
            doDeregister(shape,
                         handler);
        }
    }

    @Override
    public void deregister(final Shape shape) {
        if (null != shape) {
            ViewHandler<?> handler = handlers.get(shape.getUUID());
            doDeregister(shape,
                         handler);
        }
    }

    protected void doDeregister(final Shape shape,
                                final ViewHandler<?> handler) {
        if (null != handler) {
            final HasEventHandlers hasEventHandlers = (HasEventHandlers) shape.getShapeView();
            hasEventHandlers.removeHandler(handler);
        }
    }
}
