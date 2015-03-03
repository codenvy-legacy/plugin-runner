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

import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Environment;
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
 * @author Dmitry Shnurenko
 */
@Singleton
public class ChooseRunnerAction extends AbstractRunnerActions implements CustomComponentAction {
    private final ListBox    environments;
    private final AppContext appContext;

    private Environment       selectedEnvironment;
    private List<Environment> systemRunners;
    private List<Environment> projectRunners;

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
        environments.addStyleName(resources.runnerCss().runnerFontStyle());
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
    public void addSystemRunners(@Nonnull List<Environment> systemEnvironments) {
        systemRunners.clear();
        environments.clear();

        for (Environment environment : projectRunners) {
            environments.addItem(environment.getName());
        }

        for (Environment environment : systemEnvironments) {
            String name = environment.getName();
            environments.addItem(name);
        }

        systemRunners.addAll(systemEnvironments);

        selectDefaultRunner();
    }

    /**
     * Adds project environments to the list.
     *
     * @param projectEnvironments
     *         list of system environments
     */
    public void addProjectRunners(@Nonnull List<Environment> projectEnvironments) {
        projectRunners.clear();
        environments.clear();

        for (Environment environment : projectEnvironments) {
            String name = environment.getName();
            environments.addItem(name);
        }

        for (Environment environment : systemRunners) {
            environments.addItem(environment.getName());
        }

        projectRunners.addAll(projectEnvironments);

        selectDefaultRunner();
    }

    /** @return selected environment. */
    @Nullable
    public Environment getSelectedEnvironment() {
        return selectedEnvironment;
    }

    @Nonnull
    private String getRunnerName(@Nonnull String runnerId) {
        return runnerId.substring(runnerId.lastIndexOf("/") + 1, runnerId.length());
    }

    private void selectEnvironment() {
        String selectedEnvironmentName = environments.getValue(environments.getSelectedIndex());

        for (Environment environment : projectRunners) {
            if (environment.getName().equals(selectedEnvironmentName)) {
                selectedEnvironment = environment;
                return;
            }
        }

        for (Environment environment : systemRunners) {
            if (environment.getName().equals(selectedEnvironmentName)) {
                selectedEnvironment = environment;
                return;
            }
        }
    }

    private void selectDefaultRunner() {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }
        for (int index = 0; index < environments.getItemCount(); index++) {
            if (getRunnerName(currentProject.getRunner()).equals(environments.getValue(index))) {
                environments.setItemSelected(index, true);
                return;
            }
        }
    }
}
