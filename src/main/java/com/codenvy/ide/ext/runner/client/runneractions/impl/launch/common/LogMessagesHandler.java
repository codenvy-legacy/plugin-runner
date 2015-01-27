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

import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.util.TimeInterval.FIVE_SEC;

/**
 * This class listens for log messages from the server and process it. Logic of this class is slightly complicated since we can't guaranty
 * correct order of messages and delivery it from the server over WebSocket connection. So messages may be received in shuffled order and
 * some messages may be never received.
 *
 * @author Artem Zatsarynnyy
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class LogMessagesHandler extends SubscriptionHandler<LogMessage> {

    private final RunnerManagerView        view;
    private final Runner                   runner;
    private final ErrorHandler             errorHandler;
    private final Map<Integer, LogMessage> postponedMessages;
    private final Timer                    flushTimer;

    private int lastPrintedMessageNum;

    @Inject
    public LogMessagesHandler(RunnerManagerPresenter runnerManagerPresenter,
                              LogMessageUnmarshaller unmarshaller,
                              @Nonnull @Assisted Runner runner,
                              @Nonnull @Assisted ErrorHandler errorHandler) {
        super(unmarshaller);

        this.runner = runner;
        this.errorHandler = errorHandler;
        this.view = runnerManagerPresenter.getView();
        this.postponedMessages = new HashMap<>();

        this.flushTimer = new Timer() {
            @Override
            public void run() {
                printAllPostponedMessages();
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessageReceived(LogMessage message) {
        int number = message.getNumber();

        if (number == lastPrintedMessageNum + 1) {
            flushTimer.cancel();
            printLine(message);
        } else if (number > lastPrintedMessageNum) {
            postponedMessages.put(number, message);
        }

        printNextPostponedMessages();

        flushTimer.schedule(FIVE_SEC.getValue());
    }

    /** Print all messages from buffer for the moment and stop handling. */
    public void stop() {
        printAllPostponedMessages();
        flushTimer.cancel();
    }

    /** Print next postponed messages with contiguous line numbers. */
    private void printNextPostponedMessages() {
        LogMessage nextLogMessage = postponedMessages.remove(lastPrintedMessageNum + 1);

        while (nextLogMessage != null) {
            printLine(nextLogMessage);

            nextLogMessage = postponedMessages.remove(nextLogMessage.getNumber() + 1);
        }
    }

    /** Print all postponed messages in correct order. */
    private void printAllPostponedMessages() {
        for (int i = lastPrintedMessageNum + 1; !postponedMessages.isEmpty(); i++) {
            LogMessage nextLogMessage = postponedMessages.remove(i);

            if (nextLogMessage == null) {
                continue;
            }

            printLine(nextLogMessage);
        }
    }

    private void printLine(@Nonnull LogMessage logMessage) {
        view.printMessage(runner, logMessage.getText());
        lastPrintedMessageNum = logMessage.getNumber();
    }

    /** {@inheritDoc} */
    @Override
    protected void onErrorReceived(Throwable throwable) {
        Log.error(LogMessagesHandler.class, throwable);
        errorHandler.onErrorHappened();
    }

    public interface ErrorHandler {
        void onErrorHappened();
    }

}