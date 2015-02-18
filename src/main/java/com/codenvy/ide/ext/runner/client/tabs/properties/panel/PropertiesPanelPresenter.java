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
package com.codenvy.ide.ext.runner.client.tabs.properties.panel;

import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PropertyListener;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.api.texteditor.UndoableEditor;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.impl.docker.DockerFileFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

import static com.codenvy.ide.api.editor.EditorPartPresenter.PROP_DIRTY;
import static com.codenvy.ide.api.editor.EditorPartPresenter.PROP_INPUT;
import static com.codenvy.ide.ext.runner.client.constants.TimeInterval.ONE_SEC;

/**
 * The class that manages Properties panel widget.
 *
 * @author Andrey Plotnikov
 */
public class PropertiesPanelPresenter implements PropertiesPanelView.ActionDelegate, PropertiesPanel {

    private final PropertiesPanelView view;
    private final Timer               timer;

    private EditorPartPresenter editor;
    private int                 undoOperations;

    @Inject
    public PropertiesPanelPresenter(final PropertiesPanelView view,
                                    final EditorRegistry editorRegistry,
                                    final FileTypeRegistry fileTypeRegistry,
                                    final DockerFileFactory dockerFileFactory,
                                    @Assisted @Nonnull final Runner runner) {
        this.view = view;
        this.view.setDelegate(this);

        // we're waiting for getting application descriptor from server. so we can't show editor without knowing about configuration file.
        timer = new Timer() {
            @Override
            public void run() {
                String dockerUrl = runner.getDockerUrl();
                if (dockerUrl == null) {
                    timer.schedule(ONE_SEC.getValue());
                    return;
                }

                timer.cancel();
                initializeEditor(dockerUrl, editorRegistry, fileTypeRegistry, dockerFileFactory);
            }
        };
        timer.schedule(ONE_SEC.getValue());
    }

    private void initializeEditor(@Nonnull String dockerUrl,
                                  @Nonnull EditorRegistry editorRegistry,
                                  @Nonnull FileTypeRegistry fileTypeRegistry,
                                  @Nonnull DockerFileFactory dockerFileFactory) {
        FileNode file = dockerFileFactory.newInstance(dockerUrl);
        FileType fileType = fileTypeRegistry.getFileTypeByFile(file);
        editor = editorRegistry.getEditor(fileType).getEditor();

        // wait when editor is initialized
        editor.addPropertyListener(new PropertyListener() {
            @Override
            public void propertyChanged(PartPresenter source, int propId) {
                switch (propId) {
                    case PROP_INPUT:
                        view.showEditor(editor);
                        break;

                    case PROP_DIRTY:
                        if (validateUndoOperation()) {
                            view.setEnableSaveButton(true);
                            view.setEnableCancelButton(true);
                        }
                        break;

                    default:
                }
            }
        });

        try {
            editor.init(new DockerFileEditorInput(fileType, file));
        } catch (EditorInitException e) {
            Log.error(getClass(), e);
        }
    }

    private boolean validateUndoOperation() {
        // this code needs for right behaviour when someone is clicking on 'Cancel' button. We need to make disable some buttons.
        if (undoOperations == 0) {
            return true;
        }

        undoOperations--;
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationChanged() {

    }

    /** {@inheritDoc} */
    @Override
    public void onSaveButtonClicked() {
        view.setEnableSaveButton(false);
        view.setEnableCancelButton(false);

        editor.doSave();
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteButtonClicked() {

    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        view.setEnableSaveButton(false);
        view.setEnableCancelButton(false);

        if (editor instanceof UndoableEditor) {
            HandlesUndoRedo undoRedo = ((UndoableEditor)editor).getUndoRedo();
            while (editor.isDirty() && undoRedo.undoable()) {
                undoOperations++;
                undoRedo.undo();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        if (editor == null) {
            return;
        }

        editor.activate();
        editor.onOpen();
    }

}