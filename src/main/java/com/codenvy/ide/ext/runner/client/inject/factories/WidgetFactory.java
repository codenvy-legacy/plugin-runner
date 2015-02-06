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
package com.codenvy.ide.ext.runner.client.inject.factories;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.widgets.button.ButtonWidget;
import com.codenvy.ide.ext.runner.client.widgets.console.Console;
import com.codenvy.ide.ext.runner.client.widgets.console.FullLogMessageWidget;
import com.codenvy.ide.ext.runner.client.widgets.history.runner.RunnerWidget;
import com.codenvy.ide.ext.runner.client.widgets.tab.Tab;
import com.codenvy.ide.ext.runner.client.widgets.tab.TabWidget;
import com.codenvy.ide.ext.runner.client.widgets.templates.environment.EnvironmentWidget;
import com.codenvy.ide.ext.runner.client.widgets.terminal.Terminal;
import com.codenvy.ide.ext.runner.client.widgets.tooltip.MoreInfo;
import com.codenvy.ide.ext.runner.client.widgets.templates.typebutton.TypeButton;
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
     * @param tab
     *         enum which contains string value of height
     * @return an instance of {@link TabWidget}
     */
    @Nonnull
    TabWidget createTab(@Nonnull String title, @Nonnull Tab tab);

    /**
     * Creates runner widget.
     *
     * @return an instance of {@link RunnerWidget}
     */
    @Nonnull
    RunnerWidget createRunner();

    /**
     * Creates environment widget.
     *
     * @return an instance of {@link EnvironmentWidget}
     */
    @Nonnull
    EnvironmentWidget createEnvironment();

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
     * @return an instance of {@link Terminal}
     */
    @Nonnull
    Terminal createTerminal();

    /**
     * Creates more info popup widget.
     *
     * @return an instance of {@link MoreInfo}
     */
    @Nonnull
    MoreInfo createMoreInfo();

    /**
     * Creates message widget that need to be displayed in the console.
     *
     * @param logUrl
     *         url where full log is located
     * @return an instance of {@link FullLogMessageWidget}
     */
    @Nonnull
    FullLogMessageWidget createFullLogMessage(@Nonnull String logUrl);

    /**
     * Creates type button widget.
     *
     * @return an instance of {@link TypeButton}
     */
    @Nonnull
    TypeButton createTypeButton();

}