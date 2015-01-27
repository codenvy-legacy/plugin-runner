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
package com.codenvy.ide.ext.runner.client.widgets.templates;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.mvp.View;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides methods which allow display runner environments on special widget.
 *
 * @author Dmitry Shnurenko
 */
public interface TemplatesWidget extends View<TemplatesWidget.ActionDelegate> {

    /**
     * Shows special widget on which displayed available runner environments.
     *
     * @param environmentTree
     *         environments tree which are available for runner
     */
    void addEnvironments(@Nonnull RunnerEnvironmentTree environmentTree);

    interface ActionDelegate {
        /**
         * Performs some actions in response to user's choosing an environment.
         *
         * @param environment
         *         environment that was chosen
         */
        void onEnvironmentSelected(@Nullable RunnerEnvironment environment);
    }
}
