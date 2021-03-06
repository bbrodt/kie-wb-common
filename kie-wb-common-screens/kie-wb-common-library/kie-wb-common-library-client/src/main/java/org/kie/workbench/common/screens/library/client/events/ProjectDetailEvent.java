/*
 * Copyright 2016 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.library.client.events;

import org.guvnor.common.services.project.model.Project;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

public class ProjectDetailEvent {

    private Project projectSelected;

    public ProjectDetailEvent() {

    }

    public ProjectDetailEvent( Project projectSelected ) {
        checkNotNull( "projectSelected", projectSelected );

        this.projectSelected = projectSelected;
    }

    public Project getProjectSelected() {
        return projectSelected;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ProjectDetailEvent ) ) {
            return false;
        }

        final ProjectDetailEvent that = (ProjectDetailEvent) o;

        return !( getProjectSelected() != null ? !getProjectSelected().equals( that.getProjectSelected() ) : that.getProjectSelected() != null );

    }

    @Override
    public int hashCode() {
        return getProjectSelected() != null ? getProjectSelected().hashCode() : 0;
    }
}
