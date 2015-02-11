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
package com.codenvy.ide.ext.runner.client.runneractions.impl;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.properties.common.RAM;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.LaunchAction;
import com.codenvy.ide.ext.runner.client.util.RunnerUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunActionTest {
    private static final String PATH_TO_PROJECT = "somePath";
    private static final String PROJECT_NAME    = "projectName";

    /*constructor variables*/
    @Mock
    private RunnerServiceClient                                          service;
    @Mock
    private AppContext                                                   appContext;
    @Mock
    private RunnerLocalizationConstant                                   locale;
    @Mock
    private RunnerManagerPresenter                                       presenter;
    @Mock
    private Provider<AsyncCallbackBuilder<ApplicationProcessDescriptor>> callbackBuilderProvider;
    @Mock
    private RunnerUtil                                                   runnerUtil;
    @Mock
    private RunnerActionFactory                                          actionFactory;

    @Mock
    private Throwable                                                     reason;
    //callbacks for server
    @Mock
    private AsyncCallbackBuilder<ApplicationProcessDescriptor>            asyncCallbackBuilder;
    @Mock
    private AsyncRequestCallback<ApplicationProcessDescriptor>            asyncRequestCallback;
    //project variables
    @Mock
    private CurrentProject                                                project;
    @Mock
    private ProjectDescriptor                                             projectDescriptor;
    //run variables
    @Mock
    private RunOptions                                                    runOptions;
    @Mock
    private Runner                                                        runner;
    //action variables
    @Mock
    private ApplicationProcessDescriptor                                  descriptor;
    @Mock
    private LaunchAction                                                  launchAction;
    //captors
    @Captor
    private ArgumentCaptor<FailureCallback>                               failedCallBackCaptor;
    @Captor
    private ArgumentCaptor<SuccessCallback<ApplicationProcessDescriptor>> successCallBackCaptor;

    private RunAction runAction;

    @Before
    public void setUp() {
        when(actionFactory.createLaunch()).thenReturn(launchAction);
        runAction = new RunAction(service, appContext, locale, presenter,
                                  callbackBuilderProvider, runnerUtil, actionFactory);

        //preparing callbacks for server
        when(appContext.getCurrentProject()).thenReturn(project);
        when(callbackBuilderProvider.get()).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.unmarshaller(ApplicationProcessDescriptor.class)).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.failure(any(FailureCallback.class))).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.success(Matchers.<SuccessCallback<ApplicationProcessDescriptor>>anyObject()))
                .thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.build()).thenReturn(asyncRequestCallback);
        //preparing project data
        when(project.getProjectDescription()).thenReturn(projectDescriptor);
        when(projectDescriptor.getPath()).thenReturn(PATH_TO_PROJECT);
    }

    @Test
    public void shouldPerformWhenCurrentProjectIsNull() {
        reset(launchAction);
        when(appContext.getCurrentProject()).thenReturn(null);

        runAction.perform(runner);

        verify(appContext).getCurrentProject();
        verifyNoMoreInteractions(runner, service, locale, launchAction, presenter, callbackBuilderProvider);
    }

    @Test
    public void shouldSuccessPerform() {
        //preparing descriptor data
        when(descriptor.getMemorySize()).thenReturn(RAM._512.getValue());
        when(descriptor.getProcessId()).thenReturn(12345678L);

        when(runner.getOptions()).thenReturn(runOptions);

        runAction.perform(runner);

        verify(presenter).setActive();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<ApplicationProcessDescriptor> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(descriptor);

        verify(runner).setProcessDescriptor(descriptor);
        verify(runner).setRAM(RAM._512.getValue());
        verify(runner).setStatus(Runner.Status.IN_PROGRESS);
        verify(project).setProcessDescriptor(descriptor);

        verify(presenter).addRunnerId(12345678L);

        verify(launchAction).perform(runner);

        verify(service).run(PATH_TO_PROJECT, runOptions, asyncRequestCallback);
    }

    @Test
    public void shouldFailedPerform() {
        String someRunningMessage = "run information";

        when(runner.getOptions()).thenReturn(runOptions);
        when(locale.startApplicationFailed(PROJECT_NAME)).thenReturn(someRunningMessage);

        runAction.perform(runner);

        verify(presenter).setActive();

        verify(asyncCallbackBuilder).failure(failedCallBackCaptor.capture());
        FailureCallback failureCallback = failedCallBackCaptor.getValue();
        failureCallback.onFailure(reason);

        runnerUtil.showError(runner, someRunningMessage, null);

        verify(service).run(PATH_TO_PROJECT, runOptions, asyncRequestCallback);
    }
}
