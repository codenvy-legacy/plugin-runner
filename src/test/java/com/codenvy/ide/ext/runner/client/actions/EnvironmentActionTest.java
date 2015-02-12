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

import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.manager.RunnerManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class EnvironmentActionTest {

    private static final String SOME_TEXT = "someText";

    @Mock
    private RunnerManager runnerManager;
    @Mock
    private RunOptions    runOptions;
    @Mock
    private DtoFactory    dtoFactory;
    @Mock
    private AppContext    appContext;
    @Mock
    private ActionEvent   actionEvent;

    private EnvironmentAction action;

    @Before
    public void setUp() {
        action = new EnvironmentAction(appContext, runnerManager, dtoFactory, SOME_TEXT, SOME_TEXT, SOME_TEXT);
    }

    @Test
    public void actionShouldBePerformed() throws Exception {
        when(dtoFactory.createDto(RunOptions.class)).thenReturn(runOptions);
        when(runOptions.withEnvironmentId(anyString())).thenReturn(runOptions);

        action.actionPerformed(actionEvent);

        verify(dtoFactory).createDto(RunOptions.class);
        verify(runOptions).withEnvironmentId("project://" + SOME_TEXT);
        verify(runnerManager).launchRunner(runOptions, SOME_TEXT);
    }
}