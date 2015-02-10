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
package com.codenvy.ide.ext.runner.client.widgets.templates.environment;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.properties.common.Scope;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.widgets.general.GeneralWidget;
import com.codenvy.ide.ext.runner.client.widgets.general.RunnerItems;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ext.runner.client.properties.common.Scope.SYSTEM;

/**
 * The class contains methods which allow change view representation of runner.
 *
 * @author Dmitry Shnurenko
 */
public class EnvironmentWidget implements RunnerItems<RunnerEnvironment> {

    private static final String DEFAULT_DESCRIPTION = "DEFAULT";

    private final GeneralWidget   generalWidget;
    private final RunnerResources resources;

    private Scope             environmentScope;
    private RunnerEnvironment environment;

    @Inject
    public EnvironmentWidget(final GeneralWidget generalWidget, RunnerResources resources, final SelectionManager selectionManager) {
        this.generalWidget = generalWidget;
        this.resources = resources;

        generalWidget.setDelegate(new GeneralWidget.ActionDelegate() {
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
        generalWidget.select();
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        generalWidget.unSelect();
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull RunnerEnvironment environment) {
        this.environment = environment;

        String id = environment.getId();

        int index = id.lastIndexOf('/') + 1;

        String name = id.substring(index);

        generalWidget.setName(name);

        String description = environment.getDescription();

        generalWidget.setDescription(description == null ? DEFAULT_DESCRIPTION : description);

        setImage();
    }

    private void setImage() {
        switch (environmentScope) {
            case PROJECT:
                generalWidget.setImage(resources.scopeProject());
                break;
            case SYSTEM:
                generalWidget.setImage(resources.scopeSystem());
                break;
            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return generalWidget.asWidget();
    }

}