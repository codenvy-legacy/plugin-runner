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
package org.eclipse.che.ide.ext.runner.client.tabs.templates;

import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentLeaf;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.container.PropertiesContainer;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.scopepanel.ScopePanel;
import org.eclipse.che.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * The class contains business logic to change displaying of environments depending on scope or type.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TemplatesPresenter implements TemplatesContainer, TemplatesView.ActionDelegate, ScopePanel.ActionDelegate {

    private final TemplatesView                 view;
    private final GetProjectEnvironmentsAction  projectEnvironmentsAction;
    private final GetSystemEnvironmentsAction   systemEnvironmentsAction;
    private final GetEnvironmentsUtil           environmentUtil;
    private final List<Environment>             systemEnvironments;
    private final List<Environment>             projectEnvironments;
    private final Map<Scope, List<Environment>> environmentMap;
    private final PropertiesContainer           propertiesContainer;

    private Scope   scope;
    private boolean isFirstClick;
    private boolean isProjectChecked;

    @Inject
    public TemplatesPresenter(TemplatesView view,
                              GetProjectEnvironmentsAction projectEnvironmentsAction,
                              GetSystemEnvironmentsAction systemEnvironmentsAction,
                              GetEnvironmentsUtil environmentUtil,
                              ScopePanel scopePanel,
                              RunnerResources resources,
                              PropertiesContainer propertiesContainer) {
        this.view = view;
        this.view.setDelegate(this);

        this.projectEnvironmentsAction = projectEnvironmentsAction;
        this.systemEnvironmentsAction = systemEnvironmentsAction;
        this.environmentUtil = environmentUtil;
        this.propertiesContainer = propertiesContainer;

        this.projectEnvironments = new ArrayList<>();
        this.systemEnvironments = new ArrayList<>();

        this.environmentMap = new EnumMap<>(Scope.class);
        this.environmentMap.put(PROJECT, projectEnvironments);
        this.environmentMap.put(SYSTEM, systemEnvironments);

        scopePanel.setDelegate(this);

        scopePanel.addButton(SYSTEM, resources.scopeSystem(), false);
        scopePanel.addButton(PROJECT, resources.scopeProject(), false);

        this.view.setScopePanel(scopePanel);

        this.scope = SYSTEM;
        this.isFirstClick = true;
        this.isProjectChecked = true;
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
        systemEnvironments.addAll(environmentUtil.getEnvironmentsFromNodes(environments, SYSTEM));

        environmentMap.put(SYSTEM, systemEnvironments);
        view.addEnvironment(environmentMap);
        selectFirstEnvironment();
    }

    /** {@inheritDoc} */
    @Override
    public void select(@Nonnull Environment environment) {
        propertiesContainer.show(environment);
        view.selectEnvironment(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void addEnvironments(@Nonnull List<Environment> environmentList, @Nonnull Scope scope) {
        switch (scope) {
            case SYSTEM:
                addEnvironments(systemEnvironments, environmentList, scope, true);
                break;
            case PROJECT:
                addEnvironments(projectEnvironments, environmentList, scope, isProjectChecked);
                break;
            default:
        }
    }

    private void addEnvironments(@Nonnull List<Environment> sourceList,
                                 @Nonnull List<Environment> targetList,
                                 @Nonnull Scope scope,
                                 boolean isChecked) {
        sourceList.clear();
        sourceList.addAll(targetList);

        environmentMap.put(scope, sourceList);
        if (isChecked) {
            view.addEnvironment(environmentMap);
            selectFirstEnvironment();
        }
    }

    private void selectFirstEnvironment() {
        propertiesContainer.setVisible(true);
        Environment environment = null;

        for (Map.Entry<Scope, List<Environment>> entry : environmentMap.entrySet()) {
            List<Environment> value = entry.getValue();
            if (!value.isEmpty()) {
                environment = value.get(0);
                break;
            }
        }

        if (environment == null) {
            propertiesContainer.setVisible(false);
        } else {
            select(environment);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addButton(@Nonnull RunnerEnvironmentTree tree) {
        view.clearTypeButtonsPanel();
        view.addButton(tree);
    }

    /** {@inheritDoc} */
    @Override
    public void showSystemEnvironments() {
        if (isFirstClick) {
            onButtonChecked(SYSTEM);
            isFirstClick = false;
        }

        //TODO need select element which was selected before
        selectFirstEnvironment();
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
                isProjectChecked = true;
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
                isProjectChecked = false;
                break;
            case SYSTEM:
                view.clearTypeButtonsPanel();
                break;
            default:
        }

        environmentMap.get(scope).clear();
        view.addEnvironment(environmentMap);

        selectFirstEnvironment();
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