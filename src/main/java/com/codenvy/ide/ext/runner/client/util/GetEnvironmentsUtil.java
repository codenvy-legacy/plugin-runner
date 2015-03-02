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
package com.codenvy.ide.ext.runner.client.util;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Provides methods which allow gets all environments and gets nodes with language type environments.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(GetEnvironmentsUtilImpl.class)
public interface GetEnvironmentsUtil {

    /**
     * The method allows get all environments.
     *
     * @param tree
     *         node which contains environments
     * @return list environments.
     */
    @Nonnull
    List<RunnerEnvironmentLeaf> getAllEnvironments(@Nonnull RunnerEnvironmentTree tree);

    /**
     * Gets all environments from nodes and adds them to list.
     *
     * @param leaves
     *         list with nodes
     * @param scope
     *         scope of runner environments
     * @return list of environments
     */
    @Nonnull
    List<Environment> getEnvironmentsFromNodes(@Nonnull List<RunnerEnvironmentLeaf> leaves, @Nonnull Scope scope);

    /**
     * Returns list of environments from environments tree which relate to current project type.
     *
     * @param tree
     *         tree from which need get environments
     * @param projectType
     *         type of project for which need get environments
     * @param scope
     *         scope of runner environments
     * @return list environments
     */
    @Nonnull
    List<Environment> getEnvironmentsByProjectType(@Nonnull RunnerEnvironmentTree tree,
                                                   @Nonnull String projectType,
                                                   @Nonnull Scope scope);

    /**
     * Returns category of runner for current project type.
     *
     * @param tree
     *         tree from which we get runner environments for current project type
     * @param projectType
     *         type of project
     * @return tree which contains all runner environments for current project type
     */
    @Nonnull
    RunnerEnvironmentTree getRunnerCategoryByProjectType(@Nonnull RunnerEnvironmentTree tree, @Nonnull String projectType);
}