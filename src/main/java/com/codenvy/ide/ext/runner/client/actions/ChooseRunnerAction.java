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
package com.codenvy.ide.ext.runner.client.actions;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Action which allows user select runner from all environments.
 *
 * @author Valeriy Svydenko
 */
@Singleton
public class ChooseRunnerAction extends AbstractRunnerActions implements CustomComponentAction {
    private final ListBox    environments;
    private final AppContext appContext;

    private RunnerEnvironment       selectedEnvironment;
    private List<RunnerEnvironment> systemRunners;
    private List<RunnerEnvironment> projectRunners;

    @Inject
    public ChooseRunnerAction(RunnerResources resources,
                              RunnerLocalizationConstant locale,
                              AppContext appContext) {
        super(appContext, locale.actionChooseRunner(), locale.actionChooseRunner(), null);

        this.appContext = appContext;

        systemRunners = new LinkedList<>();
        projectRunners = new LinkedList<>();

        environments = new ListBox();
        environments.addStyleName(resources.runnerCss().runnersAction());
        environments.addStyleName(resources.runnerCss().fontStyle());
        environments.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                selectEnvironment();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        //do nothing
    }

    /** {@inheritDoc} */
    @Override
    public Widget createCustomComponent(Presentation presentation) {
        return environments;
    }

    /**
     * Adds system environments to the list.
     *
     * @param systemEnvironments
     *         list of system environments
     */
    public void addSystemRunners(@Nonnull List<RunnerEnvironment> systemEnvironments) {
        systemRunners.addAll(systemEnvironments);
    }

    /**
     * Adds project environments to the list.
     *
     * @param projectEnvironments
     *         list of system environments
     */
    public void addProjectRunners(@Nonnull List<RunnerEnvironment> projectEnvironments) {
        projectRunners.clear();
        environments.clear();

        addDefaultRunner();

        for (RunnerEnvironment environment : projectEnvironments) {
            String name = getRunnerName(environment.getId());

            environments.addItem(name);
        }

        for (RunnerEnvironment environment : systemRunners) {
            environments.addItem(getRunnerName(environment.getId()));
        }

        projectRunners.addAll(projectEnvironments);
    }

    /** @return selected environment. */
    @Nullable
    public RunnerEnvironment getSelectedEnvironment() {
        return selectedEnvironment;
    }

    @Nonnull
    private String getRunnerName(@Nonnull String runnerId) {
        return runnerId.substring(runnerId.lastIndexOf("/") + 1, runnerId.length());
    }

    private void selectEnvironment() {
        String selectedEnvironmentName = environments.getValue(environments.getSelectedIndex());

        for (RunnerEnvironment environment : projectRunners) {
            if (environment.getId().endsWith('/' + selectedEnvironmentName)) {
                selectedEnvironment = environment;
                return;
            }
        }

        for (RunnerEnvironment environment : systemRunners) {
            if (environment.getId().endsWith('/' + selectedEnvironmentName)) {
                selectedEnvironment = environment;
                return;
            }
        }
    }

    private void addDefaultRunner() {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            environments.addItem(getRunnerName(currentProject.getRunner()));
        }
    }
}
