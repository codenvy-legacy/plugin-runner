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
package com.codenvy.ide.ext.runner.client.tab;

import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Andrey Plotnikov
 */
@ImplementedBy(TabContainerViewImpl.class)
public interface TabContainerView extends View<TabContainerView.ActionDelegate> {

    void showTab(@Nonnull Tab tab);

    void setVisibleTitle(@Nonnull Map<String, Boolean> tabVisibilities);

    void addTab(@Nonnull Tab tab);

    interface ActionDelegate {
        void onTabClicked(@Nonnull String title);
    }

}