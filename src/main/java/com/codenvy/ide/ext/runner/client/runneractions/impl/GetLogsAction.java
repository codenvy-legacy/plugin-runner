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
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;

/**
 * Action for getting  logs from current runner.
 *
 * @author Valeriy Svydenko
 */
public class GetLogsAction implements RunnerAction {

    private final RunnerServiceClient        service;
    private final AppContext                 appContext;
    private final AsyncCallbackFactory       asyncCallbackFactory;
    private final RunnerLocalizationConstant constant;
    private final NotificationManager        notificationManager;
    private final Notification               notification;
    private final RunnerManagerView          view;
    private final RunnerManagerPresenter     presenter;

    private Runner runner;

    @Inject
    public GetLogsAction(RunnerServiceClient service,
                         AppContext appContext,
                         AsyncCallbackFactory asyncCallbackFactory,
                         RunnerLocalizationConstant constant,
                         NotificationManager notificationManager,
                         RunnerManagerPresenter runnerManagerPresenter) {
        this.service = service;
        this.appContext = appContext;
        this.asyncCallbackFactory = asyncCallbackFactory;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.presenter = runnerManagerPresenter;

        view = runnerManagerPresenter.getView();
        notification = new Notification("", ERROR);
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        this.runner = runner;

        CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        final Link viewLogsLink = runner.getLogUrl();
        if (viewLogsLink == null) {
            onFail(constant.applicationLogsFailed(), null);
            return;
        }

        presenter.setActive();

        service.getLogs(viewLogsLink, asyncCallbackFactory
                .build(new StringUnmarshaller(), new SuccessCallback<String>() {
                           @Override
                           public void onSuccess(String result) {
                               view.printMessage(runner, result);
                           }
                       },
                       new FailureCallback() {
                           @Override
                           public void onFailure(@Nonnull Throwable reason) {
                               onFail(constant.applicationLogsFailed(), reason);
                           }
                       }));
    }

    private void onFail(@Nonnull String message, @Nullable Throwable exception) {
        notification.setMessage(message);
        notificationManager.showNotification(notification);

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