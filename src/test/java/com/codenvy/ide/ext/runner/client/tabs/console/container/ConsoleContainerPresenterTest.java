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
package com.codenvy.ide.ext.runner.client.tabs.console.container;

import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.selection.Selection;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.tabs.console.panel.Console;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class ConsoleContainerPresenterTest {
    private static final String MESSAGE = "message";
    private static final String MESSAGE2 = "message";

    @Mock
    private ConsoleContainerView view;
    @Mock
    private SelectionManager     selectionManager;
    @Mock
    private WidgetFactory        widgetFactory;

    @Mock
    private Runner  runner;
    @Mock
    private Console console;

    @InjectMocks
    private ConsoleContainerPresenter consoleContainerPresenter;

    @Before
    public void setUp() {
        when(widgetFactory.createConsole(runner)).thenReturn(console);
    }

    @Test
    public void shouldPrintWhenConsoleIsNull() {
        consoleContainerPresenter.print(runner, MESSAGE);

        verify(widgetFactory).createConsole(runner);
        verify(console).print(MESSAGE);
    }

    @Test
    public void shouldPrintWhenConsoleIsNotNull() {
        consoleContainerPresenter.print(runner, MESSAGE);

        reset(widgetFactory, console);

        consoleContainerPresenter.print(runner, MESSAGE2);

        verify(widgetFactory, never()).createConsole(runner);
        verify(console).print(MESSAGE);
    }

    @Test
    public void shouldPrintInfoWhenConsoleIsNull() {
        consoleContainerPresenter.printInfo(runner, MESSAGE);

        verify(widgetFactory).createConsole(runner);
        verify(console).printInfo(MESSAGE);
    }

    @Test
    public void shouldPrintInfoWhenConsoleIsNotNull() {
        consoleContainerPresenter.printInfo(runner, MESSAGE);

        reset(widgetFactory, console);

        consoleContainerPresenter.printInfo(runner, MESSAGE2);

        verify(widgetFactory, never()).createConsole(runner);
        verify(console).printInfo(MESSAGE);
    }

    @Test
    public void shouldPrintErrorWhenConsoleIsNull() {
        consoleContainerPresenter.printError(runner, MESSAGE);

        verify(widgetFactory).createConsole(runner);
        verify(console).printError(MESSAGE);
    }

    @Test
    public void shouldPrintErrorWhenConsoleIsNotNull() {
        consoleContainerPresenter.printError(runner, MESSAGE);

        reset(widgetFactory, console);

        consoleContainerPresenter.printError(runner, MESSAGE2);

        verify(widgetFactory, never()).createConsole(runner);
        verify(console).printError(MESSAGE);
    }

    @Test
    public void shouldPrintWarnWhenConsoleIsNull() {
        consoleContainerPresenter.printWarn(runner, MESSAGE);

        verify(widgetFactory).createConsole(runner);
        verify(console).printWarn(MESSAGE);
    }

    @Test
    public void shouldPrintWarnWhenConsoleIsNotNull() {
        consoleContainerPresenter.printWarn(runner, MESSAGE);

        reset(widgetFactory, console);

        consoleContainerPresenter.printWarn(runner, MESSAGE2);

        verify(widgetFactory, never()).createConsole(runner);
        verify(console).printWarn(MESSAGE);
    }

    @Test
    public void shouldClearWhenConsoleIsNull() {
        consoleContainerPresenter.clear(runner);

        verify(widgetFactory).createConsole(runner);
        verify(console).clear();
    }

    @Test
    public void shouldClearWhenConsoleIsNotNull() {
        consoleContainerPresenter.clear(runner);

        reset(widgetFactory, console);

        consoleContainerPresenter.clear(runner);

        verify(widgetFactory, never()).createConsole(runner);
        verify(console).clear();
    }

    @Test
    public void shouldOnSelectionChangedWhenSelectionIsEnvironment() {
        consoleContainerPresenter.onSelectionChanged(Selection.ENVIRONMENT);

        verify(selectionManager, never()).getRunner();
        verify(view, never()).showWidget(any(IsWidget.class));
    }

    @Test
    public void shouldOnSelectionChangedWhenSelectionIsRunnerIsNull() {
        consoleContainerPresenter.onSelectionChanged(Selection.RUNNER);

        verify(selectionManager).getRunner();
        verify(view, never()).showWidget(any(IsWidget.class));
    }

    @Test
    public void shouldOnSelectionChangedWhenSelectionIsRunner() {
        when(selectionManager.getRunner()).thenReturn(runner);

        consoleContainerPresenter.onSelectionChanged(Selection.RUNNER);

        verify(selectionManager).getRunner();
        verify(view).showWidget(console);
    }

    @Test
    public void shouldGetView() {
        assertThat(consoleContainerPresenter.getView(), CoreMatchers.<IsWidget>is(view));
    }

    @Test
    public void shouldSetVisibleTrue() {
        consoleContainerPresenter.setVisible(true);
        verify(view).setVisible(true);
    }

    @Test
    public void shouldSetVisibleFalse() {
        consoleContainerPresenter.setVisible(false);
        verify(view).setVisible(false);
    }

    @Test
    public void shouldGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        consoleContainerPresenter.go(container);

        verify(container).setWidget(view);
    }
}
