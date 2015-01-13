/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.runner.client.widgets.button;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

/**
 * Class provides view representation of button.
 *
 * @author Dmitry Shnurenko
 */
public class ButtonWidgetImpl extends Composite implements ButtonWidget {

    @Singleton
    interface ButtonWidgetImplUiBinder extends UiBinder<Widget, ButtonWidgetImpl> {
    }

    @UiField
    FlowPanel buttonPanel;
    @UiField
    Image     image;

    @UiField(provided = true)
    final RunnerResources resources;

    private ActionDelegate delegate;
    private boolean        isEnable;

    @Inject
    public ButtonWidgetImpl(ButtonWidgetImplUiBinder ourUiBinder, RunnerResources resources, @Nonnull @Assisted ImageResource image) {
        this.resources = resources;

        initWidget(ourUiBinder.createAndBindUi(this));

        this.image.setResource(image);

        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isEnable) {
                    delegate.onButtonClicked();
                }
            }
        }, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void setDisable() {
        isEnable = false;

        image.addStyleName(resources.runnerCss().opacityButton());
    }

    /** {@inheritDoc} */
    @Override
    public void setEnable() {
        isEnable = true;

        image.removeStyleName(resources.runnerCss().opacityButton());
    }


    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }
}