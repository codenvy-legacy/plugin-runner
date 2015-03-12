/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.runner.client.models;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.project.shared.dto.ProjectTypeDefinition;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.project.type.ProjectTypeRegistry;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static com.google.gwt.user.client.Window.Location.getHost;
import static com.google.gwt.user.client.Window.Location.getProtocol;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class EnvironmentImpl implements Environment {

    public static final String ROOT_FOLDER          = "/.codenvy/runners/environments/";
    public static final String PROJECT_SCOPE_PREFIX = "project://";
    public static final String SYSTEM_SCOPE_PREFIX  = "system:/";

    private final RunnerEnvironment   runnerEnvironment;
    private final String              path;
    private final Map<String, String> options;
    private final String              id;
    private final String              name;
    private final Scope               scope;
    private final String              type;

    private int ram;

    @Inject
    public EnvironmentImpl(AppContext appContext,
                           ProjectTypeRegistry projectTypeRegistry,
                           @Assisted @Nonnull RunnerEnvironment runnerEnvironment,
                           @Assisted @Nonnull Scope scope) {
        this.runnerEnvironment = runnerEnvironment;
        this.scope = scope;
        this.ram = RAM.DEFAULT.getValue();
        this.id = runnerEnvironment.getId();

        int index = id.lastIndexOf('/') + 1;
        String lastIdPart = id.substring(index);

        String name = runnerEnvironment.getDisplayName();
        if (name == null || name.isEmpty()) {
            this.name = lastIdPart;
        } else {
            this.name = name;
        }

        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            throw new IllegalStateException("Application context doesn't have current project. " +
                                            "This code can't work without this param.");
        }

        if (SYSTEM.equals(scope)) {
            String wsId = currentProject.getProjectDescription().getWorkspaceId();
            path = getProtocol() + "//" + getHost() + "/api/runner/" + wsId + "/recipe?id=" + id;
        } else {
            path = currentProject.getProjectDescription().getPath() + ROOT_FOLDER + lastIdPart;
        }

        options = Collections.unmodifiableMap(runnerEnvironment.getOptions());

        String type = id.replaceFirst(SYSTEM.equals(scope) ? SYSTEM_SCOPE_PREFIX : PROJECT_SCOPE_PREFIX, "");
        index = type.lastIndexOf('/');
        type = type.substring(0, index);

        if (type.isEmpty()) {
            String projectType = currentProject.getProjectDescription().getType();
            ProjectTypeDefinition typeDefinition = projectTypeRegistry.getProjectType(projectType);

            this.type = typeDefinition.getRunnerCategories().get(0);
        } else {
            this.type = type;
        }
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getDescription() {
        return runnerEnvironment.getDescription();
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Scope getScope() {
        return scope;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getPath() {
        return path;
    }

    /** {@inheritDoc} */
    @Override
    public int getRam() {
        return ram;
    }

    /** {@inheritDoc} */
    @Override
    public void setRam(@Nonnegative int ram) {
        this.ram = ram;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getType() {
        return type;
    }

    @Nonnull
    @Override
    public Map<String, String> getOptions() {
        return options;
    }

}