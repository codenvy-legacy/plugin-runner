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
package com.codenvy.ide.ext.runner.client.tabs.common.item;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGImage;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class ItemWidgetImplTest {
    private static final String TEST_TEXT = "some text for test";

    @Mock
    private RunnerResources           resources;
    @Mock
    private RunnerResources.RunnerCss css;

    @InjectMocks
    private ItemWidgetImpl itemWidget;

    @Before
    public void setUp() {
        when(resources.runnerCss()).thenReturn(css);
        when(css.runnerShadow()).thenReturn(TEST_TEXT);
        when(css.runnerWidgetBorders()).thenReturn(TEST_TEXT);
    }

    @Test
    public void shouldSelect() {
        itemWidget.select();

        verify(resources, times(2)).runnerCss();
        verify(css).runnerShadow();
        verify(css).runnerWidgetBorders();
    }

    @Test
    public void shouldUnSelect() {
        itemWidget.unSelect();

        verify(resources, times(2)).runnerCss();
        verify(css).runnerShadow();
        verify(css).runnerWidgetBorders();
    }

    @Test
    public void userClickEventShouldBeDelegated() throws Exception {
        ItemWidget.ActionDelegate delegate = mock(ItemWidget.ActionDelegate.class);

        itemWidget.setDelegate(delegate);
        itemWidget.onClick(mock(ClickEvent.class));

        verify(delegate).onWidgetClicked();
    }

    @Test
    public void shouldSetName() {
        itemWidget.setName(TEST_TEXT);

        verify(itemWidget.runnerName).setText(TEST_TEXT);
    }

    @Test
    public void shouldSetDescription() {
        itemWidget.setDescription(TEST_TEXT);

        verify(itemWidget.ram).setText(TEST_TEXT);
    }

    @Test
    public void shouldSetStartTime() {
        long time = 1424258546812l;
        String date = ItemWidgetImpl.DATE_TIME_FORMAT.format(new Date(time));

        itemWidget.setStartTime(time);

        verify(itemWidget.startTime).setText(date);
    }

    @Test
    public void shouldSetImage() {
        ImageResource imageResource = mock(ImageResource.class);

        itemWidget.setImage(imageResource);

        verify(itemWidget.image).setWidget(any(Image.class));
    }

    @Test
    public void shouldSetSVGImage() {
        SVGImage svgImageResource = mock(SVGImage.class);

        itemWidget.setImage(svgImageResource);

        verify(itemWidget.image).setWidget(any(SVGImage.class));
    }

}
