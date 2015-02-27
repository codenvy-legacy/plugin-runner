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

import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.manager.RunnerManager;
import com.codenvy.ide.ext.runner.client.models.Environment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class RunActionTest {
    private final String SOME_STRING = "some_string";

    @Mock
    private RunnerManager              runnerManager;
    @Mock
    private ActionEvent                actionEvent;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private RunnerResources            resources;
    @Mock
    private DtoFactory                 dtoFactory;
    @Mock
    private RunOptions                 runOptions;

    @Mock
    private ChooseRunnerAction chooseRunnerAction;
    @Mock
    private AppContext         appContext;
    @Mock
    private CurrentProject     currentProject;
    @Mock
    private Environment        environment;

    @InjectMocks
    private RunAction action;

    @Test
    public void constructorShouldBeVerified() throws Exception {
        verify(locale).actionRun();
        verify(locale).actionRunDescription();
        verify(resources).runAppImage();
    }

    @Test
    public void actionShouldNotBePerformedIfCurrentProjectIsNull() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(null);

        action.actionPerformed(actionEvent);

        verify(runnerManager, never()).launchRunner();
        verify(runnerManager, never()).launchRunner(Matchers.<RunOptions>any(), anyString());
    }

    @Test
    public void actionShouldBeLaunchDefaultRunner() throws Exception {
        when(chooseRunnerAction.getSelectedEnvironment()).thenReturn(environment);
        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.getRunner()).thenReturn(SOME_STRING);
        when(environment.getName()).thenReturn(SOME_STRING);

        action.actionPerformed(actionEvent);

        verify(runnerManager).launchRunner();
    }

    @Test
    public void actionShouldBeLaunchDefaultRunnerIfEnvironmentIsNull() throws Exception {
        when(chooseRunnerAction.getSelectedEnvironment()).thenReturn(null);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        action.actionPerformed(actionEvent);

        verify(runnerManager).launchRunner();
    }

    @Test
    public void actionShouldBeLaunchCustomEnvironment() throws Exception {
        when(chooseRunnerAction.getSelectedEnvironment()).thenReturn(environment);
        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.getRunner()).thenReturn(SOME_STRING + SOME_STRING);
        when(environment.getName()).thenReturn(SOME_STRING);
        when(dtoFactory.createDto(RunOptions.class)).thenReturn(runOptions);
        when(runOptions.withOptions(Matchers.<Map<String, String>>any())).thenReturn(runOptions);

        action.actionPerformed(actionEvent);

        verify(environment).getOptions();
        verify(environment, times(2)).getName();
        verify(runOptions).withOptions(Matchers.<Map<String, String>>any());
        verify(runnerManager).launchRunner(runOptions, SOME_STRING);
    }

}