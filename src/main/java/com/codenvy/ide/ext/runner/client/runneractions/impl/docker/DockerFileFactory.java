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
package com.codenvy.ide.ext.runner.client.runneractions.impl.docker;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static com.codenvy.ide.ext.runner.client.runneractions.impl.docker.DockerFile.GET_CONTENT;

/**
 * The factory that provides an ability to create instances of {@link DockerFile}. The main idea of this class is to simplify work flow of
 * using  {@link DockerFile}.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
@Singleton
public class DockerFileFactory {

    public static final String NAME = "Runner Recipe";
    public static final String PATH = "runner_recipe";
    public static final String TYPE = "text/x-dockerfile-config";

    private final EventBus               eventBus;
    private final ProjectServiceClient   projectServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final DtoFactory             dtoFactory;
    private final AppContext             appContext;

    @Inject
    public DockerFileFactory(EventBus eventBus,
                             ProjectServiceClient projectServiceClient,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             DtoFactory dtoFactory,
                             AppContext appContext) {
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.appContext = appContext;
    }

    /**
     * Create a new instance of {@link DockerFile} for a given href.
     *
     * @param href
     *         URL where recipe file is located
     * @return an instance of {@link DockerFile}
     * @throws IllegalStateException
     *         when no project is opened
     */
    @Nonnull
    public FileNode newInstance(@Nonnull String href) {
        return newInstance(href, NAME, PATH);
    }

    @Nonnull
    public FileNode newInstance(@Nonnull String href, @Nonnull String name, @Nonnull String path) {
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            throw new IllegalStateException("No project is opened");
        }

        Link link = dtoFactory.createDto(Link.class)
                              .withHref(href)
                              .withRel(GET_CONTENT);
        List<Link> links = Arrays.asList(link);

        ItemReference recipeFileItem = dtoFactory.createDto(ItemReference.class)
                                                 .withName(name)
                                                 .withPath(path)
                                                 .withMediaType(TYPE)
                                                 .withLinks(links);

        return new DockerFile(eventBus, projectServiceClient, dtoUnmarshallerFactory, recipeFileItem, currentProject.getCurrentTree());
    }

}