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
package org.eclipse.che.ide.ext.runner.client.actions;

import com.google.inject.Inject;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.runner.dto.RunOptions;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManager;
import org.eclipse.che.ide.ext.runner.client.models.Environment;

import javax.annotation.Nonnull;

/**
 * Action which allows run project with default runner parameters.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class RunAction extends AbstractRunnerActions {

    private final RunnerManager              runnerManager;
    private final DtoFactory                 dtoFactory;
    private final ChooseRunnerAction         chooseRunnerAction;
    private final AppContext                 appContext;
    private final AnalyticsEventLogger       eventLogger;
    private final NotificationManager        notificationManager;
    private final RunnerLocalizationConstant locale;

    @Inject
    public RunAction(RunnerManager runnerManager,
                     RunnerLocalizationConstant locale,
                     AppContext appContext,
                     ChooseRunnerAction chooseRunnerAction,
                     DtoFactory dtoFactory,
                     RunnerResources resources,
                     AnalyticsEventLogger eventLogger,
                     NotificationManager notificationManager) {
        super(appContext, locale.actionRun(), locale.actionRunDescription(), resources.run());

        this.locale = locale;
        this.runnerManager = runnerManager;
        this.dtoFactory = dtoFactory;
        this.appContext = appContext;
        this.chooseRunnerAction = chooseRunnerAction;
        this.eventLogger = eventLogger;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(@Nonnull ActionEvent event) {
        eventLogger.log(this);

        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        Environment environment = chooseRunnerAction.getSelectedEnvironment();

        String defaultRunnerName = currentProject.getRunner();

        if (defaultRunnerName == null) {
            notificationManager.showError(locale.actionRunnerNotSpecified());
            return;
        }

        if (environment == null || defaultRunnerName.endsWith('/' + environment.getName())) {
            runnerManager.launchRunner();
        } else {
            RunOptions runOptions = dtoFactory.createDto(RunOptions.class)
                                              .withOptions(environment.getOptions())
                                              .withEnvironmentId(environment.getId());

            runnerManager.launchRunner(runOptions, environment.getName());
        }
    }
}