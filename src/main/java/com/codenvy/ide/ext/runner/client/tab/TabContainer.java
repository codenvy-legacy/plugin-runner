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

import com.codenvy.ide.api.mvp.Presenter;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * Provides methods which allow work with tab container.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TabContainerPresenter.class)
public interface TabContainer extends Presenter {

    /**
     * Adds tab to tab container and saves tab visibility.
     *
     * @param tab
     *         tab which need add
     */
    void addTab(@Nonnull Tab tab);

    interface TabSelectHandler {
        /** Performs some actions when user clicks on tab. */
        void onTabSelected();
    }

}