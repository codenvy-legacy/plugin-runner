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
import com.codenvy.ide.ext.runner.client.runneractions.impl.CheckRamAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetLogsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetResourceAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetRunningProcessesAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.RunAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.StopAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.docker.ShowDockerAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.subactions.OutputAction;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.CHECK_RAM;
import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.GET_LOGS;
import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.GET_RESOURCES;
import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.GET_RUNNING_PROCESS;
import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.OUTPUT;
import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.RUN;
import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.SHOW_DOCKER;
import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.STOP;

/**
 * It provides an possibility to create runner action by action type. It needs for simplifying work flow of using runner actions.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
@Singleton
public class ActionFactory {

    private final Map<ActionType, Provider<? extends RunnerAction>> providers;

    @Inject
    public ActionFactory(Provider<RunAction> runActionProvider,
                         Provider<GetLogsAction> getLogsActionProvider,
                         Provider<StopAction> stopActionProvider,
                         Provider<GetResourceAction> getResourceProvider,
                         Provider<GetRunningProcessesAction> runningProcessesActionProvider,
                         Provider<CheckRamAction> checkRamActionProvider,
                         Provider<ShowDockerAction> showDockerActionProvider,
                         Provider<OutputAction> outputActionProvider) {
        providers = new EnumMap<>(ActionType.class);

        providers.put(RUN, runActionProvider);
        providers.put(STOP, stopActionProvider);
        providers.put(GET_LOGS, getLogsActionProvider);
        providers.put(GET_RESOURCES, getResourceProvider);
        providers.put(GET_RUNNING_PROCESS, runningProcessesActionProvider);
        providers.put(CHECK_RAM, checkRamActionProvider);
        providers.put(SHOW_DOCKER, showDockerActionProvider);
        providers.put(OUTPUT, outputActionProvider);
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
        RunnerAction action = newInstance(actionType);
        action.perform(runner);

        return action;
    }

    /**
     * Create a new instance of {@link RunnerAction} by a given type.
     *
     * @param actionType
     *         type of action that needs to be created
     * @return an instance of {@link RunnerAction}
     */
    @Nonnull
    public RunnerAction newInstance(@Nonnull ActionType actionType) {
        Provider<? extends RunnerAction> provider = providers.get(actionType);

        if (provider == null) {
            throw new UnsupportedOperationException("Project type " + actionType + " isn't supported. Please contact developers.");
        }

        return provider.get();
    }

}