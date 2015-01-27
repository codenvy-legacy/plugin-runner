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
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.customrun.CustomRunPresenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomRunActionTest {

    @Mock
    private CustomRunPresenter         customRunPresenter;
    @Mock
    private RunnerLocalizationConstant locale;
    @Mock
    private ActionEvent                actionEvent;
    @Mock
    private RunnerResources            resources;

    @InjectMocks
    private CustomRunAction action;

    @Test
    public void constructorShouldBeDone() throws Exception {
        verify(locale).actionCustomRun();
        verify(locale).actionCustomRunDescription();
        verify(resources).runAppImage();
    }

    @Test
    public void actionShouldBePerformed() throws Exception {
        action.actionPerformed(actionEvent);

        verify(customRunPresenter).showDialog();
    }
}