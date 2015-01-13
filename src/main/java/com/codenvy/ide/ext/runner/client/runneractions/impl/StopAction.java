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
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.util.RunnerUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.STOPPED;

/**
 * Action for stopping current runner.
 *
 * @author Valeriy Svydenko
 */
public class StopAction extends AbstractRunnerAction {
    private final RunnerServiceClient                                          service;
    private final AppContext                                                   appContext;
    private final Provider<AsyncCallbackBuilder<ApplicationProcessDescriptor>> callbackBuilderProvider;
    private final RunnerLocalizationConstant                                   constant;
    private final NotificationManager                                          notificationManager;
    private final RunnerUtil                                                   runnerUtil;
    private final GetLogsAction                                                logsAction;

    private CurrentProject         project;
    private Runner                 runner;
    private RunnerManagerView      view;
    private RunnerManagerPresenter presenter;

    @Inject
    public StopAction(RunnerServiceClient service,
                      AppContext appContext,
                      Provider<AsyncCallbackBuilder<ApplicationProcessDescriptor>> callbackBuilderProvider,
                      RunnerLocalizationConstant constant,
                      NotificationManager notificationManager,
                      RunnerUtil runnerUtil,
                      GetLogsAction logsAction,
                      RunnerManagerPresenter runnerManagerPresenter) {
        this.service = service;
        this.appContext = appContext;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.runnerUtil = runnerUtil;
        this.logsAction = logsAction;

        presenter = runnerManagerPresenter;
        view = runnerManagerPresenter.getView();

        addAction(logsAction);
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
            runnerUtil.showError(runner, constant.applicationFailed(project.getProjectDescription().getName()), null);
            return;
        }

        AsyncRequestCallback<ApplicationProcessDescriptor> callback = callbackBuilderProvider
                .get()
                .unmarshaller(ApplicationProcessDescriptor.class)
                .success(new SuccessCallback<ApplicationProcessDescriptor>() {
                    @Override
                    public void onSuccess(ApplicationProcessDescriptor result) {
                        processStoppedMessage();
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        runner.setAppRunningStatus(false);
                        runner.setProcessDescriptor(null);

                        project.setIsRunningEnabled(true);
                        project.setProcessDescriptor(null);

                        runnerUtil.showError(runner,
                                             constant.applicationFailed(project.getProjectDescription().getName()),
                                             reason);
                    }
                })
                .build();

        service.stop(stopLink, callback);
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

}