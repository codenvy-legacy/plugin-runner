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
package com.codenvy.ide.ext.runner.client.runneractions;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetLogsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetRunningProcessesAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.StopAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.run.RunAction;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * It provides an possibility to create runner action by action type. It needs for simplifying work flow of using runner actions.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class ActionFactory {

    private final Provider<RunAction>                 runActionProvider;
    private final Provider<GetLogsAction>             getLogsActionProvider;
    private final Provider<StopAction>                stopActionProvider;
    private final Provider<GetResourceAction>         getResourceProvider;
    private final Provider<GetRunningProcessesAction> runningProcessesActionProvider;

    @Inject
    public ActionFactory(Provider<RunAction> runActionProvider,
                         Provider<GetLogsAction> getLogsActionProvider,
                         Provider<StopAction> stopActionProvider,
                         Provider<GetResourceAction> getResourceProvider,
                         Provider<GetRunningProcessesAction> runningProcessesActionProvider) {
        this.runActionProvider = runActionProvider;
        this.getLogsActionProvider = getLogsActionProvider;
        this.stopActionProvider = stopActionProvider;
        this.getResourceProvider = getResourceProvider;
        this.runningProcessesActionProvider = runningProcessesActionProvider;
    }

    /**
     * Create action by a given type and perform it for a given runner.
     *
     * @param actionType
     *         type of action that needs to be created
     * @param runner
     *         runner that needs to be given for an action
     * @return an instance of {@link RunnerAction}
     */
    @Nonnull
    public RunnerAction createAndPerform(@Nonnull ActionType actionType, @Nonnull Runner runner) {
        RunnerAction action;

        switch (actionType) {
            case RUN:
                action = runActionProvider.get();
                break;

            case GET_LOGS:
                action = getLogsActionProvider.get();
                break;

            case STOP:
                action = stopActionProvider.get();
                break;

            case GET_RESOURCES:
                action = getResourceProvider.get();
                break;

            case GET_RUNNING_PROCESS:
                action = runningProcessesActionProvider.get();
                break;

            default:
                throw new UnsupportedOperationException("Project type " + actionType + " isn't supported. Please contact developers.");
        }

        action.perform(runner);

        return action;
    }

}