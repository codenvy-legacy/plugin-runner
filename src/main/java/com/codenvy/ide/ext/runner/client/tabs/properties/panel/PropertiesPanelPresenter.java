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
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.impl.docker.DockerFileFactory;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.codenvy.ide.api.editor.EditorPartPresenter.PROP_INPUT;

/**
 * The class that manages Properties panel widget.
 *
 * @author Andrey Plotnikov
 */
public class PropertiesPanelPresenter implements PropertiesPanelView.ActionDelegate, PropertiesPanel {

    private final PropertiesPanelView view;

    @Inject
    public PropertiesPanelPresenter(final PropertiesPanelView view,
                                    EditorRegistry editorRegistry,
                                    FileTypeRegistry fileTypeRegistry,
                                    DockerFileFactory dockerFileFactory,
                                    @Assisted Runner runner) throws EditorInitException {
        this.view = view;
        this.view.setDelegate(this);

        String dockerUrl = runner.getDockerUrl();
        if (dockerUrl == null) {
            return;
        }

        FileNode file = dockerFileFactory.newInstance(dockerUrl);
        FileType fileType = fileTypeRegistry.getFileTypeByFile(file);
        final EditorPartPresenter editor = editorRegistry.getEditor(fileType).getEditor();
        editor.addPropertyListener(new PropertyListener() {
            @Override
            public void propertyChanged(PartPresenter source, int propId) {
                if (propId == PROP_INPUT) {
                    view.showEditor(editor);
                }
            }
        });

        editor.init(new DockerFileEditorInput(fileType, file));
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationChanged() {

    }

    /** {@inheritDoc} */
    @Override
    public void onSaveButtonClicked() {

    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteButtonClicked() {

    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {

    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

}