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

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDefinition;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PartStack;
import com.codenvy.ide.api.projecttype.ProjectTypeRegistry;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.inject.factories.ModelsFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.models.RunnerCounter;
import com.codenvy.ide.ext.runner.client.runneractions.impl.CheckRamAndRunAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetRunningProcessesAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.StopAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetSystemEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.LaunchAction;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.state.PanelState;
import com.codenvy.ide.ext.runner.client.state.State;
import com.codenvy.ide.ext.runner.client.tabs.common.Tab;
import com.codenvy.ide.ext.runner.client.tabs.common.TabBuilder;
import com.codenvy.ide.ext.runner.client.tabs.common.TabPresenter;
import com.codenvy.ide.ext.runner.client.tabs.console.container.ConsoleContainer;
import com.codenvy.ide.ext.runner.client.tabs.container.TabContainer;
import com.codenvy.ide.ext.runner.client.tabs.container.tab.TabType;
import com.codenvy.ide.ext.runner.client.tabs.history.HistoryPanel;
import com.codenvy.ide.ext.runner.client.tabs.properties.container.PropertiesContainer;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import com.codenvy.ide.ext.runner.client.tabs.templates.TemplatesContainer;
import com.codenvy.ide.ext.runner.client.tabs.terminal.container.TerminalContainer;
import com.codenvy.ide.ext.runner.client.util.TimerFactory;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.codenvy.ide.ext.runner.client.constants.TimeInterval.ONE_SEC;
import static com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter.TIMER_STUB;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.DONE;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.IN_PROGRESS;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.IN_QUEUE;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.STOPPED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.TIMEOUT;
import static com.codenvy.ide.ext.runner.client.selection.Selection.ENVIRONMENT;
import static com.codenvy.ide.ext.runner.client.selection.Selection.RUNNER;
import static com.codenvy.ide.ext.runner.client.state.State.RUNNERS;
import static com.codenvy.ide.ext.runner.client.state.State.TEMPLATE;
import static com.codenvy.ide.ext.runner.client.tabs.common.Tab.VisibleState.REMOVABLE;
import static com.codenvy.ide.ext.runner.client.tabs.common.Tab.VisibleState.VISIBLE;
import static com.codenvy.ide.ext.runner.client.tabs.container.tab.TabType.LEFT_PANEL;
import static com.codenvy.ide.ext.runner.client.tabs.container.tab.TabType.RIGHT_PANEL;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM._512;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunnerManagerPresenterTest {

    private static final String TEXT           = "any text";
    private static final String HISTORY_TAB    = "history";
    private static final String TEMPLATES      = "templates";
    private static final String CONSOLE        = "console";
    private static final String TERMINAL       = "terminal";
    private static final String PROPERTIES     = "properties";
    private static final String STOPPED_RUNNER = "application shut down";
    private static final String APP_URL        = "http://runner1.codenvy.com/";
    private static final long   PROCESS_ID     = 1234567L;

    //mocks for constructor
    @Mock
    private RunnerManagerView            view;
    @Mock
    private RunnerActionFactory          actionFactory;
    @Mock
    private ModelsFactory                modelsFactory;
    @Mock
    private AppContext                   appContext;
    @Mock
    private DtoFactory                   dtoFactory;
    @Mock
    private EventBus                     eventBus;
    @Mock
    private RunnerLocalizationConstant   locale;
    @Mock
    private TabContainer                 leftTabContainer;
    @Mock
    private TabContainer                 rightTabContainer;
    @Mock
    private PanelState                   panelState;
    @Mock
    private Provider<TabBuilder>         tabBuilderProvider;
    @Mock
    private ConsoleContainer             consoleContainer;
    @Mock
    private TerminalContainer            terminalContainer;
    @Mock
    private PropertiesContainer          propertiesContainer;
    @Mock
    private HistoryPanel                 history;
    @Mock
    private TemplatesContainer           templates;
    @Mock
    private SelectionManager             selectionManager;
    @Mock
    private ApplicationProcessDescriptor processDescriptor;
    @Mock
    private TimerFactory                 timerFactory;
    @Mock
    private RunnerCounter                runnerCounter;
    @Mock
    private GetSystemEnvironmentsAction  getSystemEnvironmentsAction;
    @Mock
    private GetProjectEnvironmentsAction getProjectEnvironmentsAction;

    //tab builder mocks
    @Mock
    private TabBuilder tabBuilderHistory;
    @Mock
    private TabBuilder tabBuilderTemplate;
    @Mock
    private TabBuilder tabBuilderConsole;
    @Mock
    private TabBuilder tabBuilderTerminal;
    @Mock
    private TabBuilder tabBuilderProperties;

    //tab mocks
    @Mock
    private Tab historyTab;
    @Mock
    private Tab templateTab;
    @Mock
    private Tab consoleTab;
    @Mock
    private Tab terminalTab;
    @Mock
    private Tab propertiesTab;

    @Mock
    private Runner                      runner;
    @Mock
    private RunOptions                  runOptions;
    @Mock
    private LaunchAction                launchAction;
    @Mock
    private CheckRamAndRunAction        checkRamAndRunAction;
    @Mock
    private CurrentProject              currentProject;
    @Mock
    private ProjectDescriptor           descriptor;
    @Mock
    private Environment                 runnerEnvironment;
    @Mock
    private ProjectActionEvent          projectActionEvent;
    @Mock
    private GetRunningProcessesAction   getRunningProcessAction;
    @Mock
    private PartStack                   partStack;
    @Mock
    private StopAction                  stopAction;
    @Mock
    private PartPresenter               activePart;
    @Mock
    private Timer                       timer;
    @Mock
    private GetSystemEnvironmentsAction systemEnvironmentsAction;
    @Mock
    private ProjectTypeRegistry         typeRegistry;
    @Mock
    private ProjectTypeDefinition       definition;

    private RunnerManagerPresenter presenter;

    @Before
    public void setUp() {
        when(locale.runnerTabHistory()).thenReturn(HISTORY_TAB);
        when(locale.runnerTabTemplates()).thenReturn(TEMPLATES);
        when(locale.runnerTabConsole()).thenReturn(CONSOLE);
        when(locale.runnerTabTerminal()).thenReturn(TERMINAL);
        when(locale.runnerTabProperties()).thenReturn(PROPERTIES);

        when(tabBuilderProvider.get()).thenReturn(tabBuilderHistory)
                                      .thenReturn(tabBuilderTemplate)
                                      .thenReturn(tabBuilderConsole)
                                      .thenReturn(tabBuilderTerminal)
                                      .thenReturn(tabBuilderProperties);
        //init new historyTab
        initTab(tabBuilderHistory, history, REMOVABLE, LEFT_PANEL, EnumSet.allOf(State.class), HISTORY_TAB);
        when(tabBuilderHistory.build()).thenReturn(historyTab);

        //init template tab
        initTab(tabBuilderTemplate, templates, REMOVABLE, LEFT_PANEL, EnumSet.allOf(State.class), TEMPLATES);
        when(tabBuilderTemplate.build()).thenReturn(templateTab);

        //init console tab
        initTab(tabBuilderConsole, consoleContainer, REMOVABLE, RIGHT_PANEL, EnumSet.of(RUNNERS), CONSOLE);
        when(tabBuilderConsole.build()).thenReturn(consoleTab);

        //init terminal tab
        initTab(tabBuilderTerminal, terminalContainer, VISIBLE, RIGHT_PANEL, EnumSet.of(RUNNERS), TERMINAL);
        when(tabBuilderTerminal.build()).thenReturn(terminalTab);

        //init properties tab
        initTab(tabBuilderProperties, propertiesContainer, REMOVABLE, RIGHT_PANEL, EnumSet.allOf(State.class), PROPERTIES);
        when(tabBuilderProperties.build()).thenReturn(propertiesTab);

        when(timerFactory.newInstance(any(TimerFactory.TimerCallBack.class))).thenReturn(timer);

        presenter = new RunnerManagerPresenter(view,
                                               actionFactory,
                                               modelsFactory,
                                               appContext,
                                               dtoFactory,
                                               eventBus,
                                               locale,
                                               leftTabContainer,
                                               rightTabContainer,
                                               panelState,
                                               tabBuilderProvider,
                                               consoleContainer,
                                               terminalContainer,
                                               propertiesContainer,
                                               history,
                                               templates,
                                               runnerCounter,
                                               selectionManager,
                                               timerFactory,
                                               getSystemEnvironmentsAction,
                                               getProjectEnvironmentsAction,
                                               typeRegistry);

        //adding runner
        when(dtoFactory.createDto(RunOptions.class)).thenReturn(runOptions);
        when(modelsFactory.createRunner(runOptions)).thenReturn(runner);
        when(processDescriptor.getProcessId()).thenReturn(PROCESS_ID);
        when(processDescriptor.getMemorySize()).thenReturn(_512.getValue());
        when(actionFactory.createLaunch()).thenReturn(launchAction);
        when(runner.getTimeout()).thenReturn(TEXT);
        when(selectionManager.getRunner()).thenReturn(runner);
        when(runner.getActiveTab()).thenReturn(TEXT);
        when(runner.getStatus()).thenReturn(IN_PROGRESS);

        //application url
        when(locale.uplAppWaitingForBoot()).thenReturn(TEXT);
        when(locale.urlAppRunnerStopped()).thenReturn(STOPPED_RUNNER);
        when(locale.urlAppRunning()).thenReturn(APP_URL);

        //init run options
        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.getProjectDescription()).thenReturn(descriptor);
        when(descriptor.getType()).thenReturn(TEXT);
        when(typeRegistry.getProjectType(TEXT)).thenReturn(definition);
        when(definition.getRunnerCategories()).thenReturn(Arrays.asList(TEXT));
        when(currentProject.getAttributeValue("runner:skipBuild")).thenReturn("true");
        when(runOptions.withSkipBuild(true)).thenReturn(runOptions);
        when(runOptions.withMemorySize(_512.getValue())).thenReturn(runOptions);
        when(actionFactory.createCheckRamAndRun()).thenReturn(checkRamAndRunAction);

        //part stack
        when(partStack.getActivePart()).thenReturn(activePart);

        when(actionFactory.createGetRunningProcess()).thenReturn(getRunningProcessAction);
        when(runner.getTimeout()).thenReturn(TEXT);
    }

    private void initTab(TabBuilder tabBuilder,
                         TabPresenter tabPresenter,
                         Tab.VisibleState state,
                         TabType tabType,
                         Set<State> stateSet,
                         String title) {
        when(tabBuilder.presenter(tabPresenter)).thenReturn(tabBuilder);
        when(tabBuilder.selectHandler(any(TabContainer.TabSelectHandler.class))).thenReturn(tabBuilder);
        when(tabBuilder.title(title)).thenReturn(tabBuilder);
        when(tabBuilder.visible(state)).thenReturn(tabBuilder);
        when(tabBuilder.scope(stateSet)).thenReturn(tabBuilder);
        when(tabBuilder.type(tabType)).thenReturn(tabBuilder);
    }

    @Test
    public void shouldVerifyConstructor() {
        verify(view).setDelegate(presenter);
        verify(selectionManager).addListener(presenter);
        verify(eventBus).addHandler(ProjectActionEvent.TYPE, presenter);

        /* verify initialize LeftPanel */
        //init new historyTab
        verifyInitTab(tabBuilderHistory, history, REMOVABLE, LEFT_PANEL, EnumSet.allOf(State.class), HISTORY_TAB);
        verify(leftTabContainer).addTab(historyTab);

        verifyTabSelectHandler(tabBuilderHistory);
        verify(panelState).setState(RUNNERS);
        verify(view).showOtherButtons();

        //init template tab
        verifyInitTab(tabBuilderTemplate, templates, REMOVABLE, LEFT_PANEL, EnumSet.allOf(State.class), TEMPLATES);
        verify(leftTabContainer).addTab(templateTab);

        verifyTabSelectHandler(tabBuilderTemplate);
        verify(panelState).setState(TEMPLATE);
        verify(templates).showSystemEnvironments();
        verify(view).hideOtherButtons();

        presenter.addRunner(processDescriptor);

        ArgumentCaptor<TimerFactory.TimerCallBack> argumentCaptor = ArgumentCaptor.forClass(TimerFactory.TimerCallBack.class);
        verify(timerFactory).newInstance(argumentCaptor.capture());
        argumentCaptor.getValue().onRun();

        verify(runner, times(2)).getTimeout();
        verify(view, times(2)).updateMoreInfoPopup(runner);
        verify(view, times(2)).setTimeout(TEXT);
        verify(timer, times(2)).schedule(ONE_SEC.getValue());

        /* verify initialize RightPanel*/
        //init console tab
        verifyInitTab(tabBuilderConsole, consoleContainer, REMOVABLE, RIGHT_PANEL, EnumSet.of(RUNNERS), CONSOLE);
        verify(rightTabContainer).addTab(consoleTab);

        //verify consoleHandler
        verifyTabSelectHandler(tabBuilderConsole);
        verify(runner).setActiveTab(CONSOLE);

        //init terminal tab
        verifyInitTab(tabBuilderTerminal, terminalContainer, VISIBLE, RIGHT_PANEL, EnumSet.of(RUNNERS), TERMINAL);
        verify(rightTabContainer).addTab(terminalTab);

        //verify terminalHandler
        verifyTabSelectHandler(tabBuilderTerminal);
        verify(runner).setActiveTab(TERMINAL);

        //init properties tab
        verifyInitTab(tabBuilderProperties, propertiesContainer, REMOVABLE, RIGHT_PANEL, EnumSet.allOf(State.class), PROPERTIES);
        verify(rightTabContainer).addTab(propertiesTab);

        //verify templatesHandler
        verifyTabSelectHandler(tabBuilderProperties);
        verify(panelState).setState(TEMPLATE);
        verify(view).hideOtherButtons();

        verify(view).setLeftPanel(leftTabContainer);
        verify(view).setRightPanel(rightTabContainer);
    }

    private void verifyInitTab(TabBuilder tabBuilder,
                               TabPresenter tabPresenter,
                               Tab.VisibleState state,
                               TabType type,
                               Set<State> states,
                               String title) {
        verify(tabBuilder).presenter(tabPresenter);
        verify(tabBuilder).title(title);
        verify(tabBuilder).visible(state);
        verify(tabBuilder).scope(states);
        verify(tabBuilder).type(type);
    }

    private void verifyTabSelectHandler(TabBuilder tabBuilder) {
        ArgumentCaptor<TabContainer.TabSelectHandler> tabSelectHandlerCaptor = ArgumentCaptor.forClass(TabContainer.TabSelectHandler.class);
        verify(tabBuilder).selectHandler(tabSelectHandlerCaptor.capture());
        tabSelectHandlerCaptor.getValue().onTabSelected();
    }

    @Test
    public void viewShouldBeReturned() {
        assertThat(presenter.getView(), is(view));
    }

    @Test
    public void runnerShouldBeAdded() {
        presenter.addRunner(processDescriptor);

        verify(dtoFactory).createDto(RunOptions.class);
        verify(modelsFactory).createRunner(runOptions);
        verify(processDescriptor).getProcessId();
        verify(runner).setProcessDescriptor(processDescriptor);
        verify(runner).setRAM(RAM._512.getValue());
        verify(runner).setStatus(DONE);
        verify(runner).resetCreationTime();
        verify(history).addRunner(runner);

        verifyRunnerSelected();

        verify(timer).schedule(ONE_SEC.getValue());

        verify(actionFactory).createLaunch();
        verify(launchAction).perform(runner);
    }

    @Test
    public void runnerIsExist1() {
        presenter.addRunner(processDescriptor);
        assertThat(presenter.isRunnerExist(PROCESS_ID), is(true));
    }

    @Test
    public void runnerIsExist2() {
        presenter.addRunnerId(PROCESS_ID);

        assertThat(presenter.isRunnerExist(PROCESS_ID), is(true));
    }

    @Test
    public void runnerIsNotExist1() {
        assertThat(presenter.isRunnerExist(PROCESS_ID), is(false));
    }

    @Test
    public void runnerIsNotExist2() {
        presenter.addRunnerId(Long.MIN_VALUE);

        assertThat(presenter.isRunnerExist(PROCESS_ID), is(false));
    }

    @Test
    public void runnerShouldBeUpdated() {
        presenter.update(runner);

        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
    }

    @Test
    public void runnerWhichIsAlreadyExistWithStatusInProgressShouldBeUpdated() {
        presenter.addRunner(processDescriptor);
        reset(history, terminalContainer, view);

        presenter.update(runner);

        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
        verify(view).setApplicationURl(TEXT);
    }

    @Test
    public void runnerWhichIsAlreadyExistWithStatusInQueueShouldBeUpdated() {
        presenter.addRunner(processDescriptor);
        reset(history, terminalContainer, view);
        when(runner.getStatus()).thenReturn(IN_QUEUE);

        presenter.update(runner);

        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
        verify(view).setApplicationURl(TEXT);
    }

    @Test
    public void runnerWhichIsAlreadyExistWithStatusStoppedShouldBeUpdated() {
        presenter.addRunner(processDescriptor);
        reset(history, terminalContainer, view);
        when(runner.getStatus()).thenReturn(STOPPED);

        presenter.update(runner);

        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
        verify(view).setApplicationURl(STOPPED_RUNNER);
    }

    @Test
    public void runnerWhichIsAlreadyExistWithStatusFailedShouldBeUpdated() {
        presenter.addRunner(processDescriptor);
        reset(history, terminalContainer, view);
        when(runner.getStatus()).thenReturn(FAILED);

        presenter.update(runner);

        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
        verify(view).setApplicationURl(null);
    }

    @Test
    public void runnerWhichIsAlreadyExistWithStatusDoneAndUrlAppIsNotNullShouldBeUpdated() {
        presenter.addRunner(processDescriptor);
        reset(history, terminalContainer, view);
        when(runner.getStatus()).thenReturn(DONE);

        presenter.update(runner);

        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
        verify(view).setApplicationURl(APP_URL);
    }

    @Test
    public void runnerWhichIsAlreadyExistWithStatusDoneAndUrlAppIsNullShouldBeUpdated() {
        presenter.addRunner(processDescriptor);
        reset(history, terminalContainer, view);
        when(runner.getStatus()).thenReturn(DONE);
        when(runner.getApplicationURL()).thenReturn(null);

        presenter.update(runner);

        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
        verify(view).setApplicationURl(APP_URL);
    }

    @Test
    public void runnerWhichIsAlreadyExistWithStatusTimeOutShouldBeUpdated() {
        presenter.addRunner(processDescriptor);
        reset(history, terminalContainer, view);
        when(runner.getStatus()).thenReturn(TIMEOUT);

        presenter.update(runner);

        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
        verify(view).setApplicationURl(APP_URL);
    }

    @Test
    public void runnerShouldNotBeRunIfRunnerAndSelectedEnvironmentAreNull() {
        presenter.onRunButtonClicked();

        verifyLaunchRunnerWithNotNullCurrentProject();
    }

    @Test
    public void runnerShouldBeRunIfSelectedRunnerNotNullAndStatusIsInProgress() {
        when(runner.getStatus()).thenReturn(IN_PROGRESS);
        presenter.addRunner(processDescriptor);
        reset(view, history);

        presenter.onRunButtonClicked();

        verify(appContext, times(2)).getCurrentProject();
        verify(dtoFactory, times(2)).createDto(RunOptions.class);
        verify(runOptions).withSkipBuild(true);
        verify(runOptions).withMemorySize(_512.getValue());
        verify(modelsFactory, times(2)).createRunner(runOptions);

        //verify launch runner
        verify(panelState).setState(RUNNERS);
        verify(view).showOtherButtons();
        verify(history).addRunner(runner);
        verify(actionFactory).createCheckRamAndRun();
        verify(checkRamAndRunAction).perform(runner);
        verify(runner, times(2)).resetCreationTime();
    }

    @Test
    public void newRunnerShouldBeRunIfPanelStateIsNotTemplate() {
        when(panelState.getState()).thenReturn(RUNNERS);
        presenter.addRunner(processDescriptor);
        reset(view, history);

        presenter.onRunButtonClicked();

        //verify launch runner
        verify(panelState).setState(RUNNERS);
        verify(view).showOtherButtons();
        verify(history).addRunner(runner);
        verify(actionFactory).createCheckRamAndRun();
        verify(checkRamAndRunAction).perform(runner);
        verify(runner, times(2)).resetCreationTime();
    }

    @Test
    public void newRunnerShouldBeRunIfPanelStateIsTemplate() {
        Map<String, String> options = new HashMap<>();
        when(selectionManager.getEnvironment()).thenReturn(runnerEnvironment);
        when(runnerEnvironment.getOptions()).thenReturn(options);
        when(runnerEnvironment.getName()).thenReturn(TEXT);
        when(runnerEnvironment.getId()).thenReturn(TEXT);
        when(runOptions.withOptions(options)).thenReturn(runOptions);
        when(runOptions.withEnvironmentId(TEXT)).thenReturn(runOptions);
        when(modelsFactory.createRunner(runOptions, TEXT)).thenReturn(runner);
        when(panelState.getState()).thenReturn(TEMPLATE);

        presenter.onSelectionChanged(ENVIRONMENT);

        presenter.onRunButtonClicked();

        verify(panelState).getState();
        verify(runnerEnvironment).getOptions();
        verify(runnerEnvironment).getName();
        verify(dtoFactory).createDto(RunOptions.class);
        verify(runOptions).withOptions(options);
        verify(modelsFactory).createRunner(runOptions, TEXT);

        //verify launch runner
        verify(panelState).setState(RUNNERS);
        verify(view).showOtherButtons();
        verify(history).addRunner(runner);
        verify(actionFactory).createCheckRamAndRun();
        verify(checkRamAndRunAction).perform(runner);
        verify(runner).resetCreationTime();
    }

    @Test
    public void runnerShouldBeRerunIfRunnerActionIsNull() throws Exception {
        presenter.addRunner(processDescriptor);
        reset(view, history);

        presenter.launchRunner(runOptions);
        presenter.onRerunButtonClicked();

        verify(runner, times(2)).getStatus();
        verify(history).update(runner);
        verify(terminalContainer, times(2)).update(runner);
        verify(view).update(runner);
        verify(view).setApplicationURl(TEXT);

        verify(runner, times(3)).resetCreationTime();

    }

    @Test
    public void runnerShouldBeRerunIfRunnerNotNullAndStatusIsStopped() {
        when(runner.getStatus()).thenReturn(STOPPED);
        presenter.addRunner(processDescriptor);
        reset(view, history);

        presenter.onRerunButtonClicked();

        verify(runner, times(1)).getStatus();

        verify(panelState).setState(RUNNERS);
        verify(view).showOtherButtons();
        verify(history).addRunner(runner);
        verify(actionFactory).createCheckRamAndRun();
        verify(checkRamAndRunAction).perform(runner);
        verify(runner, times(2)).resetCreationTime();
    }

    @Test
    public void runnerShouldBeRunIfRunnerNotNullAndStatusIsTemplate() {
        Map<String, String> options = new HashMap<>();
        when(selectionManager.getEnvironment()).thenReturn(runnerEnvironment);
        when(runnerEnvironment.getOptions()).thenReturn(options);
        when(runnerEnvironment.getName()).thenReturn(TEXT);
        when(runnerEnvironment.getId()).thenReturn(TEXT);
        when(runOptions.withOptions(options)).thenReturn(runOptions);
        when(runOptions.withEnvironmentId(TEXT)).thenReturn(runOptions);
        when(modelsFactory.createRunner(runOptions, TEXT)).thenReturn(runner);
        when(panelState.getState()).thenReturn(TEMPLATE);

        presenter.addRunner(processDescriptor);
        reset(view, history);
        presenter.onSelectionChanged(ENVIRONMENT);

        presenter.onRunButtonClicked();

        verify(panelState).getState();
        verify(runnerEnvironment).getOptions();
        verify(runnerEnvironment).getName();
        verify(dtoFactory, times(2)).createDto(RunOptions.class);
        verify(runOptions).withOptions(options);
        verify(modelsFactory).createRunner(runOptions, TEXT);

        //verify launch runner
        verify(panelState).setState(RUNNERS);
        verify(view).showOtherButtons();
        verify(history).addRunner(runner);
        verify(actionFactory).createCheckRamAndRun();
        verify(checkRamAndRunAction).perform(runner);
        verify(runner, times(2)).resetCreationTime();
    }

    @Test
    public void runnerShouldBeStoppedWhenButtonStopIsClicked() {
        StopAction stopAction = mock(StopAction.class);
        when(actionFactory.createStop()).thenReturn(stopAction);
        presenter.addRunner(processDescriptor);
        reset(view);

        presenter.onStopButtonClicked();

        verify(launchAction).stop();
        verify(actionFactory).createStop();
        verify(stopAction).perform(runner);
        verify(view).updateMoreInfoPopup(runner);
    }

    @Test
    public void runnerShouldBeStopped() {
        when(actionFactory.createStop()).thenReturn(stopAction);

        presenter.addRunner(processDescriptor);

        presenter.stopRunner(runner);
        verify(launchAction).stop();
        verify(stopAction).perform(runner);
    }

    @Test
    public void moreInfoPopupShouldBeShownWhenMouseIsOver() {
        presenter.addRunner(processDescriptor);

        presenter.onMoreInfoBtnMouseOver();

        verify(view).showMoreInfoPopup(runner);
    }

    @Test(expected = IllegalStateException.class)
    public void runnerShouldNotBeLaunchedIfCurrentProjectIsNull() {
        when(appContext.getCurrentProject()).thenReturn(null);

        presenter.launchRunner();
    }

    @Test
    public void runnerShouldNotBeLaunched() {
        presenter.launchRunner();

        verifyLaunchRunnerWithNotNullCurrentProject();
    }

    @Test
    public void shouldCreateAndLaunchRunnerFromRunOptions() {
        presenter.launchRunner(runOptions);

        verify(modelsFactory).createRunner(runOptions);
    }

    @Test
    public void shouldCreateAndLaunchRunnerFromRunOptionsAndEnvironmentName() {
        when(modelsFactory.createRunner(runOptions, TEXT)).thenReturn(runner);
        presenter.launchRunner(runOptions, TEXT);

        verify(modelsFactory).createRunner(runOptions, TEXT);
    }

    @Test
    public void presenterShouldGoneContainer() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);
        presenter.go(container);
        verify(container).setWidget(view);
    }

    @Test
    public void partStackShouldBeActive() {
        presenter.setPartStack(partStack);

        presenter.setActive();
        verify(partStack).getActivePart();
        verify(partStack).setActivePart(presenter);
    }

    @Test
    public void partStackShouldNotBeActive() {
        when(partStack.getActivePart()).thenReturn(presenter);
        presenter.setPartStack(partStack);

        presenter.setActive();

        verify(partStack).getActivePart();
        verify(partStack, never()).setActivePart(presenter);
    }

    @Test
    public void titleShouldBeReturned() {
        when(locale.runnerTitle()).thenReturn(TEXT);

        presenter.getTitle();

        verify(locale).runnerTitle();
        assertThat(presenter.getTitle(), is(TEXT));
    }

    @Test
    public void titleImageShouldBeReturned() {
        assertThat(presenter.getTitleImage(), nullValue());
    }

    @Test
    public void titleToolTipShouldBeReturned() {
        when(locale.tooltipRunnerPanel()).thenReturn(TEXT);

        presenter.getTitleToolTip();

        verify(locale).tooltipRunnerPanel();
        assertThat(presenter.getTitleToolTip(), is(TEXT));
    }

    @Test
    public void selectionShouldNotBeChangedWhenSelectionIsRunnerAndRunnerIsNull() {
        when(selectionManager.getRunner()).thenReturn(null);
        reset(history, rightTabContainer, view);

        presenter.onSelectionChanged(RUNNER);

        verify(selectionManager).getRunner();
        verifyNoMoreInteractions(history, rightTabContainer, view);
    }

    @Test
    public void selectionShouldBeChangedWhenSelectionIsRunner() {
        presenter.onSelectionChanged(RUNNER);

        verify(selectionManager).getRunner();

        verifyRunnerSelected();
    }

    @Test
    public void selectionShouldBeChangedWhenSelectionIsEnvironmentAndRunnerEnvironmentIsNull() {
        presenter.onSelectionChanged(ENVIRONMENT);

        verify(selectionManager).getEnvironment();
        verifyNoMoreInteractions(templates);
    }

    @Test
    public void selectionShouldBeChangedWhenSelectionIsEnvironment() {
        when(selectionManager.getEnvironment()).thenReturn(runnerEnvironment);
        presenter.onSelectionChanged(ENVIRONMENT);

        verify(selectionManager).getEnvironment();
        verify(templates).select(runnerEnvironment);
    }

    @Test
    public void actionsShouldBePerformedWhenCurrentProjectIsNotNull() {
        presenter.onProjectOpened(projectActionEvent);

        verify(view).setEnableRunButton(true);
        verify(templates).setVisible(true);
        verify(actionFactory).createGetRunningProcess();
        verify(appContext).getCurrentProject();
        verify(getRunningProcessAction).perform();
        verify(timer).schedule(ONE_SEC.getValue());
        verify(getSystemEnvironmentsAction).perform();
        verify(getProjectEnvironmentsAction).perform();
    }

    @Test
    public void runningProcessActionShouldNotBePerformedWhenCurrentProjectIsNull() {
        when(appContext.getCurrentProject()).thenReturn(null);

        presenter.onProjectOpened(projectActionEvent);

        verify(view).setEnableRunButton(true);
        verify(templates).setVisible(true);
        verify(actionFactory).createGetRunningProcess();
        verify(appContext).getCurrentProject();
        verifyNoMoreInteractions(getRunningProcessAction);
    }

    @Test
    public void projectShouldBeClosed() {
        presenter.addRunner(processDescriptor);
        presenter.onRunButtonClicked();
        presenter.setPartStack(partStack);
        presenter.onProjectOpened(projectActionEvent);

        presenter.onProjectClosed(projectActionEvent);

        verify(partStack).hidePart(presenter);
        verify(getRunningProcessAction).stop();
        verify(selectionManager).setRunner(null);
        verify(propertiesContainer).setVisible(false);
        verify(templates).setVisible(false);

        verify(view).setEnableRunButton(false);
        verify(view).setEnableStopButton(false);

        verify(view).setApplicationURl(null);
        verify(view).setTimeout(TIMER_STUB);
        verify(history).clear();

        verify(runnerCounter).reset();
        verify(terminalContainer).reset();
        verify(consoleContainer).reset();
        verify(propertiesContainer).reset();

    }

    private void verifyLaunchRunnerWithNotNullCurrentProject() {
        verify(appContext, times(2)).getCurrentProject();
        verify(currentProject).getProjectDescription();
        verify(descriptor).getType();
        verify(typeRegistry).getProjectType(TEXT);
        verify(definition).getRunnerCategories();
        verify(runner).setType(TEXT);

        verify(dtoFactory).createDto(RunOptions.class);
        verify(runOptions).withSkipBuild(true);
        verify(runOptions).withMemorySize(_512.getValue());
        verify(modelsFactory).createRunner(runOptions);

        //verify launch runner
        verify(panelState).setState(RUNNERS);
        verify(view).showOtherButtons();
        verify(history).addRunner(runner);
        verify(actionFactory).createCheckRamAndRun();
        verify(checkRamAndRunAction).perform(runner);
        verify(runner).resetCreationTime();
        verify(timer).schedule(ONE_SEC.getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void runnerShouldNotLaunchWhenCurrentProjectIsNull() {
        when(appContext.getCurrentProject()).thenReturn(null);
        presenter.launchRunner(runOptions);
    }

    private void verifyRunnerSelected() {
        verify(history).selectRunner(runner);
        verify(rightTabContainer).showTab(TEXT);

        //update
        verify(history).update(runner);
        verify(terminalContainer).update(runner);
        verify(view).update(runner);
        verify(runner).getStatus();
        verify(view).setApplicationURl(TEXT);

        //update runner timer
        verify(runner).getTimeout();
        verify(view).setTimeout(TEXT);
        verify(view).updateMoreInfoPopup(runner);
    }

    @Test
    public void timerShouldNotUpdateIfRunnerIsNull() {
        when(selectionManager.getRunner()).thenReturn(null);
        presenter.onSelectionChanged(RUNNER);
        reset(view, timer);

        ArgumentCaptor<TimerFactory.TimerCallBack> argumentCaptor = ArgumentCaptor.forClass(TimerFactory.TimerCallBack.class);
        verify(timerFactory).newInstance(argumentCaptor.capture());
        argumentCaptor.getValue().onRun();

        verifyNoMoreInteractions(view);
        verify(timer).schedule(ONE_SEC.getValue());
    }
}
