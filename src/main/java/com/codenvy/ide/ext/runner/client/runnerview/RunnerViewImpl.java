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
package com.codenvy.ide.ext.runner.client.runnerview;

import com.codenvy.ide.ext.runner.client.models.Runner;
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
 * Class provides view representation of runner.
 *
 * @author Dmitry Shnurenko
 */
public class RunnerViewImpl extends Composite implements RunnerView {

    @Singleton
    interface RunnerViewImplUiBinder extends UiBinder<Widget, RunnerViewImpl> {
    }

    @UiField
    FlowPanel runnerImage;
    @UiField
    Label     runnerName;
    @UiField
    Label     ram;
    @UiField
    Label     startTime;

    @Inject
    public RunnerViewImpl(RunnerViewImplUiBinder ourUiBinder) {
        ourUiBinder.createAndBindUi(this);

    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        //TODO  need add update runner image which depends on the runner state

        runnerName.setText(runner.getTitle());
        ram.setText(String.valueOf(runner.getRAM()));
        startTime.setText(String.valueOf(runner.getCreationTime()));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
    }

}