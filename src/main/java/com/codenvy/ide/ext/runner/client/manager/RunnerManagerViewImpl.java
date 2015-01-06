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
import com.codenvy.ide.ext.runner.client.inject.factories.ConsoleFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.widgets.console.Console;
import com.codenvy.ide.ext.runner.client.widgets.runner.RunnerWidget;
import com.codenvy.ide.ext.runner.client.widgets.terminal.Terminal;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
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

    //runners panel
    @UiField
    FlowPanel runnersPanel;

    //buttons panel
    @UiField
    FlowPanel runPanel;
    @UiField
    FlowPanel stopPanel;
    @UiField
    FlowPanel cleanPanel;
    @UiField
    FlowPanel dockerPanel;

    //tab panel
    @UiField
    FlowPanel consoleTab;
    @UiField
    FlowPanel terminalTab;

    //info panel
    @UiField
    Label appReference;
    @UiField
    Label timeout;

    //print area
    @UiField
    FlowPanel mainArea;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    private final Provider<RunnerWidget>    runnerViewProvider;
    private final ConsoleFactory            consoleFactory;
    private final Provider<Terminal>        terminalProvider;
    private final Map<Runner, Console>      consoles;
    private final Map<Runner, Terminal>     terminals;
    private final Map<Runner, RunnerWidget> runnerWidgets;

    private String url;

    @Inject
    public RunnerManagerViewImpl(PartStackUIResources partStackUIResources,
                                 RunnerManagerViewImplUiBinder uiBinder,
                                 RunnerResources resources,
                                 RunnerLocalizationConstant locale,
                                 Provider<RunnerWidget> runnerViewProvider,
                                 ConsoleFactory consoleFactory,
                                 Provider<Terminal> terminalProvider) {
        super(partStackUIResources);

        this.resources = resources;
        this.locale = locale;
        this.runnerViewProvider = runnerViewProvider;
        this.consoleFactory = consoleFactory;
        this.terminalProvider = terminalProvider;

        this.consoles = new HashMap<>();
        this.terminals = new HashMap<>();
        this.runnerWidgets = new HashMap<>();

        container.add(uiBinder.createAndBindUi(this));
        titleLabel.setText(locale.runnersPanelTitle());

        initializePanelsEvents();
    }

    private void initializePanelsEvents() {
        runPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onRunButtonClicked();
            }
        }, ClickEvent.getType());

        stopPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onStopButtonClicked();
            }
        }, ClickEvent.getType());

        cleanPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCleanConsoleButtonClicked();
            }
        }, ClickEvent.getType());

        dockerPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onReceiptButtonClicked();
            }
        }, ClickEvent.getType());

        consoleTab.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onConsoleButtonClicked();
            }
        }, ClickEvent.getType());

        terminalTab.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onTerminalButtonClicked();
            }
        }, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void onRunnerSelected(@Nonnull Runner runner) {
        delegate.onRunnerSelected(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        Terminal terminal = terminals.get(runner);

        if (terminal != null) {
            terminal.update(runner);
        }

        RunnerWidget runnerWidget = runnerWidgets.get(runner);

        if (runnerWidget != null) {
            runnerWidget.update(runner);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addRunner(@Nonnull Runner runner) {
        RunnerWidget runnerWidget = runnerViewProvider.get();
        runnerWidgets.put(runner, runnerWidget);

        runnerWidget.update(runner);
        runnerWidget.setDelegate(this);

        runnersPanel.add(runnerWidget);

        Console console = consoleFactory.createConsole(runner);
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
    public void activateConsole(@Nonnull Runner runner) {
        terminalTab.removeStyleName(resources.runnerCss().activeTab());
        consoleTab.addStyleName(resources.runnerCss().activeTab());

        Console console = consoles.get(runner);
        if (console == null) {
            mainArea.clear();
            return;
        }

        mainArea.clear();
        mainArea.add(console);

        console.scrollBottom();
    }

    /** {@inheritDoc} */
    @Override
    public void activateTerminal(@Nonnull Runner runner) {
        consoleTab.removeStyleName(resources.runnerCss().activeTab());
        terminalTab.addStyleName(resources.runnerCss().activeTab());

        Terminal terminal = terminals.get(runner);
        if (terminal == null) {
            terminal = terminalProvider.get();
            terminal.update(runner);

            terminals.put(runner, terminal);
        }

        mainArea.clear();
        mainArea.add(terminal);
    }

    @UiHandler("appReference")
    public void onAppReferenceClicked(@SuppressWarnings("UnusedParameters") ClickEvent clickEvent) {
        Window.open(url, "_blank", "");
    }
}