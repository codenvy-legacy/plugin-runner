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
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The class contains general actions business logic of runners.
 *
 * @author Dmitry Shnurenko
 */
public abstract class AbstractRunnerActions extends ProjectAction {

    private final AppContext appContext;

    public AbstractRunnerActions(@Nonnull AppContext appContext,
                                 @Nonnull String actionName,
                                 @Nonnull String actionPrompt,
                                 @Nullable SVGResource image) {
        super(actionName, actionPrompt, image);

        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    protected void updateProjectAction(ActionEvent event) {
        CurrentProject currentProject = appContext.getCurrentProject();

        event.getPresentation().setEnabledAndVisible(currentProject != null);
    }
}