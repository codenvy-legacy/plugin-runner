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
package com.codenvy.ide.ext.runner.client.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.api.wizard.Wizard.UpdateDelegate;

/**
 * @author Evgen Vidolob
 * @author Valeriy Svydenko
 */
public class SelectRunnerPagePresenter extends AbstractWizardPage implements SelectRunnerPageView.ActionDelegate {

    private final Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider;
    private final SelectRunnerPageView                                  view;
    private final RunnerServiceClient                                   runnerServiceClient;
    private final ProjectServiceClient                                  service;
    private final DtoFactory                                            dtoFactory;

    /** Create wizard page. */
    @Inject
    public SelectRunnerPagePresenter(SelectRunnerPageView view,
                                     RunnerServiceClient runnerServiceClient,
                                     Provider<AsyncCallbackBuilder<RunnerEnvironmentTree>> callbackBuilderProvider,
                                     ProjectServiceClient projectServiceClient,
                                     DtoFactory dtoFactory) {
        super("Select Runner", null);

        this.view = view;
        this.runnerServiceClient = runnerServiceClient;
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.service = projectServiceClient;
        this.dtoFactory = dtoFactory;

        view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdateDelegate(@Nonnull UpdateDelegate delegate) {
        super.setUpdateDelegate(delegate);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        requestRunnerEnvironments();
    }

    private void requestRunnerEnvironments() {
        ProjectDescriptor projectForUpdate = wizardContext.getData(ProjectWizard.PROJECT_FOR_UPDATE);
        if (projectForUpdate == null) {
            // wizard is opened for new project, so we haven't project-scoped environments
            requestSystemEnvironments();
            return;
        }

        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {
                        if (!result.getLeaves().isEmpty() || !result.getNodes().isEmpty()) {
                            view.addRunner(result);
                        }
                        requestSystemEnvironments();
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable exception) {
                        Log.error(SelectRunnerPagePresenter.class, "Can't get project-scoped runner environments", exception);
                    }
                })
                .build();

        service.getRunnerEnvironments(projectForUpdate.getPath(), callback);
    }

    private void requestSystemEnvironments() {
        AsyncRequestCallback<RunnerEnvironmentTree> callback = callbackBuilderProvider
                .get()
                .unmarshaller(RunnerEnvironmentTree.class)
                .success(new SuccessCallback<RunnerEnvironmentTree>() {
                    @Override
                    public void onSuccess(RunnerEnvironmentTree result) {
                        ProjectTypeDescriptor data = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
                        if (data == null) {
                            return;
                        }

                        String typeCategory = data.getTypeCategory();
                        if (typeCategory != null && !typeCategory.equalsIgnoreCase("blank")) {
                            RunnerEnvironmentTree tree =
                                    dtoFactory.createDto(RunnerEnvironmentTree.class).withDisplayName(result.getDisplayName());
                            tree.addNode(result.getNode(typeCategory.toLowerCase()));
                            view.addRunner(tree);
                        } else {
                            view.addRunner(result);
                        }
                        selectRunner();
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable exception) {
                        Log.error(SelectRunnerPagePresenter.class, "Can't receive runners info", exception);
                    }
                })
                .build();

        runnerServiceClient.getRunners(callback);

    }

    private void selectRunner() {
        ProjectDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT);
        if (descriptor == null) {
            return;
        }

        RunnersDescriptor runners = descriptor.getRunners();
        if (runners == null) {
            return;
        }

        view.selectRunnerEnvironment(runners.getDefault());
        final RunnerConfiguration runnerConfiguration = runners.getConfigs().get(runners.getDefault());
        if (runnerConfiguration != null) {
            view.setRecommendedMemorySize(runnerConfiguration.getRam());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void recommendedMemoryChanged() {
        ProjectDescriptor projectDescriptor = wizardContext.getData(ProjectWizard.PROJECT);
        if (projectDescriptor == null) {
            return;
        }

        if (projectDescriptor.getRunners() != null) {
            String defaultRunner = projectDescriptor.getRunners().getDefault();
            RunnerConfiguration defaultRunnerConf = projectDescriptor.getRunners().getConfigs().get(defaultRunner);
            if (defaultRunnerConf != null) {
                defaultRunnerConf.setRam(view.getRecommendedMemorySize());
            }
        }

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void environmentSelected(@Nullable RunnerEnvironment environment) {
        ProjectDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT);

        if (descriptor == null) {
            return;
        }

        if (environment != null) {
            RunnersDescriptor runnersDescriptor = dtoFactory.createDto(RunnersDescriptor.class);
            runnersDescriptor.setDefault(environment.getId());
            RunnerConfiguration runnerConfiguration = dtoFactory.createDto(RunnerConfiguration.class);
            runnerConfiguration.setOptions(environment.getOptions());
            runnerConfiguration.setVariables(environment.getVariables());
            Map<String, RunnerConfiguration> configurations = new HashMap<>();
            configurations.put(environment.getId(), runnerConfiguration);
            runnersDescriptor.setConfigs(configurations);

            runnerConfiguration.setRam(view.getRecommendedMemorySize());

            descriptor.setRunners(runnersDescriptor);
            view.showRunnerDescriptions(environment.getDescription());
        } else {
            descriptor.setRunners(null);
            view.showRunnerDescriptions("");
        }
    }

}