/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.runner.client.runneractions.impl;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackFactory;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.inject.factories.HandlerFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

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
public class GetRunningProcessesAction extends AbstractAppLaunchAction {

    private final RunnerServiceClient  service;
    private final AsyncCallbackFactory asyncCallbackFactory;
    private final String               workspaceId;

    @Inject
    public GetRunningProcessesAction(NotificationManager notificationManager,
                                     RunnerServiceClient service,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     AppContext appContext,
                                     RunnerManagerPresenter presenter,
                                     RunnerLocalizationConstant locale,
                                     HandlerFactory handlerFactory,
                                     MessageBus messageBus,
                                     GetLogsAction logsAction,
                                     AsyncCallbackFactory asyncCallbackFactory,
                                     DtoFactory dtoFactory,
                                     EventBus eventBus,
                                     @Named("workspaceId") String workspaceId) {
        super(notificationManager,
              presenter,
              locale,
              handlerFactory,
              messageBus,
              logsAction,
              dtoUnmarshallerFactory,
              appContext,
              dtoFactory,
              eventBus);

        this.service = service;
        this.workspaceId = workspaceId;
        this.asyncCallbackFactory = asyncCallbackFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        super.perform(runner);

        if (project == null) {
            return;
        }

        startCheckingNewProcesses();

        service.getRunningProcesses(project.getProjectDescription().getPath(), asyncCallbackFactory
                .build(dtoUnmarshallerFactory.newArrayUnmarshaller(ApplicationProcessDescriptor.class),
                       new SuccessCallback<Array<ApplicationProcessDescriptor>>() {
                           @Override
                           public void onSuccess(Array<ApplicationProcessDescriptor> result) {
                               for (ApplicationProcessDescriptor processDescriptor : result.asIterable()) {
                                   if (processDescriptor.getStatus() == NEW || processDescriptor.getStatus() == RUNNING) {

                                       prepareRunnerWithRunningApp(processDescriptor);

                                   }
                               }
                           }
                       },
                       new FailureCallback() {
                           @Override
                           public void onFailure(@Nonnull Throwable reason) {
                               Log.error(GetRunningProcessesAction.class, reason);
                           }
                       }));
    }

    private void startCheckingNewProcesses() {
        SubscriptionHandler<ApplicationProcessDescriptor> processStartedHandler =
                new SubscriptionHandler<ApplicationProcessDescriptor>(
                        dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class)) {
                    @Override
                    protected void onMessageReceived(ApplicationProcessDescriptor processDescriptor) {
                        if (!runner.isAnyAppLaunched() &&
                            (processDescriptor.getStatus() == NEW || processDescriptor.getStatus() == RUNNING)) {

                            prepareRunnerWithRunningApp(processDescriptor);

                        }
                    }

                    @Override
                    protected void onErrorReceived(Throwable exception) {
                        Log.error(GetRunningProcessesAction.class, exception);
                    }
                };

        String channel = PROCESS_STARTED_CHANNEL + workspaceId + ':' + project.getProjectDescription().getPath();

        try {
            messageBus.subscribe(channel, processStartedHandler);
        } catch (WebSocketException e) {
            Log.error(GetRunningProcessesAction.class, e);
        }
    }

    private void prepareRunnerWithRunningApp(@Nonnull ApplicationProcessDescriptor processDescriptor) {
        runner.setAliveStatus(true); // set true here because we don't get information
        runner.setAppRunningStatus(true); // about app health in case we open already run app
        runner.setAppLaunchStatus(true);
        runner.setStatus(Status.RUNNING);

        onAppLaunched(processDescriptor);

        //TODO  isUserAction parameter is false
        logsAction.perform(runner);

        Notification notification = new Notification(locale.projectRunningNow(project.getProjectDescription().getName()), INFO, true);
        notificationManager.showNotification(notification);
    }

}