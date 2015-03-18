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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentLeaf;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
import org.eclipse.che.ide.ext.runner.client.selection.SelectionManager;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.container.PropertiesContainer;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.filterwidget.FilterWidget;
import org.eclipse.che.ide.ext.runner.client.util.GetEnvironmentsUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class TemplatesPresenter implements TemplatesContainer, FilterWidget.ActionDelegate {

    private final TemplatesView view;
    private final FilterWidget  filter;
    private final SelectionManager selectionManager;
    private final GetProjectEnvironmentsAction  projectEnvironmentsAction;
    private final GetSystemEnvironmentsAction   systemEnvironmentsAction;
    private final GetEnvironmentsUtil           environmentUtil;
    private final List<Environment>             systemEnvironments;
    private final List<Environment>             projectEnvironments;
    private final Map<Scope, List<Environment>> environmentMap;
    private final PropertiesContainer           propertiesContainer;
    private final AppContext                    appContext;
    private final String                        typeAll;

    private RunnerEnvironmentTree tree;
    private String                currentType;
    private Scope                 currentScope;
    private boolean               isFirstClick;

    @Inject
    public TemplatesPresenter(TemplatesView view,
                              FilterWidget filter,
                              RunnerLocalizationConstant locale,
                              AppContext appContext,
                              GetProjectEnvironmentsAction projectEnvironmentsAction,
                              GetSystemEnvironmentsAction systemEnvironmentsAction,
                              GetEnvironmentsUtil environmentUtil,
                              PropertiesContainer propertiesContainer,
                              SelectionManager selectionManager) {
        this.filter = filter;
        this.selectionManager = selectionManager;
        this.filter.setDelegate(this);

        this.view = view;
        this.view.setFilterWidget(filter);

        this.projectEnvironmentsAction = projectEnvironmentsAction;
        this.systemEnvironmentsAction = systemEnvironmentsAction;
        this.environmentUtil = environmentUtil;
        this.propertiesContainer = propertiesContainer;
        this.appContext = appContext;

        this.projectEnvironments = new ArrayList<>();
        this.systemEnvironments = new ArrayList<>();

        this.environmentMap = new EnumMap<>(Scope.class);
        this.environmentMap.put(PROJECT, projectEnvironments);
        this.environmentMap.put(SYSTEM, systemEnvironments);

        this.isFirstClick = true;
        this.currentScope = PROJECT;
        this.typeAll = locale.configsTypeAll();
    }

    /** {@inheritDoc} */
    @Override
    public void select(@Nullable Environment environment) {
        propertiesContainer.show(environment);
        view.selectEnvironment(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void addEnvironments(@Nonnull RunnerEnvironmentTree tree, @Nonnull Scope scope) {
        ProjectDescriptor descriptor = getProjectDescriptor();

        switch (scope) {
            case SYSTEM:
                this.tree = tree;
                List<Environment> system = environmentUtil.getEnvironmentsByProjectType(tree, descriptor.getType(), scope);
                addEnvironments(systemEnvironments, system, scope);
                break;
            case PROJECT:
                if (!SYSTEM.equals(currentScope)) {
                    List<Environment> project = environmentUtil.getEnvironmentsByProjectType(tree, descriptor.getType(), scope);
                    addEnvironments(projectEnvironments, project, scope);
                }
                break;
            default:
        }
    }

    @Nonnull
    private ProjectDescriptor getProjectDescriptor() {
        CurrentProject currentProject = appContext.getCurrentProject();

        if (currentProject == null) {
            throw new IllegalStateException("Current project is null");
        }

        return currentProject.getProjectDescription();
    }

    private void addEnvironments(@Nonnull List<Environment> sourceList,
                                 @Nonnull List<Environment> targetList,
                                 @Nonnull Scope scope) {
        sourceList.clear();
        sourceList.addAll(targetList);

        environmentMap.put(scope, sourceList);
        view.addEnvironment(environmentMap);

        selectFirstEnvironment();
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

        select(environment);
        selectionManager.setEnvironment(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void setTypeItem(@Nonnull String item) {
        currentType = item;

        filter.addType(currentType);
    }

    /** {@inheritDoc} */
    @Override
    public void showEnvironments() {
        if (isFirstClick) {
            view.clearEnvironmentsPanel();
            systemEnvironments.clear();

            projectEnvironmentsAction.perform();

            filter.selectScope(PROJECT);
            filter.selectType(currentType);

            isFirstClick = false;
        }

        //TODO need select element which was selected before
        selectFirstEnvironment();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        view.clearEnvironmentsPanel();

        currentType = filter.getType();
        currentScope = filter.getScope();

        switch (currentScope) {
            case SYSTEM:
                selectSystemScope();
                break;

            case PROJECT:
                performProjectEnvironments();
                break;

            default:
                selectAllScope();
        }
    }

    private void selectSystemScope() {
        projectEnvironments.clear();

        if (currentType.equals(typeAll)) {
            performSystemEnvironments();
        } else {
            systemEnvironmentsAction.perform();
        }
    }

    private void performProjectEnvironments() {
        projectEnvironments.clear();
        systemEnvironments.clear();

        projectEnvironmentsAction.perform();
    }

    private void selectAllScope() {
        if (currentType.equals(typeAll)) {
            performProjectEnvironments();
            performSystemEnvironments();
        } else {
            projectEnvironmentsAction.perform();
            systemEnvironmentsAction.perform();
        }
    }

    private void performSystemEnvironments() {
        systemEnvironments.clear();
        List<RunnerEnvironmentLeaf> leaves = environmentUtil.getAllEnvironments(tree);
        List<Environment> environments = environmentUtil.getEnvironmentsFromNodes(leaves, SYSTEM);

        addEnvironments(systemEnvironments, environments, SYSTEM);
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