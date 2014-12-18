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
package com.codenvy.ide.ext.runner.client.models;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * It contains all necessary information for every Runner.
 *
 * @author Andrey Plotnikov
 */
public interface Runner {

    /** @return amount of available RAM for current runner */
    @Nonnegative
    int getRAM();

    /** @return the date when this runner was launched */
    @Nonnull
    Date getCreationTime();

    /** @return <code>true</code> if this runner has already been started, <code>false</code> it hasn't */
    boolean isStarted();

    /**
     * Returns title of runner. This value uses for unique identifier every runner on UI components.
     *
     * @return title of runner
     */
    @Nonnull
    String getTitle();

    /** @return status of runner */
    @Nonnull
    Status getStatus();

    /**
     * Changes status of runner.
     *
     * @param status
     *         new status that needs to be applied
     */
    void setStatus(@Nonnull Status status);

    /** @return url where application is running */
    @Nullable
    String getApplicationURL();

    /** @return url where terminal of current runner is located */
    @Nullable
    String getTerminalURL();

    /**
     * Changes application process description.
     *
     * @param description
     *         application process description that needs to set
     */
    void setProcessDescription(@Nonnull ApplicationProcessDescriptor description);

    /** @return <code>true</code> if this runner is alive, <code>false</code> it isn't */
    boolean isAlive();

    /**
     * Changes alive status of a runner.
     *
     * @param isAlive
     *         new alive status of a runner
     */
    void setAliveStatus(boolean isAlive);

    /** The list of available states of a runner. */
    enum Status {
        IN_PROGERESS,
        IN_QUEUE,
        FAILED,
        TIMEOUT,
        IDLE,
        DONE
    }

}