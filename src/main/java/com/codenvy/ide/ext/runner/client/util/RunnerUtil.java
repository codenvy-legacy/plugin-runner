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

import com.google.inject.ImplementedBy;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * The class contains methods which are general used.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(RunnerUtilImpl.class)
public interface RunnerUtil {

    /**
     * Checks correctness total and used memory. Method shows different messages depending on the values of total and used memory.
     *
     * @param totalMemory
     *         total memory value which accessed current user
     * @param usedMemory
     *         value of memory which used by runner
     * @return <code>true</code> memory values are correct,<code>false</code> memory values are incorrect
     */
    boolean isRunnerMemoryCorrect(@Nonnegative int totalMemory, @Nonnegative int usedMemory);

    /**
     * Shows warning message using dialog factory.
     *
     * @param warningMessage
     *         message which need to show
     */
    void showWarning(@Nonnull String warningMessage);

}