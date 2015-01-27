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
package com.codenvy.ide.ext.runner.client.actions;

import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.wizard.DefaultWizard;
import com.codenvy.ide.api.wizard.DefaultWizardFactory;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunWithGroupTest {

    @Mock
    private DefaultWizard              wizard;
    @Mock
    private DefaultWizardFactory       defaultWizardFactory;
    @Mock
    private WizardDialog               dialog;
    @Mock
    private AppContext                 appContext;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private ActionManager              actionManager;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ActionEvent                event;
    @Mock
    private CurrentProject             currentProject;
    @Mock
    private RunnerResources            resources;

    @InjectMocks
    private RunWithGroup runWithGroup;

    @Test
    public void constructorShouldBeVerified() throws Exception {
        verify(locale).actionGroupRunWith();
        verify(locale).actionGroupRunWithDescription();
        verify(resources).runWithImage();
    }

    @Test
    public void groupShouldNotBeVisibleWhenProjectIsNull() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(null);

        runWithGroup.update(event);

        verify(event.getPresentation()).setEnabledAndVisible(false);
    }

    @Test
    public void groupShouldNotBeVisibleWhenCurrentProjectIsReadOnly() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.isReadOnly()).thenReturn(true);

        runWithGroup.update(event);

        verify(event.getPresentation()).setEnabledAndVisible(false);
    }

    @Test
    public void groupShouldBeVisibleWhenProjectIsNotNullAndNotReadOnly() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(currentProject.isReadOnly()).thenReturn(false);

        runWithGroup.update(event);

        verify(event.getPresentation()).setEnabledAndVisible(true);
    }

}