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

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.actions.ChooseRunnerAction;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.tabs.templates.TemplatesContainer;
import com.codenvy.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class GetSystemEnvironmentsActionTest {
    private static final String MESSAGE = "some message";

    //constructor variables
    @Mock
    private Provider<TemplatesContainer>                          templatesContainerProvider;
    @Mock
    private RunnerServiceClient                                   runnerService;
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
    private AsyncCallbackBuilder<RunnerEnvironmentTree>            asyncCallbackBuilder;
    @Mock
    private AsyncRequestCallback<RunnerEnvironmentTree>            asyncRequestCallback;
    //project variables
    @Mock
    private ProjectDescriptor                                      projectDescriptor;
    //captors
    @Captor
    private ArgumentCaptor<FailureCallback>                        failedCallBackCaptor;
    @Captor
    private ArgumentCaptor<SuccessCallback<RunnerEnvironmentTree>> successCallBackCaptor;
    //run variables
    @Mock
    private List<RunnerEnvironmentLeaf>                            leaves;
    @Mock
    private List<RunnerEnvironment>                                environments;
    @Mock
    private TemplatesContainer                                     templatesContainer;
    @Mock
    private RunnerEnvironmentTree                                  tree;

    @InjectMocks
    private GetSystemEnvironmentsAction action;

    @Before
    public void setUp() {
        action = new GetSystemEnvironmentsAction(runnerService,
                                                 notificationManager,
                                                 callbackBuilderProvider,
                                                 locale,
                                                 environmentUtil,
                                                 chooseRunnerAction,
                                                 templatesContainerProvider);
        //preparing callbacks for server
        when(templatesContainerProvider.get()).thenReturn(templatesContainer);
        when(environmentUtil.getAllEnvironments(tree)).thenReturn(leaves);
        when(environmentUtil.getEnvironmentsFromNodes(leaves)).thenReturn(environments);
        when(callbackBuilderProvider.get()).thenReturn(asyncCallbackBuilder).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.unmarshaller(RunnerEnvironmentTree.class)).thenReturn(asyncCallbackBuilder)
                                                                            .thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.failure(any(FailureCallback.class))).thenReturn(asyncCallbackBuilder).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.success(Matchers.<SuccessCallback<RunnerEnvironmentTree>>anyObject()))
                .thenReturn(asyncCallbackBuilder).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.build()).thenReturn(asyncRequestCallback).thenReturn(asyncRequestCallback);

        when(locale.customRunnerGetEnvironmentFailed()).thenReturn(MESSAGE);
    }

    @Test
    public void shouldSuccessPerformWhenEnvironmentTreeIsNull() {
        action.perform();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(null);

        verify(runnerService).getRunners(asyncRequestCallback);
    }

    @Test
    public void shouldFailurePerformWhenEnvironmentTreeIsNull() {
        action.perform();

        verify(asyncCallbackBuilder).failure(failedCallBackCaptor.capture());
        FailureCallback failureCallback = failedCallBackCaptor.getValue();
        failureCallback.onFailure(reason);

        verify(notificationManager).showError(MESSAGE);
        verify(runnerService).getRunners(asyncRequestCallback);
    }

    @Test
    public void shouldSuccessPerformWhenEnvironmentTreeIsNotNull() throws Exception {
        //launch perform first time for set environmentTree not null
        action.perform();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(tree);

        action.perform();

        verify(asyncCallbackBuilder, times(2)).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback2 = successCallBackCaptor.getValue();
        successCallback2.onSuccess(tree);

        verify(runnerService).getRunners(asyncRequestCallback);

        verify(templatesContainer, times(3)).addButton(tree);
        verify(templatesContainer, times(3)).addEnvironments(environments, SYSTEM);
        verify(chooseRunnerAction, times(3)).addSystemRunners(environments);
    }

    @Test
    public void shouldFailurePerformWhenEnvironmentTreeIsNotNull() {
        //launch perform first time for set environmentTree not null
        action.perform();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(tree);

        action.perform();

        verify(asyncCallbackBuilder, times(2)).failure(failedCallBackCaptor.capture());
        FailureCallback failureCallback = failedCallBackCaptor.getValue();
        failureCallback.onFailure(reason);

        verify(notificationManager).showError(MESSAGE);
    }

}