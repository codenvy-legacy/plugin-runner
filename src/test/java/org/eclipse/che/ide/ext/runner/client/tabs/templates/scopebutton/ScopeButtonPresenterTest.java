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

import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.vectomatic.dom.svg.ui.SVGResource;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class ScopeButtonPresenterTest {

    private static final String PROJECT_TEXT = "project";
    private static final String SYSTEM_TEXT  = "system";

    @Mock
    private ScopeButtonView            view;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private SVGResource                image;

    @Mock
    private ScopeButton.ActionDelegate delegate;

    private ScopeButtonPresenter presenter;

    @Before
    public void setUp() {
        when(locale.tooltipScopeSystem()).thenReturn(SYSTEM_TEXT);
        when(locale.tooltipScopeProject()).thenReturn(PROJECT_TEXT);
    }

    @Test
    public void shouldVerifyConstructorWhenScopeSystemAndIsCheckedTrue() {
        presenter = new ScopeButtonPresenter(view, locale, SYSTEM, image, true);

        verify(view).setDelegate(presenter);
        verify(view).setImage(image);
        verify(view).select();

        verify(view).setPrompt(SYSTEM_TEXT);
    }

    @Test
    public void shouldVerifyConstructorWhenScopeProjectAndIsCheckedTrue() {
        presenter = new ScopeButtonPresenter(view, locale, PROJECT, image, true);

        verify(view).setDelegate(presenter);
        verify(view).setImage(image);

        verify(view).setPrompt(PROJECT_TEXT);
    }

    @Test
    public void shouldOnButtonClickedWhenScopeSystemAndIsCheckedTrue() {
        presenter = new ScopeButtonPresenter(view, locale, SYSTEM, image, true);
        reset(view);

        presenter.setDelegate(delegate);
        presenter.onButtonClicked();

        verify(delegate).onButtonChecked(SYSTEM);
        verify(view).select();
    }

    @Test
    public void shouldOnButtonClickedWhenScopeSystemAndIsCheckedFalse() {
        presenter = new ScopeButtonPresenter(view, locale, SYSTEM, image, false);
        reset(view);

        presenter.setDelegate(delegate);
        presenter.onButtonClicked();

        verify(delegate).onButtonUnchecked(SYSTEM);
        verify(view).unSelect();
    }

    @Test
    public void shouldOnButtonClickedWhenScopeProjectAndIsCheckedTrue() {
        presenter = new ScopeButtonPresenter(view, locale, PROJECT, image, true);
        reset(view);

        presenter.setDelegate(delegate);
        presenter.onButtonClicked();

        verify(delegate).onButtonChecked(PROJECT);
        verify(view).select();
    }

    @Test
    public void shouldOnButtonClickedWhenScopeProjectAndIsCheckedFalse() {
        presenter = new ScopeButtonPresenter(view, locale, PROJECT, image, false);
        reset(view);

        presenter.setDelegate(delegate);
        presenter.onButtonClicked();

        verify(delegate).onButtonUnchecked(PROJECT);
        verify(view).unSelect();
    }

    @Test
    public void shouldGetView() {
        presenter = new ScopeButtonPresenter(view, locale, PROJECT, image, false);

        assertThat(view, is(presenter.getView()));
    }

}