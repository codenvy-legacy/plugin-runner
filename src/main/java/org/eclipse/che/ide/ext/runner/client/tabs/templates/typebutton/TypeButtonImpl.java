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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * Class provides view representation of type buttons on templates panel.
 *
 * @author Dmitry Shnurenko
 */
public class TypeButtonImpl extends Composite implements TypeButton, ClickHandler {

    interface TypeButtonImplUiBinder extends UiBinder<Widget, TypeButtonImpl> {
    }

    private static final TypeButtonImplUiBinder UI_BINDER = GWT.create(TypeButtonImplUiBinder.class);

    @UiField
    Label name;

    @UiField(provided = true)
    final RunnerResources resources;

    private ActionDelegate delegate;

    @Inject
    public TypeButtonImpl(RunnerResources resources) {
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        addDomHandler(this, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        addStyleName(resources.runnerCss().typeButton());
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        removeStyleName(resources.runnerCss().typeButton());
    }

    /** {@inheritDoc} */
    @Override
    public void setName(@Nonnull String buttonName) {
        name.setText(buttonName);
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