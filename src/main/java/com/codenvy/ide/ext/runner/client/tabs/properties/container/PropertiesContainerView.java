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
package com.codenvy.ide.ext.runner.client.tabs.properties.container;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.PropertiesPanel;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * @author Andrey Plotnikov
 */
@ImplementedBy(PropertiesContainerViewImpl.class)
public interface PropertiesContainerView extends View<PropertiesContainerView.ActionDelegate> {

    /**
     * Changes visibility of the widget.
     *
     * @param visible
     *         visible state that needs to be applied
     */
    void setVisible(boolean visible);

    /**
     * Show a given widget in the special place in the container.
     *
     * @param panel
     *         properties panel that needs to be shown
     */
    void showWidget(@Nonnull PropertiesPanel panel);

    interface ActionDelegate {
    }

}