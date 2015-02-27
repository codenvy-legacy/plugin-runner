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
package com.codenvy.ide.ext.runner.client.inject.factories;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The factory for creating an instances of different models which use in the project.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public interface ModelsFactory {

    /**
     * Creates a runner with runner options without environment name. It means the title of the runner will be generated without additional
     * suffix.
     *
     * @param runOptions
     *         options which needs to be used
     * @return an instance of {@link Runner}
     */
    @Nonnull
    Runner createRunner(@Nonnull RunOptions runOptions);

    /**
     * Creates a runner with runner options and environment name.  It means the title of the runner will be generated with additional
     * suffix.
     *
     * @param runOptions
     *         options which needs to be used
     * @param environmentName
     *         additional part of name for runner
     * @return an instance of {@link Runner}
     */
    @Nonnull
    Runner createRunner(@Nonnull RunOptions runOptions, @Nullable String environmentName);

    /**
     * Creates environments with options and special name.
     *
     * @param runnerEnvironment
     *         runner environment which we get from server
     * @param scope
     *         scope which need set to environment
     * @return an instance of {@link Environment}
     */
    @Nonnull
    Environment createEnvironment(@Nonnull RunnerEnvironment runnerEnvironment, @Nonnull Scope scope);

}