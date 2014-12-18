/*
 * Copyright 2014 Codenvy, S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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