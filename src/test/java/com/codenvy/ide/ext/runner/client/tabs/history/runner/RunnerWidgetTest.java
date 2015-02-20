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
package com.codenvy.ide.ext.runner.client.tabs.history.runner;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.tabs.common.item.ItemWidget;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunnerWidgetTest {
    private static final String TEXT          = "text";
    private static final long   CREATION_TIME = 1234567L;
    @Mock
    private ItemWidget       itemWidget;
    @Mock
    private RunnerResources  resources;
    @Mock
    private SelectionManager selectionManager;

    @Mock
    private Runner                    runner;
    @Mock
    private RunnerResources.RunnerCss css;

    private RunnerWidget runnerWidget;

    @Before
    public void setUp() {
        SVGResource svgResource = mock(SVGResource.class, RETURNS_DEEP_STUBS);

        when(resources.runnerInProgressImage()).thenReturn(svgResource);
        when(resources.runnerInQueueImage()).thenReturn(svgResource);
        when(resources.runnerFailedImage()).thenReturn(svgResource);
        when(resources.runnerTimeoutImage()).thenReturn(svgResource);
        when(resources.runnerDoneImage()).thenReturn(svgResource);
        when(resources.runnerDoneImage()).thenReturn(svgResource);

        runnerWidget = new RunnerWidget(itemWidget, resources, selectionManager);

        when(resources.runnerCss()).thenReturn(css);
        when(runner.getTitle()).thenReturn(TEXT);
        when(runner.getRAM()).thenReturn(RAM._512.getValue());
        when(runner.getCreationTime()).thenReturn(CREATION_TIME);
    }

    @Test
    public void shouldVerifyConstructor() {
        ArgumentCaptor<ItemWidget.ActionDelegate> actionDelegateCaptor =
                ArgumentCaptor.forClass(ItemWidget.ActionDelegate.class);

        verify(resources).runnerInProgressImage();
        verify(resources).runnerInQueueImage();
        verify(resources).runnerFailedImage();
        verify(resources).runnerTimeoutImage();
        verify(resources, times(2)).runnerDoneImage();

        verify(itemWidget).setDelegate(actionDelegateCaptor.capture());
        ItemWidget.ActionDelegate actionDelegate = actionDelegateCaptor.getValue();
        actionDelegate.onWidgetClicked();

        verify(selectionManager).setRunner(any(Runner.class));
    }

    @Test
    public void shouldSelect() {
        runnerWidget.select();

        verify(itemWidget).select();
    }

    @Test
    public void shouldUnSelect() {
        runnerWidget.unSelect();

        verify(itemWidget).unSelect();
    }

    @Test
    public void shouldUpdateRunnerWithStatusInProgress() {
        when(runner.getStatus()).thenReturn(Runner.Status.IN_PROGRESS);
        when(css.blueColor()).thenReturn(TEXT);

        runnerWidget.update(runner);

        verify(css).blueColor();

        shouldUpdateItemWidgetParameter();
    }

    @Test
    public void shouldUpdateRunnerWithStatusQueue() {
        when(runner.getStatus()).thenReturn(Runner.Status.IN_QUEUE);
        when(css.blueColor()).thenReturn(TEXT);

        runnerWidget.update(runner);

        verify(css).yellowColor();

        shouldUpdateItemWidgetParameter();
    }

    @Test
    public void shouldUpdateRunnerWithStatusFailed() {
        when(runner.getStatus()).thenReturn(Runner.Status.FAILED);
        when(css.blueColor()).thenReturn(TEXT);

        runnerWidget.update(runner);

        verify(css).redColor();

        shouldUpdateItemWidgetParameter();
    }

    @Test
    public void shouldUpdateRunnerWithStatusTimeOut() {
        when(runner.getStatus()).thenReturn(Runner.Status.TIMEOUT);
        when(css.blueColor()).thenReturn(TEXT);

        runnerWidget.update(runner);

        verify(css).whiteColor();

        shouldUpdateItemWidgetParameter();
    }

    @Test
    public void shouldUpdateRunnerWithStatusStopped() {
        when(runner.getStatus()).thenReturn(Runner.Status.STOPPED);
        when(css.blueColor()).thenReturn(TEXT);

        runnerWidget.update(runner);

        verify(css).redColor();

        shouldUpdateItemWidgetParameter();
    }

    @Test
    public void shouldUpdateRunnerWithStatusDone() {
        when(runner.getStatus()).thenReturn(Runner.Status.DONE);
        when(css.blueColor()).thenReturn(TEXT);

        runnerWidget.update(runner);

        verify(css).greenColor();

        shouldUpdateItemWidgetParameter();
    }

    @Test
    public void shouldAsWidget() {
        runnerWidget.asWidget();

        verify(itemWidget).asWidget();
    }

    private void shouldUpdateItemWidgetParameter() {
        verify(itemWidget).setImage(any(SVGImage.class));
        verify(runner).getTitle();
        verify(itemWidget).setName(TEXT);
        verify(runner).getRAM();
        verify(itemWidget).setDescription(RAM._512.toString());
        verify(runner).getCreationTime();
        verify(itemWidget).setStartTime(CREATION_TIME);
    }
}