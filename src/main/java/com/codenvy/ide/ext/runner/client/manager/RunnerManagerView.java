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
package com.codenvy.ide.ext.runner.client.manager;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is abstract representation of widget that provides an ability to show runners and manage them.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
@ImplementedBy(RunnerManagerViewImpl.class)
public interface RunnerManagerView extends View<RunnerManagerView.ActionDelegate> {

    /** Sets active runner panel when runner is started */
    void setActive();

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
     * @param url
     *         url that needs to be shown
     */
    void setApplicationURl(@Nullable String url);

    /**
     * Shows timeout on the view.
     *
     * @param timeout
     *         timeout that needs to be shown
     */
    void setTimeout(@Nonnull String timeout);

    /**
     * Prints a given line with info content in the console for a given runner.Printed line will look like this: [INFO] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printInfo(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Prints a given line with error content in the console for a given runner.Printed line will look like this: [ERROR] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printError(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Prints a given line with warning content in the console for a given runner.Printed line will look like this: [WARNING] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printWarn(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Prints a given line with docker content in the console for a given runner.Printed line will look like this: [DOCKER] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printDocker(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Prints a given line with stand out content in the console for a given runner.Printed line will look like this: [STDOUT] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printStdOut(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Prints a given line with stand error content in the console for a given runner.Printed line will look like this:
     * [STDERR] some string
     *
     * @param runner
     *         runner that needs to contain a given line
     * @param line
     *         line that needs to be printed
     */
    void printStdErr(@Nonnull Runner runner, @Nonnull String line);

    /**
     * Binds a terminal on the server part with a GWT widget on the view.
     *
     * @param runner
     *         runner that needs to be bound with terminal widget on the view
     */
    // TODO may be this method is unnecessary. Needs to have a look when implementing it. It seems we can use just activate terminal method
    void setTerminalURL(@Nonnull Runner runner);

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

    interface ActionDelegate {

        /**
         * Performs some actions in response to user's choosing a runner.
         *
         * @param runner
         *         runner that was chosen
         */
        void onRunnerSelected(@Nonnull Runner runner);

        /** Performs some actions in response to user's clicking on the 'Run' button. */
        void onRunButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Stop' button. */
        void onStopButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Clean console' button. */
        void onCleanConsoleButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Receipt' button. */
        void onReceiptButtonClicked();

        /** Performs some actions in response to user's choosing to show runner console widget. */
        void onConsoleButtonClicked();

        /** Performs some actions in response to user's choosing to show runner terminal widget. */
        void onTerminalButtonClicked();
    }

}