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
import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * Provides methods which allow display runner environments on special widget.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TemplatesWidgetImpl.class)
public interface TemplatesWidget extends View<TemplatesWidget.ActionDelegate> {

    /**
     * Adds environment on templates panel and.
     *
     * @param environment
     *         runner which was added
     */
    void addEnvironment(@Nonnull RunnerEnvironment environment);

    /** Clears panel with environments */
    void clear();

    interface ActionDelegate {

        /**
         * Performs some actions when user click on environment.
         *
         * @param environment
         *         selected environment
         */
        void onEnvironmentSelected(@Nonnull RunnerEnvironment environment);

        /** Performs some actions when user click on all type button. */
        void onAllTypeButtonClicked();

        void onLangTypeButtonClicked(@Nonnull EnvironmentType languageType);

        /** Performs some actions when user click on project scope button. */
        void onProjectScopeButtonClicked();

        /** Performs some actions when user click on system scope button. */
        void onSystemScopeButtonClicked();
    }
}
