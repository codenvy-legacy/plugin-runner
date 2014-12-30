package com.codenvy.ide.ext.runner.client.widgets.terminal;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Plotnikov
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
    public void unavilableMessageShouldBeShownWhenApplicationIsNotRunning() throws Exception {
        terminal.update(runner);

        verify(terminal.unavailableLabel).setVisible(true);
        verify(terminal.terminal).setVisible(false);

        verify(element).removeAttribute("src");
    }

    @Test
    public void unavilableMessageShouldBeShownWhenApplicationURLIsNull() throws Exception {
        when(runner.isAnyAppRunning()).thenReturn(true);

        terminal.update(runner);

        verify(terminal.unavailableLabel).setVisible(true);
        verify(terminal.terminal).setVisible(false);

        verify(element).removeAttribute("src");
    }

    @Test
    public void terminalShouldBeShownWhenAllParameterAreExist() throws Exception {
        when(runner.isAnyAppRunning()).thenReturn(true);
        when(runner.getTerminalURL()).thenReturn(SOME_TEXT);

        terminal.update(runner);

        verify(terminal.unavailableLabel).setVisible(false);
        verify(terminal.terminal).setVisible(true);

        verify(terminal.terminal).setUrl(SOME_TEXT);
    }

}