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

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.customrun.RunnerDataAdapter;
import com.codenvy.ide.ext.runner.client.customrun.RunnerRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * The Class provides graphical implementation of runner environments which are represented as environment's tree.
 *
 * @author Dmitry Shnurenko
 */
public class TemplatesWidgetImpl extends Composite implements TemplatesWidget {
    interface TemplatesViewImplUiBinder extends UiBinder<Widget, TemplatesWidgetImpl> {
    }

    private static final TemplatesViewImplUiBinder UI_BINDER = GWT.create(TemplatesViewImplUiBinder.class);

    @UiField
    FlowPanel treeContainer;

    private final RunnerEnvironmentTree rootNode;
    private final Tree<Object>          tree;

    private ActionDelegate actionDelegate;

    @Inject
    public TemplatesWidgetImpl(com.codenvy.ide.Resources resources,
                               DtoFactory dtoFactory,
                               RunnerDataAdapter runnerDataAdapter,
                               RunnerRenderer runnerRenderer) {
        initWidget(UI_BINDER.createAndBindUi(this));

        rootNode = dtoFactory.createDto(RunnerEnvironmentTree.class);

        tree = Tree.create(resources, runnerDataAdapter, runnerRenderer);
        tree.getModel().setRoot(rootNode);

        tree.setTreeEventHandler(new Tree.Listener<Object>() {
            @Override
            public void onNodeAction(TreeNodeElement<Object> treeNodeElement) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<Object> treeNodeElement) {
            }

            @Override
            public void onNodeContextMenu(int i, int i2, TreeNodeElement<Object> treeNodeElement) {
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Object> treeNodeElement, MouseEvent mouseEvent) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Object> treeNodeElement, MouseEvent mouseEvent) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<Object> treeNodeElement) {
            }

            @Override
            public void onNodeSelected(TreeNodeElement<Object> treeNodeElement, SignalEvent signalEvent) {
                Object data = treeNodeElement.getData();

                boolean isLeaf = data instanceof RunnerEnvironmentLeaf;

                actionDelegate.onEnvironmentSelected(isLeaf ? ((RunnerEnvironmentLeaf)data).getEnvironment() : null);
            }

            @Override
            public void onRootContextMenu(int i, int i2) {
            }

            @Override
            public void onRootDragDrop(MouseEvent mouseEvent) {
            }

            @Override
            public void onKeyboard(KeyboardEvent keyboardEvent) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void addEnvironments(@Nonnull RunnerEnvironmentTree environmentTree) {
        rootNode.getNodes().clear();

        rootNode.getNodes().add(environmentTree);
        tree.renderTree(1);

        treeContainer.add(tree);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate actionDelegate) {
        this.actionDelegate = actionDelegate;
    }
}