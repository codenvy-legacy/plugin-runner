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
package com.codenvy.ide.ext.runner.client.runneractions.impl.launch;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackFactory;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.ActionFactory;
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
public class RunAction extends AbstractAppLaunchAction {

    private final RunnerServiceClient    service;
    private final AsyncCallbackFactory   asyncCallbackFactory;
    private final RunnerManagerPresenter presenter;
    private final RunnerUtil             runnerUtil;

    @Inject
    public RunAction(RunnerServiceClient service,
                     AppContext appContext,
                     AsyncCallbackFactory asyncCallbackFactory,
                     RunnerLocalizationConstant locale,
                     NotificationManager notificationManager,
                     RunnerManagerPresenter presenter,
                     ActionFactory actionFactory,
                     RunnerActionFactory runnerActionFactory,
                     RunnerUtil runnerUtil) {

        super(notificationManager,
              presenter,
              locale,
              appContext,
              actionFactory,
              runnerActionFactory);

        this.service = service;
        this.asyncCallbackFactory = asyncCallbackFactory;
        this.presenter = presenter;
        this.runnerUtil = runnerUtil;
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        super.perform(runner);

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
                                           onAppLaunched(descriptor);
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