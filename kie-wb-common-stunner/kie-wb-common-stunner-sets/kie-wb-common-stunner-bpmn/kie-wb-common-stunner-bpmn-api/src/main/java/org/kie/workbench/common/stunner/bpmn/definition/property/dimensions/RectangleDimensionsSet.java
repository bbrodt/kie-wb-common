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
package org.kie.workbench.common.stunner.bpmn.definition.property.dimensions;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.metaModel.Slider;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.FIELDDEF_HEIGHT;
import static org.kie.workbench.common.stunner.bpmn.util.FieldLabelConstants.FIELDDEF_WIDTH;

@Portable
@Bindable
@PropertySet
public class RectangleDimensionsSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Shape Dimensions";

    @Property
    @FieldDef(label = FIELDDEF_WIDTH, property = "value")
    @Slider(min = 100.0, max = 300.0, step = 10.0, precision = 0.0)
    @Valid
    protected Width width;

    @Property
    @FieldDef(label = FIELDDEF_HEIGHT, property = "value")
    @Slider(min = 40.0, max = 100.0, step = 5.0, precision = 0.0)
    @Valid
    protected Height height;

    public RectangleDimensionsSet() {
        this(new Width(Width.defaultValue),
             new Height(Height.defaultValue));
    }

    public RectangleDimensionsSet(final Double width,
                                  final Double height) {
        this(new Width(width),
             new Height(height));
    }

    public RectangleDimensionsSet(final @MapsTo("width") Width width,
                                  final @MapsTo("height") Height height) {
        this.width = width;
        this.height = height;
    }

    public Width getWidth() {
        return width;
    }

    public void setWidth(final Width width) {
        this.width = width;
    }

    public Height getHeight() {
        return height;
    }

    public void setHeight(final Height height) {
        this.height = height;
    }
}
