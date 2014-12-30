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
package com.codenvy.ide.ext.runner.client.customrun;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * The class allows adapts custom node tree to gwt tree.
 *
 * @author Evgen Vidolob
 * @author Dmitry Shnurenko
 */
@Singleton
public class RunnerDataAdapter implements NodeDataAdapter<Object> {

    private final Map<Object, TreeNodeElement<Object>> treeNodeElements;
    private final RunnerComparator                     runnerComparator;

    @Inject
    public RunnerDataAdapter(RunnerComparator runnerComparator) {
        this.runnerComparator = runnerComparator;
        this.treeNodeElements = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public int compare(@Nonnull Object current, @Nonnull Object other) {
        return runnerComparator.compare(current, other);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(@Nonnull Object tree) {
        if (!(tree instanceof RunnerEnvironmentTree)) {
            return false;
        }

        RunnerEnvironmentTree environmentTree = (RunnerEnvironmentTree)tree;
        return !(environmentTree.getNodes().isEmpty() && environmentTree.getLeaves().isEmpty());
    }

    /** {@inheritDoc} */
    @Override
    public Array<Object> getChildren(@Nonnull Object tree) {
        Array<Object> children = Collections.createArray();

        if (!(tree instanceof RunnerEnvironmentTree)) {
            return children;
        }

        RunnerEnvironmentTree environmentTree = (RunnerEnvironmentTree)tree;
        for (RunnerEnvironmentTree runnerEnvironmentTree : environmentTree.getNodes()) {
            children.add(runnerEnvironmentTree);
        }

        for (RunnerEnvironmentLeaf leaf : environmentTree.getLeaves()) {
            RunnerEnvironment environment = leaf.getEnvironment();
            if (environment != null) {
                children.add(leaf);
            }
        }

        children.sort(runnerComparator);
        return children;
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeId(@Nonnull Object node) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeName(@Nonnull Object node) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Object getParent(@Nonnull Object node) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public TreeNodeElement<Object> getRenderedTreeNode(@Nonnull Object node) {
        return treeNodeElements.get(node);
    }

    /** {@inheritDoc} */
    @Override
    public void setNodeName(@Nonnull Object node, @Nonnull String nodeName) {

    }

    /** {@inheritDoc} */
    @Override
    public void setRenderedTreeNode(@Nonnull Object node, @Nonnull TreeNodeElement<Object> treeNodeElement) {
        treeNodeElements.put(node, treeNodeElement);
    }

    /** {@inheritDoc} */
    @Override
    public Object getDragDropTarget(@Nonnull Object node) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Array<String> getNodePath(@Nonnull Object node) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Object getNodeByPath(@Nonnull Object node, @Nonnull Array<String> relativeNodePath) {
        return null;
    }
}
