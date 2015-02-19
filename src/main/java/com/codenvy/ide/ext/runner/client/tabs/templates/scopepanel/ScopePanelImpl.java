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
package com.codenvy.ide.ext.runner.client.tabs.templates.scopepanel;

import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.templates.scopebutton.ScopeButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

/**
 * The class contains methods which allow change view representation of scope panel
 *
 * @author Dmitry Shnurenko
 */
public class ScopePanelImpl extends Composite implements ScopePanel {
    interface ScopePanelImplUiBinder extends UiBinder<Widget, ScopePanelImpl> {
    }

    private static final ScopePanelImplUiBinder UI_BINDER = GWT.create(ScopePanelImplUiBinder.class);

    @UiField
    FlowPanel buttonsPanel;

    @UiField(provided = true)
    final RunnerLocalizationConstant locale;
    @UiField(provided = true)
    final RunnerResources            resources;

    private final WidgetFactory widgetFactory;

    private ActionDelegate delegate;

    @Inject
    public ScopePanelImpl(RunnerLocalizationConstant locale, RunnerResources resources, WidgetFactory widgetFactory) {
        this.locale = locale;
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        this.widgetFactory = widgetFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void addButton(@Nonnull Scope scope, @Nonnull SVGResource resource, boolean isUnChecked) {
        SVGImage image = new SVGImage(resource);

        ScopeButton button = widgetFactory.createScopeButton(scope, image, isUnChecked);
        button.setDelegate(new ScopeButton.ActionDelegate() {
            @Override
            public void onButtonChecked(@Nonnull Scope buttonScope) {
                delegate.onButtonChecked(buttonScope);
            }

            @Override
            public void onButtonUnchecked(@Nonnull Scope buttonScope) {
                delegate.onButtonUnchecked(buttonScope);
            }
        });

        buttonsPanel.add(button.getView());
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

}