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
package com.codenvy.ide.ext.runner.client.widgets.templates;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.ide.ext.runner.client.tab.TabPresenter;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * Provides methods which allow work with templates panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TemplatesPresenter.class)
public interface TemplatesPanel extends TabPresenter {
    /**
     * Calls special method on view which set current environment selected.
     *
     * @param environment
     *         environment which was selected
     */
    void select(@Nonnull RunnerEnvironment environment);
}