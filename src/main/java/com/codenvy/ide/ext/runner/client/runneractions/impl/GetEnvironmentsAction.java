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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;

/**
 * This action executes a request on the server side for getting environments of project and runner. These environments are used
 * for displaying on special Templates widget {@link com.codenvy.ide.ext.runner.client.widgets.templates.TemplatesWidget}
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class GetEnvironmentsAction extends AbstractRunnerAction {

    private final RunnerManagerView                                     view;
    private final AppContext                                            appContext;
    private final ProjectServiceClient                                  projectService;
    private final RunnerServiceClient                                   runnerService;
    private final NotificationManager                                   notificationManager;
    private final Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider;
    private final RunnerLocalizationConstant                            locale;
    private final RunnerEnvironmentTree                                 tree;

    @Inject
    public GetEnvironmentsAction(RunnerManagerView view,
                                 AppContext appContext,
                                 ProjectServiceClient projectService,
                                 RunnerServiceClient runnerService,
                                 NotificationManager notificationManager,
                                 Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider,
                                 RunnerLocalizationConstant locale,
                                 DtoFactory dtoFactory) {
        this.view = view;
        this.appContext = appContext;
        this.projectService = projectService;
        this.runnerService = runnerService;
        this.notificationManager = notificationManager;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.locale = locale;
        this.tree = dtoFactory.createDto(RunnerEnvironmentTree.class).withDisplayName(locale.actionManagerAvailableEnvironments());
    }

    /** {@inheritDoc} */
    @Override
    public void perform() {
        CurrentProject project = appContext.getCurrentProject();

        if (project == null) {
            return;
        }

        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {

                        if (!result.getLeaves().isEmpty()) {
                            tree.addNode(result);
                        }

                        getSystemEnvironments();
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        notificationManager.showError(locale.customRunnerGetEnvironmentFailed());
                    }
                })
                .build();

        String path = project.getProjectDescription().getPath();

        projectService.getRunnerEnvironments(path, callback);

        //clean environments tree
        tree.setNodes(null);
    }

    private void getSystemEnvironments() {
        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {
                        tree.addNode(result);

                        view.showTemplates(tree);
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        notificationManager.showError(locale.customRunnerGetEnvironmentFailed());
                    }
                })
                .build();

        runnerService.getRunners(callback);
    }

}