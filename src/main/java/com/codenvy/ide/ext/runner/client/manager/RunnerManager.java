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
package com.codenvy.ide.ext.runner.client.manager;

import com.codenvy.api.runner.dto.RunOptions;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * It is the main class of structure. It provides an ability to launch a new runner. It can launch default runner or custom runner. Default
 * runner will be launched when one uses method without runner options in other case will be launched custom runner that described into
 * options.
 *
 * @author Andrey Plotnikov
 */
@ImplementedBy(RunnerManagerPresenter.class)
public interface RunnerManager {

    /** Launch a new default runner. */
    void launchRunner();

    /**
     * Launch a new runner with given configurations.
     *
     * @param environmentName
     *         name of custom configuration
     */
    void launchRunner(@Nonnull RunOptions runOptions, @Nonnull String environmentName);

}