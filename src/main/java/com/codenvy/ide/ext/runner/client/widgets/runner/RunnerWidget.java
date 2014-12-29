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
package com.codenvy.ide.ext.runner.client.widgets.runner;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * Provides methods which allow change visual representation of runner.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(RunnerWidgetImpl.class)
public interface RunnerWidget extends View<RunnerWidget.ActionDelegate> {

    /**
     * Updates runner view representation when runner state changed.
     *
     * @param runner
     *         runner which was changed
     */
    void update(@Nonnull Runner runner);

    interface ActionDelegate {
    }

}
