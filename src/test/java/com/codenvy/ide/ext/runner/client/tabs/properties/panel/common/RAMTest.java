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

import org.junit.Test;

import java.util.EnumSet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class RAMTest {
    @Test
    public void valueShouldBeDetected() throws Exception {
        for (RAM size : EnumSet.range(RAM._128, RAM._8192)) {
            assertThat(size, is(RAM.detect(size.toString())));
        }
    }

    @Test
    public void shouldReturnRamBySizeRam() throws Exception {
        for (RAM size : EnumSet.range(RAM._128, RAM._8192)) {
            assertThat(RAM.detect(size.getValue()), is(size));
        }
    }

    @Test
    public void shouldBeReturnDefaultValueOfMemory1() throws Exception {
        assertThat(512, is(RAM.detect(1).getValue()));
    }

    @Test
    public void shouldBeReturnDefaultValueOfMemory2() throws Exception {
        assertThat(512, is(RAM.detect("text").getValue()));
    }
}