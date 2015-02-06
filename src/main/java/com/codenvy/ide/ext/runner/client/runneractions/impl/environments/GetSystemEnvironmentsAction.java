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
package com.codenvy.ide.ext.runner.client.runneractions.impl.environments;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.codenvy.ide.ext.runner.client.widgets.templates.TemplatesWidget;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ext.runner.client.properties.common.Scope.SYSTEM;

/**
 * The class contains business logic to get system environments which are added on templates panel.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class GetSystemEnvironmentsAction extends AbstractRunnerAction {

    private final TemplatesWidget                                       templatesWidget;
    private final RunnerServiceClient                                   runnerService;
    private final NotificationManager                                   notificationManager;
    private final Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider;
    private final RunnerLocalizationConstant                            locale;
    private final GetEnvironmentsUtil                                   environmentUtil;

    private RunnerEnvironmentTree environmentTree;

    @Inject
    public GetSystemEnvironmentsAction(TemplatesWidget templatesWidget,
                                       RunnerServiceClient runnerService,
                                       NotificationManager notificationManager,
                                       Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider,
                                       RunnerLocalizationConstant locale,
                                       GetEnvironmentsUtil environmentUtil) {
        this.templatesWidget = templatesWidget;
        this.runnerService = runnerService;
        this.notificationManager = notificationManager;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.locale = locale;
        this.environmentUtil = environmentUtil;
    }

    /** {@inheritDoc} */
    @Override
    public void perform() {
        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {
                        environmentTree = result;

                        getEnvironments(result);
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        notificationManager.showError(locale.customRunnerGetEnvironmentFailed());
                    }
                })
                .build();

        if (environmentTree == null) {
            runnerService.getRunners(callback);
        } else {
            getEnvironments(environmentTree);
        }
    }

    private void getEnvironments(@Nonnull RunnerEnvironmentTree tree) {
        templatesWidget.clearEnvironmentsPanel();
        templatesWidget.clearTypeButtonsPanel();

        for (RunnerEnvironmentLeaf environment : environmentUtil.getAllEnvironments(tree)) {
            templatesWidget.addEnvironment(environment.getEnvironment(), SYSTEM);
        }

        for (RunnerEnvironmentTree environment : environmentUtil.getAllEnvironments(tree, 1)) {
            templatesWidget.addButton(environment);
        }
    }
}
