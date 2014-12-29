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
package com.codenvy.ide.ext.runner.client.inject.factories;

import com.codenvy.ide.ext.runner.client.console.Console;
import com.codenvy.ide.ext.runner.client.models.Runner;

import javax.annotation.Nonnull;

/**
 * The factory for creating an instances of the console widget.
 *
 * @author Andrey Plotnikov
 */
public interface ConsoleFactory {
    /**
     * Creates a console widget for a given runner.
     *
     * @param runner
     *         runner that needs to be bound with a widget
     * @return an instance of {@link Console}
     */
    @Nonnull
    Console createConsole(@Nonnull Runner runner);
}