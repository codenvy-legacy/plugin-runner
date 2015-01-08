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
package com.codenvy.ide.ext.runner.client.runneractions.impl.recipe;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static com.codenvy.ide.ext.runner.client.runneractions.impl.recipe.RecipeFile.GET_CONTENT;

/**
 * The factory that provides an ability to create instances of {@link RecipeFile}. The main idea of this class is to simplify work flow of
 * using  {@link RecipeFile}.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class RecipeFileFactory {

    private final EventBus               eventBus;
    private final ProjectServiceClient   projectServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final DtoFactory             dtoFactory;

    @Inject
    public RecipeFileFactory(EventBus eventBus,
                             ProjectServiceClient projectServiceClient,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             DtoFactory dtoFactory) {
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
    }

    /**
     * Create a new instance of {@link RecipeFile} for a given href.
     *
     * @param href
     *         URL where recipe file is located
     * @return an instance of {@link RecipeFile}
     */
    @Nonnull
    public FileNode newInstance(@Nonnull String href) {
        Link link = dtoFactory.createDto(Link.class)
                              .withHref(href)
                              .withRel(GET_CONTENT);
        List<Link> links = Arrays.asList(link);

        ItemReference recipeFileItem = dtoFactory.createDto(ItemReference.class)
                                                 .withName("Runner Recipe")
                                                 .withPath("runner_recipe")
                                                 .withMediaType("text/x-dockerfile-config")
                                                 .withLinks(links);

        return new RecipeFile(eventBus, projectServiceClient, dtoUnmarshallerFactory, recipeFileItem);
    }
}