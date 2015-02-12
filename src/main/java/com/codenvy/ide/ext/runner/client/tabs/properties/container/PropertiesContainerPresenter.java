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
import com.codenvy.ide.ext.runner.client.selection.Selection;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.PropertiesPanel;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.selection.Selection.ENVIRONMENT;

/**
 * @author Andrey Plotnikov
 */
@Singleton
public class PropertiesContainerPresenter implements PropertiesContainer,
                                                     PropertiesContainerView.ActionDelegate,
                                                     SelectionManager.SelectionChangeListener {

    private final PropertiesContainerView      view;
    private final SelectionManager             selectionManager;
    private final WidgetFactory                widgetFactory;
    private final Map<Runner, PropertiesPanel> panels;

    private PropertiesPanel currentPanel;

    @Inject
    public PropertiesContainerPresenter(PropertiesContainerView view, WidgetFactory widgetFactory, SelectionManager selectionManager) {
        this.view = view;
        this.selectionManager = selectionManager;
        this.view.setDelegate(this);
        this.widgetFactory = widgetFactory;

        panels = new HashMap<>();

        selectionManager.addListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void show(@Nonnull Runner runner) {
        currentPanel = panels.get(runner);
        if (currentPanel == null) {
            currentPanel = widgetFactory.createPropertiesPanel(runner);
            panels.put(runner, currentPanel);
        }

        view.showWidget(currentPanel);
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
        view.showWidget(currentPanel);
    }

    /** {@inheritDoc} */
    @Override
    public void onSelectionChanged(@Nonnull Selection selection) {
        if (ENVIRONMENT.equals(selection)) {
            return;
        }

        Runner runner = selectionManager.getRunner();
        if (runner == null) {
            return;
        }

        show(runner);
    }

}