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
package com.codenvy.ide.ext.runner.client.manager;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.widgets.button.ButtonWidget;
import com.codenvy.ide.ext.runner.client.widgets.console.Console;
import com.codenvy.ide.ext.runner.client.widgets.runner.RunnerWidget;
import com.codenvy.ide.ext.runner.client.widgets.tab.TabWidget;
import com.codenvy.ide.ext.runner.client.widgets.terminal.Terminal;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Class provides view representation of runner panel.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class RunnerManagerViewImpl extends BaseView<RunnerManagerView.ActionDelegate> implements RunnerManagerView,
                                                                                                 RunnerWidget.ActionDelegate {

    @Singleton
    interface RunnerManagerViewImplUiBinder extends UiBinder<Widget, RunnerManagerViewImpl> {
    }

    @UiField
    FlowPanel runnersPanel;
    @UiField
    FlowPanel buttonsPanel;
    @UiField
    FlowPanel tabsPanel;
    @UiField
    FlowPanel textArea;

    //info panel
    @UiField
    Label appReference;
    @UiField
    Label timeout;
    @UiField
    Image moreInfo;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    private final WidgetFactory             widgetFactory;
    private final Map<Runner, Console>      consoles;
    private final Map<Runner, Terminal>     terminals;
    private final Map<Runner, RunnerWidget> runnerWidgets;

    private TabWidget consoleTab;
    private TabWidget terminalTab;

    private ButtonWidget run;
    private ButtonWidget stop;
    private ButtonWidget clean;
    private ButtonWidget docker;

    private String url;

    @Inject
    public RunnerManagerViewImpl(PartStackUIResources partStackUIResources,
                                 RunnerManagerViewImplUiBinder uiBinder,
                                 RunnerResources resources,
                                 RunnerLocalizationConstant locale,
                                 WidgetFactory widgetFactory) {
        super(partStackUIResources);

        this.resources = resources;
        this.locale = locale;
        this.widgetFactory = widgetFactory;

        titleLabel.setText(locale.runnersPanelTitle());
        container.add(uiBinder.createAndBindUi(this));

        this.consoles = new HashMap<>();
        this.terminals = new HashMap<>();
        this.runnerWidgets = new HashMap<>();

        initializeTabs();

        initializeButtons();
    }

    private void initializeTabs() {
        TabWidget.ActionDelegate consoleDelegate = new TabWidget.ActionDelegate() {
            @Override
            public void onMouseClicked() {
                delegate.onConsoleButtonClicked();
            }
        };
        consoleTab = createTab(locale.runnerTabConsole(), consoleDelegate);
        consoleTab.select();

        TabWidget.ActionDelegate terminalDelegate = new TabWidget.ActionDelegate() {
            @Override
            public void onMouseClicked() {
                delegate.onTerminalButtonClicked();
            }
        };
        terminalTab = createTab(locale.runnerTabTerminal(), terminalDelegate);
    }

    @Nonnull
    private TabWidget createTab(@Nonnull String tabName, @Nonnull TabWidget.ActionDelegate actionDelegate) {
        TabWidget tab = widgetFactory.createTab(tabName);
        tab.setDelegate(actionDelegate);

        tabsPanel.add(tab);

        return tab;
    }

    private void initializeButtons() {
        ButtonWidget.ActionDelegate runDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onRunButtonClicked();
            }
        };
        run = createButton(resources.runButton(), runDelegate);

        ButtonWidget.ActionDelegate stopDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onStopButtonClicked();
            }
        };
        stop = createButton(resources.stopButton(), stopDelegate);

        ButtonWidget.ActionDelegate cleanDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onCleanConsoleButtonClicked();
            }
        };
        clean = createButton(resources.cleanButton(), cleanDelegate);

        ButtonWidget.ActionDelegate dockerDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onDockerButtonClicked();
            }
        };
        docker = createButton(resources.dockerButton(), dockerDelegate);
    }

    @Nonnull
    private ButtonWidget createButton(@Nonnull ImageResource icon, @Nonnull ButtonWidget.ActionDelegate delegate) {
        ButtonWidget button = widgetFactory.createButton(icon);
        button.setDelegate(delegate);
        button.setDisable();

        buttonsPanel.add(button);

        return button;
    }

    /** {@inheritDoc} */
    @Override
    public void onRunnerSelected(@Nonnull Runner runner) {
        delegate.onRunnerSelected(runner);

        RunnerWidget widget = runnerWidgets.get(runner);
        selectWidget(widget);
    }

    private void selectWidget(@Nonnull RunnerWidget selectWidget) {
        for (RunnerWidget widget : runnerWidgets.values()) {
            widget.unSelect();
        }

        selectWidget.select();
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        changeButtonsState(runner);

        Terminal terminal = terminals.get(runner);
        if (terminal != null) {
            terminal.update(runner);
        }

        RunnerWidget runnerWidget = runnerWidgets.get(runner);
        if (runnerWidget != null) {
            runnerWidget.update(runner);
        }
    }

    private void changeButtonsState(@Nonnull Runner runner) {
        run.setEnable();
        stop.setEnable();
        clean.setEnable();
        docker.setEnable();

        switch (runner.getStatus()) {
            case IN_PROGRESS:
                run.setDisable();
                break;

            case IN_QUEUE:
                run.setDisable();
                docker.setDisable();
                stop.setDisable();
                break;

            case FAILED:
                stop.setDisable();
                docker.setDisable();
                break;

            case RUNNING:
                run.setDisable();
                break;

            case STOPPED:
                stop.setDisable();
                break;

            case DONE:
                run.setDisable();
                break;

            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addRunner(@Nonnull Runner runner) {
        RunnerWidget runnerWidget = widgetFactory.createRunner();
        runnerWidget.update(runner);
        runnerWidget.setDelegate(this);

        runnerWidgets.put(runner, runnerWidget);
        runnersPanel.add(runnerWidget);

        selectWidget(runnerWidget);

        Console console = widgetFactory.createConsole(runner);
        consoles.put(runner, console);
        activateConsole(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationURl(@Nullable String url) {
        this.url = url;
        appReference.setText(url);
    }

    /** {@inheritDoc} */
    @Override
    public void setTimeout(@Nonnull String timeout) {

    }

    /** {@inheritDoc} */
    @Override
    public void printMessage(@Nonnull Runner runner, @Nonnull String line) {
        Console console = consoles.get(runner);
        if (console == null) {
            return;
        }

        console.printMessage(line);
    }

    /** {@inheritDoc} */
    @Override
    public void printInfo(@Nonnull Runner runner, @Nonnull String line) {
        Console console = consoles.get(runner);
        if (console == null) {
            return;
        }

        console.printInfo(line);
    }

    /** {@inheritDoc} */
    @Override
    public void printError(@Nonnull Runner runner, @Nonnull String line) {
        Console console = consoles.get(runner);
        if (console == null) {
            return;
        }

        console.printError(line);
    }

    /** {@inheritDoc} */
    @Override
    public void printWarn(@Nonnull Runner runner, @Nonnull String line) {
        Console console = consoles.get(runner);
        if (console == null) {
            return;
        }

        console.printWarn(line);
    }

    /** {@inheritDoc} */
    @Override
    public void printDocker(@Nonnull Runner runner, @Nonnull String line) {
        Console console = consoles.get(runner);
        if (console == null) {
            return;
        }

        console.printDocker(line);
    }

    /** {@inheritDoc} */
    @Override
    public void printStdOut(@Nonnull Runner runner, @Nonnull String line) {
        Console console = consoles.get(runner);
        if (console == null) {
            return;
        }

        console.printStdOut(line);
    }

    /** {@inheritDoc} */
    @Override
    public void printStdErr(@Nonnull Runner runner, @Nonnull String line) {
        Console console = consoles.get(runner);
        if (console == null) {
            return;
        }

        console.printStdErr(line);
    }

    /** {@inheritDoc} */
    @Override
    public void clearConsole(@Nonnull Runner runner) {
        Console console = consoles.get(runner);
        if (console == null) {
            return;
        }

        console.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void activateConsole(@Nonnull Runner runner) {
        consoleTab.select();
        terminalTab.unSelect();

        for (Console console : consoles.values()) {
            console.setVisible(false);
        }

        Console console = consoles.get(runner);
        if (console == null) {
            textArea.clear();
            return;
        }

        console.setVisible(true);
        textArea.add(console);

        console.scrollBottom();
    }


    /** {@inheritDoc} */
    @Override
    public void activateTerminal(@Nonnull Runner runner) {
        terminalTab.select();
        consoleTab.unSelect();

        for (Console console : consoles.values()) {
            console.setVisible(false);
        }

        for (Terminal terminal : terminals.values()) {
            terminal.setTerminalVisible(false);
            terminal.setUnavailableLabelVisible(false);
        }

        Terminal terminal = terminals.get(runner);
        if (terminal == null) {
            terminal = widgetFactory.createTerminal();
            terminal.update(runner);

            terminals.put(runner, terminal);

            textArea.add(terminal);
        } else {
            boolean isAnyAppRun = runner.isAnyAppRunning();

            terminal.setTerminalVisible(isAnyAppRun);
            terminal.setUnavailableLabelVisible(!isAnyAppRun);
        }

    }

    @UiHandler("appReference")
    public void onAppReferenceClicked(@SuppressWarnings("UnusedParameters") ClickEvent clickEvent) {
        Window.open(url, "_blank", "");
    }
}