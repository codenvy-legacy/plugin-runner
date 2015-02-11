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
package com.codenvy.ide.ext.runner.client.tabs.templates;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.common.item.RunnerItems;
import com.codenvy.ide.ext.runner.client.tabs.templates.environment.EnvironmentWidget;
import com.codenvy.ide.ext.runner.client.tabs.templates.typebutton.TypeButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class provides graphical implementation of runner environments.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TemplatesViewImpl extends Composite implements TemplatesView {

    interface TemplatesViewImplUiBinder extends UiBinder<Widget, TemplatesViewImpl> {
    }

    private static final TemplatesViewImplUiBinder UI_BINDER = GWT.create(TemplatesViewImplUiBinder.class);

    @UiField
    FlowPanel environmentsPanel;

    //type buttons panels
    @UiField
    FlowPanel buttonsPanel;
    @UiField
    FlowPanel allButtonPanel;

    //scope
    @UiField
    Image systemScope;
    @UiField
    Image projectScope;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    private final Map<RunnerEnvironment, RunnerItems> environments;
    private final List<TypeButton>                    typeButtons;
    private final WidgetFactory                       widgetFactory;

    private ActionDelegate delegate;
    private Scope          environmentScope;
    private TypeButton     allButton;

    @Inject
    public TemplatesViewImpl(RunnerResources resources, RunnerLocalizationConstant locale, WidgetFactory widgetFactory) {
        this.resources = resources;
        this.locale = locale;
        this.widgetFactory = widgetFactory;

        initWidget(UI_BINDER.createAndBindUi(this));

        this.environments = new HashMap<>();
        this.typeButtons = new ArrayList<>();

        initializeActions();
    }

    private void initializeActions() {
        allButton = widgetFactory.createTypeButton();
        allButton.setDelegate(new TypeButton.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onAllTypeButtonClicked();

                allButton.select();
            }
        });
        allButton.setName(locale.templateTypeAll());
        allButton.select();

        typeButtons.add(allButton);
        allButtonPanel.add(allButton);

        systemScope.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onSystemScopeButtonClicked();

                selectScopeButton();
            }
        }, ClickEvent.getType());

        projectScope.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onProjectScopeButtonClicked();

                selectScopeButton();
            }
        }, ClickEvent.getType());
    }

    private void selectScopeButton() {
        allButton.select();

        switch (environmentScope) {
            case SYSTEM:
                systemScope.addStyleName(resources.runnerCss().opacityButton());
                projectScope.removeStyleName(resources.runnerCss().opacityButton());
                break;
            case PROJECT:
                projectScope.addStyleName(resources.runnerCss().opacityButton());
                systemScope.removeStyleName(resources.runnerCss().opacityButton());
                break;
            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addEnvironment(@Nonnull RunnerEnvironment environment, @Nonnull Scope environmentScope) {
        this.environmentScope = environmentScope;

        selectScopeButton();

        final EnvironmentWidget environmentWidget = widgetFactory.createEnvironment();

        environmentWidget.setScope(environmentScope);
        environmentWidget.update(environment);

        if (environments.isEmpty()) {
            environmentWidget.select();
        }

        environments.put(environment, environmentWidget);

        environmentsPanel.add(environmentWidget);
    }

    /** {@inheritDoc} */
    @Override
    public void selectEnvironment(@Nonnull RunnerEnvironment selectedEnvironment) {
        for (RunnerItems widget : environments.values()) {
            widget.unSelect();
        }

        environments.get(selectedEnvironment).select();
    }

    /** {@inheritDoc} */
    @Override
    public void addButton(@Nonnull final RunnerEnvironmentTree environmentTree) {
        final TypeButton typeButton = widgetFactory.createTypeButton();
        typeButton.setDelegate(new TypeButton.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onLangTypeButtonClicked(environmentTree);

                selectTypeButton(typeButton);
            }
        });
        typeButton.setName(environmentTree.getDisplayName());

        typeButtons.add(typeButton);
        buttonsPanel.add(typeButton);
    }

    private void selectTypeButton(@Nonnull TypeButton selectedButton) {
        for (TypeButton button : typeButtons) {
            button.unSelect();
        }

        selectedButton.select();
    }

    /** {@inheritDoc} */
    @Override
    public void clearEnvironmentsPanel() {
        environments.clear();
        environmentsPanel.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void clearTypeButtonsPanel() {
        buttonsPanel.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate actionDelegate) {
        this.delegate = actionDelegate;
    }
}