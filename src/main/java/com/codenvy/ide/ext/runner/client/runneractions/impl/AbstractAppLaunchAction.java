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
package com.codenvy.ide.ext.runner.client.runneractions.impl;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.inject.factories.HandlerFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.RunnerAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.run.LogMessagesHandler;
import com.codenvy.ide.ext.runner.client.runneractions.impl.run.RunnerApplicationStatusEvent;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.StringUnmarshallerWS;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.api.notification.Notification.Type.WARNING;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.DONE;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.IN_PROGRESS;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.RUNNING;

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
public abstract class AbstractAppLaunchAction implements RunnerAction {
    /** WebSocket channel to get runner output. */
    public static final String OUTPUT_CHANNEL = "runner:output:";

    protected static final String PROCESS_STARTED_CHANNEL = "runner:process_started:";

    /** WebSocket channel to get application's status. */
    private static final String STATUS_CHANNEL     = "runner:status:";
    /** WebSocket channel to check application's health. */
    private static final String APP_HEALTH_CHANNEL = "runner:app_health:";

    private static final String STATUS    = "status";
    private static final String URL       = "url";
    private static final String OK_STATUS = "OK";

    private static final int TIMEOUT = 30_000;// 30 sec == 30 000 ms

    protected final GetLogsAction              logsAction;
    protected final DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    protected final NotificationManager        notificationManager;
    protected final RunnerLocalizationConstant locale;
    protected final RunnerManagerPresenter     presenter;
    protected final MessageBus                 messageBus;

    protected CurrentProject project;
    protected Runner         runner;

    private final RunnerManagerView view;
    private final AppContext        appContext;
    private final HandlerFactory    handlerFactory;
    private final EventBus          eventBus;
    private final DtoFactory        dtoFactory;

    private LogMessagesHandler                                runnerOutputHandler;
    private SubscriptionHandler<ApplicationProcessDescriptor> runnerStatusHandler;
    private SubscriptionHandler<String>                       runnerHealthHandler;
    // The server makes the limited quantity of tries checking application's health,
    // so we're waiting for some time (about 30 sec.) and assume that app health is OK.
    private Timer                                             changeAppAliveTimer;
    private Notification                                      notification;

    protected AbstractAppLaunchAction(@Nonnull NotificationManager notificationManager,
                                      @Nonnull RunnerManagerPresenter presenter,
                                      @Nonnull RunnerLocalizationConstant locale,
                                      @Nonnull HandlerFactory handlerFactory,
                                      @Nonnull MessageBus messageBus,
                                      @Nonnull GetLogsAction logsAction,
                                      @Nonnull DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                      @Nonnull AppContext appContext,
                                      @Nonnull DtoFactory dtoFactory,
                                      @Nonnull EventBus eventBus) {
        this.notificationManager = notificationManager;
        this.handlerFactory = handlerFactory;
        this.locale = locale;
        this.messageBus = messageBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.logsAction = logsAction;
        this.dtoFactory = dtoFactory;
        this.appContext = appContext;
        this.eventBus = eventBus;
        this.presenter = presenter;
        this.view = presenter.getView();
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

        notification = new Notification(message, PROGRESS, true);
        notificationManager.showNotification(notification);

        view.printInfo(runner, locale.environmentCooking(projectName));

        project.setProcessDescriptor(applicationProcessDescriptor);
        project.setIsRunningEnabled(false);

        startCheckingAppStatus();
        startCheckingAppOutput();
    }

    private void startCheckingAppOutput() {
        runnerOutputHandler = handlerFactory.createLogMessageHandler(runner);

        try {
            messageBus.subscribe(OUTPUT_CHANNEL + runner.getProcessId(), runnerOutputHandler);
        } catch (WebSocketException e) {
            Log.error(AbstractAppLaunchAction.class, e);
        }
    }

    private void startCheckingAppStatus() {
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
                runner.setAppRunningStatus(false);

                if (exception instanceof ServerException && ((ServerException)exception).getHTTPStatus() == 500) {
                    ServiceError e = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class);
                    onFail(locale.startApplicationFailed(project.getProjectDescription().getName()) + ": " + e.getMessage(), null);
                } else {
                    onFail(locale.startApplicationFailed(project.getProjectDescription().getName()), exception);
                }

                try {
                    messageBus.unsubscribe(STATUS_CHANNEL + runner.getProcessId(), this);
                } catch (WebSocketException e) {
                    Log.error(AbstractAppLaunchAction.class, e);
                }

                project.setProcessDescriptor(null);
                project.setIsRunningEnabled(true);
            }
        };

        try {
            messageBus.subscribe(STATUS_CHANNEL + runner.getProcessId(), runnerStatusHandler);
        } catch (WebSocketException e) {
            Log.error(AbstractAppLaunchAction.class, e);
        }
    }

    /**
     * Shows messages into the console if run process failed.
     *
     * @param message
     *         message that need to be print
     * @param exception
     *         exception that was throwing when project was running
     */
    protected void onFail(@Nonnull String message, @Nullable Throwable exception) {
        runner.setAppLaunchStatus(false);
        runner.setStatus(FAILED);

        presenter.update(runner);

        if (notification == null) {
            notification = new Notification(message, ERROR, true);
            notificationManager.showNotification(notification);
        } else {
            notification.update(message, ERROR, FINISHED, null, true);
        }

        if (exception != null && exception.getMessage() != null) {
            view.printError(runner, message + ": " + exception.getMessage());
        } else {
            view.printError(runner, message);
        }
    }

    private void onApplicationStatusUpdated(@Nonnull ApplicationProcessDescriptor descriptor) {
        project.setProcessDescriptor(descriptor);
        runner.setProcessDescriptor(descriptor);

        switch (descriptor.getStatus()) {
            case RUNNING:
                processRunningMessage();
                break;

            case STOPPED:
                processStoppedMessage();
                break;

            case FAILED:
                processFailedMessage();
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

        eventBus.fireEvent(new RunnerApplicationStatusEvent(descriptor, appContext, descriptor.getStatus()));
    }

    private void processRunningMessage() {
        runner.setStatus(RUNNING);
        runner.setAppRunningStatus(true);

        presenter.update(runner);

        startCheckingAppHealth();

        String projectName = project.getProjectDescription().getName();
        String message = locale.applicationStarting(projectName);
        notification.update(message, INFO, FINISHED, null, true);

        view.printInfo(runner, message);
    }

    private void processStoppedMessage() {
        runner.setAppRunningStatus(false);
        runner.setAppLaunchStatus(false);
        runner.setAliveStatus(false);

        project.setIsRunningEnabled(true);
        project.setProcessDescriptor(null);

        String projectName = project.getProjectDescription().getName();
        String message = locale.applicationStopped(projectName);

        Notification.Type notificationType;

        if (runner.isStarted()) {
            notificationType = INFO;

            runner.setStatus(DONE);
            view.printInfo(runner, message);
        } else {
            // this mean that application has failed to start
            notificationType = ERROR;

            runner.setStatus(FAILED);
            logsAction.perform(runner);
            view.printError(runner, message);
        }

        notification.update(message, notificationType, FINISHED, null, true);
        presenter.update(runner);

        stop();
    }

    private void processFailedMessage() {
        runner.setAppLaunchStatus(false);
        runner.setAppRunningStatus(false);
        runner.setAliveStatus(false);
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
        runner.setAppLaunchStatus(false);
        runner.setAppRunningStatus(false);
        runner.setAliveStatus(false);
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

    private void startCheckingAppHealth() {
        if (runner.getApplicationURL() == null) {
            return;
        }

        changeAppAliveTimer = new Timer() {
            /** {@inheritDoc} */
            @Override
            public void run() {
                runner.setAliveStatus(true);
                runner.setStatus(RUNNING);

                presenter.update(runner);

                String projectName = project.getProjectDescription().getName();
                String notificationMessage = locale.applicationMaybeStarted(projectName);

                notification.update(notificationMessage, WARNING, FINISHED, null, true);
                view.printWarn(runner, notificationMessage);
            }
        };
        changeAppAliveTimer.schedule(TIMEOUT);

        runnerHealthHandler = new SubscriptionHandler<String>(new StringUnmarshallerWS()) {
            /** {@inheritDoc} */
            @Override
            protected void onMessageReceived(String result) {
                JSONObject jsonObject = JSONParser.parseStrict(result).isObject();

                if (jsonObject == null || !jsonObject.containsKey(URL) || !jsonObject.containsKey(STATUS)) {
                    return;
                }

                String urlStatus = jsonObject.get(STATUS).isString().stringValue();
                if (!OK_STATUS.equals(urlStatus)) {
                    return;
                }

                changeAppAliveTimer.cancel();

                runner.setAliveStatus(true);
                runner.setStatus(RUNNING);

                presenter.update(runner);

                String projectName = project.getProjectDescription().getName();
                String notificationMessage = locale.applicationStarted(projectName);

                notification.update(notificationMessage, INFO, FINISHED, null, true);
                view.printInfo(runner, notificationMessage);

                stop();
            }

            /** {@inheritDoc} */
            @Override
            protected void onErrorReceived(Throwable exception) {
                Log.error(AbstractAppLaunchAction.class, exception);
            }
        };

        try {
            messageBus.subscribe(APP_HEALTH_CHANNEL + runner.getProcessId(), runnerHealthHandler);
        } catch (WebSocketException e) {
            Log.error(AbstractAppLaunchAction.class, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull Runner runner) {
        this.runner = runner;
        project = appContext.getCurrentProject();
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        if (changeAppAliveTimer != null) {
            changeAppAliveTimer.cancel();
        }

        long processId = runner.getProcessId();

        unsubscribeHandler(APP_HEALTH_CHANNEL + processId, runnerHealthHandler);
        unsubscribeHandler(STATUS_CHANNEL + processId, runnerStatusHandler);
        unsubscribeHandler(OUTPUT_CHANNEL + processId, runnerOutputHandler);

        logsAction.stop();
    }

    private void unsubscribeHandler(@Nonnull String channel, @Nonnull SubscriptionHandler handler) {
        if (!messageBus.isHandlerSubscribed(handler, channel)) {
            return;
        }

        try {
            messageBus.unsubscribe(channel, handler);
        } catch (WebSocketException e) {
            Log.error(AbstractAppLaunchAction.class, e);
        }
    }

}