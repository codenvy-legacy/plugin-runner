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
package com.codenvy.ide.ext.runner.client.runneractions.impl.launch.common;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.app.AppContext;

import javax.annotation.Nonnull;

/**
 * Handler to listen to runner extension {@link RunnerApplicationStatusEvent} events.
 *
 * @author Sun Tan
 * @author Andrey Plotnikov
 */
public interface RunnerApplicationStatusEventHandler {

    /**
     * Performs any actions when an application is being running in the runner.
     *
     * @param applicationProcessDescriptor
     *         descriptor of application process in the runner
     * @param appContext
     *         application context
     */
    void onRunnerAppRunning(@Nonnull ApplicationProcessDescriptor applicationProcessDescriptor, @Nonnull AppContext appContext);

    /**
     * Performs any actions when an application is failed in the runner.
     *
     * @param applicationProcessDescriptor
     *         descriptor of application process in the runner
     * @param appContext
     *         application context
     */
    void onRunnerAppFailed(@Nonnull ApplicationProcessDescriptor applicationProcessDescriptor, @Nonnull AppContext appContext);

    /**
     * Performs any actions when an application is stopped in the runner.
     *
     * @param applicationProcessDescriptor
     *         descriptor of application process in the runner
     * @param appContext
     *         application context
     */
    void onRunnerAppStopped(@Nonnull ApplicationProcessDescriptor applicationProcessDescriptor, @Nonnull AppContext appContext);

    /**
     * Performs any actions when an application is cancelled in the runner.
     *
     * @param applicationProcessDescriptor
     *         descriptor of application process in the runner
     * @param appContext
     *         application context
     */
    void onRunnerCancelled(@Nonnull ApplicationProcessDescriptor applicationProcessDescriptor, @Nonnull AppContext appContext);

    /**
     * Performs any actions when an application is created in the runner.
     *
     * @param applicationProcessDescriptor
     *         descriptor of application process in the runner
     * @param appContext
     *         application context
     */
    void onRunnerAppNew(@Nonnull ApplicationProcessDescriptor applicationProcessDescriptor, @Nonnull AppContext appContext);

}