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
package com.codenvy.ide.ext.runner.client.runneractions.impl.launch;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.runneractions.ActionFactory;
import com.codenvy.ide.ext.runner.client.runneractions.RunnerAction;

import javax.annotation.Nonnull;

import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.ext.runner.client.runneractions.ActionType.OUTPUT;

/**
 * The class provides general business logic of launch application.
 *
 * @author Artem Zatsarynnyy
 * @author Roman Nikitenko
 * @author Stéphane Daviet
 * @author Vitaliy Guliy
 * @author Stéphane Tournié
 * @author Sun Tan
 * @author Sergey Leschenko
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public abstract class AbstractAppLaunchAction extends AbstractRunnerAction {

    protected final NotificationManager        notificationManager;
    protected final RunnerLocalizationConstant locale;

    protected CurrentProject project;
    protected Runner         runner;

    private final RunnerActionFactory runnerActionFactory;
    private final RunnerManagerView   view;
    private final AppContext          appContext;
    private final RunnerAction        outputAction;

    protected AbstractAppLaunchAction(@Nonnull NotificationManager notificationManager,
                                      @Nonnull RunnerManagerPresenter presenter,
                                      @Nonnull RunnerLocalizationConstant locale,
                                      @Nonnull AppContext appContext,
                                      @Nonnull ActionFactory actionFactory,
                                      @Nonnull RunnerActionFactory runnerActionFactory) {
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.appContext = appContext;
        this.runnerActionFactory = runnerActionFactory;
        this.view = presenter.getView();

        outputAction = actionFactory.newInstance(OUTPUT);
        addAction(outputAction);
    }

    /**
     * Configures run process.
     *
     * @param applicationProcessDescriptor
     *         describes an application process
     */
    protected void onAppLaunched(@Nonnull ApplicationProcessDescriptor applicationProcessDescriptor) {
        runner.setProcessDescriptor(applicationProcessDescriptor);

        String projectName = project.getProjectDescription().getName();
        String message = locale.environmentCooking(projectName);

        Notification notification = new Notification(message, PROGRESS, true);
        notificationManager.showNotification(notification);

        view.printInfo(runner, locale.environmentCooking(projectName));

        project.setProcessDescriptor(applicationProcessDescriptor);
        project.setIsRunningEnabled(false);

        RunnerAction statusAction = runnerActionFactory.createStatusAction(notification);
        addAction(statusAction);

        statusAction.perform(runner);
        outputAction.perform(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull Runner runner) {
        this.runner = runner;
        project = appContext.getCurrentProject();
    }

}