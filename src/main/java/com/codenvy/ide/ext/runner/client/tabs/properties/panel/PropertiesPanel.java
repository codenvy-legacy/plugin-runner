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
package com.codenvy.ide.ext.runner.client.tabs.properties.panel;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.models.Runner;

import javax.annotation.Nonnull;

/**
 * The common representation of properties panel widget.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public interface PropertiesPanel extends Presenter {

    /**
     * Updates properties panel using values from current runner.
     *
     * @param runner
     *         runner for which need update panel
     */
    void update(@Nonnull Runner runner);

    /**
     * Updates properties panel using values from current environment.
     *
     * @param environment
     *         environment for which need update panel
     */
    void update(@Nonnull Environment environment);

    /** Resets all parameters of the panel. */
    void reset();

    /**
     * Adds a new listener for detecting removing panel.
     *
     * @param listener
     *         listener that needs to be added
     */
    void addListener(@Nonnull RemovePanelListener listener);


    public interface RemovePanelListener {

        /**
         * Methods calls by all listeners which must be notified that environment was deleted.
         *
         * @param environment
         *         environment which was deleted
         */
        void onPanelRemoved(@Nonnull Environment environment);
    }

}