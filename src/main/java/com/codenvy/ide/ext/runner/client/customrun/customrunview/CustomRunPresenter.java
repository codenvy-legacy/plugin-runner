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
package com.codenvy.ide.ext.runner.client.customrun.customrunview;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.ProjectDescriptorChangedEvent;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * The class contains business logic which allows changes settings of custom run project environments and provides ability save current
 * run options. Also class contains validation of input ram values.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public class CustomRunPresenter implements CustomRunView.ActionDelegate {

    private final static String MULTIPLE_RAM_SIZE   = "128";
    private final static String DEFAULT_MEMORY_SIZE = "256MB";

    private final CustomRunView              view;
    private final AppContext                 appContext;
    private final DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private final ProjectServiceClient       serviceClient;
    private final RunnerServiceClient        runnerServiceClient;
    private final RunnerLocalizationConstant locale;
    private final NotificationManager        notificationManager;
    private final DtoFactory                 dtoFactory;
    private final EventBus                   eventBus;
    private final RunnerManagerPresenter     managerPresenter;
    private final DialogFactory              dialogFactory;

    private CurrentProject    currentProject;
    private RunnerEnvironment runnerEnvironment;

    @Inject
    public CustomRunPresenter(CustomRunView view,
                              AppContext appContext,
                              DtoUnmarshallerFactory dtoUnmarshallerFactory,
                              ProjectServiceClient serviceClient,
                              RunnerServiceClient runnerServiceClient,
                              RunnerLocalizationConstant locale,
                              NotificationManager notificationManager,
                              DtoFactory dtoFactory,
                              EventBus eventBus,
                              RunnerManagerPresenter managerPresenter,
                              DialogFactory dialogFactory) {
        this.view = view;
        this.view.setDelegate(this);

        this.appContext = appContext;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.serviceClient = serviceClient;
        this.runnerServiceClient = runnerServiceClient;
        this.locale = locale;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        this.managerPresenter = managerPresenter;
        this.dialogFactory = dialogFactory;
    }

    /** Shows dialog window editing custom run parameters. */
    public void showDialog() {
        currentProject = appContext.getCurrentProject();

        if (currentProject == null) {
            return;
        }
        getRunnerEnvironments();

        view.showDialog();
    }

    private void getRunnerEnvironments() {
        String projectPath = currentProject.getProjectDescription().getPath();

        Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);

        serviceClient.getRunnerEnvironments(projectPath, new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
            @Override
            protected void onSuccess(RunnerEnvironmentTree result) {
                if (!result.getLeaves().isEmpty() || !result.getNodes().isEmpty()) {
                    view.addRunner(result);
                }

                getSystemEnvironments();
            }

            @Override
            protected void onFailure(Throwable exception) {
                getSystemEnvironments();
            }
        });
    }

    private void getSystemEnvironments() {
        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);

        runnerServiceClient.getRunners(new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
                                           @Override
                                           protected void onSuccess(RunnerEnvironmentTree result) {
                                               view.addRunner(result);
                                               restoreOptions();
                                           }

                                           @Override
                                           protected void onFailure(Throwable exception) {
                                               notificationManager.showError(locale.customRunnerGetEnvironmentFailed());
                                           }
                                       }
                                      );
    }

    private void restoreOptions() {
        final Unmarshallable<ResourcesDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class);

        runnerServiceClient.getResources(new AsyncRequestCallback<ResourcesDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ResourcesDescriptor resourcesDescriptor) {
                int totalMemory = Integer.valueOf(resourcesDescriptor.getTotalMemory());
                int usedMemory = Integer.valueOf(resourcesDescriptor.getUsedMemory());

                view.setEnabledRadioButtons(totalMemory);
                view.setTotalMemorySize(String.valueOf(totalMemory));
                view.setAvailableMemorySize(String.valueOf(totalMemory - usedMemory));
                view.setRunnerMemorySize(String.valueOf(DEFAULT_MEMORY_SIZE));
            }

            @Override
            protected void onFailure(Throwable throwable) {
                notificationManager.showError(locale.customRunnerGetEnvironmentFailed());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onEnvironmentSelected(@Nullable RunnerEnvironment runnerEnvironment) {
        this.runnerEnvironment = runnerEnvironment;

        boolean isEnvironmentExist = runnerEnvironment != null;

        view.setRunButtonState(isEnvironmentExist);
        view.setEnvironmentDescription(isEnvironmentExist ? runnerEnvironment.getDescription() : "");
    }

    /** {@inheritDoc} */
    @Override
    public void onRunClicked() {
        if (runnerEnvironment == null) {
            return;
        }

        if (view.isRememberOptionsSelected()) {
            saveOptions();
        }

        if (isRunnerMemoryCorrect()) {
            RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
            runOptions.setEnvironmentId(runnerEnvironment.getId());
            runOptions.setMemorySize(view.getRunnerMemorySize());
            runOptions.setSkipBuild(view.isSkipBuildSelected());

            view.close();

            managerPresenter.launchRunner(runOptions);
        }
    }

    private boolean isRunnerMemoryCorrect() {
        int runnerMemory = view.getRunnerMemorySize();
        int totalMemory = view.getTotalMemorySize();
        int availableMemory = view.getAvailableMemorySize();

        if (runnerMemory == -1 || totalMemory == -1 || availableMemory == -1) {
            showWarning(locale.messagesIncorrectValue());
            return false;
        }

        if (runnerMemory < 0 || runnerMemory % 128 != 0) {
            showWarning(locale.ramSizeMustBeMultipleOf(MULTIPLE_RAM_SIZE));
            return false;
        }

        if (runnerMemory > totalMemory) {
            showWarning(locale.messagesTotalRamLessCustom(runnerMemory, totalMemory));
            return false;
        }

        if (runnerMemory > availableMemory) {
            showWarning(locale.messagesAvailableRamLessCustom(runnerMemory, totalMemory, totalMemory - availableMemory));
            return false;
        }

        return true;
    }

    private void showWarning(@Nonnull String warningMessage) {
        dialogFactory.createMessageDialog("Warning", warningMessage, null).show();
    }

    private void saveOptions() {
        String selectedEnvironmentId = runnerEnvironment.getId();
        int selectedMemorySize = view.getRunnerMemorySize();

        ProjectDescriptor projectDescriptor = currentProject.getProjectDescription();
        RunnersDescriptor runners = projectDescriptor.getRunners();

        if (runners == null) {
            runners = dtoFactory.createDto(RunnersDescriptor.class);
            projectDescriptor.setRunners(runners);
        }

        Map<String, RunnerConfiguration> runnerConfigurations = runners.getConfigs();
        RunnerConfiguration runnerConfiguration = runnerConfigurations.get(selectedEnvironmentId);

        if (runnerConfiguration == null) {
            runnerConfiguration = dtoFactory.createDto(RunnerConfiguration.class);
            runnerConfigurations.put(selectedEnvironmentId, runnerConfiguration);
        }

        runnerConfiguration.setRam(selectedMemorySize);
        runners.setDefault(selectedEnvironmentId);

        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);

        serviceClient.updateProject(projectDescriptor.getPath(), projectDescriptor,
                                    new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
                                        @Override
                                        protected void onSuccess(ProjectDescriptor result) {
                                            eventBus.fireEvent(new ProjectDescriptorChangedEvent(result));
                                        }

                                        @Override
                                        protected void onFailure(Throwable exception) {
                                            showWarning(locale.messageFailRememberOptions());
                                        }
                                    });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}
