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
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class TemplatesPresenterTest {

    private List<RunnerEnvironment>     projectEnvironments;
    private List<RunnerEnvironment>     systemEnvironments;
    private List<RunnerEnvironmentTree> runnerEnvironmentTrees;

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
    private SVGResource           systemImage;
    @Mock
    private SVGResource           projectImage;
    @Mock
    private RunnerEnvironment     runnerEnvironment1;
    @Mock
    private RunnerEnvironment     runnerEnvironment2;
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

        runnerEnvironmentTrees = Arrays.asList(tree1, tree2);

        when(resources.scopeSystem()).thenReturn(systemImage);
        when(resources.scopeProject()).thenReturn(projectImage);

        presenter = new TemplatesPresenter(view,
                                           projectEnvironmentsAction,
                                           systemEnvironmentsAction,
                                           environmentUtil,
                                           scopePanel,
                                           resources);

    }

    @Test
    public void shouldVerifyConstructor() {
        verify(view).setDelegate(presenter);
        verify(scopePanel).setDelegate(presenter);
        verify(scopePanel).addButton(SYSTEM, systemImage, false);
        verify(scopePanel).addButton(PROJECT, projectImage, true);
        verify(view).setScopePanel(scopePanel);
        verify(systemEnvironmentsAction).perform();
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
        presenter.onAllTypeButtonClicked();

        verify(view).clearEnvironmentsPanel();
        verify(view).clearTypeButtonsPanel();

        verify(systemEnvironmentsAction).perform();
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

        verify(systemEnvironmentsAction, times(2)).perform();
    }

    @Test
    public void shouldOnLangTypeButtonClicked() {
        presenter.onLangTypeButtonClicked(tree1);

        verify(view).addEnvironment(Matchers.<Map<Scope, List<RunnerEnvironment>>>anyObject());
    }

    @Test
    public void shouldSelect() {
        presenter.select(runnerEnvironment1);

        verify(view).selectEnvironment(runnerEnvironment1);
    }

    @Test
    public void shouldAddEnvironmentsWhenScopeProject() {
        projectEnvironments.add(runnerEnvironment1);

        presenter.addEnvironments(projectEnvironments, PROJECT);
        verify(view).addEnvironment(Matchers.<Map<Scope, List<RunnerEnvironment>>>anyObject());
    }

    @Test
    public void shouldAddEnvironmentsWhenScopeSystem() {
        projectEnvironments.add(runnerEnvironment1);

        presenter.addEnvironments(projectEnvironments, SYSTEM);
        verify(view).addEnvironment(Matchers.<Map<Scope, List<RunnerEnvironment>>>anyObject());
    }

    @Test
    public void shouldAddButton() {
        when(environmentUtil.getAllEnvironments(tree1, 1)).thenReturn(runnerEnvironmentTrees);
        presenter.addButton(tree1);

        verify(view).clearTypeButtonsPanel();
        verify(view).addButton(tree1);
        verify(view).addButton(tree2);
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
        verify(view).addEnvironment(Matchers.<Map<Scope, List<RunnerEnvironment>>>anyObject());
    }

    @Test
    public void shouldOnButtonUncheckedWhenScopeIsProject() {
        reset(systemEnvironmentsAction);
        presenter.onButtonUnchecked(PROJECT);

        verify(view).clearEnvironmentsPanel();
        verify(view).addEnvironment(Matchers.<Map<Scope, List<RunnerEnvironment>>>anyObject());
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

}