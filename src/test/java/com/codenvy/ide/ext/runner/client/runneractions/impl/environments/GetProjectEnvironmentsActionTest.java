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
package com.codenvy.ide.ext.runner.client.runneractions.impl.environments;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.actions.ChooseRunnerAction;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.tabs.templates.TemplatesContainer;
import com.codenvy.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class GetProjectEnvironmentsActionTest {
    private static final String PATH = "somePath";

    //variables for constructor
    @Mock
    private Provider<TemplatesContainer>                          templatesContainerProvider;
    @Mock
    private AppContext                                            appContext;
    @Mock
    private ProjectServiceClient                                  projectService;
    @Mock
    private NotificationManager                                   notificationManager;
    @Mock
    private Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider;
    @Mock
    private RunnerLocalizationConstant                            locale;
    @Mock
    private GetEnvironmentsUtil                                   environmentUtil;
    @Mock
    private ChooseRunnerAction                                    chooseRunnerAction;

    @Mock
    private Throwable reason;

    //callbacks for server
    @Mock
    private AsyncCallbackBuilder<RunnerEnvironmentTree> asyncCallbackBuilder;
    @Mock
    private AsyncRequestCallback<RunnerEnvironmentTree> asyncRequestCallback;
    //project variables
    @Mock
    private CurrentProject                              project;
    @Mock
    private ProjectDescriptor                           projectDescriptor;
    //runner variables
    @Mock
    private TemplatesContainer                          templatesContainer;
    @Mock
    private List<RunnerEnvironmentLeaf>                 environmentLeaves;
    @Mock
    private List<Environment>                           environments;
    @Mock
    private RunnerEnvironmentTree                       result;

    //captors
    @Captor
    private ArgumentCaptor<FailureCallback>                        failedCallBackCaptor;
    @Captor
    private ArgumentCaptor<SuccessCallback<RunnerEnvironmentTree>> successCallBackCaptor;

    private GetProjectEnvironmentsAction action;

    @Before
    public void setUp() {
        action = new GetProjectEnvironmentsAction(appContext,
                                                  projectService,
                                                  notificationManager,
                                                  callbackBuilderProvider,
                                                  locale,
                                                  environmentUtil,
                                                  chooseRunnerAction,
                                                  templatesContainerProvider);

        when(templatesContainerProvider.get()).thenReturn(templatesContainer);
        //preparing callbacks for server
        when(appContext.getCurrentProject()).thenReturn(project);
        when(callbackBuilderProvider.get()).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.unmarshaller(RunnerEnvironmentTree.class)).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.failure(any(FailureCallback.class))).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.success(Matchers.<SuccessCallback<RunnerEnvironmentTree>>anyObject()))
                .thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.build()).thenReturn(asyncRequestCallback);
        //preparing project data
        when(project.getProjectDescription()).thenReturn(projectDescriptor);
        when(projectDescriptor.getPath()).thenReturn(PATH);
    }

    @Test
    public void shouldPerformWhenCurrentProjectIsNull() {
        when(appContext.getCurrentProject()).thenReturn(null);

        action.perform();

        verify(appContext).getCurrentProject();
        verifyZeroInteractions(project);
        verifyNoMoreInteractions(templatesContainerProvider,
                                 appContext,
                                 projectService,
                                 notificationManager,
                                 callbackBuilderProvider,
                                 locale,
                                 environmentUtil);
    }

    @Test
    public void shouldPerformFailure() {
        String errorMessage = "error message";
        when(locale.customRunnerGetEnvironmentFailed()).thenReturn(errorMessage);

        action.perform();

        verify(appContext).getCurrentProject();

        verify(asyncCallbackBuilder).failure(failedCallBackCaptor.capture());
        FailureCallback failureCallback = failedCallBackCaptor.getValue();
        failureCallback.onFailure(reason);

        verify(locale).customRunnerGetEnvironmentFailed();
        verify(notificationManager).showError(errorMessage);

        verify(projectService).getRunnerEnvironments(PATH, asyncRequestCallback);
    }

    @Test
    public void shouldPerformSuccessWithToRunnerEnvironment() {
        when(environmentUtil.getAllEnvironments(result)).thenReturn(environmentLeaves);
        when(environmentUtil.getEnvironmentsFromNodes(environmentLeaves, PROJECT)).thenReturn(environments);

        action.perform();

        verify(appContext).getCurrentProject();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(result);

        verify(environmentUtil).getAllEnvironments(result);
        verify(environmentUtil).getEnvironmentsFromNodes(environmentLeaves, PROJECT);

        verify(templatesContainer).addEnvironments(environments, PROJECT);

        verify(projectService).getRunnerEnvironments(PATH, asyncRequestCallback);
        verify(chooseRunnerAction).addProjectRunners(environments);
    }
}