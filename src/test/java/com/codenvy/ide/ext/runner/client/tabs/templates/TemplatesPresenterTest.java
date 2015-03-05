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

import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.tabs.properties.container.PropertiesContainer;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.templates.scopepanel.ScopePanel;
import com.codenvy.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class TemplatesPresenterTest {

    private List<Environment>           projectEnvironments;
    private List<Environment>           systemEnvironments;

    @Mock
    private TemplatesView                view;
    @Mock
    private GetProjectEnvironmentsAction projectEnvironmentsAction;
    @Mock
    private GetSystemEnvironmentsAction  systemEnvironmentsAction;
    @Mock
    private GetEnvironmentsUtil          environmentUtil;
    @Mock
    private ScopePanel                   scopePanel;
    @Mock
    private RunnerResources              resources;
    @Mock
    private PropertiesContainer          propertiesContainer;

    @Mock
    private SVGResource           systemImage;
    @Mock
    private SVGResource           projectImage;
    @Mock
    private Environment           runnerEnvironment1;
    @Mock
    private Environment           runnerEnvironment2;
    @Mock
    private RunnerEnvironmentTree tree1;
    @Mock
    private RunnerEnvironmentTree tree2;
    @Mock
    private AcceptsOneWidget      container;

    private TemplatesPresenter presenter;

    @Before
    public void setUp() {
        //empty lists
        projectEnvironments = new ArrayList<>();
        systemEnvironments = new ArrayList<>();

        when(resources.scopeSystem()).thenReturn(systemImage);
        when(resources.scopeProject()).thenReturn(projectImage);

        presenter = new TemplatesPresenter(view,
                                           projectEnvironmentsAction,
                                           systemEnvironmentsAction,
                                           environmentUtil,
                                           scopePanel,
                                           resources,
                                           propertiesContainer);

    }

    @Test
    public void constructorShouldBeVerified() {
        verify(view).setDelegate(presenter);
        verify(scopePanel).setDelegate(presenter);
        verify(resources).scopeSystem();
        verify(resources).scopeProject();
        verify(scopePanel).addButton(SYSTEM, systemImage, false);
        verify(scopePanel).addButton(PROJECT, projectImage, false);
        verify(view).setScopePanel(scopePanel);
    }

    @Test
    public void shouldOnAllTypeButtonClickedWhenScopeSystem() {
        reset(systemEnvironmentsAction);

        presenter.onAllTypeButtonClicked();

        verify(view).clearEnvironmentsPanel();
        verify(view).clearTypeButtonsPanel();

        verify(systemEnvironmentsAction).perform();
    }

    @Test
    public void shouldOnAllTypeButtonClickedWhenScopeProject() {
        presenter.onButtonChecked(PROJECT);

        reset(projectEnvironmentsAction);

        presenter.onAllTypeButtonClicked();

        verify(view).clearEnvironmentsPanel();
        verify(view).clearTypeButtonsPanel();

        verify(projectEnvironmentsAction).perform();
    }

    @Test
    public void shouldOnAllTypeButtonClickedWhenScopeIsNull() {
        //add system and project runner environments
        projectEnvironments.add(runnerEnvironment1);
        systemEnvironments.add(runnerEnvironment2);

        presenter.addEnvironments(projectEnvironments, PROJECT);
        presenter.addEnvironments(systemEnvironments, SYSTEM);

        presenter.onAllTypeButtonClicked();

        verify(view).clearEnvironmentsPanel();
        verify(view).clearTypeButtonsPanel();

        verify(systemEnvironmentsAction).perform();
        verify(projectEnvironmentsAction).perform();
    }

    @Test
    public void shouldOnLangTypeButtonClicked() {
        presenter.onLangTypeButtonClicked(tree1);

        verify(view).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());
    }

    @Test
    public void shouldSelect() {
        presenter.select(runnerEnvironment1);

        verify(view).selectEnvironment(runnerEnvironment1);
        verify(propertiesContainer).show(runnerEnvironment1);
    }

    @Test
    public void projectEnvironmentsShouldBeShownWhenScopeIsProject1() {
        projectEnvironments.add(runnerEnvironment1);
        presenter.onButtonUnchecked(PROJECT);
        reset(view);

        presenter.addEnvironments(projectEnvironments, PROJECT);
        verify(view, never()).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());
    }

    @Test
    public void projectEnvironmentsShouldBeShownWhenScopeIsProject2() {
        presenter.onButtonChecked(PROJECT);
        projectEnvironments.add(runnerEnvironment1);

        presenter.addEnvironments(projectEnvironments, PROJECT);
        verify(view).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());
    }

    @Test
    public void shouldAddEnvironmentsWhenScopeSystem() {
        projectEnvironments.add(runnerEnvironment1);

        presenter.addEnvironments(projectEnvironments, SYSTEM);
        verify(view).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());
    }

    @Test
    public void addButtonShouldBeAdded() {
        presenter.addButton(tree1);

        verify(view).clearTypeButtonsPanel();
        verify(view).addButton(tree1);
    }

    @Test
    public void shouldOnButtonCheckedWhenScopeIsSystem() {
        reset(projectEnvironmentsAction);
        presenter.onButtonChecked(PROJECT);

        verify(projectEnvironmentsAction).perform();
    }

    @Test
    public void shouldOnButtonCheckedWhenScopeIsProject() {
        reset(systemEnvironmentsAction);
        presenter.onButtonChecked(SYSTEM);

        verify(systemEnvironmentsAction).perform();
    }

    @Test
    public void shouldOnButtonUncheckedWhenScopeIsSystem() {
        reset(systemEnvironmentsAction);
        presenter.onButtonUnchecked(SYSTEM);

        verify(view).clearEnvironmentsPanel();
        verify(view).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());
    }

    @Test
    public void shouldOnButtonUncheckedWhenScopeIsProject() {
        reset(systemEnvironmentsAction);
        presenter.onButtonUnchecked(PROJECT);

        verify(view).clearEnvironmentsPanel();
        verify(view).addEnvironment(Matchers.<Map<Scope, List<Environment>>>anyObject());
    }

    @Test
    public void shouldGo() {
        presenter.go(container);

        verify(container).setWidget(view);
    }

    @Test
    public void shouldGetView() {
        assertThat(presenter.getView(), CoreMatchers.<IsWidget>is(view));
    }

    @Test
    public void shouldSetVisibleTrue() {
        presenter.setVisible(true);

        verify(view).setVisible(true);
    }

    @Test
    public void shouldSetVisibleFalse() {
        presenter.setVisible(false);

        verify(view).setVisible(false);
    }

    @Test
    public void systemEnvironmentsShouldBeShow() {
        presenter.showSystemEnvironments();

        verify(systemEnvironmentsAction).perform();
    }

    @Test
    public void systemEnvironmentsShouldNotBeShow() {
        presenter.showSystemEnvironments();
        reset(systemEnvironmentsAction);

        presenter.showSystemEnvironments();
        verify(systemEnvironmentsAction, never()).perform();
    }
}