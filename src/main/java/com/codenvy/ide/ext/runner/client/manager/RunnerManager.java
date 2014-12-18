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
package com.codenvy.ide.ext.runner.client.manager;

import com.codenvy.api.runner.dto.RunOptions;

import javax.annotation.Nonnull;

/**
 * It is the main class of structure. It provides an ability to launch a new runner. It can launch default runner or custom runner. Default
 * runner will be launched when one uses method without runner options in other case will be launched custom runner that described into
 * options.
 *
 * @author Andrey Plotnikov
 */
public interface RunnerManager {

    /** Launch a new default runner. */
    void launchRunner();

    /**
     * Launch a new runner with given configurations.
     *
     * @param runOptions
     *         options which need to be applied to runner
     */
    void launchRunner(@Nonnull RunOptions runOptions);

}