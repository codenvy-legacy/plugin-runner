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
package com.codenvy.ide.ext.runner.client.widgets.runner;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.widgets.runner.RunnerWidget.ActionDelegate;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Date;

import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.MEMORY_128;
import static com.codenvy.ide.ext.runner.client.widgets.runner.RunnerWidgetImpl.DATE_TIME_FORMAT;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Plotnikov
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunnerWidgetImplTest {

    private static final String SOME_TEXT = "some text";

    @Mock
    private Element          imageElement;
    @Mock
    private Runner           runner;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources  resources;
    @InjectMocks
    private RunnerWidgetImpl widget;

    private String expectedFormat;

    @Before
    public void setUp() throws Exception {
        Date currentDate = new Date();
        expectedFormat = DATE_TIME_FORMAT.format(currentDate);

        when(runner.getTitle()).thenReturn(SOME_TEXT);
        when(runner.getRAM()).thenReturn(MEMORY_128.getValue());
        when(runner.getCreationTime()).thenReturn(currentDate.getTime());

        imageElement = mock(Element.class);
        when(widget.image.getElement()).thenReturn(imageElement);
    }

    @Test
    public void imagesShouldBeCreated() throws Exception {
        verify(resources).runnerInProgressImage();
        verify(resources).runnerInQueueImage();
        verify(resources).runnerFailedImage();
        verify(resources).runnerTimeoutImage();
        verify(resources, times(2)).runnerDoneImage();
    }

    @Test
    public void widgetShouldBeSelected() throws Exception {
        widget.select();

        RunnerResources.RunnerCss css = resources.runnerCss();
        verify(css).runnerShadow();
        verify(css).runnerWidgetBorders();
    }

    @Test
    public void widgetShouldBeUnSelected() throws Exception {
        widget.unSelect();

        RunnerResources.RunnerCss css = resources.runnerCss();
        verify(css).runnerShadow();
        verify(css).runnerWidgetBorders();
    }

    @Test
    public void userClickEventShouldBeDelegated() throws Exception {
        ActionDelegate delegate = mock(ActionDelegate.class);
        when(runner.getStatus()).thenReturn(Runner.Status.DONE);

        widget.setDelegate(delegate);
        widget.update(runner);

        widget.onClick(mock(ClickEvent.class));

        verify(delegate).onRunnerSelected(runner);
    }

    @Test
    public void specialStyleShouldBeAppliedWhenRunnerHasInProgressStatus() throws Exception {
        when(runner.getStatus()).thenReturn(Runner.Status.IN_PROGRESS);

        widget.update(runner);

        verify(widget.runnerName).setText(SOME_TEXT);
        verify(widget.ram).setText(MEMORY_128.toString());
        verify(widget.startTime).setText(expectedFormat);

        verify(resources.runnerCss()).blueColor();
        verify(imageElement).setInnerHTML(anyString());
    }

    @Test
    public void specialStyleShouldBeAppliedWhenRunnerHasInQueueStatus() throws Exception {
        when(runner.getStatus()).thenReturn(Runner.Status.IN_QUEUE);

        widget.update(runner);

        verify(widget.runnerName).setText(SOME_TEXT);
        verify(widget.ram).setText(MEMORY_128.toString());
        verify(widget.startTime).setText(expectedFormat);

        verify(resources.runnerCss()).yellowColor();
        verify(imageElement).setInnerHTML(anyString());
    }

    @Test
    public void specialStyleShouldBeAppliedWhenRunnerHasFailedStatus() throws Exception {
        when(runner.getStatus()).thenReturn(Runner.Status.FAILED);

        widget.update(runner);

        verify(widget.runnerName).setText(SOME_TEXT);
        verify(widget.ram).setText(MEMORY_128.toString());
        verify(widget.startTime).setText(expectedFormat);

        verify(resources.runnerCss()).redColor();
        verify(imageElement).setInnerHTML(anyString());
    }

    @Test
    public void specialStyleShouldBeAppliedWhenRunnerHasTimeoutStatus() throws Exception {
        when(runner.getStatus()).thenReturn(Runner.Status.TIMEOUT);

        widget.update(runner);

        verify(widget.runnerName).setText(SOME_TEXT);
        verify(widget.ram).setText(MEMORY_128.toString());
        verify(widget.startTime).setText(expectedFormat);

        verify(resources.runnerCss()).whiteColor();
        verify(imageElement).setInnerHTML(anyString());
    }

    @Test
    public void specialStyleShouldBeAppliedWhenRunnerHasStoppedStatus() throws Exception {
        when(runner.getStatus()).thenReturn(Runner.Status.STOPPED);

        widget.update(runner);

        verify(widget.runnerName).setText(SOME_TEXT);
        verify(widget.ram).setText(MEMORY_128.toString());
        verify(widget.startTime).setText(expectedFormat);

        verify(resources.runnerCss()).redColor();
        verify(imageElement).setInnerHTML(anyString());
    }

    @Test
    public void specialStyleShouldBeAppliedWhenRunnerHasDoneStatus() throws Exception {
        when(runner.getStatus()).thenReturn(Runner.Status.DONE);

        widget.update(runner);

        verify(widget.runnerName).setText(SOME_TEXT);
        verify(widget.ram).setText(MEMORY_128.toString());
        verify(widget.startTime).setText(expectedFormat);

        verify(resources.runnerCss()).greenColor();
        verify(imageElement).setInnerHTML(anyString());
    }

    @Test
    public void noSpecialStyleShouldBeAppliedWhenRunnerHasUnusedStatus() throws Exception {
        when(runner.getStatus()).thenReturn(Runner.Status.RUNNING);

        widget.update(runner);

        verify(widget.runnerName).setText(SOME_TEXT);
        verify(widget.ram).setText(MEMORY_128.toString());
        verify(widget.startTime).setText(expectedFormat);

        verify(imageElement, never()).setInnerHTML(anyString());
        verifyNoMoreInteractions(resources.runnerCss());
    }

}