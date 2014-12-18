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

import com.codenvy.api.project.server.ProjectDescription;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.ext.runner.client.models.Runner;

import javax.annotation.Nonnull;

/**
 * The factory for creating an instances of different models which uses in the project.
 *
 * @author Andrey Plotnikov
 */
public interface ModelsFactory {

    /**
     * Creates a runner from project description. Runner with default configuration.
     *
     * @param projectDescription
     *         project description that needs to be analyzed
     * @return an instance of {@link Runner}
     */
    @Nonnull
    Runner createRunner(@Nonnull ProjectDescription projectDescription);

    /**
     * Creates a runner from custom configuration.
     *
     * @param runOptions
     *         runner options which need to analyzed
     * @param environmentName
     *         additional part of name for runner
     * @return an instance of {@link Runner}
     */
    @Nonnull
    Runner createRunner(@Nonnull RunOptions runOptions, String environmentName);

}