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

import javax.annotation.Nonnull;

/**
 * @author Dmitry Shnurenko
 */
public enum EnvironmentType {
    JAVA("java"), CPP("cpp"), PHP("php"), GO("go"), JAVASCRIPT("javascript"), PYTHON("python"), RUBY("ruby");

    private final String type;

    EnvironmentType(@Nonnull String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
