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

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * @author Andrey Plotnikov
 */
public class ConsoleContainerViewImpl extends Composite implements ConsoleContainerView {

    interface ConsoleContainerViewImplUiBinder extends UiBinder<Widget, ConsoleContainerViewImpl> {
    }

    private static final ConsoleContainerViewImplUiBinder UI_BINDER = GWT.create(ConsoleContainerViewImplUiBinder.class);

    @UiField
    SimpleLayoutPanel mainPanel;
    @UiField(provided = true)
    final RunnerResources resources;

    @Inject
    public ConsoleContainerViewImpl(RunnerResources resources) {
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        // elided
    }

    /** {@inheritDoc} */
    @Override
    public void showWidget(@Nonnull IsWidget console) {
        mainPanel.setWidget(console);
    }

    /** {@inheritDoc} */
    @Override
    public void removeWidget(@Nonnull IsWidget console) {
        mainPanel.remove(console);
    }

}