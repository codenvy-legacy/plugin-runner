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
package com.codenvy.ide.ext.runner.client.manager;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.tab.TabContainer;
import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is abstract representation of widget that provides an ability to show runners and manage them.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
@Singleton
@ImplementedBy(RunnerManagerViewImpl.class)
public interface RunnerManagerView extends View<RunnerManagerView.ActionDelegate> {

    /**
     * Updates runner view representation when runner state changed.
     *
     * @param runner
     *         runner which was changed
     */
    void update(@Nonnull Runner runner);

    /**
     * Shows application url on the view.
     *
     * @param applicationUrl
     *         url which needs set
     */
    void setApplicationURl(@Nullable String applicationUrl);

    /**
     * Shows timeout on the view.
     *
     * @param timeout
     *         timeout that needs to be shown
     */
    void setTimeout(@Nonnull String timeout);

    /**
     * Shows special popup panel which displays additional information about runner.
     *
     * @param runner
     *         runner for which need display additional info
     */
    void showMoreInfoPopup(@Nonnull Runner runner);

    /**
     * Updates special popup window which contains info about current runner.
     *
     * @param runner
     *         runner for which need update info
     */
    void updateMoreInfoPopup(@Nonnull Runner runner);

    /**
     * Sets left panel view representation to container. This panel contains history and templates containers.
     *
     * @param containerPresenter
     *         container to which need set panel
     */
    void setLeftPanel(@Nonnull TabContainer containerPresenter);

    /**
     * Sets left panel view representation to container. This panel contains history and templates containers.
     *
     * @param containerPresenter
     *         container to which need set panel
     */
    void setRightPanel(@Nonnull TabContainer containerPresenter);

    /** Hides all buttons on buttons panel except run button. */
    void hideOtherButtons();

    /** Shows all buttons on buttons panel. */
    void showOtherButtons();

    interface ActionDelegate extends BaseActionDelegate {

        /** Performs some actions in response to user's clicking on the 'Run' button. */
        void onRunButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Stop' button. */
        void onStopButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Clean console' button. */
        void onCleanConsoleButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Docker' button. */
        void onDockerButtonClicked();

        /** Performs some actions in response to user's over mouse on timeout label. */
        void onMoreInfoBtnMouseOver();
    }

}