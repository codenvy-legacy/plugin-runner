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
package com.codenvy.ide.ext.runner.client.properties.common;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Andrey Plotnikov
 */
public class ShutdownTest {

    @Test
    public void contentShouldBeDetected() throws Exception {
        for (Shutdown shutdown : Shutdown.values()) {
            assertThat(shutdown, is(Shutdown.detect(shutdown.toString())));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exceptionShouldBeThrownWhenContentIsIncorrect() throws Exception {
        Shutdown.detect("some content");
    }

}