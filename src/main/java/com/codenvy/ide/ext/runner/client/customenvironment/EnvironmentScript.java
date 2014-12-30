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
package com.codenvy.ide.ext.runner.client.customenvironment;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * The Class represents custom environment which is file node with special name which displayed on tab of docker.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public class EnvironmentScript extends FileNode {

    private final String environmentName;

    public EnvironmentScript(TreeNode<?> parent,
                             ItemReference data,
                             EventBus eventBus,
                             ProjectServiceClient projectServiceClient,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             String environmentName) {

        super(parent, data, eventBus, projectServiceClient, dtoUnmarshallerFactory);

        this.environmentName = environmentName;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getDisplayName() {
        return '[' + environmentName + "] " + data.getName();
    }

}