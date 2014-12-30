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
package com.codenvy.ide.ext.runner.client.widgets.terminal;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.ImplementedBy;

import javax.annotation.Nullable;

/**
 * The widget that provides an ability to work like terminal. It contains methods for updating visual components.
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
@ImplementedBy(TerminalImpl.class)
public interface Terminal extends IsWidget {
    /**
     * Updates widget components from a given runner.
     *
     * @param runner
     *         runner where all parameters are located
     */
    void update(@Nullable Runner runner);
}