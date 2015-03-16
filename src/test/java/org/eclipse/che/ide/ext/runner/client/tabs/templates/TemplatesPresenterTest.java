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

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentLeaf;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.container.PropertiesContainer;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.filterwidget.FilterWidget;
import org.eclipse.che.ide.ext.runner.client.util.GetEnvironmentsUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.ALL;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplatesPresenterTest {

    private static final String SOME_TEXT = "someText";
    private static final String TYPE_ALL  = "All";

    //constructor mocks
    @Mock
    private TemplatesView                view;
    @Mock
    private FilterWidget                 filter;
    @Mock
    private RunnerLocalizationConstant   locale;
    @Mock
    private GetProjectEnvironmentsAction projectEnvironmentsAction;
    @Mock
    private GetSystemEnvironmentsAction  systemEnvironmentsAction;
    @Mock
    private GetEnvironmentsUtil          environmentUtil;
    @Mock
    private PropertiesContainer          propertiesContainer;
    @Mock
    private AppContext                   appContext;

    //additional mocks
    @Mock
    private Environment                 environment;
    @Mock
    private RunnerEnvironmentTree       tree;
    @Mock
    private CurrentProject              currentProject;
    @Mock
    private ProjectDescriptor           descriptor;
    @Mock
    private Environment                 systemEnvironment1;
    @Mock
    private Environment                 systemEnvironment2;
    @Mock
    private Environment                 projectEnvironment1;
    @Mock
    private Environment                 projectEnvironment2;
    @Mock
    private List<RunnerEnvironmentLeaf> leaves;
    @Mock
    private AcceptsOneWidget            container;

    private TemplatesPresenter presenter;

    @Before
    public void setUp() throws Exception {
        when(locale.configsTypeAll()).thenReturn(TYPE_ALL);

        presenter = new TemplatesPresenter(view,
                                           filter,
                                           locale,
                                           appContext,
                                           projectEnvironmentsAction,
                                           systemEnvironmentsAction,
                                           environmentUtil,
                                           propertiesContainer);

        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.getProjectDescription()).thenReturn(descriptor);
        when(descriptor.getType()).thenReturn(SOME_TEXT);

        when(environmentUtil.getEnvironmentsByProjectType(tree, SOME_TEXT, SYSTEM)).thenReturn(Arrays.asList(systemEnvironment1,
                                                                                                             systemEnvironment2));
        when(environmentUtil.getEnvironmentsByProjectType(tree, SOME_TEXT, PROJECT)).thenReturn(Arrays.asList(projectEnvironment1,
                                                                                                              projectEnvironment2));
        when(environmentUtil.getAllEnvironments(tree)).thenReturn(leaves);
        when(environmentUtil.getEnvironmentsFromNodes(leaves, SYSTEM)).thenReturn(Arrays.asList(systemEnvironment1, systemEnvironment2));
    }

    @Test
    public void constructorShouldBeVerified() throws Exception {
        verify(view).setFilterWidget(filter);
        verify(locale).configsTypeAll();
    }

    @Test
    public void environmentShouldBeSelected() throws Exception {
        presenter.select(environment);

        verify(propertiesContainer).show(environment);
        verify(view).selectEnvironment(environment);
    }

    @Test(expected = IllegalStateException.class)
    public void illegalStateExceptionShouldBeThrownWhenCurrentProjectIsNull() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(null);

        presenter.addEnvironments(tree, SYSTEM);

        verify(currentProject, never()).getProjectDescription();
    }

    @Test
    public void environmentWithScopeSystemShouldBeAdded() throws Exception {
        presenter.addEnvironments(tree, SYSTEM);

        getProjectDescriptorShouldBeVerified();

        verify(environmentUtil).getEnvironmentsByProjectType(tree, SOME_TEXT, SYSTEM);
        verify(view).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());

        verify(propertiesContainer).setVisible(true);
        verify(propertiesContainer).show(systemEnvironment1);
        verify(view).selectEnvironment(systemEnvironment1);
    }

    private void getProjectDescriptorShouldBeVerified() {
        verify(appContext).getCurrentProject();
        verify(currentProject).getProjectDescription();
    }

    @Test
    public void environmentShouldNotBeSelectedWhenEnvironmentListIsEmpty() throws Exception {
        when(environmentUtil.getEnvironmentsByProjectType(tree, SOME_TEXT, SYSTEM)).thenReturn(Collections.<Environment>emptyList());

        presenter.addEnvironments(tree, SYSTEM);

        verify(propertiesContainer).setVisible(true);
        verify(propertiesContainer).setVisible(false);
    }

    @Test
    public void environmentWithScopeProjectShouldBeAdded() throws Exception {
        presenter.addEnvironments(tree, PROJECT);

        getProjectDescriptorShouldBeVerified();

        verify(environmentUtil).getEnvironmentsByProjectType(tree, SOME_TEXT, PROJECT);
        verify(view).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());

        verify(propertiesContainer).setVisible(true);
        verify(propertiesContainer).show(projectEnvironment1);
        verify(view).selectEnvironment(projectEnvironment1);
    }

    @Test
    public void typeItemShouldBeSet() throws Exception {
        presenter.setTypeItem(SOME_TEXT);

        verify(filter).addType(SOME_TEXT);
    }

    @Test
    public void environmentsShouldBeShownWhenClickTheFirstTime() throws Exception {
        presenter.setTypeItem(SOME_TEXT);

        presenter.showEnvironments();
        presenter.addEnvironments(tree, PROJECT);

        verify(view).clearEnvironmentsPanel();
        verify(projectEnvironmentsAction).perform();
        verify(filter).selectScope(PROJECT);
        verify(filter).selectType(SOME_TEXT);

        verify(propertiesContainer).show(projectEnvironment1);
        verify(view).selectEnvironment(projectEnvironment1);
    }

    @Test
    public void environmentShouldNotBeShownWhenClickTheSecondTime() throws Exception {
        presenter.showEnvironments();
        reset(projectEnvironmentsAction);
        reset(filter);

        presenter.showEnvironments();

        verify(projectEnvironmentsAction, never()).perform();
        verify(filter, never()).selectScope(PROJECT);
        verify(filter, never()).selectType(anyString());
    }

    @Test
    public void systemEnvironmentsShouldBePerformedWhenTypeAll() throws Exception {
        when(filter.getScope()).thenReturn(SYSTEM);
        when(filter.getType()).thenReturn(TYPE_ALL);

        presenter.addEnvironments(tree, SYSTEM);
        reset(view);

        presenter.onValueChanged();

        verify(view).clearEnvironmentsPanel();
        systemEnvironmentsPerformShouldBeVerified();
    }

    private void systemEnvironmentsPerformShouldBeVerified() {
        verify(environmentUtil).getAllEnvironments(tree);
        verify(environmentUtil).getEnvironmentsFromNodes(leaves, SYSTEM);
        verify(view).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());
    }

    @Test
    public void systemEnvironmentsShouldBePerformedWhenTypeIsNotAll() throws Exception {
        when(filter.getScope()).thenReturn(SYSTEM);
        when(filter.getType()).thenReturn(SOME_TEXT);

        presenter.onValueChanged();

        verify(systemEnvironmentsAction).perform();
    }

    @Test
    public void projectEnvironmentsShouldBePerformed() throws Exception {
        when(filter.getScope()).thenReturn(PROJECT);

        presenter.onValueChanged();

        verify(projectEnvironmentsAction).perform();
    }

    @Test
    public void allEnvironmentShouldBeSelectedWhenScopeIsAllAndTypeIsNotAll() throws Exception {
        when(filter.getScope()).thenReturn(ALL);
        when(filter.getType()).thenReturn(SOME_TEXT);

        presenter.onValueChanged();

        verify(projectEnvironmentsAction).perform();
        verify(systemEnvironmentsAction).perform();
    }

    @Test
    public void allEnvironmentShouldBeSelectedWhenScopeIsAllAndTypeIsAll() throws Exception {
        when(filter.getScope()).thenReturn(ALL);
        when(filter.getType()).thenReturn(TYPE_ALL);

        presenter.addEnvironments(tree, SYSTEM);
        reset(view);

        presenter.onValueChanged();

        systemEnvironmentsPerformShouldBeVerified();
        verify(projectEnvironmentsAction).perform();
    }

    @Test
    public void widgetShouldBeSetToContainer() throws Exception {
        presenter.go(container);

        verify(container).setWidget(view);
    }

    @Test
    public void viewShouldBeReturned() throws Exception {
        TemplatesView widget = (TemplatesView)presenter.getView();

        assertThat(widget, equalTo(view));
    }

    @Test
    public void visibleParameterShouldBeSet() throws Exception {
        presenter.setVisible(true);

        verify(view).setVisible(true);
    }
}