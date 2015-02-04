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
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType;
import com.codenvy.ide.ext.runner.client.widgets.templates.TemplatesWidget;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.CPP;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.GO;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.JAVA;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.JAVASCRIPT;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.PHP;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.PYTHON;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.RUBY;

/**
 * This action executes a request on the server side for getting environments of project and runner. These environments are used
 * for displaying on special Templates widget {@link com.codenvy.ide.ext.runner.client.widgets.templates.TemplatesWidget}
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@Singleton
public class GetEnvironmentsAction extends AbstractRunnerAction {

    private final TemplatesWidget                                       templatesWidget;
    private final AppContext                                            appContext;
    private final ProjectServiceClient                                  projectService;
    private final RunnerServiceClient                                   runnerService;
    private final NotificationManager                                   notificationManager;
    private final Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider;
    private final RunnerLocalizationConstant                            locale;

    private CurrentProject currentProject;

    @Inject
    public GetEnvironmentsAction(TemplatesWidget templatesWidget,
                                 AppContext appContext,
                                 ProjectServiceClient projectService,
                                 RunnerServiceClient runnerService,
                                 NotificationManager notificationManager,
                                 Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider,
                                 RunnerLocalizationConstant locale) {
        this.templatesWidget = templatesWidget;
        this.appContext = appContext;
        this.projectService = projectService;
        this.runnerService = runnerService;
        this.notificationManager = notificationManager;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public void perform() {
        currentProject = appContext.getCurrentProject();

        if (currentProject == null) {
            return;
        }

        getProjectEnvironments();

        getSystemEnvironments();
    }

    public void getLanguageEnvironments(@Nonnull final EnvironmentType environmentType) {
        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {

                        getEnvironmentsByType(environmentType, result);
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

    private void getEnvironmentsByType(@Nonnull EnvironmentType type, @Nonnull RunnerEnvironmentTree tree) {
        switch (type) {
            case JAVA:
                getEnvironments(tree.getNode(JAVA.toString()));
                break;
            case CPP:
                getEnvironments(tree.getNode(CPP.toString()));
                break;
            case GO:
                getEnvironments(tree.getNode(GO.toString()));
                break;
            case JAVASCRIPT:
                getEnvironments(tree.getNode(JAVASCRIPT.toString()));
                break;
            case PHP:
                getEnvironments(tree.getNode(PHP.toString()));
                break;
            case PYTHON:
                getEnvironments(tree.getNode(PYTHON.toString()));
                break;
            case RUBY:
                getEnvironments(tree.getNode(RUBY.toString()));
                break;

            default:
        }
    }

    public void getProjectEnvironments() {
        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {
                        for (RunnerEnvironmentLeaf environment : result.getLeaves()) {
                            templatesWidget.addEnvironment(environment.getEnvironment());
                        }
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        notificationManager.showError(locale.customRunnerGetEnvironmentFailed());
                    }
                })
                .build();

        String path = currentProject.getProjectDescription().getPath();

        projectService.getRunnerEnvironments(path, callback);
    }

    public void getSystemEnvironments() {
        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {
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

        runnerService.getRunners(callback);
    }

    private void getEnvironments(@Nonnull RunnerEnvironmentTree environmentTree) {
        if (!environmentTree.getLeaves().isEmpty()) {
            for (RunnerEnvironmentLeaf environment : environmentTree.getLeaves()) {
                templatesWidget.addEnvironment(environment.getEnvironment());
            }
        }

        for (RunnerEnvironmentTree tree : environmentTree.getNodes()) {
            if (tree.getNodes().isEmpty()) {
                for (RunnerEnvironmentLeaf environment : tree.getLeaves()) {
                    templatesWidget.addEnvironment(environment.getEnvironment());
                }
            } else {
                getEnvironments(tree);
            }
        }
    }

}