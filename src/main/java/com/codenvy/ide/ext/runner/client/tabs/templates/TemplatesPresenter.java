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
package com.codenvy.ide.ext.runner.client.tabs.templates;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * The class contains business logic to change displaying of environments depending on scope or type.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TemplatesPresenter implements TemplatesPanel, TemplatesView.ActionDelegate {

    private final TemplatesView                view;
    private final GetProjectEnvironmentsAction projectEnvironments;
    private final GetSystemEnvironmentsAction  systemEnvironments;
    private final GetEnvironmentsUtil          environmentUtil;

    private boolean isSystemScope;

    @Inject
    public TemplatesPresenter(TemplatesView view,
                              GetProjectEnvironmentsAction projectEnvironments,
                              GetSystemEnvironmentsAction systemEnvironments,
                              GetEnvironmentsUtil environmentUtil) {
        this.view = view;
        this.view.setDelegate(this);

        this.projectEnvironments = projectEnvironments;
        this.systemEnvironments = systemEnvironments;
        this.environmentUtil = environmentUtil;

        this.systemEnvironments.perform();

        this.isSystemScope = true;
    }

    /** {@inheritDoc} */
    @Override
    public void onAllTypeButtonClicked() {
        view.clearEnvironmentsPanel();
        view.clearTypeButtonsPanel();

        if (isSystemScope) {
            systemEnvironments.perform();
        } else {
            projectEnvironments.perform();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onLangTypeButtonClicked(@Nonnull RunnerEnvironmentTree environmentTree) {
        view.clearEnvironmentsPanel();

        for (RunnerEnvironmentLeaf environment : environmentUtil.getAllEnvironments(environmentTree)) {
            view.addEnvironment(environment.getEnvironment(), SYSTEM);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectScopeButtonClicked() {
        view.clearEnvironmentsPanel();
        view.clearTypeButtonsPanel();

        projectEnvironments.perform();

        isSystemScope = false;
    }

    /** {@inheritDoc} */
    @Override
    public void onSystemScopeButtonClicked() {
        view.clearEnvironmentsPanel();
        view.clearTypeButtonsPanel();

        systemEnvironments.perform();

        isSystemScope = true;
    }

    /** {@inheritDoc} */
    @Override
    public void go(@Nonnull AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public IsWidget getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public void select(@Nonnull RunnerEnvironment environment) {
        view.selectEnvironment(environment);
    }
}