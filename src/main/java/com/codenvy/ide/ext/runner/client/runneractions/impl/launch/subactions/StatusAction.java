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
package com.codenvy.ide.ext.runner.client.runneractions.impl.launch.subactions;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.runneractions.RunnerAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetLogsAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.common.RunnerApplicationStatusEvent;
import com.codenvy.ide.ext.runner.client.util.RunnerUtil;
import com.codenvy.ide.ext.runner.client.util.WebSocketUtil;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.IN_PROGRESS;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.RUNNING;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.STOPPED;

/**
 * The action that checks status of a runner and changes it on UI part.
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 * @author Dmitry Shnurenko
 */
public class StatusAction extends AbstractRunnerAction {
    /** WebSocket channel to get application's status. */
    private static final String STATUS_CHANNEL = "runner:status:";

    private final DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private final DtoFactory                 dtoFactory;
    private final WebSocketUtil              webSocketUtil;
    private final AppContext                 appContext;
    private final EventBus                   eventBus;
    private final RunnerLocalizationConstant locale;
    private final RunnerManagerPresenter     presenter;
    private final GetLogsAction              logsAction;
    private final RunnerUtil                 runnerUtil;
    private final RunnerAction               checkHealthStatusAction;
    private final Notification               notification;
    private final RunnerManagerView          view;

    private SubscriptionHandler<ApplicationProcessDescriptor> runnerStatusHandler;
    private String                                            webSocketChannel;
    private Runner                                            runner;
    private CurrentProject                                    project;

    @Inject
    public StatusAction(DtoUnmarshallerFactory dtoUnmarshallerFactory,
                        DtoFactory dtoFactory,
                        WebSocketUtil webSocketUtil,
                        AppContext appContext,
                        EventBus eventBus,
                        RunnerLocalizationConstant locale,
                        RunnerManagerPresenter presenter,
                        RunnerUtil runnerUtil,
                        RunnerActionFactory actionFactory,
                        @Nonnull @Assisted Notification notification) {
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.webSocketUtil = webSocketUtil;
        this.appContext = appContext;
        this.eventBus = eventBus;
        this.locale = locale;
        this.presenter = presenter;
        this.view = presenter.getView();
        this.runnerUtil = runnerUtil;
        this.notification = notification;

        this.logsAction = actionFactory.createGetLogs();
        this.checkHealthStatusAction = actionFactory.createCheckHealthStatus(notification);

        addAction(logsAction);
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        this.runner = runner;
        project = appContext.getCurrentProject();
        webSocketChannel = STATUS_CHANNEL + runner.getProcessId();

        runnerStatusHandler = new SubscriptionHandler<ApplicationProcessDescriptor>(
                dtoUnmarshallerFactory.newWSUnmarshaller(ApplicationProcessDescriptor.class)) {
            /** {@inheritDoc} */
            @Override
            protected void onMessageReceived(ApplicationProcessDescriptor descriptor) {
                onApplicationStatusUpdated(descriptor);
            }

            /** {@inheritDoc} */
            @Override
            protected void onErrorReceived(Throwable exception) {
                runner.setStatus(FAILED);

                if (exception instanceof ServerException && ((ServerException)exception).getHTTPStatus() == 500) {
                    ServiceError e = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class);
                    runnerUtil.showError(runner,
                                         locale.startApplicationFailed(project.getProjectDescription().getName()) + ": " + e.getMessage(),
                                         null,
                                         notification);
                } else {
                    runnerUtil.showError(runner,
                                         locale.startApplicationFailed(project.getProjectDescription().getName()),
                                         exception,
                                         notification);
                }

                stop();

                project.setProcessDescriptor(null);
                project.setIsRunningEnabled(true);
            }
        };

        webSocketUtil.subscribeHandler(webSocketChannel, runnerStatusHandler);

        notification.setStatus(FINISHED);
    }

    private void onApplicationStatusUpdated(@Nonnull ApplicationProcessDescriptor descriptor) {
        project.setProcessDescriptor(descriptor);
        runner.setProcessDescriptor(descriptor);

        switch (descriptor.getStatus()) {
            case RUNNING:
                processRunningMessage();
                break;

            case FAILED:
                processFailedMessage();
                break;

            case STOPPED:
                processStoppedMessage();
                break;

            case CANCELLED:
                processCancelledMessage();
                break;

            case NEW:
                runner.setStatus(IN_PROGRESS);
                presenter.update(runner);
                break;

            default:
        }

        eventBus.fireEvent(new RunnerApplicationStatusEvent(descriptor, runner));
    }

    private void processStoppedMessage() {
        runner.setStatus(STOPPED);

        view.updateMoreInfoPopup(runner);
        view.update(runner);

        project.setIsRunningEnabled(true);

        String projectName = project.getProjectDescription().getName();
        String message = locale.applicationStopped(projectName);
        notification.update(message, INFO, FINISHED, null, true);

        view.printInfo(runner, message);

        presenter.stopRunner(runner);
        stop();
    }

    private void processRunningMessage() {
        runner.setStatus(RUNNING);

        presenter.update(runner);

        checkHealthStatusAction.perform(runner);

        String projectName = project.getProjectDescription().getName();
        String message = locale.applicationStarting(projectName);
        notification.update(message, INFO, FINISHED, null, true);

        view.printInfo(runner, message);
    }

    private void processFailedMessage() {
        runner.setStatus(FAILED);

        presenter.update(runner);

        project.setIsRunningEnabled(true);

        logsAction.perform(runner);

        String projectName = project.getProjectDescription().getName();
        String message = locale.applicationFailed(projectName);
        notification.update(message, ERROR, FINISHED, null, true);

        view.printError(runner, message);

        stop();
    }

    private void processCancelledMessage() {
        runner.setStatus(FAILED);

        presenter.update(runner);

        project.setIsRunningEnabled(true);
        project.setProcessDescriptor(null);

        String projectName = project.getProjectDescription().getName();
        String message = locale.applicationCanceled(projectName);
        notification.update(message, ERROR, FINISHED, null, true);

        view.printError(runner, message);

        stop();
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        if (webSocketChannel == null || runnerStatusHandler == null) {
            // It is impossible to perform stop event twice.
            return;
        }

        webSocketUtil.unSubscribeHandler(webSocketChannel, runnerStatusHandler);

        checkHealthStatusAction.stop();
        super.stop();

        webSocketChannel = null;
        runnerStatusHandler = null;
    }

}