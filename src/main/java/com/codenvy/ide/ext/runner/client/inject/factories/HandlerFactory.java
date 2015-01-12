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
package com.codenvy.ide.ext.runner.client.inject.factories;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.common.LogMessagesHandler;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.common.LogMessagesHandler.ErrorHandler;

import javax.annotation.Nonnull;

/**
 * The factory for creating an instances of different handlers.
 *
 * @author Andrey Plotnikov
 */
public interface HandlerFactory {
    /**
     * Creates a handler for a given runner. This handler provides an ability to analyze received console message.
     *
     * @param runner
     *         runner that needs to be bound with a handler
     * @param errorHandler
     *         handler that delegate actions which need to perform when error happened
     * @return an instance of {@link LogMessagesHandler}
     */
    @Nonnull
    LogMessagesHandler createLogMessageHandler(@Nonnull Runner runner, @Nonnull ErrorHandler errorHandler);
}