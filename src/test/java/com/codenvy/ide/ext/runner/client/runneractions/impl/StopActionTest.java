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

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.console.ConsoleContainer;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 */
@RunWith(GwtMockitoTestRunner.class)
public class StopActionTest {
    private static final String PATH_TO_PROJECT = "somePath";
    private static final String PROJECT_NAME    = "projectName";
    private static final String MESSAGE         = "some tested message";

    //constructor variables
    @Mock
    private RunnerServiceClient                                          service;
    @Mock
    private AppContext                                                   appContext;
    @Mock
    private Provider<AsyncCallbackBuilder<ApplicationProcessDescriptor>> callbackBuilderProvider;
    @Mock
    private RunnerLocalizationConstant                                   constant;
    @Mock
    private NotificationManager                                          notificationManager;
    @Mock
    private RunnerUtil                                                   runnerUtil;
    @Mock
    private RunnerActionFactory                                          actionFactory;
    @Mock
    private RunnerManagerPresenter                                       presenter;
    @Mock
    private ConsoleContainer                                             consoleContainer;


    //action varables
    @Mock
    private GetLogsAction                                                 logsAction;
    @Mock
    private LaunchAction                                                  launchAction;
    //project variables
    @Mock
    private CurrentProject                                                project;
    @Mock
    private ProjectDescriptor                                             projectDescriptor;
    @Mock
    private Link                                                          stopLink;
    //runner variables
    @Mock
    private Runner                                                        runner;
    @Mock
    private RunnerManagerView                                             view;
    @Mock
    private RunOptions                                                    runOptions;
    //callbacks for server
    @Mock
    private AsyncCallbackBuilder<ApplicationProcessDescriptor>            asyncCallbackBuilder;
    @Mock
    private AsyncRequestCallback<ApplicationProcessDescriptor>            callback;
    @Mock
    private Throwable                                                     reason;
    @Mock
    private ApplicationProcessDescriptor                                  descriptor;
    //captors
    @Captor
    private ArgumentCaptor<FailureCallback>                               failedCallBackCaptor;
    @Captor
    private ArgumentCaptor<SuccessCallback<ApplicationProcessDescriptor>> successCallBackCaptor;
    @Captor
    private ArgumentCaptor<Notification>                                  notificationCaptor;

    private StopAction stopAction;

    @Before
    public void setUp() {
        when(actionFactory.createGetLogs()).thenReturn(logsAction);
        when(presenter.getView()).thenReturn(view);
        when(actionFactory.createLaunch()).thenReturn(launchAction);

        stopAction = new StopAction(service,
                                    appContext,
                                    callbackBuilderProvider,
                                    constant,
                                    notificationManager,
                                    runnerUtil,
                                    actionFactory,
                                    consoleContainer,
                                    presenter);

        when(appContext.getCurrentProject()).thenReturn(project);
        when(runner.getStopUrl()).thenReturn(stopLink);
        when(callbackBuilderProvider.get()).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.unmarshaller(ApplicationProcessDescriptor.class)).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.failure(any(FailureCallback.class))).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.success(Matchers.<SuccessCallback<ApplicationProcessDescriptor>>anyObject())).thenReturn(
                asyncCallbackBuilder);
        when(asyncCallbackBuilder.build()).thenReturn(callback);
        //preparing project data
        when(project.getProjectDescription()).thenReturn(projectDescriptor);
        when(projectDescriptor.getPath()).thenReturn(PATH_TO_PROJECT);
        when(projectDescriptor.getName()).thenReturn(PROJECT_NAME);
        when(constant.applicationStopped(PROJECT_NAME)).thenReturn(MESSAGE);
    }

    @Test
    public void shouldPerformWhenCurrentProjectIsNull() {
        reset(launchAction, actionFactory, presenter);
        when(appContext.getCurrentProject()).thenReturn(null);

        stopAction.perform(runner);

        verify(appContext).getCurrentProject();
        verifyNoMoreInteractions(service, appContext, callbackBuilderProvider, constant, notificationManager, runnerUtil,
                                 actionFactory, presenter, runner);
    }

    @Test
    public void shouldFailedPerformWhenStopLinkIsNull() {
        reset(launchAction, actionFactory, presenter);
        when(runner.getStopUrl()).thenReturn(null);
        when(constant.applicationFailed(PROJECT_NAME)).thenReturn(MESSAGE);

        stopAction.perform(runner);

        verify(presenter).setActive();
        verify(runner).getStopUrl();
        verify(runnerUtil).showError(runner, MESSAGE, null);
        verify(runner).setStatus(Runner.Status.STOPPED);
        verify(presenter).update(runner);
        verifyNoMoreInteractions(callbackBuilderProvider);
        verify(service, never()).stop(any(Link.class), Matchers.<AsyncRequestCallback<ApplicationProcessDescriptor>>anyObject());
    }

    @Test
    public void shouldFailedPerform() {
        when(constant.applicationFailed(PROJECT_NAME)).thenReturn(MESSAGE);

        when(runner.getOptions()).thenReturn(runOptions);

        stopAction.perform(runner);

        verify(presenter).setActive();
        verify(runner).getStopUrl();

        verify(asyncCallbackBuilder).failure(failedCallBackCaptor.capture());
        FailureCallback failureCallback = failedCallBackCaptor.getValue();
        failureCallback.onFailure(reason);

        verify(runner).setStatus(Runner.Status.FAILED);
        verify(runner).setProcessDescriptor(null);
        verify(project).setIsRunningEnabled(true);
        verify(project).setProcessDescriptor(null);
        verify(runnerUtil).showError(runner, MESSAGE, reason);

        verify(service).stop(stopLink, callback);
    }

    @Test
    public void shouldSuccessPerformWithStatusRunning() {
        //set status running
        when(runner.getStatus()).thenReturn(Runner.Status.RUNNING);

        when(runner.getOptions()).thenReturn(runOptions);

        stopAction.perform(runner);

        verify(presenter).setActive();
        verify(runner).getStopUrl();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<ApplicationProcessDescriptor> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(descriptor);

        verify(runner).setProcessDescriptor(descriptor);
        verify(project).setIsRunningEnabled(true);
        verify(project).setProcessDescriptor(null);

        verify(runner).getStatus();
        verify(runner).setStatus(Runner.Status.STOPPED);
        verify(consoleContainer).printInfo(runner, MESSAGE);

        verify(notificationManager).showNotification(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();
        assertEquals(notification.getMessage(), MESSAGE);
        assertEquals(notification.getType(), Notification.Type.INFO);

        verify(presenter).update(runner);

        verify(service).stop(stopLink, callback);
    }

    @Test
    public void shouldSuccessPerformWithStatusDone() {
        //set status done
        when(runner.getStatus()).thenReturn(Runner.Status.DONE);

        when(runner.getOptions()).thenReturn(runOptions);

        stopAction.perform(runner);

        verify(presenter).setActive();
        verify(runner).getStopUrl();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<ApplicationProcessDescriptor> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(descriptor);

        verify(runner).setProcessDescriptor(descriptor);
        verify(project).setIsRunningEnabled(true);
        verify(project).setProcessDescriptor(null);

        verify(runner).getStatus();
        verify(runner).setStatus(Runner.Status.STOPPED);
        verify(consoleContainer).printInfo(runner, MESSAGE);

        verify(notificationManager).showNotification(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();
        assertEquals(notification.getMessage(), MESSAGE);
        assertEquals(notification.getType(), Notification.Type.INFO);

        verify(presenter).update(runner);

        verify(service).stop(stopLink, callback);
    }

    @Test
    public void shouldSuccessPerformWithStatusNotRunningOrDone() {
        //set status not running or done
        when(runner.getStatus()).thenReturn(Runner.Status.TIMEOUT);

        when(runner.getOptions()).thenReturn(runOptions);

        stopAction.perform(runner);

        verify(presenter).setActive();
        verify(runner).getStopUrl();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<ApplicationProcessDescriptor> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(descriptor);

        verify(runner).setProcessDescriptor(descriptor);
        verify(project).setIsRunningEnabled(true);
        verify(project).setProcessDescriptor(null);

        verify(runner).getStatus();
        verify(runner).setStatus(Runner.Status.FAILED);
        verify(logsAction).perform(runner);
        verify(consoleContainer).printError(runner, MESSAGE);

        verify(notificationManager).showNotification(notificationCaptor.capture());
        Notification notification = notificationCaptor.getValue();
        //because type of message ERROR
        assertEquals(notification.getType(), Notification.Type.ERROR);

        verify(presenter).update(runner);

        verify(service).stop(stopLink, callback);
    }
}
