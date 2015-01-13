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

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.widgets.button.ButtonWidget;
import com.codenvy.ide.ext.runner.client.widgets.console.Console;
import com.codenvy.ide.ext.runner.client.widgets.runner.RunnerWidget;
import com.codenvy.ide.ext.runner.client.widgets.tab.TabWidget;
import com.codenvy.ide.ext.runner.client.widgets.terminal.Terminal;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nonnull;

/**
 * The factory for creating an instances of the widget.
 *
 * @author Dmitry Shnurenko
 */
public interface WidgetFactory {

    /**
     * Creates button widget with special icon.
     *
     * @param resource
     *         icon which need set to button
     * @return an instance of {@link ButtonWidget}
     */
    @Nonnull
    ButtonWidget createButton(@Nonnull ImageResource resource);

    /**
     * Creates tab widget with special title.
     *
     * @param title
     *         title which need set to widget's special place
     * @return an instance of {@link TabWidget}
     */
    @Nonnull
    TabWidget createTab(@Nonnull String title);

    /**
     * Creates runner widget.
     *
     * @return an instance of{@link RunnerWidget}
     */
    @Nonnull
    RunnerWidget createRunner();

    /**
     * Creates a console widget for a given runner.
     *
     * @param runner
     *         runner that needs to be bound with a widget
     * @return an instance of {@link Console}
     */
    @Nonnull
    Console createConsole(@Nonnull Runner runner);

    /**
     * Creates terminal widget.
     *
     * @return an instance of{@link Terminal}
     */
    @Nonnull
    Terminal createTerminal();

}