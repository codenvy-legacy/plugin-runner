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
package com.codenvy.ide.ext.runner.client.widgets.tooltip;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Date;

import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.MEMORY_128;
import static com.codenvy.ide.ext.runner.client.widgets.tooltip.MoreInfoImpl.DATE_TIME_FORMAT;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Plotnikov
 */
@RunWith(GwtMockitoTestRunner.class)
public class MoreInfoImplTest {

    private static final String SOME_TEXT = "some text";

    @Mock
    private Runner       runner;
    @InjectMocks
    private MoreInfoImpl widget;

    @Test
    public void contentShouldBeUpdated() throws Exception {
        Date currentDate = new Date();
        String expectedFormat = DATE_TIME_FORMAT.format(currentDate);

        when(runner.getCreationTime()).thenReturn(currentDate.getTime());
        when(runner.getStopTime()).thenReturn(SOME_TEXT);
        when(runner.getTimeout()).thenReturn(SOME_TEXT);
        when(runner.getActiveTime()).thenReturn(SOME_TEXT);
        when(runner.getRAM()).thenReturn(MEMORY_128.getValue());

        widget.update(runner);

        verify(widget.started).setText(expectedFormat);
        verify(widget.finished).setText(SOME_TEXT);
        verify(widget.timeout).setText(SOME_TEXT);
        verify(widget.activeTime).setText(SOME_TEXT);
        verify(widget.ram).setText(MEMORY_128.toString());
    }
}