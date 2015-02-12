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
package com.codenvy.ide.ext.runner.client.tabs.properties.container;

import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.PropertiesPanel;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrey Plotnikov
 */
@Singleton
public class PropertiesContainerPresenter implements PropertiesContainer, PropertiesContainerView.ActionDelegate {

    private final PropertiesContainerView      view;
    private final WidgetFactory                widgetFactory;
    private final Map<Runner, PropertiesPanel> panels;

    @Inject
    public PropertiesContainerPresenter(PropertiesContainerView view, WidgetFactory widgetFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.widgetFactory = widgetFactory;

        panels = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public void show(@Nonnull Runner runner) {
        PropertiesPanel panel = panels.get(runner);
        if (panel == null) {
            panel = widgetFactory.createPropertiesPanel(runner);
            panels.put(runner, panel);
        }

        view.showWidget(panel);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public IsWidget getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

}