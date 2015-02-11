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
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.console.ConsoleContainer;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.util.RunnerUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;

/**
 * Action for getting  logs from current runner.
 *
 * @author Valeriy Svydenko
 */
public class GetLogsAction extends AbstractRunnerAction {

    private final RunnerServiceClient                    service;
    private final AppContext                             appContext;
    private final Provider<AsyncCallbackBuilder<String>> callbackBuilderProvider;
    private final RunnerLocalizationConstant             constant;
    private final RunnerUtil                             runnerUtil;
    private final RunnerManagerPresenter                 presenter;
    private final ConsoleContainer                       consoleContainer;

    @Inject
    public GetLogsAction(RunnerServiceClient service,
                         AppContext appContext,
                         Provider<AsyncCallbackBuilder<String>> callbackBuilderProvider,
                         RunnerLocalizationConstant constant,
                         RunnerUtil runnerUtil,
                         ConsoleContainer consoleContainer,
                         RunnerManagerPresenter runnerManagerPresenter) {
        this.service = service;
        this.appContext = appContext;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.constant = constant;
        this.runnerUtil = runnerUtil;
        this.presenter = runnerManagerPresenter;
        this.consoleContainer = consoleContainer;
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

        AsyncRequestCallback<String> callback = callbackBuilderProvider
                .get()
                .unmarshaller(new StringUnmarshaller())
                .success(new SuccessCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        consoleContainer.print(runner, result);
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        runnerUtil.showError(runner, constant.applicationLogsFailed(), reason);
                    }
                })
                .build();

        service.getLogs(viewLogsLink, callback);
    }
}