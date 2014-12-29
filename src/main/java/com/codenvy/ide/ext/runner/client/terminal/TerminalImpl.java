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
package com.codenvy.ide.ext.runner.client.terminal;

import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * @author Andrey Plotnikov
 */
public class TerminalImpl extends Composite implements Terminal {

    interface TerminalImplUiBinder extends UiBinder<HTMLPanel, TerminalImpl> {
    }

    private static final TerminalImplUiBinder UI_BINDER = GWT.create(TerminalImplUiBinder.class);

    @UiField
    Label unavailableLabel;
    @UiField
    Frame terminal;

    @UiField(provided = true)
    final RunnerResources            res;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    @Inject
    public TerminalImpl(RunnerResources resources, RunnerLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        String url = runner.getTerminalURL();
        boolean visible = runner.isAnyAppRunning() && url != null;

        unavailableLabel.setVisible(!visible);
        terminal.setVisible(visible);

        if (url == null) {
            terminal.getElement().removeAttribute("src");
        } else {
            terminal.setUrl(url);
        }
    }
}