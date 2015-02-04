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

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.codenvy.ide.ext.runner.client.models.Runner;
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
     * Adds a new runner part on the view.
     *
     * @param runner
     *         runner that needs to be added on the view
     */
    void addRunner(@Nonnull Runner runner);

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
     * Prints a given line with unknown content in the console for a given runner.
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printMessage(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Prints a given line with info content in the console for a given runner.
     * Printed line will look like this: [INFO] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printInfo(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Prints a given line with error content in the console for a given runner.
     * Printed line will look like this: [ERROR] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printError(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Prints a given line with warning content in the console for a given runner.
     * Printed line will look like this: [WARNING] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printWarn(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Clean console for a given runner.
     *
     * @param runner
     *         runner that is bound wth console which needs to be cleaned
     */
    void clearConsole(@Nonnull Runner runner);

    /**
     * Shows a console widget for a given runner on the view.
     *
     * @param runner
     *         runner that bound with console widget
     */
    void activateConsole(@Nonnull Runner runner);

    /**
     * Shows a terminal widget for a given runner on the view.
     *
     * @param runner
     *         runner that bound with terminal widget
     */
    void activateTerminal(@Nonnull Runner runner);

    /** Shows history widget which contains all created runners. */
    void activateHistoryTab();

    /** Shows templates which are available for runner. */
    void activeTemplatesTab();

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

    interface ActionDelegate extends BaseActionDelegate {

        /**
         * Performs some actions in response to user's choosing a runner.
         *
         * @param runner
         *         runner that was chosen
         */
        void onRunnerSelected(@Nonnull Runner runner);

        void onEnvironmentSelected(@Nonnull RunnerEnvironment selectedEnvironment);

        /** Performs some actions in response to user's clicking on the 'Run' button. */
        void onRunButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Stop' button. */
        void onStopButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Clean console' button. */
        void onCleanConsoleButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Docker' button. */
        void onDockerButtonClicked();

        /** Performs some actions in response to user's choosing to show runner console widget. */
        void onConsoleButtonClicked();

        /** Performs some actions in response to user's choosing to show runner terminal widget. */
        void onTerminalButtonClicked();

        /** Performs some actions in response to user's over mouse on timeout label. */
        void onMoreInfoBtnMouseOver();

        /** Performs some actions in response to user's choosing to show history of runners. */
        void onHistoryButtonClicked();

        /** Performs some actions in response to user's choosing to runner's templates. */
        void onTemplatesButtonClicked();
    }

}