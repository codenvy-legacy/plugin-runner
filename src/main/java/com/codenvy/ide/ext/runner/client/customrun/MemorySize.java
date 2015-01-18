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
package com.codenvy.ide.ext.runner.client.customrun;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Enums which store information about memory size.
 *
 * @author Dmitry Shnurenko
 */
public enum MemorySize {
    MEMORY_128(128), MEMORY_256(256), MEMORY_512(512), MEMORY_1024(1024), MEMORY_2048(2048), OTHER_MEMORY(-1);

    private final int memorySize;

    MemorySize(@Nonnegative int memorySize) {
        this.memorySize = memorySize;
    }

    /**
     * Returns an instance of {@link MemorySize} using special memory string value.
     *
     * @param inputMemory
     *         value of string for which need return {@link MemorySize} enum
     * @return an instance {@link MemorySize}
     */
    @Nonnull
    public static MemorySize getItemByValue(@Nonnull String inputMemory) {
        for (MemorySize size : MemorySize.values()) {
            if (inputMemory.equals(size.toString())) {
                return size;
            }
        }

        return OTHER_MEMORY;
    }

    /** @return integer value of enum. */
    @Nonnegative
    public int getValue() {
        return memorySize;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String toString() {
        return memorySize + "MB";
    }

}