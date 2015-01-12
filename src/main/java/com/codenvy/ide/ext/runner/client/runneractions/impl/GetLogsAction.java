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
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackFactory;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.util.RunnerUtil;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * Action for getting  logs from current runner.
 *
 * @author Valeriy Svydenko
 */
public class GetLogsAction extends AbstractRunnerAction {

    private final RunnerServiceClient        service;
    private final AppContext                 appContext;
    private final AsyncCallbackFactory       asyncCallbackFactory;
    private final RunnerLocalizationConstant constant;
    private final RunnerManagerView          view;
    private final RunnerUtil                 runnerUtil;
    private final RunnerManagerPresenter     presenter;

    @Inject
    public GetLogsAction(RunnerServiceClient service,
                         AppContext appContext,
                         AsyncCallbackFactory asyncCallbackFactory,
                         RunnerLocalizationConstant constant,
                         RunnerUtil runnerUtil,
                         RunnerManagerPresenter runnerManagerPresenter) {
        this.service = service;
        this.appContext = appContext;
        this.asyncCallbackFactory = asyncCallbackFactory;
        this.constant = constant;
        this.runnerUtil = runnerUtil;
        this.presenter = runnerManagerPresenter;
        this.view = runnerManagerPresenter.getView();
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        final Link viewLogsLink = runner.getLogUrl();
        if (viewLogsLink == null) {
            runnerUtil.showError(runner, constant.applicationLogsFailed(), null);
            return;
        }

        presenter.setActive();

        service.getLogs(viewLogsLink, asyncCallbackFactory
                .build(new StringUnmarshaller(), new SuccessCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        view.printMessage(runner, result);
                    }
                }, new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        runnerUtil.showError(runner, constant.applicationLogsFailed(), reason);
                    }
                }));
    }
}