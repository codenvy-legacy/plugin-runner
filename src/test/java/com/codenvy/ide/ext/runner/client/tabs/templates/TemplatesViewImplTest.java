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
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.templates.environment.EnvironmentWidget;
import com.codenvy.ide.ext.runner.client.tabs.templates.scopepanel.ScopePanel;
import com.codenvy.ide.ext.runner.client.tabs.templates.typebutton.TypeButton;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class TemplatesViewImplTest {

    private static final String TEXT = "some text";
    private Map<Scope, List<RunnerEnvironment>> environments;
    private List<RunnerEnvironment>             projectEnvironments;
    private List<RunnerEnvironment>             systemEnvironments;

    //mocks for variables
    @Mock
    private RunnerResources            resources;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private WidgetFactory              widgetFactory;

    @Mock
    private TypeButton                   allButton;
    @Mock
    private TemplatesView.ActionDelegate delegate;

    @Mock
    private RunnerEnvironment            runnerEnvironment1;
    @Mock
    private RunnerEnvironment            runnerEnvironment2;
    @Mock
    private RunnerEnvironment            runnerEnvironment3;
    @Mock
    private RunnerEnvironment            runnerEnvironment4;

    @Mock
    private EnvironmentWidget            widget1;
    @Mock
    private EnvironmentWidget            widget2;
    @Mock
    private EnvironmentWidget            widget3;
    @Mock
    private EnvironmentWidget            widget4;

    @Mock
    private ScopePanel                   scopePanel;
    @Mock
    private TypeButton                   typeButton;

    private TemplatesViewImpl view;

    @Before
    public void setUp() {
        environments = new HashMap<>();
        projectEnvironments = Arrays.asList(runnerEnvironment1, runnerEnvironment2);
        systemEnvironments = Arrays.asList(runnerEnvironment3, runnerEnvironment4);

        when(locale.templateTypeAll()).thenReturn(TEXT);
        when(widgetFactory.createEnvironment()).thenReturn(widget1)
                                               .thenReturn(widget2)
                                               .thenReturn(widget3)
                                               .thenReturn(widget4);

        when(widgetFactory.createTypeButton()).thenReturn(allButton);
        view = new TemplatesViewImpl(resources, locale, widgetFactory);
    }

    @Test
    public void shouldVerifyConstructor() {
        view.setDelegate(delegate);

        verify(widgetFactory).createTypeButton();

        ArgumentCaptor<TypeButton.ActionDelegate> argumentCaptor = ArgumentCaptor.forClass(TypeButton.ActionDelegate.class);
        verify(allButton).setDelegate(argumentCaptor.capture());
        TypeButton.ActionDelegate actionDelegate = argumentCaptor.getValue();
        actionDelegate.onButtonClicked();

        verify(delegate).onAllTypeButtonClicked();
        verify(allButton, times(2)).select();

        verify(allButton).setName(TEXT);
        verify(locale).templateTypeAll();
        verify(view.allButtonPanel).add(allButton);
    }

    @Test
    public void shouldNotAddEnvironmentWhenMapOfEnvironmentsAreNull() {
        reset(allButton);
        view.addEnvironment(environments);

        verify(view.environmentsPanel).clear();
        verify(allButton).unSelect();
        verify(allButton).select();
    }

    @Test
    public void shouldNotAddEnvironmentWhenMapOfEnvironmentsAreEmpty() {
        reset(allButton);
        environments.put(SYSTEM, Collections.<RunnerEnvironment>emptyList());

        view.addEnvironment(environments);

        verify(view.environmentsPanel).clear();
        verify(allButton).unSelect();
        verify(allButton).select();
    }

    @Test
    public void shouldAddEnvironmentWhenListProjectEnvironmentsIsNotEmpty() {
        reset(allButton);
        environments.put(PROJECT, projectEnvironments);

        view.addEnvironment(environments);

        verify(view.environmentsPanel).clear();

        verify(view.environmentsPanel, times(2)).add(any(EnvironmentWidget.class));
        verify(widgetFactory, times(2)).createEnvironment();

        verify(allButton).unSelect();
        verify(allButton).select();
    }

    @Test
    public void shouldAddEnvironmentWhenListSystemEnvironmentsIsNotEmpty() {
        reset(allButton);
        environments.put(PROJECT, projectEnvironments);

        view.addEnvironment(environments);

        verify(view.environmentsPanel).clear();

        verify(view.environmentsPanel, times(2)).add(any(EnvironmentWidget.class));
        verify(widgetFactory, times(2)).createEnvironment();

        verify(allButton).unSelect();
        verify(allButton).select();
    }

    @Test
    public void shouldAddEnvironmentWhenListsSystemAndProjectsEnvironmentsAreNotEmpty() {
        reset(allButton);
        environments.put(PROJECT, projectEnvironments);
        environments.put(SYSTEM, systemEnvironments);

        view.addEnvironment(environments);

        verify(view.environmentsPanel).clear();

        verify(view.environmentsPanel, times(4)).add(any(EnvironmentWidget.class));
        verify(widgetFactory, times(4)).createEnvironment();
    }

    @Test
    public void shouldNotSelectEnvironmentIfEnvironmentNotExistInList() {
        environments.put(SYSTEM, systemEnvironments);

        view.addEnvironment(environments);
        view.selectEnvironment(runnerEnvironment1);

        verify(widget1).unSelect();
        verify(widget2).unSelect();

        verifyNoMoreInteractions(runnerEnvironment3, runnerEnvironment4);
    }

    @Test
    public void shouldSelectEnvironmentIfEnvironmentExistInList() {
        environments.put(PROJECT, systemEnvironments);

        view.addEnvironment(environments);
        view.selectEnvironment(runnerEnvironment1);

        verify(widget1).unSelect();
        verify(widget2).unSelect();
        verify(widget1).select();
    }

    @Test
    public void shouldSetScopePanel() {
        view.setScopePanel(scopePanel);

        verify(view.scopePanel).setWidget(scopePanel);
    }

    @Test
    public void shouldAddButton() {
        TemplatesView.ActionDelegate actionDelegate = mock(TemplatesView.ActionDelegate.class);
        RunnerEnvironmentTree tree = mock(RunnerEnvironmentTree.class);
        when(tree.getDisplayName()).thenReturn(TEXT);
        when(widgetFactory.createTypeButton()).thenReturn(typeButton);
        view.setDelegate(actionDelegate);

        view.addButton(tree);

        verify(widgetFactory, times(2)).createTypeButton();
        verify(typeButton).setName(TEXT);
        verify(view.buttonsPanel).add(typeButton);
        verify(allButton).unSelect();
        verify(typeButton).unSelect();

        verify(allButton, times(2)).select();

        ArgumentCaptor<TypeButton.ActionDelegate> argumentCaptor = ArgumentCaptor.forClass(TypeButton.ActionDelegate.class);
        verify(typeButton).setDelegate(argumentCaptor.capture());
        TypeButton.ActionDelegate delegate = argumentCaptor.getValue();
        delegate.onButtonClicked();

        verify(actionDelegate).onLangTypeButtonClicked(tree);
        verify(allButton, times(2)).unSelect();
        verify(typeButton, times(2)).unSelect();

        verify(typeButton, times(1)).select();
        verify(allButton, times(2)).select();
    }

    @Test
    public void shouldClearEnvironmentsPanel() {
        view.clearEnvironmentsPanel();

        verify(view.environmentsPanel).clear();
    }

    @Test
    public void shouldClearTypeButtonsPanel() {
        view.clearTypeButtonsPanel();

        verify(view.buttonsPanel).clear();
    }

}