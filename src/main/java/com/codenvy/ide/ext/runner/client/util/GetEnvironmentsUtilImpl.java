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


import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Shnurenko
 */
@Singleton
public class GetEnvironmentsUtilImpl implements GetEnvironmentsUtil {

    private final List<RunnerEnvironmentTree> languageTypeEnvironments;
    private final List<RunnerEnvironmentLeaf> allEnvironments;
    private final List<RunnerEnvironment>     environments;

    @Inject
    public GetEnvironmentsUtilImpl() {
        this.languageTypeEnvironments = new ArrayList<>();
        this.allEnvironments = new ArrayList<>();
        this.environments = new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    public List<RunnerEnvironmentTree> getAllEnvironments(@Nonnull RunnerEnvironmentTree tree, @Nonnegative int deep) {
        languageTypeEnvironments.clear();

        if (deep < 0) {
            throw new IllegalArgumentException("deep value must be over zero");
        }

        if (deep == 0) {
            languageTypeEnvironments.add(tree);
        }

        getEnvironments(tree, deep);

        return languageTypeEnvironments;
    }

    private void getEnvironments(@Nonnull RunnerEnvironmentTree tree, @Nonnegative int deep) {

        while (deep-- > 0) {
            for (RunnerEnvironmentTree environment : tree.getNodes()) {
                languageTypeEnvironments.add(environment);

                getEnvironments(environment, deep);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<RunnerEnvironmentLeaf> getAllEnvironments(@Nonnull RunnerEnvironmentTree tree) {
        allEnvironments.clear();

        getEnvironments(tree, allEnvironments);

        return allEnvironments;
    }

    private void getEnvironments(@Nonnull RunnerEnvironmentTree tree, @Nonnull List<RunnerEnvironmentLeaf> allEnvironments) {
        for (RunnerEnvironmentLeaf environmentLeaf : tree.getLeaves()) {
            allEnvironments.add(environmentLeaf);
        }

        for (RunnerEnvironmentTree environmentTree : tree.getNodes()) {
            getEnvironments(environmentTree, allEnvironments);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<RunnerEnvironment> getEnvironmentsFromNodes(@Nonnull List<RunnerEnvironmentLeaf> environmentList) {
        environments.clear();

        for (RunnerEnvironmentLeaf environmentLeaf : environmentList) {
            environments.add(environmentLeaf.getEnvironment());
        }

        return environments;
    }

}