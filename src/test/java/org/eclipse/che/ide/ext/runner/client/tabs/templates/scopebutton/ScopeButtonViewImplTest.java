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
package org.eclipse.che.ide.ext.runner.client.tabs.templates.scopebutton;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.manager.tooltip.TooltipWidget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class ScopeButtonViewImplTest {
    private static final String TEXT = "some text";

    @Mock
    private RunnerResources resources;
    @Mock
    private TooltipWidget   tooltip;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private SVGResource                    image;
    @Mock
    private RunnerResources.RunnerCss      css;
    @Mock
    private Element                        element;
    @Mock
    private ScopeButtonView.ActionDelegate delegate;

    @InjectMocks
    private ScopeButtonViewImpl scopeButtonView;

    @Before
    public void setUp() {
        when(resources.runnerCss()).thenReturn(css);
        when(css.blueColor()).thenReturn(TEXT);
        when(scopeButtonView.scope.getElement()).thenReturn(element);
    }

    @Test
    public void shouldSelect() {
        scopeButtonView.setImage(image);
        scopeButtonView.select();

        verify(scopeButtonView.scope, times(2)).getElement();
        verify(element, times(2)).setInnerHTML("");
    }

    @Test
    public void shouldUnSelect() {
        scopeButtonView.setImage(image);
        scopeButtonView.unSelect();

        verify(scopeButtonView.scope, times(2)).getElement();
        verify(element, times(2)).setInnerHTML("");
    }

    @Test
    public void shouldSetImage() {
        scopeButtonView.setImage(image);

        verify(scopeButtonView.scope).getElement();
        verify(element).setInnerHTML("");
    }

    @Test
    public void shouldSetPrompt() {
        scopeButtonView.setPrompt(TEXT);

        verify(tooltip).setDescription(TEXT);
    }

    @Test
    public void shouldOnClick() {
        scopeButtonView.setDelegate(delegate);

        scopeButtonView.onClick(mock(ClickEvent.class));

        delegate.onButtonClicked();
    }

    @Test
    public void shouldOnMouseOut() {
        scopeButtonView.onMouseOut(mock(MouseOutEvent.class));

        verify(tooltip).hide();
    }

    @Test
    public void shouldOnMouseOver() {
        scopeButtonView.onMouseOver(mock(MouseOverEvent.class));

        verify(tooltip).setPopupPosition(-8, 30);
        verify(tooltip).show();
    }

}