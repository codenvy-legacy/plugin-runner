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
package com.codenvy.ide.ext.runner.client.customrun.customrunview;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides methods which allow changes settings of custom run environments.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
@ImplementedBy(CustomRunViewImpl.class)
public interface CustomRunView extends View<CustomRunView.ActionDelegate> {

    /** Shows dialog window to change custom run settings */
    void showDialog();

    /** Hides dialog. */
    void close();

    /**
     * Sets enabled radio button which matches input value parameter.
     *
     * @param workspaceRam
     *         ram value for which need enables radio button
     */
    void setEnabledRadioButtons(int workspaceRam);

    /**
     * Sets description of custom run environment in special place on view.
     *
     * @param description
     *         description which need set
     */
    void setEnvironmentDescription(@Nullable String description);

    /**
     * Sets enable or disable run button state.
     *
     * @param enabled
     *         <code>true</code>run button enable,<code>false</code> run button disable
     */
    void setRunButtonState(boolean enabled);

    /**
     * Adds custom runner to runner's tree which situated in special place on view.
     *
     * @param environmentTree
     *         runner's tree which contains system and custom runners
     */
    void addRunner(@Nonnull RunnerEnvironmentTree environmentTree);

    /**
     * Returns runner memory size from spacial place on view. Method returns -1, if memory size is incorrect.
     *
     * @return {@link Integer} value of runner memory
     */
    int getRunnerMemorySize();

    /**
     * Sets runner memory size in special place on view.
     *
     * @param memorySize
     *         value of memory size
     */
    void setRunnerMemorySize(@Nonnull String memorySize);

    /**
     * Returns total memory size from spacial place on view.
     *
     * @return {@link Integer} value of total memory
     */
    int getTotalMemorySize();

    /**
     * Sets total memory size in special place on view.
     *
     * @param memorySize
     *         value of memory size
     */
    void setTotalMemorySize(@Nonnull String memorySize);

    /**
     * Returns available memory size from spacial place on view.
     *
     * @return {@link Integer} value of available memory
     */
    int getAvailableMemorySize();

    /**
     * Sets available memory size in special place on view.
     *
     * @param memorySize
     *         value of memory size
     */
    void setAvailableMemorySize(@Nonnull String memorySize);

    /**
     * Sets flag in special place on view which allows runner skip build.
     *
     * @return <code>true</code> build will skipped,<code>false</code> build will not skiped
     */
    boolean isSkipBuildSelected();

    /**
     * Sets flag in special place on view which allows remember options for current runner.
     *
     * @return <code>true</code> options will be remembered,<code>false</code> options will not be remembered
     */
    boolean isRememberOptionsSelected();

    interface ActionDelegate {

        /**
         * Performs some actions in response to user's choosing an environment.
         *
         * @param runnerEnvironment
         *         environment that was chosen
         */
        void onEnvironmentSelected(@Nullable RunnerEnvironment runnerEnvironment);

        /** Performs some actions in response to user's clicking on the 'Run' button. */
        void onRunClicked();

        /** Performs some actions in response to user's clicking on the 'Close' button. */
        void onCancelClicked();
    }
}
