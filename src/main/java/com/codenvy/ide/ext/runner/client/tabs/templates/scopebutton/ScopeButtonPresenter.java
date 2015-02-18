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
package com.codenvy.ide.ext.runner.client.tabs.templates.scopebutton;

import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;

/**
 * Contains business logic of button's click processing, and general methods for current button.
 *
 * @author Dmitry Shnurenko
 */
public class ScopeButtonPresenter implements ScopeButton, ScopeButtonView.ActionDelegate {

    private final ScopeButtonView view;
    private final Scope           buttonScope;

    private ActionDelegate delegate;
    private boolean        isUnChecked;

    @Inject
    public ScopeButtonPresenter(ScopeButtonView view,
                                @Assisted Scope buttonScope,
                                @Assisted ImageResource image,
                                @Assisted boolean isUnChecked) {
        this.view = view;
        this.view.setDelegate(this);
        this.view.setImage(image);

        this.buttonScope = buttonScope;
        this.isUnChecked = isUnChecked;

        if (SYSTEM.equals(buttonScope)) {
            view.select();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onButtonClicked() {
        if (isUnChecked) {
            delegate.onButtonChecked(buttonScope);

            view.select();

            isUnChecked = false;
        } else {
            delegate.onButtonUnchecked(buttonScope);

            view.unSelect();

            isUnChecked = true;
        }
    }

    /** {@inheritDoc} */
    @Override
    public ScopeButtonView getView() {
        return view;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

}