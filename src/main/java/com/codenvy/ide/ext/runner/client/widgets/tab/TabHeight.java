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
package com.codenvy.ide.ext.runner.client.widgets.tab;

import javax.annotation.Nonnull;

/**
 * The class contains values of tabs height
 *
 * @author Dmitry Shnurenko
 */
public enum TabHeight {
    LEFT_PANEL("19px"), RIGHT_PANEL("20px");

    private final String tabHeight;

    TabHeight(@Nonnull String tabHeight) {
        this.tabHeight = tabHeight;
    }

    /** @return string value of tab height */
    @Override
    public String toString() {
        return tabHeight;
    }
}
