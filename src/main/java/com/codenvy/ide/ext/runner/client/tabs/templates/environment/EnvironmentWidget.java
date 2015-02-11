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
package com.codenvy.ide.ext.runner.client.tabs.templates.environment;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.tabs.common.item.ItemWidget;
import com.codenvy.ide.ext.runner.client.tabs.common.item.RunnerItems;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * The class contains methods which allow change view representation of runner.
 *
 * @author Dmitry Shnurenko
 */
public class EnvironmentWidget implements RunnerItems<RunnerEnvironment> {

    private static final String DEFAULT_DESCRIPTION = "DEFAULT";

    private final ItemWidget      itemWidget;
    private final RunnerResources resources;

    private Scope             environmentScope;
    private RunnerEnvironment environment;

    @Inject
    public EnvironmentWidget(final ItemWidget itemWidget, RunnerResources resources, final SelectionManager selectionManager) {
        this.itemWidget = itemWidget;
        this.resources = resources;

        itemWidget.setDelegate(new ItemWidget.ActionDelegate() {
            @Override
            public void onWidgetClicked() {
                selectionManager.setEnvironment(environment);
            }
        });

        this.environmentScope = SYSTEM;
    }

    /**
     * Sets special environment scope.
     *
     * @param environmentScope
     *         scope which need set
     */
    public void setScope(@Nonnull Scope environmentScope) {
        this.environmentScope = environmentScope;
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        itemWidget.select();
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        itemWidget.unSelect();
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull RunnerEnvironment environment) {
        this.environment = environment;

        String id = environment.getId();

        int index = id.lastIndexOf('/') + 1;

        String name = id.substring(index);

        itemWidget.setName(name);

        String description = environment.getDescription();

        itemWidget.setDescription(description == null ? DEFAULT_DESCRIPTION : description);

        setImage();
    }

    private void setImage() {
        switch (environmentScope) {
            case PROJECT:
                itemWidget.setImage(resources.scopeProject());
                break;
            case SYSTEM:
                itemWidget.setImage(resources.scopeSystem());
                break;
            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return itemWidget.asWidget();
    }

}