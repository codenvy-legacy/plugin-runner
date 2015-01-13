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
package com.codenvy.ide.ext.runner.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.runner.client.inject.factories.EnvironmentActionFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.HandlerFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.ModelsFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.models.RunnerImpl;
import com.codenvy.ide.ext.runner.client.widgets.button.ButtonWidget;
import com.codenvy.ide.ext.runner.client.widgets.button.ButtonWidgetImpl;
import com.codenvy.ide.ext.runner.client.widgets.console.Console;
import com.codenvy.ide.ext.runner.client.widgets.console.ConsoleImpl;
import com.codenvy.ide.ext.runner.client.widgets.runner.RunnerWidget;
import com.codenvy.ide.ext.runner.client.widgets.runner.RunnerWidgetImpl;
import com.codenvy.ide.ext.runner.client.widgets.tab.TabWidget;
import com.codenvy.ide.ext.runner.client.widgets.tab.TabWidgetImpl;
import com.codenvy.ide.ext.runner.client.widgets.terminal.Terminal;
import com.codenvy.ide.ext.runner.client.widgets.terminal.TerminalImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

/**
 * The module that contains configuration of the client side part of the plugin.
 *
 * @author Andrey Plotnikov
 */
@ExtensionGinModule
public class RunnerGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().implement(Runner.class, RunnerImpl.class)
                                             .build(ModelsFactory.class));

        install(new GinFactoryModuleBuilder().build(HandlerFactory.class));

        install(new GinFactoryModuleBuilder().build(EnvironmentActionFactory.class));

        install(new GinFactoryModuleBuilder().build(RunnerActionFactory.class));

        install(new GinFactoryModuleBuilder().implement(Terminal.class, TerminalImpl.class)
                                             .implement(Console.class, ConsoleImpl.class)
                                             .implement(RunnerWidget.class, RunnerWidgetImpl.class)
                                             .implement(ButtonWidget.class, ButtonWidgetImpl.class)
                                             .implement(TabWidget.class, TabWidgetImpl.class)
                                             .build(WidgetFactory.class));
    }
}