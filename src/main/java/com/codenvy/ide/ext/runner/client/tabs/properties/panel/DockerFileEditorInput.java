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

import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.projecttree.VirtualFile;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

/**
 * This class is copy of com.codenvy.ide.core.editor.EditorInputImpl.
 *
 * @author Vitaly Parfonov
 */
public class DockerFileEditorInput implements EditorInput {

    private VirtualFile file;
    private FileType    fileType;

    DockerFileEditorInput(FileType fileType, VirtualFile file) {
        this.fileType = fileType;
        this.file = file;
    }

    /** {@inheritDoc} */
    @Override
    public String getContentDescription() {
        return fileType.getContentDescription();
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getToolTipText() {
        return "";
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getName() {
        return file.getDisplayName();
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public ImageResource getImageResource() {
        return fileType.getImage();
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public SVGResource getSVGResource() {
        return fileType.getSVGImage();
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public VirtualFile getFile() {
        return file;
    }

    /** {@inheritDoc} */
    @Override
    public void setFile(@Nonnull VirtualFile file) {
        this.file = file;
    }

}