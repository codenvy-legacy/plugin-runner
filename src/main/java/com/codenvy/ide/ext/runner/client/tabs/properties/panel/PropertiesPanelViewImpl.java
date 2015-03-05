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
package com.codenvy.ide.ext.runner.client.tabs.properties.panel;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.tabs.container.tab.Background;
import com.codenvy.ide.ext.runner.client.tabs.properties.button.PropertyButtonWidget;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Boot;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Shutdown;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

import static com.codenvy.ide.ext.runner.client.tabs.container.tab.Background.BLUE;
import static com.codenvy.ide.ext.runner.client.tabs.container.tab.Background.GREY;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM.DEFAULT;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class PropertiesPanelViewImpl extends Composite implements PropertiesPanelView {

    interface PropertiesPanelViewImplUiBinder extends UiBinder<Widget, PropertiesPanelViewImpl> {
    }

    private static final PropertiesPanelViewImplUiBinder UI_BINDER = GWT.create(PropertiesPanelViewImplUiBinder.class);

    @UiField
    TextBox name;
    @UiField
    TextBox type;

    @UiField
    FlowPanel buttonsPanel;

    @UiField
    ListBox ram;
    @UiField
    ListBox scope;
    @UiField
    ListBox boot;
    @UiField
    ListBox shutdown;

    @UiField
    SimpleLayoutPanel editorPanel;

    @UiField(provided = true)
    final RunnerLocalizationConstant locale;
    @UiField(provided = true)
    final RunnerResources            resources;

    private final WidgetFactory widgetFactory;
    private final Label         unAvailableMessage;

    private ActionDelegate delegate;

    private PropertyButtonWidget saveBtn;
    private PropertyButtonWidget cancelBtn;
    private PropertyButtonWidget deleteBtn;

    @Inject
    public PropertiesPanelViewImpl(RunnerLocalizationConstant locale, RunnerResources resources, WidgetFactory widgetFactory) {
        this.locale = locale;
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        this.widgetFactory = widgetFactory;

        prepareField(ram, EnumSet.range(RAM._128, RAM._8192));
        prepareField(scope, EnumSet.allOf(Scope.class));
        prepareField(boot, EnumSet.allOf(Boot.class));
        prepareField(shutdown, EnumSet.allOf(Shutdown.class));

        unAvailableMessage = new Label(locale.editorNotReady());
        unAvailableMessage.addStyleName(resources.runnerCss().fullSize());
        unAvailableMessage.addStyleName(resources.runnerCss().unAvailableMessage());

        editorPanel.setWidget(unAvailableMessage);

        PropertyButtonWidget.ActionDelegate createDelegate = new PropertyButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onCopyButtonClicked();
            }
        };
        createButton(locale.propertiesButtonCreate(), createDelegate, BLUE);

        PropertyButtonWidget.ActionDelegate saveDelegate = new PropertyButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onSaveButtonClicked();
            }
        };
        saveBtn = createButton(locale.propertiesButtonSave(), saveDelegate, GREY);

        PropertyButtonWidget.ActionDelegate deleteDelegate = new PropertyButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onDeleteButtonClicked();
            }
        };
        deleteBtn = createButton(locale.propertiesButtonDelete(), deleteDelegate, GREY);

        PropertyButtonWidget.ActionDelegate cancelDelegate = new PropertyButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onCancelButtonClicked();
            }
        };
        cancelBtn = createButton(locale.propertiesButtonCancel(), cancelDelegate, GREY);
    }

    private void prepareField(@Nonnull ListBox field, @Nonnull Set<? extends Enum> items) {
        for (Enum item : items) {
            field.addItem(item.toString());
        }
    }

    @Nonnull
    private PropertyButtonWidget createButton(@Nonnull String title,
                                              @Nonnull PropertyButtonWidget.ActionDelegate delegate,
                                              @Nonnull Background background) {
        PropertyButtonWidget button = widgetFactory.createPropertyButton(title, background);
        button.setDelegate(delegate);

        buttonsPanel.add(button);

        return button;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getName() {
        return name.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(@Nonnull String name) {
        this.name.setText(name);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public RAM getRam() {
        String value = ram.getValue(ram.getSelectedIndex());
        return RAM.detect(value);
    }

    /** {@inheritDoc} */
    @Override
    public void selectMemory(@Nonnull RAM size) {
        if (DEFAULT.equals(size)) {
            selectDefaultMemory(Integer.toString(DEFAULT.getValue()));
        } else {
            ram.setItemSelected(size.ordinal(), true);
        }
    }

    private void selectDefaultMemory(@Nonnull String size) {
        for (int index = 0; index < ram.getItemCount(); index++) {
            if (size.equals(ram.getValue(index))) {
                ram.setItemSelected(index, true);
                return;
            }
        }
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Scope getScope() {
        String value = scope.getValue(scope.getSelectedIndex());
        return Scope.detect(value);
    }

    /** {@inheritDoc} */
    @Override
    public void selectScope(@Nonnull Scope scope) {
        this.scope.setItemSelected(scope.ordinal(), true);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getType() {
        return type.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setType(@Nonnull String type) {
        this.type.setText(type);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Boot getBoot() {
        String value = boot.getValue(boot.getSelectedIndex());
        return Boot.detect(value);
    }

    /** {@inheritDoc} */
    @Override
    public void selectBoot(@Nonnull Boot boot) {
        this.boot.setItemSelected(boot.ordinal(), true);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Shutdown getShutdown() {
        String value = shutdown.getValue(shutdown.getSelectedIndex());
        return Shutdown.detect(value);
    }

    /** {@inheritDoc} */
    @Override
    public void selectShutdown(@Nonnull Shutdown shutdown) {
        this.shutdown.setItemSelected(shutdown.ordinal(), true);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableSaveButton(boolean enable) {
        saveBtn.setEnable(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableCancelButton(boolean enable) {
        cancelBtn.setEnable(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableDeleteButton(boolean enable) {
        deleteBtn.setEnable(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableNameProperty(boolean enable) {
        name.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableRamProperty(boolean enable) {
        ram.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableBootProperty(boolean enable) {
        boot.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableShutdownProperty(boolean enable) {
        shutdown.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableScopeProperty(boolean enable) {
        scope.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void setVisibleButtons(boolean visible) {
        saveBtn.setVisible(visible);
        deleteBtn.setVisible(visible);
        cancelBtn.setVisible(visible);
    }

    /** {@inheritDoc} */
    @Override
    public void showEditor(@Nullable EditorPartPresenter editor) {
        if (editor == null) {
            editorPanel.setWidget(unAvailableMessage);
        } else {
            editor.go(editorPanel);
        }
    }

    @UiHandler("name")
    public void onTextInputted(@SuppressWarnings("UnusedParameters") KeyUpEvent event) {
        delegate.onConfigurationChanged();
    }

    @UiHandler({"ram", "scope", "boot", "shutdown"})
    public void handleChange(@SuppressWarnings("UnusedParameters") ChangeEvent event) {
        delegate.onConfigurationChanged();
    }

}