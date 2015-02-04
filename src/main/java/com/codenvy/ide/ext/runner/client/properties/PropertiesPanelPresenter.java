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

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

/**
 * The class that manages Properties panel widget.
 *
 * @author Andrey Plotnikov
 */
public class PropertiesPanelPresenter implements PropertiesPanelView.ActionDelegate, Presenter {

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

}