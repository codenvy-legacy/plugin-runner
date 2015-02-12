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
package com.codenvy.ide.ext.runner.client.tabs.templates.scopebutton;

import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.google.gwt.user.client.ui.IsWidget;

import javax.annotation.Nonnull;

/**
 * @author Dmitry Shnurenko
 */
public interface ScopeButton {

    /** @return view representation of scope button. */
    IsWidget getView();

    /**
     * Sets delegate which will handle user's clicks on button.
     *
     * @param delegate
     *         delegate which need set
     */
    void setDelegate(@Nonnull ActionDelegate delegate);

    interface ActionDelegate {
        /**
         * Performs some actions when user checked button
         *
         * @param buttonScope
         *         scope which refers to button
         */
        void onButtonChecked(@Nonnull Scope buttonScope);

        /**
         * Performs some actions when user un checked button
         *
         * @param buttonScope
         *         scope which refers to button
         */
        void onButtonUnchecked(@Nonnull Scope buttonScope);
    }
}