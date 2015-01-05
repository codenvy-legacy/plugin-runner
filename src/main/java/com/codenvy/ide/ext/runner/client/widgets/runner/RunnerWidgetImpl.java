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
package com.codenvy.ide.ext.runner.client.widgets.runner;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Class provides view representation of runner.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class RunnerWidgetImpl extends Composite implements RunnerWidget {

    @Singleton
    interface RunnerViewImplUiBinder extends UiBinder<Widget, RunnerWidgetImpl> {
    }

    @UiField
    Label runnerName;
    @UiField
    Label ram;
    @UiField
    Label startTime;
    @UiField
    Image image;

    private ActionDelegate delegate;
    private Runner         runner;

    @Inject
    public RunnerWidgetImpl(RunnerViewImplUiBinder ourUiBinder) {
        initWidget(ourUiBinder.createAndBindUi(this));

        addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onRunnerSelected(runner);
            }
        }, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        this.runner = runner;
        //TODO  need add update runner image which depends on the runner state

        runnerName.setText(runner.getTitle());
        //TODO need set memory size from runner options
        ram.setText(512 + " MB");
        Date startDate = new Date();
        String startDateFormatted = DateTimeFormat.getFormat("dd-MM-yy HH:mm").format(startDate);
        startTime.setText(startDateFormatted);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

}