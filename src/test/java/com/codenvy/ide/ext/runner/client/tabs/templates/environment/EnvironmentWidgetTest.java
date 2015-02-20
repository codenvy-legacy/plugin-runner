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
package com.codenvy.ide.ext.runner.client.tabs.templates.environment;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.TestUtil;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.tabs.common.item.ItemWidget;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
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
public class EnvironmentWidgetTest {
    private static final String TEXT = "text";
    private              String ID   = "hh/ooo/text";

    @Mock
    private ItemWidget       itemWidget;
    @Mock
    private RunnerResources  resources;
    @Mock
    private SelectionManager selectionManager;

    @Mock
    private RunnerResources.RunnerCss css;
    @Mock
    private RunnerEnvironment         environment;

    private EnvironmentWidget environmentWidget;

    @Before
    public void setUp() {
        SVGResource svgResource = mock(SVGResource.class, RETURNS_DEEP_STUBS);

        when(resources.scopeProject()).thenReturn(svgResource);
        when(resources.scopeSystem()).thenReturn(svgResource);
        when(resources.runnerCss()).thenReturn(css);
        when(css.environmentSvg()).thenReturn(TEXT);
        when(css.environmentSvg()).thenReturn(TEXT);

        environmentWidget = new EnvironmentWidget(itemWidget, resources, selectionManager);
    }

    @Test
    public void shouldValidateConstructor() {
        ArgumentCaptor<ItemWidget.ActionDelegate> delegateArgumentCaptor = ArgumentCaptor.forClass(ItemWidget.ActionDelegate.class);

        verify(resources).scopeProject();
        verify(resources).scopeSystem();
        verify(resources, times(2)).runnerCss();
        verify(css, times(2)).environmentSvg();

        verify(itemWidget).setDelegate(delegateArgumentCaptor.capture());
        ItemWidget.ActionDelegate delegate = delegateArgumentCaptor.getValue();
        delegate.onWidgetClicked();

        verify(selectionManager).setEnvironment(any(RunnerEnvironment.class));
    }

    @Test
    public void shouldSelect() {
        environmentWidget.select();

        verify(itemWidget).select();
    }

    @Test
    public void shouldUnSelect() {
        environmentWidget.unSelect();

        verify(itemWidget).unSelect();
    }

    @Test
    public void shouldUpdateWhenDescriptionNotNull() {
        when(environment.getId()).thenReturn(ID);
        when(environment.getDescription()).thenReturn(TEXT);

        environmentWidget.setScope(Scope.PROJECT);
        updateParameters();

        verify(itemWidget).setImage(any(SVGImage.class));
    }

    @Test
    public void shouldUpdateWhenDescriptionIsNull() {
        when(environment.getId()).thenReturn(ID);
        when(environment.getDescription()).thenReturn(TEXT);

        environmentWidget.setScope(Scope.PROJECT);
        updateParameters();

        verify(itemWidget).setImage(any(SVGImage.class));
    }

    @Test
    public void shouldUpdateWhenScopeProject() throws Exception {
        when(environment.getId()).thenReturn(ID);
        when(environment.getDescription()).thenReturn(TEXT);

        environmentWidget.setScope(Scope.PROJECT);
        updateParameters();

        SVGImage imageProjectScope = (SVGImage)TestUtil.getFieldValueByName(environmentWidget, "projectScope");

        verify(itemWidget).setImage(imageProjectScope);
    }

    @Test
    public void shouldUpdateWhenScopeSystem() throws Exception {
        when(environment.getId()).thenReturn(ID);
        when(environment.getDescription()).thenReturn(TEXT);

        environmentWidget.setScope(Scope.SYSTEM);
        updateParameters();

        SVGImage imageSystemScope = (SVGImage)TestUtil.getFieldValueByName(environmentWidget, "systemScope");

        verify(itemWidget).setImage(imageSystemScope);
    }

    @Test
    public void shouldGetAsWidget() {
        environmentWidget.asWidget();
        verify(itemWidget).asWidget();
    }

    private void updateParameters() {
        environmentWidget.update(environment);

        verify(environment).getId();

        String NAME = "text";
        verify(itemWidget).setName(NAME);
        verify(environment).getDescription();
        verify(itemWidget).setDescription(TEXT);
    }

}