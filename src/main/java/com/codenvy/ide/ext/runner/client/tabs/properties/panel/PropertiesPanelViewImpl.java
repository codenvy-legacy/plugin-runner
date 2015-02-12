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
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Boot;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Shutdown;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Andrey Plotnikov
 */
public class PropertiesPanelViewImpl extends Composite implements PropertiesPanelView {

    interface PropertiesPanelViewImplUiBinder extends UiBinder<Widget, PropertiesPanelViewImpl> {
    }

    private static final PropertiesPanelViewImplUiBinder UI_BINDER = GWT.create(PropertiesPanelViewImplUiBinder.class);

    @UiField
    TextBox           name;
    @UiField
    ListBox           ram;
    @UiField
    ListBox           scope;
    @UiField
    TextBox           type;
    @UiField
    ListBox           boot;
    @UiField
    ListBox           shutdown;
    @UiField
    Button            btnSave;
    @UiField
    Button            btnDelete;
    @UiField
    Button            btnCancel;
    @UiField
    SimpleLayoutPanel editorPanel;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;
    @UiField(provided = true)
    final RunnerResources            resources;

    private ActionDelegate delegate;

    @Inject
    public PropertiesPanelViewImpl(RunnerLocalizationConstant locale, RunnerResources resources) {
        this.locale = locale;
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        prepareField(ram, EnumSet.range(RAM._128, RAM._2048));
        prepareField(scope, EnumSet.allOf(Scope.class));
        prepareField(boot, EnumSet.allOf(Boot.class));
        prepareField(shutdown, EnumSet.allOf(Shutdown.class));
    }

    private void prepareField(@Nonnull ListBox field, @Nonnull Set<? extends Enum> items) {
        for (Enum item : items) {
            field.addItem(item.toString());
        }
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
    public RAM getMemorySize() {
        String value = ram.getValue(ram.getSelectedIndex());
        return RAM.detect(value);
    }

    /** {@inheritDoc} */
    @Override
    public void selectMemory(@Nonnull RAM size) {
        ram.setItemSelected(size.ordinal(), true);
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
    public void showEditor(@Nonnull EditorPartPresenter editor) {
        editor.go(editorPanel);
    }

    @UiHandler({"name", "type"})
    public void onTextInputted(@SuppressWarnings("UnusedParameters") KeyUpEvent event) {
        delegate.onConfigurationChanged();
    }

    @UiHandler({"ram", "scope", "boot", "shutdown"})
    public void handleChange(@SuppressWarnings("UnusedParameters") ChangeEvent event) {
        delegate.onConfigurationChanged();
    }

    @UiHandler("btnSave")
    public void onSaveButtonClicked(@SuppressWarnings("UnusedParameters") ClickEvent event) {
        delegate.onSaveButtonClicked();
    }

    @UiHandler("btnDelete")
    public void onDeleteButtonClicked(@SuppressWarnings("UnusedParameters") ClickEvent event) {
        delegate.onDeleteButtonClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelButtonClicked(@SuppressWarnings("UnusedParameters") ClickEvent event) {
        delegate.onCancelButtonClicked();
    }

}