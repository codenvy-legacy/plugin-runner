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
package com.codenvy.ide.ext.runner.client.widgets.tab;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * Class provides view representation of tab.
 *
 * @author Dmitry Shnurenko
 */
public class TabWidgetImpl extends Composite implements TabWidget {

    @Singleton
    interface TabViewImplUiBinder extends UiBinder<Widget, TabWidgetImpl> {
    }

    @UiField
    Label     tabTitle;
    @UiField
    FlowPanel tabPanel;

    @UiField(provided = true)
    final RunnerResources resources;

    private ActionDelegate delegate;

    @Inject
    public TabWidgetImpl(TabViewImplUiBinder uiBinder, RunnerResources resources) {
        this.resources = resources;

        initWidget(uiBinder.createAndBindUi(this));

        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onMouseClicked();
            }
        }, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        tabTitle.addStyleName(resources.runnerCss().activeTabText());
        addStyleName(resources.runnerCss().activeTab());
        removeStyleName(resources.runnerCss().notActiveTabText());
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        tabTitle.removeStyleName(resources.runnerCss().activeTabText());
        removeStyleName(resources.runnerCss().activeTab());
        addStyleName(resources.runnerCss().notActiveTabText());
    }

    /** {@inheritDoc} */
    @Override
    public void setTitle(@Nonnull String title) {
        tabTitle.setText(title);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

}