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
package com.codenvy.ide.ext.runner.client.customrun;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * Special comparator which allows compare two runner environments objects.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public class RunnerComparator implements Comparator<Object> {

    @Inject
    public RunnerComparator() {
    }

    /** {@inheritDoc} */
    @Override
    public int compare(@Nonnull Object current, @Nonnull Object other) {
        if (current instanceof RunnerEnvironmentTree && other instanceof RunnerEnvironmentLeaf) {
            return 1;
        }

        if (other instanceof RunnerEnvironmentTree && current instanceof RunnerEnvironmentLeaf) {
            return -1;
        }

        if (current instanceof RunnerEnvironmentTree && other instanceof RunnerEnvironmentTree) {
            return ((RunnerEnvironmentTree)current).getDisplayName().compareTo(((RunnerEnvironmentTree)other).getDisplayName());
        }

        if (current instanceof RunnerEnvironmentLeaf && other instanceof RunnerEnvironmentLeaf) {
            return ((RunnerEnvironmentLeaf)current).getDisplayName().compareTo(((RunnerEnvironmentLeaf)other).getDisplayName());
        }

        return 0;
    }
}
