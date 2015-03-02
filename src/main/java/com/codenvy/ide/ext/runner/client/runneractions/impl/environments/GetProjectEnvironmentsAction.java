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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.actions.ChooseRunnerAction;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.tabs.templates.TemplatesContainer;
import com.codenvy.ide.ext.runner.client.util.GetEnvironmentsUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.List;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;

/**
 * The class contains business logic to get project environments which are added on templates panel.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@Singleton
public class GetProjectEnvironmentsAction extends AbstractRunnerAction {

    private final Provider<TemplatesContainer>                          templatesPanelProvider;
    private final AppContext                                            appContext;
    private final ProjectServiceClient                                  projectService;
    private final NotificationManager                                   notificationManager;
    private final Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider;
    private final RunnerLocalizationConstant                            locale;
    private final ChooseRunnerAction                                    chooseRunnerAction;
    private final GetEnvironmentsUtil                                   environmentUtil;

    @Inject
    public GetProjectEnvironmentsAction(AppContext appContext,
                                        ProjectServiceClient projectService,
                                        NotificationManager notificationManager,
                                        Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider,
                                        RunnerLocalizationConstant locale,
                                        GetEnvironmentsUtil environmentUtil,
                                        ChooseRunnerAction chooseRunnerAction,
                                        Provider<TemplatesContainer> templatesPanelProvider) {
        this.templatesPanelProvider = templatesPanelProvider;
        this.chooseRunnerAction = chooseRunnerAction;
        this.appContext = appContext;
        this.projectService = projectService;
        this.notificationManager = notificationManager;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.locale = locale;
        this.environmentUtil = environmentUtil;
    }

    /** {@inheritDoc} */
    @Override
    public void perform() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        final ProjectDescriptor descriptor = currentProject.getProjectDescription();

        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {
                        TemplatesContainer panel = templatesPanelProvider.get();

                        List<Environment> projectEnvironments = environmentUtil.getEnvironmentsByProjectType(result,
                                                                                                             descriptor.getType(),
                                                                                                             PROJECT);
                        panel.addEnvironments(projectEnvironments, PROJECT);
                        chooseRunnerAction.addProjectRunners(projectEnvironments);
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        notificationManager.showError(locale.customRunnerGetEnvironmentFailed());
                    }
                })
                .build();

        projectService.getRunnerEnvironments(descriptor.getPath(), callback);
    }
}