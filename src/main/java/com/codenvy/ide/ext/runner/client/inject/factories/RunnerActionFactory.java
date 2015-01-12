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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.subactions.CheckHealthStatusAction;
import com.codenvy.ide.ext.runner.client.runneractions.impl.launch.subactions.StatusAction;

import javax.annotation.Nonnull;

/**
 * The factory for creating sub-actions for Launch action.
 *
 * @author Andrey Plotnikov
 */
public interface RunnerActionFactory {

    /**
     * Create an instance of {@link StatusAction} with a given notification for updating status of process.
     *
     * @param notification
     *         notification that has to show status of process
     * @return an instance of {@link StatusAction}
     */
    @Nonnull
    StatusAction createStatusAction(@Nonnull Notification notification);

    /**
     * Create an instance of {@link CheckHealthStatusAction} with a given notification for updating status of process.
     *
     * @param notification
     *         notification that has to show status of process
     * @return an instance of {@link CheckHealthStatusAction}
     */
    @Nonnull
    CheckHealthStatusAction createCheckHealthStatusAction(@Nonnull Notification notification);

}