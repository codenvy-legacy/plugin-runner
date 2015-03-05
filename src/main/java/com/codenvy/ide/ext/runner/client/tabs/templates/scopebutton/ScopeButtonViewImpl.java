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
import com.codenvy.ide.ext.runner.client.manager.tooltip.TooltipWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

/**
 * Class provides view representation of scope buttons on templates panel.
 *
 * @author Dmitry Shnurenko
 */
public class ScopeButtonViewImpl extends Composite implements ScopeButtonView, ClickHandler, MouseOverHandler, MouseOutHandler {
    interface ScopeButtonImplUiBinder extends UiBinder<Widget, ScopeButtonViewImpl> {
    }

    private static final int TOP_TOOLTIP_SHIFT  = 30;
    private static final int LEFT_TOOLTIP_SHIFT = 8;

    private static final ScopeButtonImplUiBinder UI_BINDER = GWT.create(ScopeButtonImplUiBinder.class);

    @UiField
    SimpleLayoutPanel scope;

    @UiField(provided = true)
    final RunnerResources resources;

    private final TooltipWidget tooltip;

    private SVGImage       image;
    private ActionDelegate delegate;

    @Inject
    public ScopeButtonViewImpl(RunnerResources resources, TooltipWidget tooltip) {
        this.resources = resources;
        this.tooltip = tooltip;

        initWidget(UI_BINDER.createAndBindUi(this));

        addDomHandler(this, ClickEvent.getType());
        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        image.addClassNameBaseVal(resources.runnerCss().blueColor());
        scope.getElement().setInnerHTML(image.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        image.removeClassNameBaseVal(resources.runnerCss().blueColor());
        scope.getElement().setInnerHTML(image.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void setImage(@Nonnull SVGResource image) {
        this.image = new SVGImage(image);
        scope.getElement().setInnerHTML(this.image.toString());
    }

    /** {@inheritDoc} */
    @Override
    public void setPrompt(@Nonnull String prompt) {
        tooltip.setDescription(prompt);
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

    /** {@inheritDoc} */
    @Override
    public void onMouseOut(MouseOutEvent mouseOutEvent) {
        tooltip.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void onMouseOver(MouseOverEvent event) {
        tooltip.setPopupPosition(getAbsoluteLeft() - LEFT_TOOLTIP_SHIFT, getAbsoluteTop() + TOP_TOOLTIP_SHIFT);
        tooltip.show();
    }
}