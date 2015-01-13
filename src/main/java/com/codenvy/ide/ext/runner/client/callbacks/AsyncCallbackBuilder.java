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
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * The builder that provides an ability to create an instance of {@link AsyncRequestCallback}. It has to simplify work flow of creating
 * callback.
 *
 * @param <T>
 *         type of element that has to be returned from server
 * @author Andrey Plotnikov
 */
public class AsyncCallbackBuilder<T> {

    private final NotificationManager        notificationManager;
    private final RunnerLocalizationConstant locale;
    private final DtoUnmarshallerFactory     dtoUnmarshallerFactory;

    private SuccessCallback<T> successCallback;
    private Unmarshallable<T>  unmarshaller;
    private FailureCallback    failureCallback;
    private Class<T>           clazz;

    @Inject
    public AsyncCallbackBuilder(NotificationManager notificationManager,
                                RunnerLocalizationConstant locale,
                                DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /**
     * Add success callback to configuration of callback that needs to be created.
     *
     * @param successCallback
     *         callback that has to be added
     * @return an instance of builder with changed configuration
     */
    @Nonnull
    public AsyncCallbackBuilder<T> success(@Nonnull SuccessCallback<T> successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    /**
     * Add unmarshaller to configuration of callback that needs to be created.
     *
     * @param unmarshaller
     *         unmarshaller that has to be added
     * @return an instance of builder with changed configuration
     */
    @Nonnull
    public AsyncCallbackBuilder<T> unmarshaller(@Nonnull Unmarshallable<T> unmarshaller) {
        this.unmarshaller = unmarshaller;
        return this;
    }

    /**
     * Add clazz of unmarshaller that has to be created automatically and add to configuration of callback that needs to be created.
     *
     * @param clazz
     *         class of unmarshaller
     * @return an instance of builder with changed configuration
     */
    @Nonnull
    public AsyncCallbackBuilder<T> unmarshaller(@Nonnull Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    /**
     * Add failure callback to configuration of callback that needs to be created.
     *
     * @param failureCallback
     *         callback that has to be added
     * @return an instance of builder with changed configuration
     */
    @Nonnull
    public AsyncCallbackBuilder<T> failure(@Nonnull FailureCallback failureCallback) {
        this.failureCallback = failureCallback;
        return this;
    }

    /** @return an instance of {link AsyncRequestCallback} with a given configuration */
    @Nonnull
    public AsyncRequestCallback<T> build() {
        if (successCallback == null) {
            throw new IllegalStateException("You forgot to initialize success callback parameter. Please, fix it and try again.");
        }

        if (unmarshaller == null && clazz != null) {
            unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(clazz);
        }

        return new RunnerAsyncRequestCallback<>(notificationManager, locale, unmarshaller, successCallback, failureCallback);
    }

}