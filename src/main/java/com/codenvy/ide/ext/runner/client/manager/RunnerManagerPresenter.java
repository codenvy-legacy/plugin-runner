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
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
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
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetRunningProcessesAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.StopAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.LaunchAction;
import com.codenvy.ide.ext.runner.client.selection.Selection;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.state.PanelState;
import com.codenvy.ide.ext.runner.client.state.State;
import com.codenvy.ide.ext.runner.client.tabs.common.Tab;
import com.codenvy.ide.ext.runner.client.tabs.common.TabBuilder;
import com.codenvy.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import com.codenvy.ide.ext.runner.client.tabs.container.TabContainer;
import com.codenvy.ide.ext.runner.client.tabs.history.HistoryPanel;
import com.codenvy.ide.ext.runner.client.tabs.properties.container.PropertiesContainer;
import com.codenvy.ide.ext.runner.client.tabs.templates.TemplatesContainer;
import com.codenvy.ide.ext.runner.client.tabs.terminal.container.TerminalContainer;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.codenvy.ide.ext.runner.client.constants.TimeInterval.ONE_SEC;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.DONE;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.STOPPED;
import static com.codenvy.ide.ext.runner.client.selection.Selection.RUNNER;
import static com.codenvy.ide.ext.runner.client.state.State.HISTORY;
import static com.codenvy.ide.ext.runner.client.state.State.TEMPLATE;
import static com.codenvy.ide.ext.runner.client.tabs.common.Tab.VisibleState.REMOVABLE;
import static com.codenvy.ide.ext.runner.client.tabs.common.Tab.VisibleState.VISIBLE;
import static com.codenvy.ide.ext.runner.client.tabs.container.TabContainer.TabSelectHandler;
import static com.codenvy.ide.ext.runner.client.tabs.container.tab.TabType.LEFT_PANEL;
import static com.codenvy.ide.ext.runner.client.tabs.container.tab.TabType.RIGHT_PANEL;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM._512;

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
public class RunnerManagerPresenter extends BasePresenter implements RunnerManager,
                                                                     RunnerManagerView.ActionDelegate,
                                                                     ProjectActionHandler,
                                                                     SelectionManager.SelectionChangeListener {
    private final RunnerManagerView          view;
    private final RunnerAction               showDockerAction;
    private final DtoFactory                 dtoFactory;
    private final AppContext                 appContext;
    private final ModelsFactory              modelsFactory;
    private final RunnerActionFactory        actionFactory;
    private final Map<Runner, RunnerAction>  runnerActions;
    private final Timer                      runnerTimer;
    private final RunnerLocalizationConstant locale;
    private final HistoryPanel               history;
    private final TemplatesContainer         templates;
    private final SelectionManager           selectionManager;
    private final ConsoleContainer           consoleContainer;
    private final TerminalContainer          terminalContainer;
    private final TabContainer               rightTabContainer;

    private Set<Long>                 runnersId;
    private GetRunningProcessesAction getRunningProcessAction;

    private Runner            selectedRunner;
    private RunnerEnvironment selectedEnvironment;

    @Inject
    public RunnerManagerPresenter(final RunnerManagerView view,
                                  RunnerActionFactory actionFactory,
                                  ModelsFactory modelsFactory,
                                  AppContext appContext,
                                  DtoFactory dtoFactory,
                                  EventBus eventBus,
                                  RunnerLocalizationConstant locale,
                                  TabContainer leftTabContainer,
                                  TabContainer rightTabContainer,
                                  PanelState panelState,
                                  Provider<TabBuilder> tabBuilderProvider,
                                  ConsoleContainer consoleContainer,
                                  TerminalContainer terminalContainer,
                                  PropertiesContainer propertiesContainer,
                                  HistoryPanel history,
                                  TemplatesContainer templates,
                                  SelectionManager selectionManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.locale = locale;
        this.dtoFactory = dtoFactory;
        this.actionFactory = actionFactory;
        this.modelsFactory = modelsFactory;
        this.appContext = appContext;
        this.showDockerAction = actionFactory.createShowDocker();

        this.selectionManager = selectionManager;
        this.selectionManager.addListener(this);

        this.templates = templates;
        this.history = history;

        this.consoleContainer = consoleContainer;
        this.terminalContainer = terminalContainer;

        this.rightTabContainer = rightTabContainer;

        this.runnerActions = new HashMap<>();

        this.runnerTimer = new Timer() {
            @Override
            public void run() {
                updateRunnerTimer();

                this.schedule(ONE_SEC.getValue());
            }
        };

        eventBus.addHandler(ProjectActionEvent.TYPE, this);
        runnersId = new HashSet<>();

        initializeLeftPanel(panelState, leftTabContainer, tabBuilderProvider, history, templates);
        initializeRightPanel(rightTabContainer, tabBuilderProvider, consoleContainer, terminalContainer, propertiesContainer);

        view.setLeftPanel(leftTabContainer);
        view.setRightPanel(rightTabContainer);
    }

    private void updateRunnerTimer() {
        view.setTimeout(selectedRunner.getTimeout());

        view.updateMoreInfoPopup(selectedRunner);
    }

    private void initializeLeftPanel(@Nonnull final PanelState panelState,
                                     @Nonnull TabContainer container,
                                     @Nonnull Provider<TabBuilder> tabBuilderProvider,
                                     @Nonnull HistoryPanel historyPanel,
                                     @Nonnull TemplatesContainer templatesContainer) {
        TabSelectHandler historyHandler = new TabSelectHandler() {
            @Override
            public void onTabSelected() {
                panelState.setState(HISTORY);

                view.showOtherButtons();
            }
        };

        Tab historyTab = tabBuilderProvider.get()
                                           .presenter(historyPanel)
                                           .selectHandler(historyHandler)
                                           .title(locale.runnerTabHistory())
                                           .visible(REMOVABLE)
                                           .scope(EnumSet.allOf(State.class))
                                           .type(LEFT_PANEL)
                                           .build();

        container.addTab(historyTab);

        TabSelectHandler templatesHandler = new TabSelectHandler() {
            @Override
            public void onTabSelected() {
                panelState.setState(TEMPLATE);

                view.hideOtherButtons();
            }
        };

        Tab templateTab = tabBuilderProvider.get()
                                            .presenter(templatesContainer)
                                            .selectHandler(templatesHandler)
                                            .title(locale.runnerTabTemplates())
                                            .visible(REMOVABLE)
                                            .scope(EnumSet.allOf(State.class))
                                            .type(LEFT_PANEL)
                                            .build();

        container.addTab(templateTab);
    }

    private void initializeRightPanel(@Nonnull TabContainer container,
                                      @Nonnull Provider<TabBuilder> tabBuilderProvider,
                                      @Nonnull ConsoleContainer consoleContainer,
                                      @Nonnull TerminalContainer terminalContainer,
                                      @Nonnull final PropertiesContainer propertiesContainer) {

        TabSelectHandler consoleHandler = new TabSelectHandler() {
            @Override
            public void onTabSelected() {
                selectedRunner.setActiveTab(locale.runnerTabConsole());
            }
        };

        Tab consoleTab = tabBuilderProvider.get()
                                           .presenter(consoleContainer)
                                           .title(locale.runnerTabConsole())
                                           .visible(REMOVABLE)
                                           .selectHandler(consoleHandler)
                                           .scope(EnumSet.allOf(State.class))
                                           .type(RIGHT_PANEL)
                                           .build();

        container.addTab(consoleTab);

        TabSelectHandler terminalHandler = new TabSelectHandler() {
            @Override
            public void onTabSelected() {
                selectedRunner.setActiveTab(locale.runnerTabTerminal());
            }
        };

        Tab terminalTab = tabBuilderProvider.get()
                                            .presenter(terminalContainer)
                                            .title(locale.runnerTabTerminal())
                                            .visible(VISIBLE)
                                            .selectHandler(terminalHandler)
                                            .scope(EnumSet.allOf(State.class))
                                            .type(RIGHT_PANEL)
                                            .build();

        container.addTab(terminalTab);

        TabSelectHandler propertiesHandler = new TabSelectHandler() {
            @Override
            public void onTabSelected() {
                selectedRunner.setActiveTab(locale.runnerTabProperties());

                propertiesContainer.show(selectedRunner);
            }
        };

        Tab propertiesTab = tabBuilderProvider.get()
                                              .presenter(propertiesContainer)
                                              .selectHandler(propertiesHandler)
                                              .title(locale.runnerTabProperties())
                                              .visible(REMOVABLE)
                                              .scope(EnumSet.allOf(State.class))
                                              .type(RIGHT_PANEL)
                                              .build();

        container.addTab(propertiesTab);
    }

    /** @return the GWT widget that is controlled by the presenter */
    @Nonnull
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
        history.update(runner);
        terminalContainer.update(runner);

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
    public void onRunButtonClicked() {
        if (selectedEnvironment != null) {
            Map<String, String> options = selectedEnvironment.getOptions();
            String environmentName = selectedEnvironment.getId();

            RunOptions runOptions = dtoFactory.createDto(RunOptions.class).withOptions(options);

            launchRunner(runOptions, environmentName);

            return;
        }

        if (FAILED.equals(selectedRunner.getStatus()) || STOPPED.equals(selectedRunner.getStatus())) {
            RunnerAction runnerAction = runnerActions.get(selectedRunner);
            if (runnerAction == null || runnerAction instanceof LaunchAction) {
                launchRunner(selectedRunner);
            } else {
                runnerAction.perform(selectedRunner);

                update(selectedRunner);
                selectedRunner.resetCreationTime();
            }
        } else {
            launchRunner();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStopButtonClicked() {
        stopRunner(selectedRunner);

        StopAction stopAction = actionFactory.createStop();
        stopAction.perform(selectedRunner);

        view.updateMoreInfoPopup(selectedRunner);
    }

    /** {@inheritDoc} */
    @Override
    public void stopRunner(@Nonnull Runner runner) {
        RunnerAction runnerAction = runnerActions.get(runner);
        if (runnerAction != null) {
            runnerAction.stop();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCleanConsoleButtonClicked() {
        consoleContainer.clear(selectedRunner);
    }

    /** {@inheritDoc} */
    @Override
    public void onDockerButtonClicked() {
        showDockerAction.perform(selectedRunner);
    }

    /** {@inheritDoc} */
    @Override
    public void onMoreInfoBtnMouseOver() {
        view.showMoreInfoPopup(selectedRunner);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Runner launchRunner() {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return null;
        }

        RunOptions runOptions = dtoFactory.createDto(RunOptions.class)
                                          .withSkipBuild(Boolean.valueOf(currentProject.getAttributeValue("runner:skipBuild")))
                                          .withMemorySize(_512.getValue());

        return launchRunner(modelsFactory.createRunner(runOptions));
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Runner launchRunner(@Nonnull RunOptions runOptions) {
        return launchRunner(modelsFactory.createRunner(runOptions));
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public Runner launchRunner(@Nonnull RunOptions runOptions, @Nonnull String environmentName) {
        return launchRunner(modelsFactory.createRunner(runOptions, environmentName));
    }

    @Nonnull
    private Runner launchRunner(@Nonnull Runner runner) {
        selectedEnvironment = null;

        history.addRunner(runner);

        CheckRamAndRunAction checkRamAndRunAction = actionFactory.createCheckRamAndRun();
        checkRamAndRunAction.perform(runner);

        runnerActions.put(runner, checkRamAndRunAction);

        runner.resetCreationTime();
        runnerTimer.schedule(ONE_SEC.getValue());

        return runner;
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
        return locale.runnerTitle();
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
        getRunningProcessAction = actionFactory.createGetRunningProcess();

        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        getRunningProcessAction.perform();

        runnerTimer.schedule(ONE_SEC.getValue());
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectClosed(@Nonnull ProjectActionEvent projectActionEvent) {
        partStack.hidePart(this);

        for (Runner runner : runnerActions.keySet()) {
            if (runner.isAlive()) {
                runner.setStatus(STOPPED);

                stopRunner(runner);
            }
        }

        getRunningProcessAction.stop();
    }

    /**
     * Adds already running runner.
     *
     * @param processDescriptor
     *         The descriptor of new runner
     * @return instance of new runner
     */
    @Nonnull
    public Runner addRunner(@Nonnull ApplicationProcessDescriptor processDescriptor) {
        RunOptions runOptions = dtoFactory.createDto(RunOptions.class);
        Runner runner = modelsFactory.createRunner(runOptions);
        runnersId.add(processDescriptor.getProcessId());

        runner.setProcessDescriptor(processDescriptor);
        runner.setRAM(processDescriptor.getMemorySize());
        runner.setStatus(DONE);
        runner.resetCreationTime();

        history.addRunner(runner);

        onSelectionChanged(RUNNER);

        runnerTimer.schedule(ONE_SEC.getValue());

        LaunchAction launchAction = actionFactory.createLaunch();
        runnerActions.put(runner, launchAction);

        launchAction.perform(runner);

        return runner;
    }

    /**
     * Adds id of new running runner.
     *
     * @param runnerId
     *         process id of runner
     */
    public void addRunnerId(@Nonnull Long runnerId) {
        runnersId.add(runnerId);
    }

    /**
     * Returns <code>true</code> if runner with current ID was already create, <code>false</code> runner does not exist* *
     *
     * @param runnerId
     *         ID of runner
     */
    public boolean isRunnerExist(@Nonnull Long runnerId) {
        return runnersId.contains(runnerId);
    }

    /** {@inheritDoc} */
    @Override
    public void onSelectionChanged(@Nonnull Selection selection) {
        if (RUNNER.equals(selection)) {
            runnerSelected();
        } else {
            environmentSelected();
        }
    }

    private void runnerSelected() {
        selectedRunner = selectionManager.getRunner();
        if (selectedRunner == null) {
            return;
        }

        history.selectRunner(selectedRunner);
        rightTabContainer.showTab(selectedRunner.getActiveTab());

        update(selectedRunner);

        updateRunnerTimer();
    }

    private void environmentSelected() {
        selectedEnvironment = selectionManager.getEnvironment();
        if (selectedEnvironment == null) {
            return;
        }

        templates.select(selectedEnvironment);
    }
}