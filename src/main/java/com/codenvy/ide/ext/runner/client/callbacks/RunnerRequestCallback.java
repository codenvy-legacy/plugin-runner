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
package com.codenvy.ide.ext.runner.client.callbacks;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.Unmarshallable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class to receive a response from a remote procedure call.
 *
 * @author Evgen Vidolob
 * @author Dmitry Shnurenko
 */
public class RunnerRequestCallback<T> extends RequestCallback<T> {

    private FailureCallback     failureCallback;
    private SuccessCallback<T>  successCallback;
    private NotificationManager notificationManager;

    public RunnerRequestCallback(@Nonnull NotificationManager notificationManager,
                                 @Nullable Unmarshallable<T> unmarshallable,
                                 @Nonnull SuccessCallback<T> successCallback,
                                 @Nullable FailureCallback failureCallback) {
        super(unmarshallable);
        this.notificationManager = notificationManager;
        this.successCallback = successCallback;
        this.failureCallback = failureCallback;
    }

    /** {@inheritDoc} */
    @Override
    public void onSuccess(T result) {
        successCallback.onSuccess(result);
    }

    /** {@inheritDoc} */
    @Override
    public void onFailure(Throwable exception) {
        if (failureCallback != null) {
            failureCallback.onFailure(exception);
            return;
        }

        notificationManager.showError(exception.getMessage());
    }
}