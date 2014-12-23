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
package com.codenvy.ide.ext.runner.client;

import com.codenvy.ide.api.constraints.Anchor;
import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Codenvy IDE3 extension provides functionality of Runner. It has to provides major operation for Runner: launch new runner, get different
 * information about runners, stop runner. The main feature is an ability to runner a few runner in the same time.
 *
 * @author Andrey Plotnikov
 */
@Singleton
@Extension(title = "Runner", version = "1.0.0")
public class RunnerExtension2 { //TODO need rename

    @Inject
    public RunnerExtension2(RunnerResources resources) {

        resources.runnerCss().ensureInjected();
    }

    @Inject
    public void setUpRunnerConsole(WorkspaceAgent workspaceAgent,
                                   RunnerManagerPresenter runnerManagerPresenter) {

        workspaceAgent.openPart(runnerManagerPresenter, PartStackType.INFORMATION, new Constraints(Anchor.AFTER, "Builder"));
    }
}