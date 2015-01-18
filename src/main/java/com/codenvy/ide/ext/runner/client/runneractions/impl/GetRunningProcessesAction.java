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

import com.codenvy.api.runner.ApplicationStatus;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.LaunchAction;
import com.codenvy.ide.ext.runner.client.util.WebSocketUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import javax.annotation.Nonnull;

import static com.codenvy.api.runner.ApplicationStatus.NEW;
import static com.codenvy.api.runner.ApplicationStatus.RUNNING;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status;

/**
 * This action executes a request on the server side for getting runner processes by project name.
 *
 * @author Valeriy Svydenko
 * @author Andrey Plotnikov
 */
public class GetRunningProcessesAction extends AbstractRunnerAction {

    private static final String PROCESS_STARTED_CHANNEL = "runner:process_started:";

    private final NotificationManager                                                 notificationManager;
    private final RunnerServiceClient                                                 service;
    private final DtoUnmarshallerFactory                                              dtoUnmarshallerFactory;
    private final AppContext                                                          appContext;
    private final RunnerLocalizationConstant                                          locale;
    private final GetLogsAction                                                       logsAction;
    private final Provider<AsyncCallbackBuilder<Array<ApplicationProcessDescriptor>>> callbackBuilderProvider;
    private final LaunchAction                                                        launchAction;
    private final WebSocketUtil                                                       webSocketUtil;
    private final String                                                              workspaceId;

    private String                                            channel;
    private SubscriptionHandler<ApplicationProcessDescriptor> processStartedHandler;
    private Runner                                            runner;
    private CurrentProject                                    project;

    @Inject
    public GetRunningProcessesAction(NotificationManager notificationManager,
                                     RunnerServiceClient service,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     AppContext appContext,
                                     RunnerLocalizationConstant locale,
                                     Provider<AsyncCallbackBuilder<Array<ApplicationProcessDescriptor>>> callbackBuilderProvider,
                                     WebSocketUtil webSocketUtil,
                                     RunnerActionFactory actionFactory,
                                     @Named("workspaceId") String workspaceId) {
        this.notificationManager = notificationManager;
        this.service = service;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.appContext = appContext;
        this.locale = locale;
        this.logsAction = actionFactory.createGetLogs();
        this.launchAction = actionFactory.createLaunch();
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.webSocketUtil = webSocketUtil;
        this.workspaceId = workspaceId;

        addAction(logsAction);
        addAction(launchAction);
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        this.runner = runner;

        project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        startCheckingNewProcesses();

        AsyncRequestCallback<Array<ApplicationProcessDescriptor>> callback = callbackBuilderProvider
                .get()
                .unmarshaller(dtoUnmarshallerFactory.newArrayUnmarshaller(ApplicationProcessDescriptor.class))
                .success(new SuccessCallback<Array<ApplicationProcessDescriptor>>() {
                    @Override
                    public void onSuccess(Array<ApplicationProcessDescriptor> result) {
                        for (ApplicationProcessDescriptor processDescriptor : result.asIterable()) {
                            if (isNewOrRunningProcess(processDescriptor)) {
                                prepareRunnerWithRunningApp(processDescriptor);

                            }
                        }
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        Log.error(GetRunningProcessesAction.class, reason);
                    }
                })
                .build();

        service.getRunningProcesses(project.getProjectDescription().getPath(), callback);
    }

    private boolean isNewOrRunningProcess(@Nonnull ApplicationProcessDescriptor processDescriptor) {
        ApplicationStatus status = processDescriptor.getStatus();
        return status == NEW || status == RUNNING;
    }

    private void startCheckingNewProcesses() {
        processStartedHandler = new SubscriptionHandler<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class)) {
            @Override
            protected void onMessageReceived(ApplicationProcessDescriptor processDescriptor) {
                if (!runner.isAnyAppLaunched() && isNewOrRunningProcess(processDescriptor)) {
                    prepareRunnerWithRunningApp(processDescriptor);
                }
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                Log.error(GetRunningProcessesAction.class, exception);
            }
        };

        channel = PROCESS_STARTED_CHANNEL + workspaceId + ':' + project.getProjectDescription().getPath();
        webSocketUtil.subscribeHandler(channel, processStartedHandler);
    }

    private void prepareRunnerWithRunningApp(@Nonnull ApplicationProcessDescriptor processDescriptor) {
        runner.setAliveStatus(true); // set true here because we don't get information
        runner.setAppRunningStatus(true); // about app health in case we open already run app
        runner.setAppLaunchStatus(true);
        runner.setStatus(Status.RUNNING);

        runner.setProcessDescriptor(processDescriptor);
        // TODO it seems it isn't logical to set descriptor into project
        project.setProcessDescriptor(processDescriptor);

        launchAction.perform(runner);
        //TODO  isUserAction parameter is false
        logsAction.perform(runner);

        Notification notification = new Notification(locale.projectRunningNow(project.getProjectDescription().getName()), INFO, true);
        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        if (channel == null || processStartedHandler == null) {
            return;
        }

        webSocketUtil.unSubscribeHandler(channel, processStartedHandler);

        super.stop();

        channel = null;
        processStartedHandler = null;
    }

}