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
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnegative;
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
     * The method allows get nodes with environments which located on certain deep.
     *
     * @param tree
     *         tree from which need get environments
     * @param deep
     *         deep of node selection
     * @return list nodes from certain deep
     */
    List<RunnerEnvironmentTree> getAllEnvironments(@Nonnull RunnerEnvironmentTree tree, @Nonnegative int deep);

    /**
     * The method allows get all environments.
     *
     * @param tree
     *         node which contains environments
     * @return list environments.
     */
    List<RunnerEnvironmentLeaf> getAllEnvironments(@Nonnull RunnerEnvironmentTree tree);
}
