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

package org.kie.workbench.common.stunner.client.widgets.palette;

import com.google.gwt.user.client.ui.IsWidget;

public interface PaletteWidgetView<V> extends IsWidget {

    // TODO: Remove emtpy view method here -> See SessionScreenView
    void showEmptyView(final boolean visible);

    // TODO: Remove from view. It can be directly used in the presenter.
    void showDragProxy(final String itemId,
                       final double x,
                       final double y);

    void setBackgroundColor(final String color);

    void setMarginTop(final int mTop);

    void show(final V paletteView);

    void show(final V paletteView,
              final int width,
              final int height);

    int getAbsoluteTop();

    int getAbsoluteLeft();

    void clear();

    void destroy();
}
