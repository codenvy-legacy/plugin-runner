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
package com.codenvy.ide.ext.runner.client;

import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.constraints.Anchor;
import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.ext.runner.client.actions.ChooseRunnerAction;
import com.codenvy.ide.ext.runner.client.actions.CustomRunAction;
import com.codenvy.ide.ext.runner.client.actions.EditRunnerAction;
import com.codenvy.ide.ext.runner.client.actions.RunAction;
import com.codenvy.ide.ext.runner.client.actions.RunWithGroup;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.action.IdeActions.GROUP_BUILD_TOOLBAR;
import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_TOOLBAR;
import static com.codenvy.ide.api.constraints.Anchor.AFTER;
import static com.codenvy.ide.api.constraints.Constraints.FIRST;
import static com.codenvy.ide.ext.runner.client.constants.ActionId.CHOOSE_RUNNER_ID;
import static com.codenvy.ide.ext.runner.client.constants.ActionId.CUSTOM_RUN_APP;
import static com.codenvy.ide.ext.runner.client.constants.ActionId.GROUP_RUN_WITH;
import static com.codenvy.ide.ext.runner.client.constants.ActionId.RUN_APP_ID;

/**
 * Codenvy IDE3 extension provides functionality of Runner. It has to provides major operation for Runner: launch new runner, get different
 * information about runners, stop runner. The main feature is an ability to runner a few runner in the same time.
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
@Singleton
@Extension(title = "Runner", version = "1.0.0")
public class RunnerExtension {
    //This constant must be synchronized with BUILDER_PART_ID constant which defined into builder extension.
    public static final String BUILDER_PART_ID = "Builder";

    @Inject
    public RunnerExtension(RunnerResources resources) {
        resources.runnerCss().ensureInjected();
    }

    @Inject
    public void setUpRunnerConsole(WorkspaceAgent workspaceAgent, RunnerManagerPresenter runnerManagerPresenter) {
        workspaceAgent.openPart(runnerManagerPresenter, PartStackType.INFORMATION, new Constraints(AFTER, BUILDER_PART_ID));
    }

    @Inject
    public void setUpRunActions(ActionManager actionManager,
                                RunAction runAction,
                                ChooseRunnerAction chooseRunner,
                                EditRunnerAction editRunnerAction,
                                RunWithGroup runWithGroup,
                                CustomRunAction customRunAction) {

        //add actions in main toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup runToolbarGroup = new DefaultActionGroup(GROUP_RUN_TOOLBAR, false, actionManager);

        actionManager.registerAction(CUSTOM_RUN_APP.getId(), customRunAction);
        actionManager.registerAction(RUN_APP_ID.getId(), runAction);
        actionManager.registerAction(CHOOSE_RUNNER_ID.getId(), chooseRunner);
        actionManager.registerAction(GROUP_RUN_TOOLBAR, runToolbarGroup);
        actionManager.registerAction(GROUP_RUN_WITH.getId(), runWithGroup);

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        DefaultActionGroup runContextGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_CONTEXT_MENU);
        runContextGroup.addSeparator();
        runContextGroup.add(runAction);
        contextMenuGroup.add(runContextGroup);

        runWithGroup.add(editRunnerAction);
        runWithGroup.addSeparator();

        //runToolbarGroup.add(runWithGroup, new Constraints(AFTER, RUN_APP_ID.getId()));
        runToolbarGroup.add(runAction);
        runToolbarGroup.add(chooseRunner, new Constraints(AFTER, RUN_APP_ID.getId()));

        mainToolbarGroup.add(runToolbarGroup, new Constraints(Anchor.AFTER, GROUP_BUILD_TOOLBAR));

        //add actions in Run menu
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN);
        runMenuActionGroup.add(runAction, FIRST);
        runMenuActionGroup.add(runWithGroup, new Constraints(AFTER, RUN_APP_ID.getId()));
        runMenuActionGroup.add(customRunAction, new Constraints(AFTER, GROUP_RUN_WITH.getId()));
    }

}