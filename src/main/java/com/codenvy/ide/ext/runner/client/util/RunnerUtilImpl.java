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
package com.codenvy.ide.ext.runner.client.util;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.console.ConsoleContainer;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.properties.common.RAM._128;

/**
 * Contains implementations of methods which are general for runner plugin classes.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class RunnerUtilImpl implements RunnerUtil {

    private final DialogFactory              dialogFactory;
    private final RunnerLocalizationConstant locale;
    private final RunnerManagerPresenter     presenter;
    private final NotificationManager        notificationManager;
    private final ConsoleContainer           consoleContainer;

    @Inject
    public RunnerUtilImpl(DialogFactory dialogFactory,
                          RunnerLocalizationConstant locale,
                          RunnerManagerPresenter presenter,
                          ConsoleContainer consoleContainer,
                          NotificationManager notificationManager) {
        this.dialogFactory = dialogFactory;
        this.locale = locale;
        this.presenter = presenter;
        this.notificationManager = notificationManager;
        this.consoleContainer = consoleContainer;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRunnerMemoryCorrect(@Nonnegative int totalMemory, @Nonnegative int usedMemory, @Nonnegative int availableMemory) {
        if (usedMemory < 0 || totalMemory < 0 || availableMemory < 0) {
            showWarning(locale.messagesIncorrectValue());
            return false;
        }

        if (usedMemory % _128.getValue() != 0) {
            showWarning(locale.ramSizeMustBeMultipleOf(_128.getValue()));
            return false;
        }

        if (usedMemory > totalMemory) {
            showWarning(locale.messagesTotalRamLessCustom(usedMemory, totalMemory));
            return false;
        }

        if (usedMemory > availableMemory) {
            showWarning(locale.messagesAvailableRamLessCustom(usedMemory, totalMemory, totalMemory - availableMemory));
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void showWarning(@Nonnull String message) {
        dialogFactory.createMessageDialog(locale.titlesWarning(), message, null).show();
    }

    /** {@inheritDoc} */
    @Override
    public void showError(@Nonnull Runner runner, @Nonnull String message, @Nullable Throwable exception) {
        Notification notification = new Notification(message, ERROR, true);

        showError(runner, message, exception, notification);

        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void showError(@Nonnull Runner runner,
                          @Nonnull String message,
                          @Nullable Throwable exception,
                          @Nonnull Notification notification) {
        runner.setStatus(FAILED);

        presenter.update(runner);

        notification.update(message, ERROR, FINISHED, null, true);

        if (exception != null && exception.getMessage() != null) {
            consoleContainer.printError(runner, message + ": " + exception.getMessage());
        } else {
            consoleContainer.printError(runner, message);
        }
    }

}