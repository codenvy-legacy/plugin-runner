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
package com.codenvy.ide.ext.runner.client.widgets.templates;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.widgets.general.RunnerItems;
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
import java.util.List;

import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.CPP;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.GO;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.JAVA;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.JAVASCRIPT;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.PHP;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.PYTHON;
import static com.codenvy.ide.ext.runner.client.widgets.templates.EnvironmentType.RUBY;

/**
 * The Class provides graphical implementation of runner environments.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TemplatesWidgetImpl extends Composite implements TemplatesWidget, RunnerItems.ActionDelegate<RunnerEnvironment> {

    interface TemplatesViewImplUiBinder extends UiBinder<Widget, TemplatesWidgetImpl> {
    }

    private static final TemplatesViewImplUiBinder UI_BINDER = GWT.create(TemplatesViewImplUiBinder.class);

    @UiField
    FlowPanel environmentsPanel;

    //type
    @UiField
    FlowPanel allButton;
    @UiField
    FlowPanel java;
    @UiField
    FlowPanel cpp;
    @UiField
    FlowPanel go;
    @UiField
    FlowPanel javascript;
    @UiField
    FlowPanel php;
    @UiField
    FlowPanel python;
    @UiField
    FlowPanel ruby;

    //scope
    @UiField
    Image systemScope;
    @UiField
    Image projectScope;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;


    private final List<RunnerItems> environments;
    private final WidgetFactory     widgetFactory;

    private ActionDelegate delegate;

    @Inject
    public TemplatesWidgetImpl(RunnerResources resources, RunnerLocalizationConstant locale, WidgetFactory widgetFactory) {
        this.resources = resources;
        this.locale = locale;
        this.widgetFactory = widgetFactory;

        initWidget(UI_BINDER.createAndBindUi(this));

        this.environments = new ArrayList<>();

        initializeActions();
    }

    private void initializeActions() {
        allButton.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onAllTypeButtonClicked();
            }
        }, ClickEvent.getType());

        addTypeBtnHandler(java, JAVA);
        addTypeBtnHandler(go, GO);
        addTypeBtnHandler(cpp, CPP);
        addTypeBtnHandler(javascript, JAVASCRIPT);
        addTypeBtnHandler(php, PHP);
        addTypeBtnHandler(python, PYTHON);
        addTypeBtnHandler(ruby, RUBY);

        systemScope.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onSystemScopeButtonClicked();
            }
        }, ClickEvent.getType());

        projectScope.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onProjectScopeButtonClicked();
            }
        }, ClickEvent.getType());
    }

    private void addTypeBtnHandler(@Nonnull final FlowPanel panel, @Nonnull final EnvironmentType environmentType) {
        panel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onLangTypeButtonClicked(environmentType);
            }
        }, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void onRunnerEnvironmentSelected(@Nonnull RunnerEnvironment environment) {
        delegate.onEnvironmentSelected(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void addEnvironment(@Nonnull RunnerEnvironment environment) {
        final RunnerItems<RunnerEnvironment> environmentWidget = widgetFactory.createEnvironment();
        environmentWidget.setDelegate(new RunnerItems.ActionDelegate<RunnerEnvironment>() {
            @Override
            public void onRunnerEnvironmentSelected(@Nonnull RunnerEnvironment environment) {
                delegate.onEnvironmentSelected(environment);

                selectEnvironment(environmentWidget);
            }
        });

        environmentWidget.update(environment);
        environments.add(environmentWidget);

        environmentsPanel.add(environmentWidget);

        selectFirstEnvironment();
    }

    private void selectFirstEnvironment() {
        for (RunnerItems environment : environments) {
            environment.unSelect();
        }

        environments.get(0).select();
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        environments.clear();
        environmentsPanel.clear();
    }

    private void selectEnvironment(@Nonnull RunnerItems selectedWidget) {
        for (RunnerItems widget : environments) {
            widget.unSelect();
        }

        selectedWidget.select();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate actionDelegate) {
        this.delegate = actionDelegate;
    }
}