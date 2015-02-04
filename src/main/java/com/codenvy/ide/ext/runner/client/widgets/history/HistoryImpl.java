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
package com.codenvy.ide.ext.runner.client.widgets.history;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.widgets.general.RunnerItems;
import com.codenvy.ide.ext.runner.client.widgets.history.runner.RunnerWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * The class contains methods which allow change view representation of history panel.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class HistoryImpl extends Composite implements History {

    interface HistoryImplUiBinder extends UiBinder<Widget, HistoryImpl> {
    }

    private static final HistoryImplUiBinder UI_BINDER = GWT.create(HistoryImplUiBinder.class);

    private final Map<Runner, RunnerWidget> runnerWidgets;

    private ActionDelegate actionDelegate;

    @UiField
    FlowPanel runnersPanel;

    @Inject
    public HistoryImpl() {
        initWidget(UI_BINDER.createAndBindUi(this));

        this.runnerWidgets = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public void addRunner(@Nonnull Runner runner, @Nonnull RunnerWidget runnerWidget) {
        runnerWidgets.put(runner, runnerWidget);

        runnerWidget.setDelegate(new RunnerItems.ActionDelegate<Runner>() {
            @Override
            public void onRunnerEnvironmentSelected(@Nonnull Runner selectedRunner) {
                actionDelegate.onRunnerSelected(selectedRunner);

                selectRunner(selectedRunner);
            }
        });

        runnerWidget.update(runner);

        runnersPanel.add(runnerWidget);

        selectRunner(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate actionDelegate) {
        this.actionDelegate = actionDelegate;
    }

    private void selectRunner(@Nonnull Runner runner) {
        for (RunnerItems widget : runnerWidgets.values()) {
            widget.unSelect();
        }

        RunnerItems widget = runnerWidgets.get(runner);

        if (widget != null) {
            widget.select();
        }
    }
}