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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.widgets.history.runner.RunnerWidget;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * Provides methods which allow change history panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(HistoryImpl.class)
public interface History extends View<History.ActionDelegate> {

    /**
     * Adds runner on panel and update runner's state.
     *
     * @param runner
     *         runner which was added
     */
    void addRunner(@Nonnull Runner runner, @Nonnull RunnerWidget runnerWidget);

    interface ActionDelegate {

        /**
         * Performs some actions when user clicks on runner.
         *
         * @param selectedRunner
         *         runner which was selected
         */
        void onRunnerSelected(@Nonnull Runner selectedRunner);
    }
}