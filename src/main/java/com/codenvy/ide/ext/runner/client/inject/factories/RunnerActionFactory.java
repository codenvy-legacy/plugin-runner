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
package com.codenvy.ide.ext.runner.client.inject.factories;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.runner.client.runneractions.impl.CheckRamAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetLogsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetResourceAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetRunningProcessesAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.RunAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.StopAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.docker.ShowDockerAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.LaunchAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.subactions.CheckHealthStatusAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.subactions.OutputAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.subactions.StatusAction;

import javax.annotation.Nonnull;

/**
 * The factory for creating sub-actions for Launch action.
 *
 * @author Andrey Plotnikov
 * @@author Dmitry Shnurenko
 */
public interface RunnerActionFactory {

    /**
     * Create an instance of {@link StatusAction} with a given notification for updating status of process.
     *
     * @param notification
     *         notification that has to show status of process
     * @return an instance of {@link StatusAction}
     */
    @Nonnull
    StatusAction createStatus(@Nonnull Notification notification);

    /**
     * Create an instance of {@link CheckHealthStatusAction} with a given notification for updating status of process.
     *
     * @param notification
     *         notification that has to show status of process
     * @return an instance of {@link CheckHealthStatusAction}
     */
    @Nonnull
    CheckHealthStatusAction createCheckHealthStatus(@Nonnull Notification notification);

    /** @return an instance of {@link ShowDockerAction} */
    @Nonnull
    ShowDockerAction createShowDocker();

    /** @return an instance of {@link OutputAction} */
    @Nonnull
    OutputAction createOutput();

    /** @return an instance of {@link LaunchAction} */
    @Nonnull
    LaunchAction createLaunch();

    /** @return an instance of {@link CheckRamAction} */
    @Nonnull
    CheckRamAction createCheckRam();

    /** @return an instance of {@link GetLogsAction} */
    @Nonnull
    GetLogsAction createGetLogs();

    /** @return an instance of {@link GetResourceAction} */
    @Nonnull
    GetResourceAction getResource();

    /** @return an instance of {@link GetRunningProcessesAction} */
    @Nonnull
    GetRunningProcessesAction createGetRunningProcess();

    /** @return an instance of {@link RunAction} */
    @Nonnull
    RunAction createRun();

    /** @return an instance of {@link StopAction} */
    @Nonnull
    StopAction createStop();
}