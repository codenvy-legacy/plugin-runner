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

import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.widgets.general.RunnerItems;
import com.codenvy.ide.ext.runner.client.widgets.history.runner.RunnerWidget;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * The class contains business logic which allows add or change state of runners and performs some logic in respond on user's actions.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class HistoryPresenter implements HistoryPanel {

    private final HistoryView               view;
    private final WidgetFactory             widgetFactory;
    private final Map<Runner, RunnerWidget> runnerWidgets;
    private final SelectionManager          selectionManager;

    @Inject
    public HistoryPresenter(HistoryView view, WidgetFactory widgetFactory, SelectionManager selectionManager) {
        this.view = view;

        this.selectionManager = selectionManager;
        this.widgetFactory = widgetFactory;
        this.runnerWidgets = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public void addRunner(@Nonnull Runner runner) {
        if (runnerWidgets.get(runner) != null) {
            return;
        }

        selectionManager.setRunner(runner);

        RunnerWidget runnerWidget = widgetFactory.createRunner();
        runnerWidget.update(runner);

        runnerWidgets.put(runner, runnerWidget);
        view.addRunner(runnerWidget);

        selectRunner(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        RunnerWidget runnerWidget = runnerWidgets.get(runner);
        if (runnerWidget == null) {
            return;
        }

        runnerWidget.update(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void selectRunner(@Nonnull Runner runner) {
        for (RunnerItems widget : runnerWidgets.values()) {
            widget.unSelect();
        }

        RunnerItems widget = runnerWidgets.get(runner);
        if (widget != null) {
            widget.select();
        }
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public IsWidget getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public void go(@Nonnull AcceptsOneWidget container) {
        container.setWidget(view);
    }
}