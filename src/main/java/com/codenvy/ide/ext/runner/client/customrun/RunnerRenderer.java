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

import elemental.dom.Element;
import elemental.html.SpanElement;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

/**
 * The class allows renders tree of custom runners. The business logic divides all nodes into leaves and trees and apply
 * suitable css styles.
 *
 * @author Evgen Vidolob
 * @author Dmitry Shnurenko
 */
@Singleton
public class RunnerRenderer implements NodeRenderer<Object> {

    private final RunnerResources resources;

    @Inject
    public RunnerRenderer(RunnerResources resources) {
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public Element getNodeKeyTextContainer(@Nonnull SpanElement spanElement) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SpanElement renderNodeContents(@Nonnull Object node) {
        SpanElement rootElement = Elements.createSpanElement();

        if (node instanceof RunnerEnvironmentTree) {
            rootElement.setInnerHTML(((RunnerEnvironmentTree)node).getDisplayName());
        } else if (node instanceof RunnerEnvironmentLeaf) {
            SVGResource environment = resources.environmentImage();
            SVGImage image = new SVGImage(environment);

            image.getElement().setAttribute("class", resources.runnerCss().treeIcon());

            rootElement.appendChild((elemental.dom.Node)image.getElement());
            rootElement.setInnerHTML(rootElement.getInnerHTML() + "&nbsp;" +
                                     ((RunnerEnvironmentLeaf)node).getDisplayName());
        }

        return rootElement;
    }

    /** {@inheritDoc} */
    @Override
    public void updateNodeContents(@Nonnull TreeNodeElement<Object> treeNode) {
    }
}
