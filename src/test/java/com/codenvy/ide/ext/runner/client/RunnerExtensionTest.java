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
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.ext.runner.client.actions.CustomRunAction;
import com.codenvy.ide.ext.runner.client.actions.EditRunnerAction;
import com.codenvy.ide.ext.runner.client.actions.RunAction;
import com.codenvy.ide.ext.runner.client.actions.RunWithGroup;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.codenvy.ide.ext.runner.client.wizard.SelectRunnerPagePresenter;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static com.codenvy.api.project.shared.Constants.BLANK_ID;
import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_CONTEXT_MENU;
import static com.codenvy.ide.api.action.IdeActions.GROUP_RUN_TOOLBAR;
import static com.codenvy.ide.ext.runner.client.RunnerExtension.BUILDER_PART_ID;
import static com.codenvy.ide.ext.runner.client.constants.ActionId.GROUP_RUN_WITH;
import static com.codenvy.ide.ext.runner.client.constants.ActionId.RUN_APP_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunnerExtensionTest {

    @Captor
    private ArgumentCaptor<Constraints>         constraintsCaptor;
    @Captor
    private ArgumentCaptor<DefaultActionGroup>  actionGroupCaptor;
    @Mock
    private RunnerResources.RunnerCss           runnerCss;
    @Mock
    private RunnerResources                     resources;
    @Mock
    private ProjectTypeWizardRegistry           wizardRegistry;
    @Mock
    private Provider<SelectRunnerPagePresenter> runnerPagePresenter;
    @Mock
    private ProjectWizard                       wizard;
    private RunnerExtension                     extension;

    @Before
    public void setUp() throws Exception {
        when(resources.runnerCss()).thenReturn(runnerCss);
        when(wizardRegistry.getWizard(BLANK_ID)).thenReturn(wizard);

        extension = new RunnerExtension(resources, wizardRegistry, runnerPagePresenter);
    }

    @Test
    public void cssResourcesShouldBeInjected() throws Exception {
        verify(runnerCss).ensureInjected();
    }

    @Test
    public void runnerPanelShouldBeOpened() throws Exception {
        WorkspaceAgent workspaceAgent = mock(WorkspaceAgent.class);
        RunnerManagerPresenter runnerManagerPresenter = mock(RunnerManagerPresenter.class);

        extension.setUpRunnerConsole(workspaceAgent, runnerManagerPresenter);

        verify(workspaceAgent).openPart(eq(runnerManagerPresenter), eq(PartStackType.INFORMATION), constraintsCaptor.capture());
        verifyConstants(Anchor.AFTER, BUILDER_PART_ID);
    }

    @Test
    public void runnerPageShouldBeAdded() throws Exception {
        verify(wizard).addPage(runnerPagePresenter);
        verify(wizardRegistry).addWizard(BLANK_ID, wizard);
    }

    @Test
    public void runnerPageShouldNotBeAddedIfWizardIsNull() throws Exception {
        when(wizardRegistry.getWizard(BLANK_ID)).thenReturn(null);
        reset(wizard);

        extension = new RunnerExtension(resources, wizardRegistry, runnerPagePresenter);

        verify(wizard, never()).addPage(runnerPagePresenter);
    }

    @Test
    public void runnerMenuActionsShouldBeAdded() throws Exception {
        // prepare step
        ActionManager actionManager = mock(ActionManager.class);

        RunAction runAction = mock(RunAction.class);
        EditRunnerAction editRunnerAction = mock(EditRunnerAction.class);
        RunWithGroup runWithGroup = mock(RunWithGroup.class);
        CustomRunAction customRunAction = mock(CustomRunAction.class);

        DefaultActionGroup mainToolbarGroup = mock(DefaultActionGroup.class);
        DefaultActionGroup runMenuActionGroup = mock(DefaultActionGroup.class);
        DefaultActionGroup runContextGroup = mock(DefaultActionGroup.class);
        DefaultActionGroup contextMenuGroup = mock(DefaultActionGroup.class);

        when(actionManager.getAction(GROUP_MAIN_TOOLBAR)).thenReturn(mainToolbarGroup);
        when(actionManager.getAction(GROUP_RUN)).thenReturn(runMenuActionGroup);
        when(actionManager.getAction(GROUP_RUN_CONTEXT_MENU)).thenReturn(runContextGroup);
        when(actionManager.getAction(GROUP_MAIN_CONTEXT_MENU)).thenReturn(contextMenuGroup);

        // test step
        extension.setUpRunActions(actionManager, runAction, editRunnerAction, runWithGroup, customRunAction);

        // check step
        verify(mainToolbarGroup).add(actionGroupCaptor.capture());

        verify(runContextGroup).addSeparator();
        verify(runContextGroup).add(runAction);
        verify(contextMenuGroup).add(runContextGroup);

        DefaultActionGroup runToolbarGroup = actionGroupCaptor.getValue();
        assertThat(runToolbarGroup.getChildrenCount(), is(2));

        verify(actionManager).registerAction(GROUP_RUN_TOOLBAR, runToolbarGroup);
        verify(actionManager).registerAction(GROUP_RUN_WITH.getId(), runWithGroup);

        verify(runWithGroup).add(editRunnerAction);
        verify(runWithGroup).addSeparator();

        verify(runMenuActionGroup).add(runAction, Constraints.FIRST);

        verify(runMenuActionGroup).add(eq(runWithGroup), constraintsCaptor.capture());
        verifyConstants(Anchor.AFTER, RUN_APP_ID.getId());

        verify(runMenuActionGroup).add(eq(customRunAction), constraintsCaptor.capture());
        verifyConstants(Anchor.AFTER, GROUP_RUN_WITH.getId());
    }

    private void verifyConstants(Anchor anchor, String actionId) {
        Constraints constraints = constraintsCaptor.getValue();
        assertThat(constraints.myAnchor, is(anchor));
        assertThat(constraints.myRelativeToActionId, is(actionId));
    }

}