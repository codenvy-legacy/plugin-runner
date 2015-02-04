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
import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.ext.runner.client.runneractions.impl.GetEnvironmentsAction;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * The class contains business logic to change displaying of environments depending on scope or type.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class TemplatesPresenter implements Presenter, TemplatesWidget.ActionDelegate {

    private final TemplatesWidget       view;
    private final GetEnvironmentsAction environmentsAction;

    private ActionDelegate delegate;

    @Inject
    public TemplatesPresenter(TemplatesWidget view, GetEnvironmentsAction environmentsAction) {
        this.view = view;
        this.view.setDelegate(this);

        this.environmentsAction = environmentsAction;
    }

    public IsWidget getView() {
        return view;
    }

    /**
     * Sets action delegate to templates widget.
     *
     * @param delegate
     *         delegate which will be set
     */
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void onEnvironmentSelected(@Nonnull RunnerEnvironment environment) {
        delegate.onEnvironmentSelected(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void onAllTypeButtonClicked() {
        view.clear();

        environmentsAction.perform();
    }

    /** {@inheritDoc} */
    @Override
    public void onLangTypeButtonClicked(@Nonnull EnvironmentType environmentType) {
        view.clear();

        environmentsAction.getLanguageEnvironments(environmentType);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectScopeButtonClicked() {
        view.clear();

        environmentsAction.getProjectEnvironments();
    }

    /** {@inheritDoc} */
    @Override
    public void onSystemScopeButtonClicked() {
        view.clear();

        environmentsAction.getSystemEnvironments();
    }

    /** {@inheritDoc} */
    @Override
    public void go(@Nonnull AcceptsOneWidget container) {
        container.setWidget(view);
    }

    public interface ActionDelegate {
        /**
         * Performs some action when user selects environment.
         *
         * @param environment
         *         environment which is selected
         */
        void onEnvironmentSelected(@Nonnull RunnerEnvironment environment);
    }

}