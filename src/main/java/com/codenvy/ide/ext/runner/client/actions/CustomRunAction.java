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
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.customrun.CustomRunPresenter;
import com.google.inject.Inject;

/**
 * Action which allows user set custom parameters to runner via special dialog window which allows set up runner.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public class CustomRunAction extends ProjectAction {

    private final CustomRunPresenter customRunPresenter;

    @Inject
    public CustomRunAction(RunnerLocalizationConstant locale, RunnerResources resources, CustomRunPresenter customRunPresenter) {
        super(locale.actionCustomRun(), locale.actionCustomRunDescription(), resources.runAppImage());

        this.customRunPresenter = customRunPresenter;
    }

    /** {@inheritDoc} */
    @Override
    protected void updateProjectAction(ActionEvent event) {
        CurrentProject currentProject = appContext.getCurrentProject();

        event.getPresentation().setEnabledAndVisible(currentProject != null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent event) {
        customRunPresenter.showDialog();
    }
}
