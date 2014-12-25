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
package com.codenvy.ide.ext.runner.client.inject.factories;

import com.codenvy.ide.ext.runner.client.actions.EnvironmentAction;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

/**
 * The factory for creating instances of custom environment actions.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public interface EnvironmentActionFactory {
    /**
     * Create an instance of a {@link EnvironmentAction} with a given parameters.
     *
     * @param title
     *         action's title
     * @param description
     *         action's description
     * @param environmentName
     *         name of custom environment
     * @return new {@link EnvironmentAction}
     */
    @Nonnull
    EnvironmentAction createAction(@Nonnull @Assisted("title") String title,
                                   @Nonnull @Assisted("description") String description,
                                   @Nonnull @Assisted("environmentName") String environmentName);
}