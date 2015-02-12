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
package com.codenvy.ide.ext.runner.client.tabs.templates.scopebutton;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * Class provides view representation of scope buttons on templates panel.
 *
 * @author Dmitry Shnurenko
 */
public class ScopeButtonViewImpl extends Composite implements ScopeButtonView, ClickHandler {
    interface ScopeButtonImplUiBinder extends UiBinder<Widget, ScopeButtonViewImpl> {
    }

    private static final ScopeButtonImplUiBinder UI_BINDER = GWT.create(ScopeButtonImplUiBinder.class);

    @UiField
    Image scope;

    @UiField(provided = true)
    final RunnerResources resources;

    private ActionDelegate delegate;

    @Inject
    public ScopeButtonViewImpl(RunnerResources resources) {
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        addDomHandler(this, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        addStyleName(resources.runnerCss().opacityButton());
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        removeStyleName(resources.runnerCss().opacityButton());
    }

    /** {@inheritDoc} */
    @Override
    public void setImage(@Nonnull ImageResource image) {
        scope.setResource(image);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(ClickEvent event) {
        delegate.onButtonClicked();
    }
}