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
package com.codenvy.ide.ext.runner.client.actions;

import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.customenvironment.customenvironmentview.CustomEnvironmentPresenter;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * Action which allows run project with user's runner parameters.
 *
 * @author Dmitry Shnurenko
 */
public class EditRunnerAction extends ProjectAction {

    private final CustomEnvironmentPresenter environmentPresenter;

    @Inject
    public EditRunnerAction(RunnerLocalizationConstant locale, RunnerResources resources, CustomEnvironmentPresenter environmentPresenter) {
        super(locale.actionEditRun(), locale.actionEditRunDescription(), resources.editEnvironmentsImage());

        this.environmentPresenter = environmentPresenter;
    }

    /** {@inheritDoc} */
    @Override
    protected void updateProjectAction(@Nonnull ActionEvent event) {
        CurrentProject currentProject = appContext.getCurrentProject();

        event.getPresentation().setEnabledAndVisible(currentProject != null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(@Nonnull ActionEvent event) {
        environmentPresenter.showDialog();
    }
}