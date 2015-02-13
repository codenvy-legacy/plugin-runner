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
package com.codenvy.ide.ext.runner.client.tabs.properties.button;

import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

/**
 * Provides methods which allow change visual representation of button on properties panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(PropertyButtonWidgetImpl.class)
public interface PropertyButtonWidget extends View<PropertyButtonWidget.ActionDelegate> {

    /**
     * Performs some actions when button is enable or disable.
     *
     * @param isEnable
     *         <code>true</code> button is enable,<code>false</code> button is disable
     */
    void setEnable(boolean isEnable);

    interface ActionDelegate {
        /** Performs some actions when user click on button. */
        void onButtonClicked();
    }
}