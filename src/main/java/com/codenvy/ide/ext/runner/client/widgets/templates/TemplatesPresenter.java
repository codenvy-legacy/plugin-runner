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
package com.codenvy.ide.ext.runner.client.widgets.templates;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ext.runner.client.properties.common.Scope.SYSTEM;

/**
 * The class contains business logic to change displaying of environments depending on scope or type.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TemplatesPresenter implements Presenter, TemplatesWidget.ActionDelegate {

    private final TemplatesWidget              view;
    private final GetProjectEnvironmentsAction projectEnvironments;
    private final GetSystemEnvironmentsAction  systemEnvironments;
    private final GetEnvironmentsUtil          environmentUtil;

    private ActionDelegate delegate;
    private boolean        isSystemScope;

    @Inject
    public TemplatesPresenter(TemplatesWidget view,
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

    /**
     * Sets action delegate to templates widget.
     *
     * @param delegate
     *         delegate which will be set
     */
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void onEnvironmentSelected(@Nonnull RunnerEnvironment environment) {
        delegate.onEnvironmentSelected(environment);
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

    public interface ActionDelegate {
        /**
         * Performs some action when user selects environment.
         *
         * @param environment
         *         environment which is selected
         */
        void onEnvironmentSelected(@Nonnull RunnerEnvironment environment);
    }

}