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
package com.codenvy.ide.ext.runner.client.runneractions;

import com.codenvy.ide.ext.runner.client.models.Runner;

import javax.annotation.Nonnull;

/**
 * The general representation of runner manager action. It provides different actions which were bound to this action.
 *
 * @author Andrey Plotnikov
 */
public interface RunnerAction {

    /**
     * Perform any actions which were bound to this action.
     *
     * @param runner
     *         runner that execute this action
     */
    void perform(@Nonnull Runner runner);

    /** Stop all actions which were started for this action. Unsubscribe for all events and etc. */
    void stop();

}