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
package com.codenvy.ide.ext.runner.client.runneractions.impl.run;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackFactory;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.inject.factories.HandlerFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.impl.AbstractAppLaunchAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetLogsAction;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.MessageBus;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

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
    private final RunnerServiceClient        service;
    private final AsyncCallbackFactory       asyncCallbackFactory;
    private final RunnerLocalizationConstant locale;
    private final RunnerManagerPresenter     runnerManagerPresenter;

    @Inject
    public RunAction(RunnerServiceClient service,
                     AppContext appContext,
                     AsyncCallbackFactory asyncCallbackFactory,
                     DtoFactory dtoFactory,
                     RunnerLocalizationConstant locale,
                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                     MessageBus messageBus,
                     NotificationManager notificationManager,
                     RunnerManagerPresenter runnerManagerPresenter,
                     GetLogsAction logsAction,
                     HandlerFactory handlerFactory,
                     EventBus eventBus) {

        super(notificationManager,
              runnerManagerPresenter,
              locale,
              handlerFactory,
              messageBus,
              logsAction,
              dtoUnmarshallerFactory,
              appContext,
              dtoFactory,
              eventBus);

        this.service = service;
        this.asyncCallbackFactory = asyncCallbackFactory;
        this.locale = locale;
        this.runnerManagerPresenter = runnerManagerPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull Runner runner) {
        super.perform(runner);

        if (project == null) {
            return;
        }

        runnerManagerPresenter.setActive();

        service.run(project.getProjectDescription().getPath(), runner.getOptions(),
                    asyncCallbackFactory
                            .build(ApplicationProcessDescriptor.class,
                                   new SuccessCallback<ApplicationProcessDescriptor>() {
                                       @Override
                                       public void onSuccess(ApplicationProcessDescriptor descriptor) {
                                           onAppLaunched(descriptor);
                                       }
                                   },
                                   new FailureCallback() {
                                       @Override
                                       public void onFailure(@Nonnull Throwable reason) {
                                           onFail(locale.startApplicationFailed(project.getProjectDescription().getName()), reason);
                                       }
                                   }));
    }

}