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

import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.console.ConsoleContainer;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.util.WebSocketUtil;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.rest.StringUnmarshallerWS;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.api.notification.Notification.Type.WARNING;
import static com.codenvy.ide.ext.runner.client.constants.TimeInterval.THIRTY_SEC;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.DONE;

/**
 * The action that checks status of runner. It pings runner every 30 second and the client side knows that the runner is alive.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class CheckHealthStatusAction extends AbstractRunnerAction {

    /** WebSocket channel to check application's health. */
    private static final String APP_HEALTH_CHANNEL = "runner:app_health:";

    private static final String STATUS    = "status";
    private static final String URL       = "url";
    private static final String OK_STATUS = "OK";

    private final AppContext                 appContext;
    private final RunnerLocalizationConstant locale;
    private final RunnerManagerPresenter     presenter;
    private final WebSocketUtil              webSocketUtil;
    private final Notification               notification;
    private final ConsoleContainer           consoleContainer;

    // The server makes the limited quantity of tries checking application's health,
    // so we're waiting for some time (about 30 sec.) and assume that app health is OK.
    private Timer changeAppAliveTimer;

    private SubscriptionHandler<String> runnerHealthHandler;
    private String                      webSocketChannel;
    private CurrentProject              project;

    @Inject
    public CheckHealthStatusAction(AppContext appContext,
                                   RunnerLocalizationConstant locale,
                                   RunnerManagerPresenter presenter,
                                   WebSocketUtil webSocketUtil,
                                   ConsoleContainer consoleContainer,
                                   @Nonnull @Assisted Notification notification) {
        this.appContext = appContext;
        this.locale = locale;
        this.presenter = presenter;
        this.webSocketUtil = webSocketUtil;
        this.consoleContainer = consoleContainer;

        this.notification = notification;
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        project = appContext.getCurrentProject();

        if (runner.getApplicationURL() == null) {
            return;
        }

        changeAppAliveTimer = new Timer() {
            /** {@inheritDoc} */
            @Override
            public void run() {
                presenter.update(runner);

                String projectName = project.getProjectDescription().getName();
                String notificationMessage = locale.applicationMaybeStarted(projectName);

                notification.update(notificationMessage, WARNING, FINISHED, null, true);
                consoleContainer.printWarn(runner, notificationMessage);
            }
        };
        changeAppAliveTimer.schedule(THIRTY_SEC.getValue());

        webSocketChannel = APP_HEALTH_CHANNEL + runner.getProcessId();
        runnerHealthHandler = new SubscriptionHandler<String>(new StringUnmarshallerWS()) {
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

                runner.setStatus(DONE);

                presenter.update(runner);

                String projectName = project.getProjectDescription().getName();
                String notificationMessage = locale.applicationStarted(projectName);

                notification.update(notificationMessage, INFO, FINISHED, null, true);
                consoleContainer.printInfo(runner, notificationMessage);

                stop();
            }

            @Override
            protected void onErrorReceived(Throwable exception) {
                Log.error(getClass(), exception);
            }
        };

        webSocketUtil.subscribeHandler(webSocketChannel, runnerHealthHandler);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        webSocketUtil.unSubscribeHandler(webSocketChannel, runnerHealthHandler);

        if (changeAppAliveTimer != null) {
            changeAppAliveTimer.cancel();
        }

        super.stop();
    }

}