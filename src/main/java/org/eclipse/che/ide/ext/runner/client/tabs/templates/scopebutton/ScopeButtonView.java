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
package org.eclipse.che.ide.ext.runner.client.tabs.templates.scopebutton;

import org.eclipse.che.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

/**
 * Provides methods which allow change visual representation of scope buttons on template panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(ScopeButtonViewImpl.class)
public interface ScopeButtonView extends View<ScopeButtonView.ActionDelegate> {

    /** Performs some actions when button is selected. */
    void select();

    /** Performs some actions when button is unselected. */
    void unSelect();

    /**
     * Sets image to current button.
     *
     * @param image
     *         image which need set
     */
    void setImage(@Nonnull SVGResource image);

    /**
     * Sets prompt to current scope button.
     *
     * @param prompt
     *         prompt which need set
     */
    void setPrompt(@Nonnull String prompt);

    interface ActionDelegate {
        /** Performs some actions when user click on button. */
        void onButtonClicked();
    }
}