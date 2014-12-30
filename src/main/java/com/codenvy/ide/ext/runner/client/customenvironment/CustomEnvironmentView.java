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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Provides methods which allow to control adding, editing and removing of custom environments.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
@ImplementedBy(CustomEnvironmentViewImpl.class)
public interface CustomEnvironmentView extends View<CustomEnvironmentView.ActionDelegate> {

    /**
     * Changes enabling of remove button.
     *
     * @param isEnabled
     *         <code>true</code> button is enable,<code>false</code> button is disable
     */
    void setRemoveButtonEnabled(boolean isEnabled);

    /**
     * Changes enabling of edit button.
     *
     * @param isEnabled
     *         <code>true</code> button is enable,<code>false</code> button is disable
     */
    void setEditButtonEnabled(boolean isEnabled);

    /**
     * Calls special method on {@link CellTable} which displays input data.Method gets values from {@link Array}
     * and put them to {@link ArrayList}.
     *
     * @param environments
     *         array with data which need display
     */
    void setRowData(@Nonnull Array<String> environments);

    /**
     * Selects needed custom environment.
     *
     * @param environment
     *         environment which need select
     */
    void selectEnvironment(@Nonnull String environment);

    /** Closes dialog window. */
    void closeDialog();

    /** Shows dialog window to add,edit and remove custom environments. */
    void showDialog();

    interface ActionDelegate {

        /**
         * Performs some actions in response to user's choosing an environment.
         *
         * @param customEnvironment
         *         environment that was chosen
         */
        void onEnvironmentSelected(@Nonnull String customEnvironment);

        /** Performs some actions in response to user's clicking on the 'Add' button. */
        void onAddBtnClicked();

        /** Performs some actions in response to user's clicking on the 'Remove' button. */
        void onRemoveBtnClicked();

        /** Performs some actions in response to user's clicking on the 'Edit' button. */
        void onEditBtnClicked();

        /** Performs some actions in response to user's clicking on the 'Close' button. */
        void onCloseBtnClicked();
    }

}