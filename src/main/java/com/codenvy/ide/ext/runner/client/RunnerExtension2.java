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

import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.constraints.Anchor;
import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.ext.runner.client.actions.CustomRunAction;
import com.codenvy.ide.ext.runner.client.actions.EditRunnerAction;
import com.codenvy.ide.ext.runner.client.actions.RunAction;
import com.codenvy.ide.ext.runner.client.actions.RunWithGroup;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_TOOLBAR;

/**
 * Codenvy IDE3 extension provides functionality of Runner. It has to provides major operation for Runner: launch new runner, get different
 * information about runners, stop runner. The main feature is an ability to runner a few runner in the same time.
 *
 * @author Andrey Plotnikov
 */
@Singleton
@Extension(title = "Runner", version = "1.0.0")
public class RunnerExtension2 { //TODO need rename

    private static final String RUN_APP_ID     = "runApp";
    public static final  String GROUP_RUN_WITH = "runWithGroup";

    @Inject
    public RunnerExtension2(RunnerResources resources) {

        resources.runnerCss().ensureInjected();
    }

    @Inject
    public void setUpRunnerConsole(WorkspaceAgent workspaceAgent,
                                   RunnerManagerPresenter runnerManagerPresenter) {

        workspaceAgent.openPart(runnerManagerPresenter, PartStackType.INFORMATION, new Constraints(Anchor.AFTER, "Builder"));
    }

    @Inject
    public void setUpRunActions(ActionManager actionManager,
                                RunAction runAction,
                                EditRunnerAction editRunnerAction,
                                RunWithGroup runWithGroup,
                                CustomRunAction customRunAction) {

        //add actions in main toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup runToolbarGroup = new DefaultActionGroup(GROUP_RUN_TOOLBAR, false, actionManager);

        actionManager.registerAction(GROUP_RUN_TOOLBAR, runToolbarGroup);

        runWithGroup.add(editRunnerAction);
        runWithGroup.addSeparator();

        runToolbarGroup.add(runWithGroup, new Constraints(Anchor.AFTER, RUN_APP_ID));
        runToolbarGroup.add(runAction);

        mainToolbarGroup.add(runToolbarGroup);

        //add actions in Run menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);
        runMenuActionGroup.add(runAction, Constraints.FIRST);
        runMenuActionGroup.add(runWithGroup, new Constraints(Anchor.AFTER, RUN_APP_ID));
        runMenuActionGroup.add(customRunAction, new Constraints(Anchor.AFTER, GROUP_RUN_WITH));
    }

}