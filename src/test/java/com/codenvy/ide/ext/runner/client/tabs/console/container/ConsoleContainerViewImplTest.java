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

import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.tabs.console.button.ConsoleButton;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class ConsoleContainerViewImplTest {
    private static final String SOME_MESSAGE = "some message";

    // constructor params
    @Mock
    private RunnerResources            resources;
    @Mock
    private WidgetFactory              widgetFactory;
    @Mock
    private RunnerLocalizationConstant locale;

    // local params
    @Mock
    private ConsoleButton                       button1;
    @Mock
    private ConsoleButton                       button2;
    @Mock
    private ConsoleContainerView.ActionDelegate delegate;

    @Captor
    private ArgumentCaptor<ConsoleButton.ActionDelegate> delegateCaptor;

    private ConsoleContainerViewImpl view;

    @Before
    public void setUp() throws Exception {
        when(widgetFactory.createConsoleButton(anyString(), any(SVGResource.class))).thenReturn(button1)
                                                                                    .thenReturn(button2);

        when(locale.consoleTooltipScroll()).thenReturn(SOME_MESSAGE);
        when(locale.consoleTooltipClear()).thenReturn(SOME_MESSAGE);

        view = new ConsoleContainerViewImpl(resources, widgetFactory, locale);
        view.setDelegate(delegate);
    }

    @Test
    public void constructorActionShouldBePerformed() throws Exception {
        verify(resources).arrowBottom();
        verify(resources).erase();

        verify(locale).consoleTooltipScroll();
        verify(locale).consoleTooltipClear();
    }

    @Test
    public void shouldShowWidget() {
        IsWidget console = mock(IsWidget.class);

        view.showWidget(console);

        verify(view.mainPanel).setWidget(console);
    }

    @Test
    public void cleanButtonActionShouldBePerformed() throws Exception {
        verify(button2).setDelegate(delegateCaptor.capture());

        ConsoleButton.ActionDelegate buttonDelegate = delegateCaptor.getValue();
        buttonDelegate.onButtonClicked();

        verify(delegate).onCleanClicked();
    }

    @Test
    public void scrollBottomButtonActionShouldBePerformed() throws Exception {
        verify(button1).setDelegate(delegateCaptor.capture());

        ConsoleButton.ActionDelegate buttonDelegate = delegateCaptor.getValue();
        buttonDelegate.onButtonClicked();

        verify(delegate).onScrollBottomClicked();
    }

}