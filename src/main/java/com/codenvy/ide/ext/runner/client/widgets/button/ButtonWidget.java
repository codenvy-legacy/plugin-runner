/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.runner.client.widgets.button;

import com.codenvy.ide.api.mvp.View;

/**
 * Provides methods which allow change visual representation of button.
 *
 * @author Dmitry Shnurenko
 */
public interface ButtonWidget extends View<ButtonWidget.ActionDelegate> {

    /** Changes state of the button on disable. */
    void setDisable();

    /** Changes state of the button on enable. */
    void setEnable();

    interface ActionDelegate {
        /** Performs some actions in response to user's clicking on the button panel. */
        void onButtonClicked();
    }
}