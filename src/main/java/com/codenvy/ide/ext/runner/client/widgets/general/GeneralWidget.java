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
package com.codenvy.ide.ext.runner.client.widgets.general;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.ImplementedBy;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides methods which allow change visual representation of runner.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@ImplementedBy(GeneralWidgetImpl.class)
public interface GeneralWidget extends View<GeneralWidget.ActionDelegate> {

    /** Performs some actions when tab is selected. */
    void select();

    /** Performs some actions when tab is unselected. */
    void unSelect();

    /**
     * Sets name to special place on widget.
     *
     * @param name
     *         name which need set
     */
    void setName(@Nonnull String name);

    /**
     * Sets description to special place on widget.
     *
     * @param description
     *         description which need set
     */
    void setDescription(@Nullable String description);

    /**
     * Sets start time of runner to special place on widget.
     *
     * @param time
     *         time which need set
     */
    void setStartTime(@Nonnegative long time);

    /**
     * Sets svg image to special place on widget.
     *
     * @param image
     *         image which need set
     */
    void setImage(@Nonnull SVGImage image);

    /**
     * Sets image to special place on widget.
     *
     * @param imageResource
     *         image which need set
     */
    void setImage(@Nonnull ImageResource imageResource);

    interface ActionDelegate {
        /** Performs some actions when user click on widget. */
        void onWidgetClicked();
    }

}