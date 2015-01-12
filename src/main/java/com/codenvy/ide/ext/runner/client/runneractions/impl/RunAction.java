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
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackFactory;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.LaunchAction;
import com.codenvy.ide.ext.runner.client.util.RunnerUtil;
import com.google.inject.Inject;

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

    private final RunnerServiceClient        service;
    private final AppContext                 appContext;
    private final AsyncCallbackFactory       asyncCallbackFactory;
    private final RunnerLocalizationConstant locale;
    private final RunnerManagerPresenter     presenter;
    private final RunnerUtil                 runnerUtil;
    private final LaunchAction               launchAction;

    @Inject
    public RunAction(RunnerServiceClient service,
                     AppContext appContext,
                     AsyncCallbackFactory asyncCallbackFactory,
                     RunnerLocalizationConstant locale,
                     RunnerManagerPresenter presenter,
                     RunnerUtil runnerUtil,
                     LaunchAction launchAction) {
        this.service = service;
        this.appContext = appContext;
        this.asyncCallbackFactory = asyncCallbackFactory;
        this.locale = locale;
        this.presenter = presenter;
        this.runnerUtil = runnerUtil;
        this.launchAction = launchAction;

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

        service.run(project.getProjectDescription().getPath(), runner.getOptions(),
                    asyncCallbackFactory
                            .build(ApplicationProcessDescriptor.class,
                                   new SuccessCallback<ApplicationProcessDescriptor>() {
                                       @Override
                                       public void onSuccess(ApplicationProcessDescriptor descriptor) {
                                           runner.setProcessDescriptor(descriptor);
                                           // TODO it seems it isn't logical to set descriptor into project
                                           project.setProcessDescriptor(descriptor);

                                           launchAction.perform(runner);
                                       }
                                   }, new FailureCallback() {
                                        @Override
                                        public void onFailure(@Nonnull Throwable reason) {
                                            runnerUtil.showError(runner,
                                                                 locale.startApplicationFailed(project.getProjectDescription().getName()),
                                                                 reason);
                                        }
                                    }));
    }
}