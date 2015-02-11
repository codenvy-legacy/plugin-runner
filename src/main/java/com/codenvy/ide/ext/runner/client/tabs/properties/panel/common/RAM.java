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
package com.codenvy.ide.ext.runner.client.tabs.properties.panel.common;

import javax.annotation.Nonnull;

/**
 * Enums which store information about memory size.
 *
 * @author Dmitry Shnurenko
 */
public enum RAM {
    _128(128), _256(256), _512(512), _1024(1024), _2048(2048), OTHER(-1);

    private final int size;

    RAM(int size) {
        this.size = size;
    }

    /** @return integer value of enum. */
    public int getValue() {
        return size;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String toString() {
        return size + "MB";
    }

    /**
     * Returns an instance of {@link RAM} using special memory string value.
     *
     * @param inputMemory
     *         value of string for which need return {@link RAM} enum
     * @return an instance {@link RAM}
     */
    @Nonnull
    public static RAM detect(@Nonnull String inputMemory) {
        for (RAM size : RAM.values()) {
            if (inputMemory.equals(size.toString())) {
                return size;
            }
        }

        return OTHER;
    }

}