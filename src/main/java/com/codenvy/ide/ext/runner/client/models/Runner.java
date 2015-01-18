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
package com.codenvy.ide.ext.runner.client.models;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunOptions;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * It contains all necessary information for every Runner.
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 * @author Dmitry Shnurenko
 */
public interface Runner {

    /**
     * Returns boolean flag which allows define is console active or not for current runner.
     *
     * @return <code>true</code> console is active,<code>false</code> console isn't active, terminal is actives
     */
    boolean isConsoleActive();

    /** Sets true value console flag, which defines that console is active. */
    void activateConsole();

    /** Sets false value console flag, which defines that terminal is active. */
    void activateTerminal();

    /** @return amount of available RAM for current runner */
    @Nonnegative
    int getRAM();

    /**
     * Sets memory of runner.
     *
     * @param ram
     *         new memory value
     */
    void setRAM(@Nonnegative int ram);

    /** @return the date when this runner was launched */
    long getCreationTime();

    /** @return string representation of runner timeout */
    @Nonnull
    String getTimeout();

    /** @return string representation of runner active time */
    @Nonnull
    String getTotalTime();

    /** @return string representation of time when runner was stopped */
    @Nonnull
    String getStopTime();

    /** @return id of the environment */
    @Nonnull
    String getEnvironmentId();

    /** @return <code>true</code> if this runner has already been started, <code>false</code> it hasn't */
    boolean isStarted();

    /**
     * Returns title of runner. This value uses for unique identifier every runner on UI components.
     *
     * @return title of runner
     */
    @Nonnull
    String getTitle();

    /** @return status of runner */
    @Nonnull
    Status getStatus();

    /**
     * Changes status of runner.
     *
     * @param status
     *         new status that needs to be applied
     */
    void setStatus(@Nonnull Status status);

    /** @return url where application is running */
    @Nullable
    String getApplicationURL();

    /** @return url where terminal of current runner is located */
    @Nullable
    String getTerminalURL();

    /** @return url where full log is located */
    @Nullable
    Link getLogUrl();

    /** @return url where docker file is located */
    @Nullable
    String getDockerUrl();

    /** @return url where the rest service for stopping runner is located */
    @Nullable
    Link getStopUrl();

    /**
     * Changes application process descriptor.
     *
     * @param descriptor
     *         application process descriptor that needs to set
     */
    void setProcessDescriptor(@Nullable ApplicationProcessDescriptor descriptor);

    /** @return <code>true</code> if this runner is alive, <code>false</code> it isn't */
    boolean isAlive();

    /**
     * Changes alive status of a runner.
     *
     * @param isAlive
     *         new alive status of a runner
     */
    void setAliveStatus(boolean isAlive);

    /** @return <code>true<code/> if any application launched, <code>false</> it isn't */
    boolean isAnyAppLaunched();

    /**
     * Changes launch status of a runner.
     *
     * @param isAnyAppLaunched
     *         new launch status of a runner. If any application launched than status must be <code>true<code/>
     */
    void setAppLaunchStatus(boolean isAnyAppLaunched);

    /** @return <code>true<code/> if any application running, <code>false</> it isn't */
    boolean isAnyAppRunning();

    /**
     * Changes running status of a runner.
     *
     * @param isAnyAppRunning
     *         new running status of a runner. If any application is running than status must be <code>true<code/>
     */
    void setAppRunningStatus(boolean isAnyAppRunning);

    /** @return id of process that was bound with this runner */
    long getProcessId();

    /** @return options of a runner */
    @Nonnull
    RunOptions getOptions();

    /** The list of available states of a runner. */
    enum Status {
        RUNNING,
        IN_PROGRESS,
        IN_QUEUE,
        FAILED,
        TIMEOUT,
        STOPPED,
        DONE
    }

}