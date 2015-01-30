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
package com.codenvy.ide.ext.runner.client.widgets.console;

import org.junit.Test;

import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.DOCKER;
import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.ERROR;
import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.INFO;
import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.STDERR;
import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.STDOUT;
import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.WARNING;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Andrey Plotnikov
 */
public class MessageTypeTest {

    private static final String SOME_TEXT = "some text";

    @Test
    public void infoMessageShouldBeDetected() throws Exception {
        String content = INFO.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), is(INFO));
    }

    @Test
    public void warningMessageShouldBeDetected() throws Exception {
        String content = WARNING.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), is(WARNING));
    }

    @Test
    public void errorMessageShouldBeDetected() throws Exception {
        String content = ERROR.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), is(ERROR));
    }

    @Test
    public void dockerMessageShouldBeDetected() throws Exception {
        String content = DOCKER.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), is(DOCKER));
    }

    @Test
    public void stdoutMessageShouldBeDetected() throws Exception {
        String content = STDOUT.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), is(STDOUT));
    }

    @Test
    public void stderrMessageShouldBeDetected() throws Exception {
        String content = STDERR.getPrefix() + SOME_TEXT;
        assertThat(MessageType.detect(content), is(STDERR));
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionShouldBeThrownWhenIncorrectValueIsInputted() throws Exception {
        MessageType.detect(SOME_TEXT);
    }

}