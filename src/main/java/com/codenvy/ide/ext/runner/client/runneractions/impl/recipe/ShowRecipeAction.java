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

import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.texteditor.HasReadOnlyProperty;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * Action for opening recipe for a runner.
 *
 * @author Andrey Plotnikov
 */
public class ShowRecipeAction extends AbstractRunnerAction {

    private final AppContext        appContext;
    private final RecipeFileFactory recipeFileFactory;
    private final EditorAgent       editorAgent;

    @Inject
    public ShowRecipeAction(AppContext appContext, RecipeFileFactory recipeFileFactory, EditorAgent editorAgent) {
        this.appContext = appContext;
        this.recipeFileFactory = recipeFileFactory;
        this.editorAgent = editorAgent;
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull Runner runner) {
        CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return;
        }

        String recipeUrl = runner.getRecipeUrl();
        if (recipeUrl == null) {
            return;
        }

        FileNode recipeFile = recipeFileFactory.newInstance(recipeUrl);
        editorAgent.openEditor(recipeFile);

        EditorPartPresenter editor = editorAgent.getOpenedEditors().get(recipeFile.getPath());
        if (editor instanceof HasReadOnlyProperty) {
            ((HasReadOnlyProperty)editor).setReadOnly(true);
        }
    }
}