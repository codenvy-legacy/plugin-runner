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
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.LaunchAction;
import com.codenvy.ide.ext.runner.client.util.RunnerUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;

/**
 * This action executes a request on the server side for running a runner. Then it adds handlers for listening WebSocket messages from
 * different events from the server.
 *
 * @author Roman Nikitenko
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class RunAction extends AbstractRunnerAction {

    private final RunnerServiceClient                                          service;
    private final AppContext                                                   appContext;
    private final RunnerLocalizationConstant                                   locale;
    private final RunnerManagerPresenter                                       presenter;
    private final Provider<AsyncCallbackBuilder<ApplicationProcessDescriptor>> callbackBuilderProvider;
    private final RunnerUtil                                                   runnerUtil;
    private final LaunchAction                                                 launchAction;

    @Inject
    public RunAction(RunnerServiceClient service,
                     AppContext appContext,
                     RunnerLocalizationConstant locale,
                     RunnerManagerPresenter presenter,
                     Provider<AsyncCallbackBuilder<ApplicationProcessDescriptor>> callbackBuilderProvider,
                     RunnerUtil runnerUtil,
                     RunnerActionFactory actionFactory) {
        this.service = service;
        this.appContext = appContext;
        this.locale = locale;
        this.presenter = presenter;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.runnerUtil = runnerUtil;
        this.launchAction = actionFactory.createLaunch();

        addAction(launchAction);
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        final CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        presenter.setActive();

        AsyncRequestCallback<ApplicationProcessDescriptor> callback = callbackBuilderProvider
                .get()
                .unmarshaller(ApplicationProcessDescriptor.class)
                .success(new SuccessCallback<ApplicationProcessDescriptor>() {
                    @Override
                    public void onSuccess(ApplicationProcessDescriptor descriptor) {
                        runner.setProcessDescriptor(descriptor);
                        // TODO it seems it isn't logical to set descriptor into project
                        project.setProcessDescriptor(descriptor);

                        launchAction.perform(runner);
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        runnerUtil.showError(runner, locale.startApplicationFailed(project.getProjectDescription().getName()), reason);
                    }
                })
                .build();

        service.run(project.getProjectDescription().getPath(), runner.getOptions(), callback);
    }
}