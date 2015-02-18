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
package com.codenvy.ide.ext.runner.client.tabs.history;

import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.tabs.history.runner.RunnerWidget;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class HistoryPresenterTest {
    @Mock
    private HistoryView      view;
    @Mock
    private WidgetFactory    widgetFactory;
    @Mock
    private SelectionManager selectionManager;

    @Mock
    private RunnerWidget     runnerWidget;
    @Mock
    private RunnerWidget     runnerWidget2;
    @Mock
    private Runner runner;
    @Mock
    private Runner runner2;

    @InjectMocks
    private HistoryPresenter historyPresenter;

    @Before
    public void setUp() {
        when(widgetFactory.createRunner()).thenReturn(runnerWidget).thenReturn(runnerWidget2);
    }

    @Test
    public void shouldAddRunner() {
        historyPresenter.addRunner(runner);

        verify(selectionManager).setRunner(runner);
        verify(widgetFactory).createRunner();
        verify(runnerWidget).update(runner);
        verify(view).addRunner(runnerWidget);

        verify(runnerWidget).unSelect();
        verify(runnerWidget).select();
    }

    @Test
    public void shouldUpdateRunnerIfAddedSomeRunner() {
        historyPresenter.addRunner(runner);
        reset(runnerWidget);
        historyPresenter.update(runner);

        verify(runnerWidget).update(runner);
    }

    @Test
    public void shouldUpdateRunnerIfAddedNoneRunner() {
        historyPresenter.update(runner);

        verify(runnerWidget, never()).update(runner);
    }

    @Test
    public void shouldOnSelectOneRunnerFromOneRunners() {
        historyPresenter.addRunner(runner);
        reset(runnerWidget);

        historyPresenter.selectRunner(runner);

        verify(runnerWidget).unSelect();
        verify(runnerWidget).select();
    }

    @Test
    public void shouldOnSelectOneRunnerFromTwoRunners() {
        historyPresenter.addRunner(runner);
        historyPresenter.addRunner(runner2);
        reset(runnerWidget);
        reset(runnerWidget2);

        historyPresenter.selectRunner(runner);

        verify(runnerWidget).unSelect();
        verify(runnerWidget2).unSelect();

        verify(runnerWidget).select();
    }

    @Test
    public void shouldGetView() {
        IsWidget v = view;
        assertThat(historyPresenter.getView(), is(v));
    }

    @Test
    public void shouldSetVisibleTrue() {
        historyPresenter.setVisible(true);
        verify(view).setVisible(true);
    }

    @Test
    public void shouldSetVisibleFalse() {
        historyPresenter.setVisible(false);
        verify(view).setVisible(false);
    }

    @Test
    public void shouldGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        historyPresenter.go(container);

        verify(container).setWidget(view);
    }

}