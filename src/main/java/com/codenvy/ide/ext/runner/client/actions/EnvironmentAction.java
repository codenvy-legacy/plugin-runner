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
import com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

/**
 * Action for executing custom runner environments.
 *
 * @author Dmitry Shnurenko
 */
public class EnvironmentAction extends AbstractRunnerActions {

    private final RunnerManagerPresenter managerPresenter;
    private final DtoFactory             dtoFactory;
    private final String                 environmentName;

    @Inject
    public EnvironmentAction(AppContext appContext,
                             RunnerManagerPresenter managerPresenter,
                             DtoFactory dtoFactory,
                             @Nonnull @Assisted("title") String title,
                             @Nonnull @Assisted("description") String description,
                             @Nonnull @Assisted("environmentName") String environmentName) {
        super(appContext, title, description, null);

        this.managerPresenter = managerPresenter;
        this.dtoFactory = dtoFactory;
        this.environmentName = environmentName;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(@Nonnull ActionEvent event) {
        RunOptions runOptions = dtoFactory.createDto(RunOptions.class)
                                          .withEnvironmentId("project://" + environmentName);

        managerPresenter.launchRunner(runOptions, environmentName);
    }
}