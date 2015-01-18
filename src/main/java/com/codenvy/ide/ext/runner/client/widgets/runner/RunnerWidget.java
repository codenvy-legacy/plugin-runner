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
package com.codenvy.ide.ext.runner.client.widgets.runner;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.runner.client.models.Runner;

import javax.annotation.Nonnull;

/**
 * Provides methods which allow change visual representation of runner.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public interface RunnerWidget extends View<RunnerWidget.ActionDelegate> {

    /** Performs some actions when tab is selected. */
    void select();

    /** Performs some actions when tab is unselected. */
    void unSelect();

    /**
     * Updates runner view representation when runner state changed.
     *
     * @param runner
     *         runner which was changed
     */
    void update(@Nonnull Runner runner);

    interface ActionDelegate {
        /**
         * Performs some actions in response to user's choosing a runner.
         *
         * @param runner
         *         runner that was chosen
         */
        void onRunnerSelected(@Nonnull Runner runner);
    }

}