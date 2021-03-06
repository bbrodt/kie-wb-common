/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.stunner.bpmn.backend.indexing;

import org.jbpm.compiler.xml.ProcessDataEventListenerProvider;

/**
 * This is referenced in
 * kie-wb-common-stunner-bpmn-backend/src/main/resources/META-INF/services/org.jbpm.compiler.xml.ProcessDataEventListenerProvider
 */
public class BpmnProcessDataEventListenerProvider implements ProcessDataEventListenerProvider {

    @Override
    public BpmnProcessDataEventListener newInstance() {
        return new BpmnProcessDataEventListener();
    }
}
