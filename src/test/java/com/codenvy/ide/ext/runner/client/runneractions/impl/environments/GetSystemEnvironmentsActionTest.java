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
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.tabs.templates.TemplatesView;
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

import java.util.Arrays;
import java.util.List;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 */
@RunWith(MockitoJUnitRunner.class)
public class GetSystemEnvironmentsActionTest {
    private static final String MESSAGE = "some message";

    //constructor variables
    @Mock
    private TemplatesView                                         templatesView;
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
    private Throwable reason;

    //callbacks for server
    @Mock
    private AsyncCallbackBuilder<RunnerEnvironmentTree> asyncCallbackBuilder;
    @Mock
    private AsyncRequestCallback<RunnerEnvironmentTree> asyncRequestCallback;
    //project variables
    @Mock
    private ProjectDescriptor                           projectDescriptor;
    //captors
    @Captor
    private ArgumentCaptor<FailureCallback>                        failedCallBackCaptor;
    @Captor
    private ArgumentCaptor<SuccessCallback<RunnerEnvironmentTree>> successCallBackCaptor;
    //run variables
    @Mock
    private RunnerEnvironmentTree                                  runnerEnvironmentTree1;
    @Mock
    private RunnerEnvironmentTree                                  runnerEnvironmentTree2;
    @Mock
    private RunnerEnvironmentLeaf                                  runnerEnvironmentLeaf1;
    @Mock
    private RunnerEnvironmentLeaf                                  runnerEnvironmentLeaf2;
    @Mock
    private RunnerEnvironmentLeaf                                  runnerEnvironmentLeaf3;
    @Mock
    private RunnerEnvironmentTree                                  environmentTree;
    @Mock
    private RunnerEnvironment                                      runnerEnvOfLeaf1;
    @Mock
    private RunnerEnvironment                                      runnerEnvOfLeaf2;
    @Mock
    private RunnerEnvironment                                      runnerEnvOfLeaf3;
    @Mock
    private RunnerEnvironment                                      runnerEnvironment1;
    @Mock
    private RunnerEnvironment                                      runnerEnvironment2;
    @Mock
    private RunnerEnvironmentTree                                  tree;

    @InjectMocks
    private GetSystemEnvironmentsAction getSystemEnvironmentsAction;

    @Before
    public void setUp() {
        //preparing callbacks for server
        when(callbackBuilderProvider.get()).thenReturn(asyncCallbackBuilder).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.unmarshaller(RunnerEnvironmentTree.class)).thenReturn(asyncCallbackBuilder)
                                                                            .thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.failure(any(FailureCallback.class))).thenReturn(asyncCallbackBuilder).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.success(Matchers.<SuccessCallback<RunnerEnvironmentTree>>anyObject()))
                .thenReturn(asyncCallbackBuilder).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.build()).thenReturn(asyncRequestCallback).thenReturn(asyncRequestCallback);

        List<RunnerEnvironmentLeaf> runnerEnvironmentLeafs =
                Arrays.asList(runnerEnvironmentLeaf1, runnerEnvironmentLeaf2, runnerEnvironmentLeaf3);
        List<RunnerEnvironmentTree> runnerEnvironmentTrees = Arrays.asList(runnerEnvironmentTree1, runnerEnvironmentTree2);

        when(environmentUtil.getAllEnvironments(tree)).thenReturn(runnerEnvironmentLeafs);
        when(environmentUtil.getAllEnvironments(tree, 1)).thenReturn(runnerEnvironmentTrees);

        when(runnerEnvironmentLeaf1.getEnvironment()).thenReturn(runnerEnvOfLeaf1);
        when(runnerEnvironmentLeaf2.getEnvironment()).thenReturn(runnerEnvOfLeaf2);
        when(runnerEnvironmentLeaf3.getEnvironment()).thenReturn(runnerEnvOfLeaf3);

        when(locale.customRunnerGetEnvironmentFailed()).thenReturn(MESSAGE);
    }

    @Test
    public void shouldSuccessPerformWhenEnvironmentTreeIsNull() {
        getSystemEnvironmentsAction.perform();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(tree);

        verify(templatesView).clearEnvironmentsPanel();
        verify(templatesView).clearTypeButtonsPanel();

        verify(environmentUtil).getAllEnvironments(tree);

        verify(templatesView).addEnvironment(runnerEnvOfLeaf1, SYSTEM);
        verify(templatesView).addEnvironment(runnerEnvOfLeaf2, SYSTEM);
        verify(templatesView).addEnvironment(runnerEnvOfLeaf3, SYSTEM);

        verify(environmentUtil).getAllEnvironments(tree, 1);

        verify(templatesView).addButton(runnerEnvironmentTree1);
        verify(templatesView).addButton(runnerEnvironmentTree1);
        verify(templatesView).addButton(runnerEnvironmentTree1);

        verify(runnerService).getRunners(asyncRequestCallback);
    }

    @Test
    public void shouldFailurePerformWhenEnvironmentTreeIsNull() {
        getSystemEnvironmentsAction.perform();

        verify(asyncCallbackBuilder).failure(failedCallBackCaptor.capture());
        FailureCallback failureCallback = failedCallBackCaptor.getValue();
        failureCallback.onFailure(reason);

        verify(notificationManager).showError(MESSAGE);
        verify(runnerService).getRunners(asyncRequestCallback);
    }

    @Test
    public void shouldSuccessPerformWhenEnvironmentTreeIsNotNull() throws Exception {
        //launch perform first time for set environmentTree not null
        getSystemEnvironmentsAction.perform();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(tree);

        getSystemEnvironmentsAction.perform();

        verify(asyncCallbackBuilder, times(2)).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback2 = successCallBackCaptor.getValue();
        successCallback2.onSuccess(tree);

        verify(templatesView, times(3)).clearEnvironmentsPanel();
        verify(templatesView, times(3)).clearTypeButtonsPanel();

        verify(environmentUtil, times(3)).getAllEnvironments(tree);

        verify(templatesView, times(3)).addEnvironment(runnerEnvOfLeaf1, SYSTEM);
        verify(templatesView, times(3)).addEnvironment(runnerEnvOfLeaf2, SYSTEM);
        verify(templatesView, times(3)).addEnvironment(runnerEnvOfLeaf3, SYSTEM);

        verify(environmentUtil, times(3)).getAllEnvironments(tree, 1);

        verify(templatesView, times(3)).addButton(runnerEnvironmentTree1);
        verify(templatesView, times(3)).addButton(runnerEnvironmentTree1);
        verify(templatesView, times(3)).addButton(runnerEnvironmentTree1);

        verify(runnerService).getRunners(asyncRequestCallback);
    }

    @Test
    public void shouldFailurePerformWhenEnvironmentTreeIsNotNull() {
        //launch perform first time for set environmentTree not null
        getSystemEnvironmentsAction.perform();

        verify(asyncCallbackBuilder).success(successCallBackCaptor.capture());
        SuccessCallback<RunnerEnvironmentTree> successCallback = successCallBackCaptor.getValue();
        successCallback.onSuccess(tree);

        getSystemEnvironmentsAction.perform();

        verify(asyncCallbackBuilder, times(2)).failure(failedCallBackCaptor.capture());
        FailureCallback failureCallback = failedCallBackCaptor.getValue();
        failureCallback.onFailure(reason);

        verify(notificationManager).showError(MESSAGE);

        verify(templatesView, times(2)).clearEnvironmentsPanel();
        verify(templatesView, times(2)).clearTypeButtonsPanel();

        verify(environmentUtil, times(2)).getAllEnvironments(tree);

        verify(templatesView, times(2)).addEnvironment(runnerEnvOfLeaf1, SYSTEM);
        verify(templatesView, times(2)).addEnvironment(runnerEnvOfLeaf2, SYSTEM);
        verify(templatesView, times(2)).addEnvironment(runnerEnvOfLeaf3, SYSTEM);

        verify(environmentUtil, times(2)).getAllEnvironments(tree, 1);

        verify(templatesView, times(2)).addButton(runnerEnvironmentTree1);
        verify(templatesView, times(2)).addButton(runnerEnvironmentTree1);
        verify(templatesView, times(2)).addButton(runnerEnvironmentTree1);
    }

}