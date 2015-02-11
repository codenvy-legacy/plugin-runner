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
package com.codenvy.ide.ext.runner.client.manager.button;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ButtonWidgetImplTest {

    private static final String SOME_TEXT = "someText";

    @Mock(answer = RETURNS_DEEP_STUBS)
    private RunnerResources             resources;
    @Mock
    private ImageResource               image;
    @Mock
    private ButtonWidget.ActionDelegate delegate;
    @Mock
    private ClickEvent                  clickEvent;

    @InjectMocks
    private ButtonWidgetImpl button;

    @Before
    public void setUp() throws Exception {
        when(resources.runnerCss().opacityButton()).thenReturn(SOME_TEXT);

        button.setDelegate(delegate);
    }

    @Test
    public void imageShouldBeSet() throws Exception {
        button.image.setResource(image);
    }

    @Test
    public void buttonShouldBeDisable() throws Exception {
        button.setDisable();

        verify(button.image).addStyleName(SOME_TEXT);
    }

    @Test
    public void buttonShouldBeEnabled() throws Exception {
        button.setEnable();

        verify(button.image).removeStyleName(SOME_TEXT);
    }

    @Test
    public void buttonActionShouldBeDoneWhenItIsEnabled() throws Exception {
        button.setEnable();

        button.onClick(clickEvent);

        verify(delegate).onButtonClicked();
    }

    @Test
    public void buttonActionShouldNotBeDoneWhenItIsDisabled() throws Exception {
        button.setDisable();

        button.onClick(clickEvent);

        verify(delegate, never()).onButtonClicked();
    }

}