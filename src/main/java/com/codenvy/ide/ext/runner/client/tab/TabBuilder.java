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
package com.codenvy.ide.ext.runner.client.tab;

import com.codenvy.ide.ext.runner.client.state.State;
import com.codenvy.ide.ext.runner.client.tab.Tab.VisibleState;
import com.codenvy.ide.ext.runner.client.tab.TabContainer.TabSelectHandler;
import com.codenvy.ide.ext.runner.client.widgets.tab.TabType;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import java.util.Set;

import static com.codenvy.ide.ext.runner.client.tab.Tab.VisibleState.REMOVABLE;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class TabBuilder {

    private String           title;
    private TabPresenter     presenter;
    private Set<State>       scopes;
    private TabSelectHandler handler;
    private TabType          tabType;
    private VisibleState     visibleState;

    @Inject
    public TabBuilder() {
        visibleState = REMOVABLE;
    }

    @Nonnull
    public TabBuilder title(@Nonnull String title) {
        this.title = title;
        return this;
    }

    @Nonnull
    public TabBuilder presenter(@Nonnull TabPresenter presenter) {
        this.presenter = presenter;
        return this;
    }

    @Nonnull
    public TabBuilder scope(@Nonnull Set<State> scopes) {
        this.scopes = scopes;
        return this;
    }

    @Nonnull
    public TabBuilder selectHandler(@Nonnull TabSelectHandler handler) {
        this.handler = handler;
        return this;
    }

    @Nonnull
    public TabBuilder type(@Nonnull TabType type) {
        tabType = type;
        return this;
    }

    @Nonnull
    public TabBuilder visible(@Nonnull VisibleState visibleState) {
        this.visibleState = visibleState;
        return this;
    }

    @Nonnull
    public Tab build() {
        if (title == null) {
            throw new IllegalStateException("You forgot to initialize 'Title' value. Please, initialize it and try again.");
        }

        if (scopes == null) {
            throw new IllegalStateException("You forgot to initialize 'Scopes' value. Please, initialize it and try again.");
        }

        if (presenter == null) {
            throw new IllegalStateException("You forgot to initialize 'Widget presenter' value. Please, initialize it and try again.");
        }

        if (tabType == null) {
            throw new IllegalStateException("You forgot to initialize 'Type' value. Please, initialize it and try again.");
        }

        return new Tab(title, presenter, scopes, handler, tabType, visibleState);
    }

}