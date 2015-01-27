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
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * Action which allows run project with default runner parameters.
 *
 * @author Dmitry Shnurenko
 */
public class RunAction extends AbstractRunnerActions {

    private final RunnerManagerPresenter runnerManagerPresenter;

    @Inject
    public RunAction(RunnerManagerPresenter runnerManagerPresenter,
                     RunnerLocalizationConstant locale,
                     AppContext appContext,
                     RunnerResources resources) {
        super(appContext, locale.actionRun(), locale.actionRunDescription(), resources.runAppImage());

        this.runnerManagerPresenter = runnerManagerPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(@Nonnull ActionEvent event) {
        runnerManagerPresenter.launchRunner();
    }
}