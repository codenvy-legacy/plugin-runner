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

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.ImplementedBy;

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
    void setImage(@Nonnull ImageResource image);

    interface ActionDelegate {
        /** Performs some actions when user click on button. */
        void onButtonClicked();
    }
}