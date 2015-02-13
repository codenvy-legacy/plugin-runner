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
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.templates.scopepanel.ScopePanel;
import com.codenvy.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * The class contains business logic to change displaying of environments depending on scope or type.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TemplatesPresenter implements TemplatesContainer, TemplatesView.ActionDelegate, ScopePanel.ActionDelegate {

    private final TemplatesView                       view;
    private final GetProjectEnvironmentsAction        projectEnvironmentsAction;
    private final GetSystemEnvironmentsAction         systemEnvironmentsAction;
    private final GetEnvironmentsUtil                 environmentUtil;
    private final List<RunnerEnvironment>             systemEnvironments;
    private final List<RunnerEnvironment>             projectEnvironments;
    private final Map<Scope, List<RunnerEnvironment>> environmentMap;

    private Scope scope;

    @Inject
    public TemplatesPresenter(TemplatesView view,
                              GetProjectEnvironmentsAction projectEnvironmentsAction,
                              GetSystemEnvironmentsAction systemEnvironmentsAction,
                              GetEnvironmentsUtil environmentUtil,
                              ScopePanel scopePanel,
                              RunnerResources resources) {
        this.view = view;
        this.view.setDelegate(this);

        this.projectEnvironmentsAction = projectEnvironmentsAction;
        this.systemEnvironmentsAction = systemEnvironmentsAction;
        this.environmentUtil = environmentUtil;

        this.projectEnvironments = new ArrayList<>();
        this.systemEnvironments = new ArrayList<>();

        this.environmentMap = new EnumMap<>(Scope.class);
        this.environmentMap.put(PROJECT, projectEnvironments);
        this.environmentMap.put(SYSTEM, systemEnvironments);

        scopePanel.setDelegate(this);

        scopePanel.addButton(SYSTEM, resources.scopeSystem(), false);
        scopePanel.addButton(PROJECT, resources.scopeProject(), true);

        this.view.setScopePanel(scopePanel);

        this.scope = SYSTEM;

        this.systemEnvironmentsAction.perform();
    }

    /** {@inheritDoc} */
    @Override
    public void onAllTypeButtonClicked() {
        view.clearEnvironmentsPanel();
        view.clearTypeButtonsPanel();

        if (scope == null) {
            return;
        }

        if (!systemEnvironments.isEmpty() && !projectEnvironments.isEmpty()) {
            performSystemEnvironments();
            performProjectEnvironments();

            return;
        }

        switch (scope) {
            case SYSTEM:
                performSystemEnvironments();
                break;
            case PROJECT:
                performProjectEnvironments();
                break;
            default:
        }
    }

    private void performSystemEnvironments() {
        systemEnvironments.clear();

        systemEnvironmentsAction.perform();
    }

    private void performProjectEnvironments() {
        projectEnvironments.clear();

        projectEnvironmentsAction.perform();
    }

    /** {@inheritDoc} */
    @Override
    public void onLangTypeButtonClicked(@Nonnull RunnerEnvironmentTree environmentTree) {
        systemEnvironments.clear();

        List<RunnerEnvironmentLeaf> environments = environmentUtil.getAllEnvironments(environmentTree);

        systemEnvironments.addAll(environmentUtil.getEnvironmentsFromNodes(environments));

        environmentMap.put(SYSTEM, systemEnvironments);

        view.addEnvironment(environmentMap);
    }

    /** {@inheritDoc} */
    @Override
    public void select(@Nonnull RunnerEnvironment environment) {
        view.selectEnvironment(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void addEnvironments(@Nonnull List<RunnerEnvironment> environmentList, @Nonnull Scope scope) {
        switch (scope) {
            case SYSTEM:
                systemEnvironments.addAll(environmentList);
                environmentMap.put(scope, systemEnvironments);

                view.addEnvironment(environmentMap);
                break;
            case PROJECT:
                projectEnvironments.addAll(environmentList);
                environmentMap.put(scope, projectEnvironments);

                view.addEnvironment(environmentMap);
                break;
            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addButton(@Nonnull RunnerEnvironmentTree tree) {
        view.clearTypeButtonsPanel();

        for (RunnerEnvironmentTree environment : environmentUtil.getAllEnvironments(tree, 1)) {
            view.addButton(environment);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onButtonChecked(@Nonnull Scope scope) {
        this.scope = scope;

        switch (scope) {
            case SYSTEM:
                systemEnvironmentsAction.perform();
                break;
            case PROJECT:
                projectEnvironmentsAction.perform();
                break;
            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onButtonUnchecked(@Nonnull Scope scope) {
        this.scope = null;
        view.clearEnvironmentsPanel();

        switch (scope) {
            case PROJECT:
                this.scope = SYSTEM;
                environmentMap.get(scope).clear();

                view.addEnvironment(environmentMap);
                break;
            case SYSTEM:
                environmentMap.get(scope).clear();
                view.clearTypeButtonsPanel();

                view.addEnvironment(environmentMap);
                break;
            default:
        }
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
}