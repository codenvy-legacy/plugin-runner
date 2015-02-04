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
import com.codenvy.ide.ext.runner.client.widgets.general.GeneralWidget;
import com.codenvy.ide.ext.runner.client.widgets.general.RunnerItems;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * The class contains methods which allow change view representation of runner.
 *
 * @author Dmitry Shnurenko
 */
public class EnvironmentWidget implements RunnerItems<RunnerEnvironment> {

    private final GeneralWidget generalWidget;

    private ActionDelegate<RunnerEnvironment> actionDelegate;
    private RunnerEnvironment                 environment;

    @Inject
    public EnvironmentWidget(final GeneralWidget generalWidget) {
        this.generalWidget = generalWidget;

        generalWidget.setDelegate(new GeneralWidget.ActionDelegate() {
            @Override
            public void onWidgetClicked() {
                actionDelegate.onRunnerEnvironmentSelected(environment);
            }
        });
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
        generalWidget.setDescription(environment.getDescription());
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate<RunnerEnvironment> actionDelegate) {
        this.actionDelegate = actionDelegate;
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return generalWidget.asWidget();
    }

}