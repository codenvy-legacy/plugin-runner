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
package com.codenvy.ide.ext.runner.client.properties;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * The class that manages Properties panel widget.
 *
 * @author Andrey Plotnikov
 */
public class PropertiesPanelPresenter implements PropertiesPanelView.ActionDelegate, PropertiesPanel {

    private final PropertiesPanelView view;

    @Inject
    public PropertiesPanelPresenter(PropertiesPanelView view) {
        this.view = view;
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationChanged() {

    }

    /** {@inheritDoc} */
    @Override
    public void onSaveButtonClicked() {

    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteButtonClicked() {

    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {

    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
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

}