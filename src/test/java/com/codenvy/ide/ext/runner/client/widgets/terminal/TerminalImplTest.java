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
package com.codenvy.ide.ext.runner.client.widgets.terminal;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class TerminalImplTest {

    private static final String SOME_TEXT = "some text";

    @Mock
    private Runner       runner;
    @Mock
    private Element      element;
    @InjectMocks
    private TerminalImpl terminal;

    @Before
    public void setUp() throws Exception {
        when(terminal.terminal.getElement()).thenReturn(element);
    }

    @Test
    public void terminalShouldBeHiddenIfRunnerIsNull() throws Exception {
        terminal.update(null);

        verify(terminal.unavailableLabel).setVisible(true);
        verify(element).removeAttribute("src");
    }

    @Test
    public void terminalContentShouldBeUpdated() throws Exception {
        when(runner.isAnyAppRunning()).thenReturn(true);
        when(runner.getTerminalURL()).thenReturn(SOME_TEXT);

        terminal.update(runner);

        verify(terminal.unavailableLabel).setVisible(false);

        when(runner.getTerminalURL()).thenReturn(SOME_TEXT + SOME_TEXT);

        terminal.update(runner);

        verify(terminal.terminal).setUrl(SOME_TEXT + SOME_TEXT);
    }

    @Test
    public void unavailableLabelShouldBeShoved() throws Exception {
        when(runner.isAnyAppRunning()).thenReturn(false);
        when(runner.getTerminalURL()).thenReturn(SOME_TEXT);

        terminal.update(runner);

        verify(element).removeAttribute("src");
        verify(terminal.unavailableLabel).setVisible(true);

        when(runner.getTerminalURL()).thenReturn(SOME_TEXT + SOME_TEXT);

        terminal.update(runner);

        verify(terminal.terminal, never()).setUrl(anyString());
    }

    @Test
    public void terminalShouldNotBeUpdate() throws Exception {
        when(runner.getTerminalURL()).thenReturn(SOME_TEXT);
        when(runner.isAnyAppRunning()).thenReturn(true);

        terminal.update(runner);
        terminal.update(runner);

        verify(terminal.terminal, times(1)).setUrl(anyString());
        verify(element, never()).removeAttribute("src");
    }

    @Test
    public void terminalShouldBeVisible() throws Exception {
        terminal.setTerminalVisible(true);

        verify(terminal.terminal).setVisible(true);
    }

    @Test
    public void unavailableLabelShouldBeVisible() throws Exception {
        terminal.setUnavailableLabelVisible(true);

        verify(terminal.unavailableLabel).setVisible(true);
    }

}