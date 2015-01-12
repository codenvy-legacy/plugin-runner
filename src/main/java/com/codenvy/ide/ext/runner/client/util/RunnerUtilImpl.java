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
package com.codenvy.ide.ext.runner.client.util;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerView;
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

/**
 * Contains implementations of methods which are general for runner plugin classes.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class RunnerUtilImpl implements RunnerUtil {

    private static final String MULTIPLE_RAM_SIZE = "128";

    private final DialogFactory              dialogFactory;
    private final RunnerLocalizationConstant locale;
    private final RunnerManagerPresenter     presenter;
    private final NotificationManager        notificationManager;
    private final RunnerManagerView          view;

    @Inject
    public RunnerUtilImpl(DialogFactory dialogFactory,
                          RunnerLocalizationConstant locale,
                          RunnerManagerPresenter presenter,
                          NotificationManager notificationManager) {
        this.dialogFactory = dialogFactory;
        this.locale = locale;
        this.presenter = presenter;
        this.view = presenter.getView();
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRunnerMemoryCorrect(@Nonnegative int totalMemory, @Nonnegative int usedMemory) {
        int availableMemory = totalMemory - usedMemory;

        if (usedMemory < 0 || totalMemory < 0 || availableMemory < 0) {
            showWarning(locale.messagesIncorrectValue());
            return false;
        }

        if (usedMemory < 0 || usedMemory % 128 != 0) {
            showWarning(locale.ramSizeMustBeMultipleOf(MULTIPLE_RAM_SIZE));
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
    public void showWarning(@Nonnull String warningMessage) {
        dialogFactory.createMessageDialog(locale.titlesWarning(), warningMessage, null).show();
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
        runner.setAppLaunchStatus(false);
        runner.setStatus(FAILED);

        presenter.update(runner);

        notification.update(message, ERROR, FINISHED, null, true);

        if (exception != null && exception.getMessage() != null) {
            view.printError(runner, message + ": " + exception.getMessage());
        } else {
            view.printError(runner, message);
        }
    }

}