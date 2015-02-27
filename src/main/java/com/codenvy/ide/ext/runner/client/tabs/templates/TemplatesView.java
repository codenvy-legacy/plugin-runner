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
package com.codenvy.ide.ext.runner.client.tabs.templates;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.templates.scopepanel.ScopePanel;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Provides methods which allow display runner environments on special widget.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TemplatesViewImpl.class)
public interface TemplatesView extends View<TemplatesView.ActionDelegate> {

    /**
     * Adds environment on templates panel and.
     *
     * @param environments
     *         runner which was added
     */
    void addEnvironment(@Nonnull Map<Scope, List<Environment>> environments);

    /**
     * The method creates and adds buttons to special place on view for each group of runner environments.
     *
     * @param environmentTree
     *         tree which contains all groups of environments
     */
    void addButton(@Nonnull RunnerEnvironmentTree environmentTree);

    /**
     * Sets visibility state to panel.
     *
     * @param isVisible
     *         <code>true</code> panel is visible, <code>false</code> panel is un visible
     */
    void setVisible(boolean isVisible);

    /** Clears panel with environments */
    void clearEnvironmentsPanel();

    /** The methods clears type buttons panel when we change project scope. */
    void clearTypeButtonsPanel();

    /**
     * Selects environment widget using current environment.
     *
     * @param selectedEnvironment
     *         environment which was selected
     */
    void selectEnvironment(@Nonnull Environment selectedEnvironment);

    /**
     * Sets scope panel {@link ScopePanel}to special place on templates panel.
     *
     * @param scopePanel
     *         panel which need set
     */
    void setScopePanel(@Nonnull ScopePanel scopePanel);

    interface ActionDelegate {

        /** Performs some actions when user click on all type button. */
        void onAllTypeButtonClicked();

        /**
         * Performs some actions when user clicks on language type button.
         *
         * @param tree
         *         tree which need analyze to get all environments from the tree
         */
        void onLangTypeButtonClicked(@Nonnull RunnerEnvironmentTree tree);
    }
}