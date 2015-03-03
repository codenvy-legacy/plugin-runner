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

import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.texteditor.HasReadOnlyProperty;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.runner.client.models.Runner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 */
@RunWith(MockitoJUnitRunner.class)
public class ShowDockerActionTest {
    private static final String RECIPE_URL          = "dockerUrl";
    private static final String PATH_TO_DOCKER_FILE = "some path";

    //variables for constructor
    @Mock
    private AppContext        appContext;
    @Mock
    private DockerFileFactory dockerFileFactory;
    @Mock
    private EditorAgent       editorAgent;

    //editor variables
    @Mock
    private DockerFile                     recipeFile;
    @Mock
    private StringMap<EditorPartPresenter> openedEditors;
    @Mock
    private EditorPartPresenter            editor;
    @Mock
    private DummyEditor                    dummyEditor;
    //another variables
    @Mock
    private CurrentProject                 project;
    @Mock
    private Runner                         runner;

    @InjectMocks
    private ShowDockerAction showDockerAction;

    @Before
    public void setUp() {
        when(appContext.getCurrentProject()).thenReturn(project);
        when(runner.getDockerUrl()).thenReturn(RECIPE_URL);
        when(dockerFileFactory.newInstance(RECIPE_URL)).thenReturn(recipeFile);
        when(editorAgent.getOpenedEditors()).thenReturn(openedEditors);
        when(recipeFile.getPath()).thenReturn(PATH_TO_DOCKER_FILE);
        when(openedEditors.get(PATH_TO_DOCKER_FILE)).thenReturn(editor);
    }

    @Test
    public void shouldPerformWhenProjectIsNull() {
        when(appContext.getCurrentProject()).thenReturn(null);

        showDockerAction.perform(runner);

        verify(appContext).getCurrentProject();
        verifyZeroInteractions(project);
        verifyNoMoreInteractions(dockerFileFactory, editorAgent, runner);
    }

    @Test
    public void shouldPerformWhenRecipeUrlIsNull() {
        when(runner.getDockerUrl()).thenReturn(null);

        showDockerAction.perform(runner);

        verify(appContext).getCurrentProject();
        verify(runner).getDockerUrl();
        verifyZeroInteractions(runner, project);
        verifyNoMoreInteractions(dockerFileFactory, editorAgent);
    }

    @Test
    public void shouldPerformWhenEditorIsInstanceOfHasReadOnlyProperty() {
        showDockerAction.perform(runner);

        verify(appContext).getCurrentProject();
        verify(runner).getDockerUrl();
        verify(dockerFileFactory).newInstance(RECIPE_URL);
        verify(editorAgent).openEditor(recipeFile);
        verify(editorAgent).getOpenedEditors();
        verify(recipeFile).getPath();
        verify(openedEditors).get(PATH_TO_DOCKER_FILE);
        verifyNoMoreInteractions(editor);
    }

    @Test
    public void shouldPerformWhenEditorIsNotInstanceOfHasReadOnlyProperty() {
        when(openedEditors.get(PATH_TO_DOCKER_FILE)).thenReturn(dummyEditor);

        showDockerAction.perform(runner);

        verify(appContext).getCurrentProject();
        verify(runner).getDockerUrl();
        verify(dockerFileFactory).newInstance(RECIPE_URL);
        verify(editorAgent).openEditor(recipeFile);
        verify(editorAgent).getOpenedEditors();
        verify(recipeFile).getPath();
        verify(openedEditors).get(PATH_TO_DOCKER_FILE);

        verify((HasReadOnlyProperty)dummyEditor).setReadOnly(true);
    }

    private interface DummyEditor extends HasReadOnlyProperty, EditorPartPresenter {
    }
}