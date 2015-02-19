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
package com.codenvy.ide.ext.runner.client.tabs.templates.scopepanel;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.google.inject.ImplementedBy;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

/**
 * The class provides methods which allow work with scope panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(ScopePanelImpl.class)
public interface ScopePanel extends View<ScopePanel.ActionDelegate> {

    /**
     * The method adds button on special place on scope panel.
     *
     * @param scope
     *         button scope
     * @param image
     *         image which need set on button
     * @param isUnChecked
     *         flag which allows understand button is checked or un checked.<code>true</code> button is un checked,
     *         <code>false</code> button is checked
     */
    void addButton(@Nonnull Scope scope, @Nonnull SVGResource image, boolean isUnChecked);

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