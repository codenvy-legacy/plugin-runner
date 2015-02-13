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
package com.codenvy.ide.ext.runner.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.runner.client.inject.factories.EnvironmentActionFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.HandlerFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.ModelsFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.manager.button.ButtonWidget;
import com.codenvy.ide.ext.runner.client.manager.button.ButtonWidgetImpl;
import com.codenvy.ide.ext.runner.client.manager.info.MoreInfo;
import com.codenvy.ide.ext.runner.client.manager.info.MoreInfoImpl;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.models.RunnerImpl;
import com.codenvy.ide.ext.runner.client.tabs.console.panel.Console;
import com.codenvy.ide.ext.runner.client.tabs.console.panel.ConsoleImpl;
import com.codenvy.ide.ext.runner.client.tabs.container.tab.TabWidget;
import com.codenvy.ide.ext.runner.client.tabs.container.tab.TabWidgetImpl;
import com.codenvy.ide.ext.runner.client.tabs.properties.button.PropertyButtonWidget;
import com.codenvy.ide.ext.runner.client.tabs.properties.button.PropertyButtonWidgetImpl;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.PropertiesPanel;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.PropertiesPanelPresenter;
import com.codenvy.ide.ext.runner.client.tabs.templates.scopebutton.ScopeButton;
import com.codenvy.ide.ext.runner.client.tabs.templates.scopebutton.ScopeButtonPresenter;
import com.codenvy.ide.ext.runner.client.tabs.templates.typebutton.TypeButton;
import com.codenvy.ide.ext.runner.client.tabs.templates.typebutton.TypeButtonImpl;
import com.codenvy.ide.ext.runner.client.tabs.terminal.panel.Terminal;
import com.codenvy.ide.ext.runner.client.tabs.terminal.panel.TerminalImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * The module that contains configuration of the client side part of the plugin.
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
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
                                             .implement(ButtonWidget.class, ButtonWidgetImpl.class)
                                             .implement(TabWidget.class, TabWidgetImpl.class)
                                             .implement(PropertiesPanel.class, PropertiesPanelPresenter.class)
                                             .implement(MoreInfo.class, MoreInfoImpl.class)
                                             .implement(TypeButton.class, TypeButtonImpl.class)
                                             .implement(ScopeButton.class, ScopeButtonPresenter.class)
                                             .implement(PropertyButtonWidget.class, PropertyButtonWidgetImpl.class)
                                             .build(WidgetFactory.class));
    }

    /** Provides project-relative path to the folder for project-scoped runner environments. */
    @Provides
    @Named("envFolderPath")
    @Singleton
    protected String provideEnvironmentsFolderRelPath() {
        return ".codenvy/runners/environments";
    }
}