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

package com.codenvy.ide.ext.runner.client.callbacks;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.Unmarshallable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class to receive a response from a remote procedure call.
 *
 * @author Evgen Vidolob
 * @author Valeriy Svydenko
 * @author Dmitry Shnurenko
 * @author Andrey Plotnikov
 */
public class RunnerAsyncRequestCallback<T> extends AsyncRequestCallback<T> {

    private final NotificationManager        notificationManager;
    private final RunnerLocalizationConstant locale;
    private final SuccessCallback<T>         successCallback;
    private final FailureCallback            failureCallback;

    public RunnerAsyncRequestCallback(@Nonnull NotificationManager notificationManager,
                                      @Nonnull RunnerLocalizationConstant locale,
                                      @Nullable Unmarshallable<T> unmarshaller,
                                      @Nonnull SuccessCallback<T> successCallback,
                                      @Nullable FailureCallback failureCallback) {
        super(unmarshaller);
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.successCallback = successCallback;
        this.failureCallback = failureCallback;
    }

    /** {@inheritDoc} */
    @Override
    protected void onSuccess(@Nonnull T result) {
        successCallback.onSuccess(result);
    }

    /** {@inheritDoc} */
    @Override
    protected void onFailure(@Nonnull Throwable exception) {
        if (failureCallback != null) {
            failureCallback.onFailure(exception);
            return;
        }

        String message = exception.getMessage();

        boolean isServerExceptionMessage = !message.isEmpty() && exception instanceof ServerException;

        notificationManager.showError(isServerExceptionMessage ? message : locale.unknownErrorMessage());
    }

}