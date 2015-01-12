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

import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * @author Andrey Plotnikov
 */
@Singleton
public class WebSocketUtilImpl implements WebSocketUtil {

    private final MessageBus messageBus;

    @Inject
    public WebSocketUtilImpl(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    /** {@inheritDoc} */
    @Override
    public void subscribeHandler(@Nonnull String channel, @Nonnull SubscriptionHandler handler) {
        try {
            messageBus.subscribe(channel, handler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void unSubscribeHandler(@Nonnull String channel, @Nonnull SubscriptionHandler handler) {
        if (!messageBus.isHandlerSubscribed(handler, channel)) {
            return;
        }

        try {
            messageBus.unsubscribe(channel, handler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }
    }

}