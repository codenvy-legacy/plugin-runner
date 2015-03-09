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
package org.eclipse.che.ide.ext.runner.client.tabs.templates.typebutton;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class TypeButtonImplTest {
    private static final String TEXT = "text";

    @Mock
    private RunnerResources resources;
    @Mock
    private RunnerResources.RunnerCss css;

    @InjectMocks
    private TypeButtonImpl typeButton;

    @Before
    public void setUp() {
        when(resources.runnerCss()).thenReturn(css);
        when(css.typeButton()).thenReturn(TEXT);
    }

    @Test
    public void shouldSelect() {
        typeButton.select();

        verify(resources).runnerCss();
        verify(css).typeButton();
    }

    @Test
    public void shouldUnSelect() {
        typeButton.unSelect();

        verify(resources).runnerCss();
        verify(css).typeButton();
    }

    @Test
    public void shouldSetName() {
        typeButton.setName(TEXT);

        verify(typeButton.name).setText(TEXT);
    }

    @Test
    public void shouldOnClick() {
        ClickEvent event = mock(ClickEvent.class);
        TypeButton.ActionDelegate delegate = mock(TypeButton.ActionDelegate.class);

        typeButton.setDelegate(delegate);
        typeButton.onClick(event);

        verify(delegate).onButtonClicked();
    }

}