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

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Boot;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Shutdown;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The visual part of Properties panel that has an ability to show configuration of a runner.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
@ImplementedBy(PropertiesPanelViewImpl.class)
public interface PropertiesPanelView extends View<PropertiesPanelView.ActionDelegate> {

    /** @return content of Name field */
    @Nonnull
    String getName();

    /**
     * Changes content of Name field.
     *
     * @param name
     *         content that needs to be set
     */
    void setName(@Nonnull String name);

    /** @return chosen value of RAM field */
    @Nonnull
    RAM getRam();

    /**
     * Select a given value into RAM field.
     *
     * @param size
     *         value that needs to be chosen
     */
    void selectMemory(@Nonnull RAM size);

    /** @return chosen value of Scope field */
    @Nonnull
    Scope getScope();

    /**
     * Select a given scope into Scope field.
     *
     * @param scope
     *         value that needs to be chosen
     */
    void selectScope(@Nonnull Scope scope);

    /** @return content of Type field */
    @Nonnull
    String getType();

    /**
     * Changes content of Type field.
     *
     * @param type
     *         content that needs to be set
     */
    void setType(@Nonnull String type);

    /** @return chosen value of Boot field */
    @Nonnull
    Boot getBoot();

    /**
     * Select a given value into Boot field.
     *
     * @param boot
     *         value that needs to be chosen
     */
    void selectBoot(@Nonnull Boot boot);

    /** @return chosen value of Shutdown field */
    @Nonnull
    Shutdown getShutdown();

    /**
     * Select a given value into Shutdown field.
     *
     * @param shutdown
     *         value that needs to be chosen
     */
    void selectShutdown(@Nonnull Shutdown shutdown);

    /**
     * Changes enable state of 'Save' button.
     *
     * @param enable
     *         enable state of button
     */
    void setEnableSaveButton(boolean enable);

    /**
     * Changes enable state of 'Cancel' button.
     *
     * @param enable
     *         enable state of button
     */
    void setEnableCancelButton(boolean enable);

    /**
     * Changes enable state of 'Delete' button.
     *
     * @param enable
     *         enable state of button
     */
    void setEnableDeleteButton(boolean enable);

    /**
     * Show a given editor in the special place on the container.
     *
     * @param editor
     *         editor that needs to be shown
     */
    void showEditor(@Nullable EditorPartPresenter editor);

    interface ActionDelegate {

        /** Performs some actions in response to user's changing some configuration. */
        void onConfigurationChanged();

        /** Performs some actions in response to user's clicking on the 'Save' button. */
        void onSaveButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Delete' button. */
        void onDeleteButtonClicked();

        /** Performs some actions in response to user's clicking on the 'Cancel' button. */
        void onCancelButtonClicked();

    }

}