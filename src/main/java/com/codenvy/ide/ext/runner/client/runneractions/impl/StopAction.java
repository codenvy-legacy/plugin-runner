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

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackFactory;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.RunnerAction;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.STOPPED;

/**
 * Action for stopping current runner.
 *
 * @author Valeriy Svydenko
 */
public class StopAction implements RunnerAction {
    private final RunnerServiceClient        service;
    private final AppContext                 appContext;
    private final AsyncCallbackFactory       asyncCallbackFactory;
    private final RunnerLocalizationConstant constant;
    private final NotificationManager        notificationManager;
    private final GetLogsAction              logsAction;

    private CurrentProject         project;
    private Runner                 runner;
    private RunnerManagerView      view;
    private RunnerManagerPresenter presenter;

    @Inject
    public StopAction(RunnerServiceClient service,
                      AppContext appContext,
                      AsyncCallbackFactory asyncCallbackFactory,
                      RunnerLocalizationConstant constant,
                      NotificationManager notificationManager,
                      GetLogsAction logsAction,
                      RunnerManagerPresenter runnerManagerPresenter) {
        this.service = service;
        this.appContext = appContext;
        this.asyncCallbackFactory = asyncCallbackFactory;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.logsAction = logsAction;

        presenter = runnerManagerPresenter;
        view = runnerManagerPresenter.getView();
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        this.runner = runner;

        project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        presenter.setActive();

        Link stopLink = runner.getStopUrl();
        if (stopLink == null) {
            onFail(constant.applicationFailed(project.getProjectDescription().getName()), null);
            return;
        }

        service.stop(stopLink,
                     asyncCallbackFactory
                             .build(ApplicationProcessDescriptor.class,
                                    new SuccessCallback<ApplicationProcessDescriptor>() {
                                        @Override
                                        public void onSuccess(ApplicationProcessDescriptor result) {
                                            processStoppedMessage();
                                        }
                                    },
                                    new FailureCallback() {
                                        @Override
                                        public void onFailure(@Nonnull Throwable reason) {
                                            runner.setAppRunningStatus(false);
                                            runner.setProcessDescriptor(null);
                                            project.setIsRunningEnabled(true);
                                            project.setProcessDescriptor(null);
                                            onFail(constant.applicationFailed(project.getProjectDescription().getName()), reason);
                                        }
                                    }));

    }

    private void processStoppedMessage() {
        runner.setAppRunningStatus(false);
        runner.setAppLaunchStatus(false);
        runner.setAliveStatus(false);

        project.setIsRunningEnabled(true);
        project.setProcessDescriptor(null);

        String projectName = project.getProjectDescription().getName();
        String message = constant.applicationStopped(projectName);

        Notification.Type notificationType;

        if (runner.isStarted()) {
            notificationType = INFO;

            runner.setStatus(STOPPED);
            view.printInfo(runner, message);
        } else {
            // this mean that application has failed to start
            notificationType = ERROR;

            runner.setStatus(FAILED);
            logsAction.perform(runner);
            view.printError(runner, message);
        }

        Notification notification = new Notification(message, notificationType);
        notificationManager.showNotification(notification);

        presenter.update(runner);
    }

    private void onFail(@Nonnull String message, @Nullable Throwable exception) {
        notificationManager.showError(message);

        if (exception != null && exception.getMessage() != null) {
            view.printError(runner, message + ": " + exception.getMessage());
        } else {
            view.printError(runner, message);
        }

        runner.setAppLaunchStatus(false);
        runner.setStatus(FAILED);

        presenter.update(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        //do nothing
    }

}