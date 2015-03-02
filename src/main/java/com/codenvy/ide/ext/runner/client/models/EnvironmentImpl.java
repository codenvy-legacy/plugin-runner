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
package com.codenvy.ide.ext.runner.client.models;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM._512;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class EnvironmentImpl implements Environment {

    public static final String PATH        = "runner_recipe";
    public static final String ROOT_FOLDER = "/.codenvy/runners/environments/";

    private final RunnerEnvironment   runnerEnvironment;
    private final String              path;
    private final Map<String, String> options;

    private String id;
    private String name;
    private Scope  scope;
    private String type;
    private int    ram;

    @Inject
    public EnvironmentImpl(AppContext appContext,
                           @Assisted @Nonnull RunnerEnvironment runnerEnvironment,
                           @Assisted @Nonnull Scope scope) {
        this.runnerEnvironment = runnerEnvironment;
        this.scope = scope;

        this.ram = _512.getValue();

        this.id = runnerEnvironment.getId();

        int index = id.lastIndexOf('/') + 1;
        this.name = id.substring(index);

        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            throw new IllegalStateException("Application context doesn't have current project. " +
                                            "This code can't work without this param.");
        }

        if (SYSTEM.equals(scope)) {
            path = PATH;
        } else {
            path = currentProject.getProjectDescription().getPath() + ROOT_FOLDER + name;
        }

        options = Collections.unmodifiableMap(runnerEnvironment.getOptions());
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(@Nonnull String name) {
        this.name = name;
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
    @Override
    public void setScope(@Nonnull Scope scope) {
        this.scope = scope;
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

    /** {@inheritDoc} */
    @Override
    public void setType(@Nonnull String type) {
        this.type = type;
    }

    @Nonnull
    @Override
    public Map<String, String> getOptions() {
        return options;
    }

}