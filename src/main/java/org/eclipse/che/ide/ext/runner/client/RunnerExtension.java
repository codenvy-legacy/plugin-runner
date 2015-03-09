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
package org.eclipse.che.ide.ext.runner.client;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.constraints.Anchor;
import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.extension.Extension;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.ext.runner.client.actions.ChooseRunnerAction;
import org.eclipse.che.ide.ext.runner.client.constants.ActionId;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.actions.RunAction;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.eclipse.che.ide.api.action.IdeActions.GROUP_BUILD_TOOLBAR;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static org.eclipse.che.ide.api.action.IdeActions.GROUP_RUN_TOOLBAR;
import static org.eclipse.che.ide.api.constraints.Anchor.AFTER;

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
                                ChooseRunnerAction chooseRunner) {

        //add actions in main toolbar
        DefaultActionGroup mainToolbarGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup runToolbarGroup = new DefaultActionGroup(GROUP_RUN_TOOLBAR, false, actionManager);

        actionManager.registerAction(GROUP_RUN_TOOLBAR, runToolbarGroup);
        actionManager.registerAction(ActionId.CHOOSE_RUNNER_ID.getId(), chooseRunner);
        actionManager.registerAction(ActionId.RUN_APP_ID.getId(), runAction);

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        DefaultActionGroup runContextGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_CONTEXT_MENU);
        runContextGroup.addSeparator();
        runContextGroup.add(runAction);
        contextMenuGroup.add(runContextGroup);

        runToolbarGroup.add(chooseRunner);
        runToolbarGroup.add(runAction, new Constraints(AFTER, ActionId.CHOOSE_RUNNER_ID.getId()));
        mainToolbarGroup.add(runToolbarGroup, new Constraints(Anchor.AFTER, GROUP_BUILD_TOOLBAR));
    }

}