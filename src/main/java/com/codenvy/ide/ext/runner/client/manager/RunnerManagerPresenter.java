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
package com.codenvy.ide.ext.runner.client.manager;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.inject.factories.ModelsFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.RunnerAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.CheckRamAndRunAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.StopAction;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.MEMORY_512;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.STOPPED;
import static com.codenvy.ide.ext.runner.client.util.TimeInterval.ONE_SEC;

/**
 * The class provides much business logic:
 * 1. Provides possibility to launch/start a new runner. It means execute request on the server (communication with server part) and change
 * UI part.
 * 2. Manage runners (stop runner, get different information about runner and etc).
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@Singleton
public class RunnerManagerPresenter extends BasePresenter implements RunnerManager, RunnerManagerView.ActionDelegate, ProjectActionHandler {

    private final RunnerManagerView                 view;
    private final RunnerAction                      showDockerAction;
    private final RunnerAction                      getEnvironmentsAction;
    private final DtoFactory                        dtoFactory;
    private final AppContext                        appContext;
    private final ModelsFactory                     modelsFactory;
    private final RunnerActionFactory               actionFactory;
    private final Map<Runner, CheckRamAndRunAction> checkRamAndRunActions;
    private final Timer                             runnerTimer;
    private final RunnerLocalizationConstant        locale;

    private Runner            selectedRunner;
    private RunnerEnvironment selectedEnvironment;

    @Inject
    public RunnerManagerPresenter(final RunnerManagerView view,
                                  RunnerActionFactory actionFactory,
                                  ModelsFactory modelsFactory,
                                  AppContext appContext,
                                  DtoFactory dtoFactory,
                                  EventBus eventBus,
                                  RunnerLocalizationConstant locale) {
        this.view = view;
        this.view.setDelegate(this);
        this.locale = locale;
        this.dtoFactory = dtoFactory;
        this.actionFactory = actionFactory;
        this.modelsFactory = modelsFactory;
        this.appContext = appContext;
        this.showDockerAction = actionFactory.createShowDocker();
        this.getEnvironmentsAction = actionFactory.createGetEnvironments();

        this.checkRamAndRunActions = new HashMap<>();

        this.runnerTimer = new Timer() {
            @Override
            public void run() {
                updateRunnerTimer();

                this.schedule(ONE_SEC.getValue());
            }
        };

        eventBus.addHandler(ProjectActionEvent.TYPE, this);
    }

    private void updateRunnerTimer() {
        view.setTimeout(selectedRunner.getTimeout());

        view.updateMoreInfoPopup(selectedRunner);
    }

    /** @return the GWT widget that is controlled by the presenter */
    public RunnerManagerView getView() {
        return view;
    }

    /**
     * Updates runner when runner state changed.
     *
     * @param runner
     *         runner which was changed
     */
    public void update(@Nonnull Runner runner) {
        view.update(runner);

        if (runner.equals(selectedRunner)) {
            changeURLDependingOnState(selectedRunner);
        }
    }

    private void changeURLDependingOnState(@Nonnull Runner runner) {

        switch (runner.getStatus()) {
            case IN_PROGRESS:
                view.setApplicationURl(locale.uplAppWaitingForBoot());
                break;
            case IN_QUEUE:
                view.setApplicationURl(locale.uplAppWaitingForBoot());
                break;
            case STOPPED:
                view.setApplicationURl(locale.urlAppRunnerStopped());
                break;
            case FAILED:
                view.setApplicationURl(null);
                break;
            default:
                String url = runner.getApplicationURL();
                view.setApplicationURl(url == null ? locale.urlAppRunning() : url);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRunnerSelected(@Nonnull Runner runner) {
        this.selectedRunner = runner;

        update(selectedRunner);
        updateRunnerTimer();

        if (runner.isConsoleActive()) {
            view.activateConsole(selectedRunner);
        } else {
            view.activateTerminal(selectedRunner);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onEnvironmentSelected(@Nonnull RunnerEnvironment selectedEnvironment) {
        this.selectedEnvironment = selectedEnvironment;
    }

    /** {@inheritDoc} */
    @Override
    public void onRunButtonClicked() {
        if (selectedEnvironment != null) {
            Map<String, String> options = selectedEnvironment.getOptions();
            String environmentName = selectedEnvironment.getId();

            RunOptions runOptions = dtoFactory.createDto(RunOptions.class).withOptions(options);

            launchRunner(runOptions, environmentName);

            return;
        }

        if (FAILED.equals(selectedRunner.getStatus()) || STOPPED.equals(selectedRunner.getStatus())) {
            CheckRamAndRunAction checkRamAndRunAction = checkRamAndRunActions.get(selectedRunner);
            if (checkRamAndRunAction == null) {
                return;
            }

            checkRamAndRunAction.perform(selectedRunner);

            update(selectedRunner);
            selectedRunner.resetCreationTime();
        } else {
            launchRunner();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStopButtonClicked() {
        CheckRamAndRunAction checkRamAndRunAction = checkRamAndRunActions.get(selectedRunner);
        if (checkRamAndRunAction != null) {
            checkRamAndRunAction.stop();
        }

        StopAction stopAction = actionFactory.createStop();
        stopAction.perform(selectedRunner);

        view.updateMoreInfoPopup(selectedRunner);
    }

    /** {@inheritDoc} */
    @Override
    public void onCleanConsoleButtonClicked() {
        view.clearConsole(selectedRunner);
    }

    /** {@inheritDoc} */
    @Override
    public void onDockerButtonClicked() {
        showDockerAction.perform(selectedRunner);
    }

    /** {@inheritDoc} */
    @Override
    public void onConsoleButtonClicked() {
        view.activateConsole(selectedRunner);

        selectedRunner.activateConsole();
    }

    /** {@inheritDoc} */
    @Override
    public void onTerminalButtonClicked() {
        view.activateTerminal(selectedRunner);

        selectedRunner.activateTerminal();
    }

    /** {@inheritDoc} */
    @Override
    public void onMoreInfoBtnMouseOver() {
        view.showMoreInfoPopup(selectedRunner);
    }

    /** {@inheritDoc} */
    @Override
    public void onHistoryButtonClicked() {
        selectedEnvironment = null;

        view.activateHistory();
    }

    /** {@inheritDoc} */
    @Override
    public void onTemplatesButtonClicked() {
        //noinspection ConstantConditions
        getEnvironmentsAction.perform(null);
    }

    /** {@inheritDoc} */
    @Override
    public void launchRunner() {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class)
                                          .withSkipBuild(Boolean.valueOf(currentProject.getAttributeValue("runner:skipBuild")))
                                          .withMemorySize(MEMORY_512.getValue());

        launchRunner(modelsFactory.createRunner(runOptions));
    }

    /** {@inheritDoc} */
    @Override
    public void launchRunner(@Nonnull RunOptions runOptions, @Nonnull String environmentName) {
        launchRunner(modelsFactory.createRunner(runOptions, environmentName));
    }

    private void launchRunner(@Nonnull Runner runner) {
        selectedRunner = runner;
        selectedEnvironment = null;

        view.activateHistory();
        view.addRunner(runner);
        update(runner);

        CheckRamAndRunAction checkRamAndRunAction = actionFactory.createCheckRamAndRun();
        checkRamAndRunAction.perform(runner);

        checkRamAndRunActions.put(runner, checkRamAndRunAction);

        runner.resetCreationTime();
        runnerTimer.schedule(ONE_SEC.getValue());

        update(selectedRunner);
    }

    /** {@inheritDoc} */
    @Override
    public void go(@Nonnull AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** Sets active runner panel when runner is started */
    public void setActive() {
        PartPresenter activePart = partStack.getActivePart();
        if (!this.equals(activePart)) {
            partStack.setActivePart(this);
        }
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getTitle() {
        return "Runner 2";
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getTitleToolTip() {
        return locale.tooltipRunnerPanel();
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(@Nonnull ProjectActionEvent projectActionEvent) {
        view.activateHistory();
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectClosed(@Nonnull ProjectActionEvent projectActionEvent) {
        partStack.hidePart(this);
    }
}