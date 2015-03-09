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
package org.eclipse.che.ide.ext.runner.client.tabs.templates.scopepanel;

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.inject.factories.WidgetFactory;
import org.eclipse.che.ide.ext.runner.client.tabs.templates.scopebutton.ScopeButton;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class ScopePanelImplTest {

    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private RunnerResources            resources;
    @Mock
    private WidgetFactory              widgetFactory;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private SVGResource resource;
    @Mock
    private ScopeButton button;

    @InjectMocks
    private ScopePanelImpl scopePanel;

    @Test
    public void shouldAddButton() {
        when(widgetFactory.createScopeButton(eq(PROJECT), any(SVGResource.class), eq(true))).thenReturn(button);
        ScopePanel.ActionDelegate actionDelegate = mock(ScopePanel.ActionDelegate.class);
        scopePanel.setDelegate(actionDelegate);
        scopePanel.addButton(PROJECT, resource, true);

        ArgumentCaptor<ScopeButton.ActionDelegate> delegateCaptor = ArgumentCaptor.forClass(ScopeButton.ActionDelegate.class);
        verify(button).setDelegate(delegateCaptor.capture());
        delegateCaptor.getValue().onButtonChecked(PROJECT);

        verify(actionDelegate).onButtonChecked(PROJECT);

        delegateCaptor.getValue().onButtonUnchecked(PROJECT);
        verify(actionDelegate).onButtonUnchecked(PROJECT);
    }

}