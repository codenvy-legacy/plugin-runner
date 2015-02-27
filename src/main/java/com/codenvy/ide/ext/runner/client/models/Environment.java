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
package com.codenvy.ide.ext.runner.client.models;

import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * The class contains all needed information about environment.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public interface Environment {

    /** @return name of current environment */
    @Nonnull
    String getName();

    /**
     * Sets name of current environment.
     *
     * @param name
     *         name which need set to environment
     */
    void setName(@Nonnull String name);

    /** @return description of current environment */
    @Nullable
    String getDescription();

    /** @return scope of current environment */
    @Nonnull
    Scope getScope();

    /**
     * Sets scope of current environment.
     *
     * @param scope
     *         scope which need set
     */
    void setScope(@Nonnull Scope scope);

    /** @return path to current environment */
    @Nonnull
    String getPath();

    /** @return value of ram for current environment */
    @Nonnegative
    int getRam();

    /**
     * Sets ram value for current environment.
     *
     * @param ram
     *         ram which need set
     */
    void setRam(@Nonnegative int ram);

    /** @return type of current environment */
    @Nonnull
    String getType();

    /**
     * Sets type to environment.
     *
     * @param type
     *         type which need set
     */
    void setType(@Nonnull String type);

    /** @return map which contains options for current environment */
    @Nonnull
    Map<String, String> getOptions();

}