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
package com.codenvy.ide.ext.runner.client.widgets.console;

import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Plotnikov
 */
@RunWith(GwtMockitoTestRunner.class)
public class ConsoleImplTest {

    private static final String SOME_TEXT = "some text";

    @Captor
    private ArgumentCaptor<HTML> htmlArgumentCaptor;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources            resources;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private Runner                     runner;
    @InjectMocks
    private ConsoleImpl                console;

    @Test
    public void infoMessageShouldBePrinted() throws Exception {
        console.printInfo(SOME_TEXT);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void errorMessageShouldBePrinted() throws Exception {
        console.printError(SOME_TEXT);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void warningMessageShouldBePrinted() throws Exception {
        console.printWarn(SOME_TEXT);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void dockerMessageShouldBePrinted() throws Exception {
        console.printDocker(SOME_TEXT);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void dockerErrorMessageShouldBePrinted() throws Exception {
        console.printDockerError(SOME_TEXT);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void stdoutMessageShouldBePrinted() throws Exception {
        console.printStdOut(SOME_TEXT);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void stderrMessageShouldBePrinted() throws Exception {
        console.printStdErr(SOME_TEXT);

        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void messageShouldBeCleanedWhenTheAmoutIs1000AndLogUrlIsAbsent() throws Exception {
        when(console.output.getWidgetCount()).thenReturn(1000);

        console.printInfo(SOME_TEXT);

        verify(console.output, times(100)).remove(0);
        verify(console.output, never()).insert(any(HTML.class), eq(0));
        verify(console.output).add(any(HTML.class));
    }

    @Test
    public void messageShouldBeCleanedWhenTheAmoutIs1000AndLogUrlIsExist() throws Exception {
        when(console.output.getWidgetCount()).thenReturn(1000);
        when(runner.getLogUrl()).thenReturn(SOME_TEXT);

        console.printInfo(SOME_TEXT);

        verify(console.output, times(100)).remove(0);
        verify(console.output).insert(any(HTML.class), eq(0));
        verify(console.output).add(any(HTML.class));

        verify(locale).fullLogTraceConsoleLink();

        verify(resources.runnerCss()).logLink();
    }

    @Test
    public void consoleShouldBeCleaned() throws Exception {
        console.clear();

        verify(console.output).clear();
    }
}