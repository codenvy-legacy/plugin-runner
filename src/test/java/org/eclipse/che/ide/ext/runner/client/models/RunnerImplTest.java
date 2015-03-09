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
package org.eclipse.che.ide.ext.runner.client.models;

import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.runner.dto.ApplicationProcessDescriptor;
import org.eclipse.che.api.runner.dto.RunOptions;
import org.eclipse.che.api.runner.dto.RunnerMetric;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.eclipse.che.ide.ext.runner.client.constants.TimeInterval;
import org.eclipse.che.ide.ext.runner.client.manager.RunnerManagerPresenter;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.eclipse.che.api.runner.internal.Constants.LINK_REL_SHELL_URL;
import static org.eclipse.che.api.runner.internal.Constants.LINK_REL_VIEW_LOG;
import static org.eclipse.che.api.runner.internal.Constants.LINK_REL_RUNNER_RECIPE;
import static org.eclipse.che.api.runner.internal.Constants.LINK_REL_STOP;
import static org.eclipse.che.api.runner.internal.Constants.LINK_REL_WEB_URL;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.eclipse.che.api.runner.ApplicationStatus.NEW;
import static org.eclipse.che.api.runner.dto.RunnerMetric.ALWAYS_ON;
import static org.eclipse.che.api.runner.dto.RunnerMetric.LIFETIME;
import static org.eclipse.che.api.runner.dto.RunnerMetric.TERMINATION_TIME;
import static org.eclipse.che.api.runner.dto.RunnerMetric.STOP_TIME;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class RunnerImplTest {

    private static final String RUNNER_NAME   = "Runner ";
    private static final String URL           = "http://runnner1.codenvy.com";
    private static final String TEXT          = "some text/runner/tomcat";
    private static final int    RUNNER_NUMBER = 1;
    private static final long   LONG_CONSTANT = 1234567L;

    private List<RunnerMetric> metricList;
    private List<Link>         links;

    //variables for constructor
    @Mock
    private RunnerLocalizationConstant   locale;
    @Mock
    private RunnerCounter                runnerCounter;
    @Mock
    private RunOptions                   runOptions;
    @Mock
    private ApplicationProcessDescriptor descriptor;

    @Mock
    private RunnerMetric stat;
    @Mock
    private Link         link;
    @Mock
    private Link         link1;

    private RunnerImpl runner;

    @Before
    public void setUp() {
        initConstructorParameter();

        runner = new RunnerImpl(locale, runnerCounter, runOptions);

        //init application descriptor
        when(descriptor.getProcessId()).thenReturn(LONG_CONSTANT);
        when(descriptor.getCreationTime()).thenReturn(LONG_CONSTANT);
        when(runOptions.getEnvironmentId()).thenReturn(TEXT);

        metricList = Arrays.asList(stat);
        links = new ArrayList<>();
        links.add(link);

        when(link.getRel()).thenReturn(LINK_REL_WEB_URL);
        when(link.getHref()).thenReturn(URL);
    }

    private void initConstructorParameter() {
        when(runOptions.getMemorySize()).thenReturn(RAM._2048.getValue());
        when(runnerCounter.getRunnerNumber()).thenReturn(RUNNER_NUMBER);
        when(locale.runnerTabConsole()).thenReturn(TEXT);
    }

    @Test
    public void shouldVerifyFirstConstructor() {
        verifySomeActionInConstructor();
        assertThat(runner.getTitle(), is(RUNNER_NAME + RUNNER_NUMBER));
    }

    private void verifySomeActionInConstructor() {
        verify(runOptions).getMemorySize();
        verify(runnerCounter).getRunnerNumber();
        verify(locale).runnerTabConsole();

        assertThat(runner.getActiveTab(), is(TEXT));
        assertThat(runner.getRAM(), Is.is(RAM._2048.getValue()));
        assertThat(runner.getStatus(), Is.is(Runner.Status.IN_QUEUE));
    }

    @Test
    public void shouldVerifySecondConstructorWithEnvironmentNameNotNull() {
        reset(locale, runnerCounter, runOptions);
        initConstructorParameter();

        runner = new RunnerImpl(locale, runnerCounter, runOptions, TEXT);

        verifySomeActionInConstructor();
        assertThat(runner.getTitle(), is(RUNNER_NAME + RUNNER_NUMBER + " - tomcat"));
    }

    @Test
    public void shouldVerifySecondConstructorWithEnvironmentNameNull() {
        reset(locale, runnerCounter, runOptions);
        initConstructorParameter();

        runner = new RunnerImpl(locale, runnerCounter, runOptions, null);

        shouldVerifyFirstConstructor();
    }

    @Test
    public void applicationDescriptorShouldBeChanged() {
        assertThat(runner.getDescriptor(), nullValue());

        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getDescriptor(), is(descriptor));
    }

    @Test
    public void activeTabShouldBeChanged() {
        assertThat(runner.getActiveTab(), is(TEXT));

        runner.setActiveTab(RUNNER_NAME);

        assertThat(runner.getActiveTab(), is(RUNNER_NAME));
    }

    @Test
    public void ramShouldBeChanged() {
        assertThat(runner.getRAM(), Is.is(RAM._2048.getValue()));

        runner.setRAM(RAM._1024.getValue());

        assertThat(runner.getRAM(), Is.is(RAM._1024.getValue()));
    }

    @Test
    public void creationTimeShouldBeReturned() {
        runner.setStatus(Runner.Status.IN_PROGRESS);
        runner.setProcessDescriptor(descriptor);
        runner.resetCreationTime();

        assertThat(runner.getCreationTime(), is(LONG_CONSTANT));
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsFailed() {
        runner.setStatus(Runner.Status.FAILED);

        runner.resetCreationTime();

        Assert.assertEquals(runner.getCreationTime(), System.currentTimeMillis(), TimeInterval.FIVE_SEC.getValue());
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsStopped() {
        runner.setStatus(Runner.Status.STOPPED);

        runner.resetCreationTime();

        Assert.assertEquals(runner.getCreationTime(), System.currentTimeMillis(), TimeInterval.FIVE_SEC.getValue());
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsQueue() {
        runner.setStatus(Runner.Status.IN_QUEUE);

        runner.resetCreationTime();

        Assert.assertEquals(runner.getCreationTime(), System.currentTimeMillis(), TimeInterval.FIVE_SEC.getValue());
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsTimeOutAndDescriptionIsNull() {
        runner.setStatus(Runner.Status.TIMEOUT);

        runner.resetCreationTime();

        Assert.assertEquals(runner.getCreationTime(), System.currentTimeMillis(), TimeInterval.FIVE_SEC.getValue());
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsRunningAndDescriptionIsNull() {
        runner.setStatus(Runner.Status.RUNNING);

        runner.resetCreationTime();

        Assert.assertEquals(runner.getCreationTime(), System.currentTimeMillis(), TimeInterval.FIVE_SEC.getValue());
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsDoneAndDescriptionIsNull() {
        runner.setStatus(Runner.Status.DONE);

        runner.resetCreationTime();

        Assert.assertEquals(runner.getCreationTime(), System.currentTimeMillis(), TimeInterval.FIVE_SEC.getValue());
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsInProgressAndDescriptionIsNull() {
        runner.setStatus(Runner.Status.IN_PROGRESS);

        runner.resetCreationTime();

        Assert.assertEquals(runner.getCreationTime(), System.currentTimeMillis(), TimeInterval.FIVE_SEC.getValue());
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsTimeOutAndDescriptionIsNotNull() {
        when(descriptor.getCreationTime()).thenReturn(LONG_CONSTANT);
        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.TIMEOUT);

        runner.resetCreationTime();

        assertThat(runner.getCreationTime(), is(LONG_CONSTANT));
        verify(descriptor).getCreationTime();
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsRunningAndDescriptionIsNotNull() {
        when(descriptor.getCreationTime()).thenReturn(LONG_CONSTANT);
        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        runner.resetCreationTime();

        assertThat(runner.getCreationTime(), is(LONG_CONSTANT));
        verify(descriptor).getCreationTime();
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsDoneAndDescriptionIsNotNull() {
        when(descriptor.getCreationTime()).thenReturn(LONG_CONSTANT);
        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        runner.resetCreationTime();

        assertThat(runner.getCreationTime(), is(LONG_CONSTANT));
        verify(descriptor).getCreationTime();
    }

    @Test
    public void shouldResetCreationTimeWithRunnerStatusIsInProgressAndDescriptionIsNotNull() {
        when(descriptor.getCreationTime()).thenReturn(LONG_CONSTANT);
        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.IN_PROGRESS);

        runner.resetCreationTime();

        assertThat(runner.getCreationTime(), is(LONG_CONSTANT));
        verify(descriptor).getCreationTime();
    }

    @Test
    public void shouldGetActiveTimeWhenRunnerIsAlive() {
        //set creation time
        when(descriptor.getCreationTime()).thenReturn(System.currentTimeMillis() - TimeInterval.FIVE_SEC.getValue());
        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);
        runner.resetCreationTime();

        String result = runner.getActiveTime();

        assertThat(result.charAt(result.length() - 1), is('s'));
        verify(descriptor).getCreationTime();
    }

    @Test
    public void shouldGetActiveTimeWhenRunnerIsNotAlive() {
        runner.setStatus(Runner.Status.STOPPED);

        assertThat(runner.getActiveTime(), Is.is(RunnerManagerPresenter.TIMER_STUB));
    }

    @Test
    public void shouldGetStopTimeWhenRunnerIsAlive() {
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.getStopTime(), Is.is(RunnerManagerPresenter.TIMER_STUB));
    }

    @Test
    public void shouldGetStopTimeWhenRunnerIsNotAliveAndStopTimeMetricIsNull() {
        runner.setStatus(Runner.Status.FAILED);

        assertThat(runner.getStopTime(), Is.is(RunnerManagerPresenter.TIMER_STUB));
    }

    @Test
    public void shouldGetStopTimeWhenRunnerIsNotAliveAndStopTimeMetricIsNotNullButHasGetValueNull() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(STOP_TIME);
        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.FAILED);

        assertThat(runner.getStopTime(), Is.is(RunnerManagerPresenter.TIMER_STUB));
        verify(descriptor).getRunStats();
        verify(stat).getName();
    }

    @Test
    public void shouldGetStopTimeWhenRunnerIsNotAliveAndStopTimeMetricIsNotNull() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(STOP_TIME);
        when(stat.getValue()).thenReturn(String.valueOf(LONG_CONSTANT));
        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.FAILED);
        String expectedDate = DateTimeFormat.getFormat("dd-MM-yy HH:mm:ss").format(new Date(LONG_CONSTANT));

        assertThat(runner.getStopTime(), is(expectedDate));
        verify(descriptor).getRunStats();
        verify(stat).getName();
        verify(stat).getValue();
    }

    @Test
    public void applicationUrlShouldBeReturnedNullWhenDescriptorIsNull() {
        assertThat(runner.getApplicationURL(), nullValue());
    }

    @Test
    public void applicationUrlShouldBeReturnedNull() {
        runner.setProcessDescriptor(descriptor);
        assertThat(runner.getApplicationURL(), nullValue());
    }

    @Test
    public void aetApplicationUrlShouldBeReturnedWhenDescriptorHasNotCorrectUrl() {
        when(descriptor.getLinks()).thenReturn(links);

        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getApplicationURL(), is(URL));
        verify(descriptor, times(2)).getLinks();
        verify(link).getHref();
    }

    @Test
    public void applicationUrlShouldBeReturnedWhenDescriptorHasCorrectUrlAndCodeServerParameterIsNotEmpty() {
        addLinkToList("code server", "http://server:1");
        when(descriptor.getLinks()).thenReturn(links);

        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getApplicationURL(), is(URL + "?h=http://server&p=1"));
        verify(link1).getHref();
    }

    @Test
    public void terminalURLShouldBeReturned() {
        addLinkToList(LINK_REL_SHELL_URL, URL);
        when(descriptor.getLinks()).thenReturn(links);

        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getTerminalURL(), is(URL));
        verify(link1).getHref();
    }

    @Test
    public void logUrlShouldBeReturned() {
        addLinkToList(LINK_REL_VIEW_LOG, URL);
        when(descriptor.getLinks()).thenReturn(links);

        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getLogUrl(), is(link1));
    }

    @Test
    public void dockerUrlShouldBeReturned() {
        addLinkToList(LINK_REL_RUNNER_RECIPE, URL);
        when(descriptor.getLinks()).thenReturn(links);

        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getDockerUrl(), is(URL));
        verify(descriptor, times(1)).getLinks();
        verify(link1).getHref();
    }

    @Test
    public void stopUrlShouldBeReturned() {
        addLinkToList(LINK_REL_STOP, URL);
        when(descriptor.getLinks()).thenReturn(links);

        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getApplicationURL(), is(URL));
        verify(descriptor, times(2)).getLinks();
    }

    @Test
    public void stopUrlWhenDescriptorIsNullShouldBeNullToo() {
        assertThat(runner.getStopUrl(), nullValue());
    }

    @Test
    public void stopUrlShouldBeReturnedWhenDescriptorIsNotNull() {
        addLinkToList(LINK_REL_STOP, URL);
        when(descriptor.getLinks()).thenReturn(links);

        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getApplicationURL(), is(URL));
        verify(descriptor, times(2)).getLinks();
    }

    private void addLinkToList(String name, String url) {
        when(link1.getRel()).thenReturn(name);
        when(link1.getHref()).thenReturn(url);

        links.add(link1);
    }

    @Test
    public void processIdShouldBeReturned() {
        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getProcessId(), is(LONG_CONSTANT));
        verify(descriptor).getProcessId();
    }

    @Test
    public void shouldSetAndGetStatus() {
        runner.setStatus(Runner.Status.IN_QUEUE);
        assertThat(runner.getStatus(), Is.is(Runner.Status.IN_QUEUE));
    }

    @Test
    public void environmentIdShouldBeReturned() {
        runner.getEnvironmentId();

        verify(runOptions).getEnvironmentId();
        assertThat(runner.getEnvironmentId(), is(TEXT));
    }

    @Test
    public void titleShouldBeChanged() {
        assertThat(runner.getTitle(), is(RUNNER_NAME + RUNNER_NUMBER));

        runner.setTitle(TEXT);

        assertThat(runner.getTitle(), is(TEXT));
    }

    @Test
    public void equalsShouldReturnTrueWhenObjectIsSame() {
        assertThat(runner.equals(runner), is(true));
    }

    @Test
    public void equalsShouldReturnFalseWhenObjectHasTypeNotRunner() {
        assertThat(runner.equals(new Object()), is(false));
    }

    @Test
    public void equalsShouldReturnFalseForRunnerObjectsWithDifferentTitle() {
        when(runnerCounter.getRunnerNumber()).thenReturn(2);

        RunnerImpl runner1 = new RunnerImpl(locale, runnerCounter, runOptions);

        assertThat(runner.equals(runner1), is(false));
        verify(runnerCounter, times(2)).getRunnerNumber();
    }

    @Test
    public void equalsShouldReturnTrueForRunnerObjectsWithSameTitle() {
        RunnerImpl runner1 = new RunnerImpl(locale, runnerCounter, runOptions);

        assertThat(runner.equals(runner1), is(true));
    }

    @Test
    public void hashCodeShouldBeEquivalentForEquivalentObjects() {
        RunnerImpl runner1 = new RunnerImpl(locale, runnerCounter, runOptions);
        RunnerImpl runner2 = new RunnerImpl(locale, runnerCounter, runOptions);
        assertThat(runner1.hashCode(), is(runner2.hashCode()));
    }

    @Test
    public void hashCodeShouldNotBeEquivalentForNotEquivalentObjects() {
        RunnerCounter runnerCounter1 = mock(RunnerCounter.class);
        when(runnerCounter1.getRunnerNumber()).thenReturn(2);

        RunnerImpl runner1 = new RunnerImpl(locale, runnerCounter, runOptions);
        RunnerImpl runner2 = new RunnerImpl(locale, runnerCounter1, runOptions);
        assertThat(runner1.hashCode(), is(not(runner2.hashCode())));
    }

    @Test
    public void setRunOptionsShouldBeReturned() {
        assertThat(runner.getOptions(), is(runOptions));
    }

    @Test
    public void runnerShouldBeAliveWhenStatusInQueue() {
        runner.setStatus(Runner.Status.IN_QUEUE);

        assertThat(runner.isAlive(), is(true));
    }

    @Test
    public void runnerShouldBeAliveWhenStatusInProgress() {
        runner.setStatus(Runner.Status.IN_PROGRESS);

        assertThat(runner.isAlive(), is(true));
    }

    @Test
    public void runnerShouldBeAliveWhenStatusRunning() {
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.isAlive(), is(true));
    }

    @Test
    public void runnerShouldBeAliveWhenStatusTimeOut() {
        runner.setStatus(Runner.Status.TIMEOUT);

        assertThat(runner.isAlive(), is(true));
    }

    @Test
    public void runnerShouldBeAliveAliveWhenStatusDone() {
        runner.setStatus(Runner.Status.DONE);

        assertThat(runner.isAlive(), is(true));
    }

    @Test
    public void runnerShouldNotBeAliveWhenStatusFailed() {
        runner.setStatus(Runner.Status.FAILED);

        assertThat(runner.isAlive(), is(false));
    }

    @Test
    public void runnerShouldNotBeAliveWhenStatusStopped() {
        runner.setStatus(Runner.Status.STOPPED);

        assertThat(runner.isAlive(), is(false));
    }

    @Test
    public void shouldGetTimeOutWhenRunnerStatusQueue() {
        runner.setStatus(Runner.Status.IN_QUEUE);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));
    }

    @Test
    public void shouldGetTimeOutWhenStatusProgress() {
        runner.setStatus(Runner.Status.IN_PROGRESS);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));
    }

    @Test
    public void shouldGetTimeOutWhenStatusTimeOut() {
        runner.setStatus(Runner.Status.TIMEOUT);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));
    }


    @Test
    public void shouldGetTimeOutWhenStatusFailed() {
        runner.setStatus(Runner.Status.FAILED);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));
    }

    @Test
    public void shouldGetTimeOutWhenStatusStopped() {
        runner.setStatus(Runner.Status.STOPPED);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricValueIsAlwaysOn() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(TERMINATION_TIME);
        when(stat.getValue()).thenReturn(ALWAYS_ON);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        assertThat(runner.getTimeout(), is(ALWAYS_ON));

        verify(descriptor).getRunStats();
        verify(stat).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricValueIsNull() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(TERMINATION_TIME);
        when(stat.getValue()).thenReturn(null);
        runner.setStatus(Runner.Status.DONE);
        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor).getRunStats();
        verify(stat, times(1)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricValueIsWaitingTimeLimit() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(TERMINATION_TIME);
        when(stat.getValue()).thenReturn(String.valueOf(System.currentTimeMillis() + TimeInterval.THIRTY_SEC.getValue()));

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        String result = runner.getTimeout();

        assertThat(result.endsWith("s"), is(true));

        assertEquals(getTime(result), 30, 10);

        verify(descriptor).getRunStats();
        verify(stat).getName();
        verify(stat).getValue();
    }

    private long getTime(String time) {
        return Long.parseLong(time.split("s")[0]);
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricValueIsNegative() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(TERMINATION_TIME);
        when(stat.getValue()).thenReturn("1000");

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor).getRunStats();
        verify(stat, times(1)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricIsNull() {
        metricList = Collections.emptyList();
        when(descriptor.getRunStats()).thenReturn(metricList);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor, times(2)).getRunStats();
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricValueIsLifeTimeButDescriptorStatusNotNew() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(LIFETIME);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor, times(2)).getRunStats();
        verify(stat, times(2)).getName();
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricValueIsLifeTimeAndStatusDescriptorIsNew1() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(descriptor.getStatus()).thenReturn(NEW);
        when(stat.getName()).thenReturn(LIFETIME);
        when(stat.getValue()).thenReturn(ALWAYS_ON);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        assertThat(runner.getTimeout(), is(ALWAYS_ON));

        verify(descriptor, times(2)).getRunStats();
        verify(descriptor).getStatus();
        verify(stat, times(2)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricValueIsLifeTimeAndStatusDescriptorIsNew2() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(descriptor.getStatus()).thenReturn(NEW);
        when(stat.getName()).thenReturn(LIFETIME);
        when(stat.getValue()).thenReturn(String.valueOf(TimeInterval.THIRTY_SEC.getValue() * 2));

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        assertThat(runner.getTimeout(), is("1m:00s"));

        verify(descriptor, times(2)).getRunStats();
        verify(descriptor).getStatus();
        verify(stat, times(2)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusDoneAndTimerMetricValueIsLifeTimeAndStatusDescriptorIsNew3() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(descriptor.getStatus()).thenReturn(NEW);
        when(stat.getName()).thenReturn(LIFETIME);
        when(stat.getValue()).thenReturn(null);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.DONE);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor, times(2)).getRunStats();
        verify(descriptor).getStatus();
        verify(stat, times(2)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricValueIsAlwaysOn() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(TERMINATION_TIME);
        when(stat.getValue()).thenReturn(ALWAYS_ON);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.getTimeout(), is(ALWAYS_ON));

        verify(descriptor).getRunStats();
        verify(stat).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricValueIsNull() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(TERMINATION_TIME);
        when(stat.getValue()).thenReturn(null);
        runner.setStatus(Runner.Status.DONE);
        runner.setProcessDescriptor(descriptor);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor).getRunStats();
        verify(stat, times(1)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricValueIsWaitingTimeLimit() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(TERMINATION_TIME);
        when(stat.getValue()).thenReturn(String.valueOf(System.currentTimeMillis() + TimeInterval.THIRTY_SEC.getValue()));

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        String result = runner.getTimeout();

        assertThat(result.endsWith("s"), is(true));

        assertEquals(getTime(result), 30, 10);

        verify(descriptor).getRunStats();
        verify(stat).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricValueIsNegative() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(TERMINATION_TIME);
        when(stat.getValue()).thenReturn("1000");

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor).getRunStats();
        verify(stat, times(1)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricIsNull() {
        metricList = Collections.emptyList();
        when(descriptor.getRunStats()).thenReturn(metricList);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor, times(2)).getRunStats();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricValueIsLifeTimeButDescriptorStatusNotNew() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(stat.getName()).thenReturn(LIFETIME);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor, times(2)).getRunStats();
        verify(stat, times(2)).getName();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricValueIsLifeTimeAndStatusDescriptorIsNew1() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(descriptor.getStatus()).thenReturn(NEW);
        when(stat.getName()).thenReturn(LIFETIME);
        when(stat.getValue()).thenReturn(ALWAYS_ON);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.getTimeout(), is(ALWAYS_ON));

        verify(descriptor, times(2)).getRunStats();
        verify(descriptor, times(1)).getStatus();
        verify(stat, times(2)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricValueIsLifeTimeAndStatusDescriptorIsNew2() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(descriptor.getStatus()).thenReturn(NEW);
        when(stat.getName()).thenReturn(LIFETIME);
        when(stat.getValue()).thenReturn(String.valueOf(TimeInterval.THIRTY_SEC.getValue()));

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.getTimeout(), is("30s"));

        verify(descriptor, times(2)).getRunStats();
        verify(descriptor, times(1)).getStatus();
        verify(stat, times(2)).getName();
        verify(stat).getValue();
    }

    @Test
    public void shouldGetTimeOutWhenStatusRunningAndTimerMetricValueIsLifeTimeAndStatusDescriptorIsNew3() {
        when(descriptor.getRunStats()).thenReturn(metricList);
        when(descriptor.getStatus()).thenReturn(NEW);
        when(stat.getName()).thenReturn(LIFETIME);
        when(stat.getValue()).thenReturn(null);

        runner.setProcessDescriptor(descriptor);
        runner.setStatus(Runner.Status.RUNNING);

        assertThat(runner.getTimeout(), Is.is(RunnerManagerPresenter.TIMER_STUB));

        verify(descriptor, times(2)).getRunStats();
        verify(descriptor, times(1)).getStatus();
        verify(stat, times(2)).getName();
        verify(stat).getValue();
    }

    @Test
    public void scopeShouldBeChanged() {
        assertThat(runner.getScope(), Is.is(Scope.SYSTEM));

        runner.setScope(Scope.PROJECT);

        assertThat(runner.getScope(), Is.is(Scope.PROJECT));
    }

    @Test
    public void typeShouldBeChanged() {
        assertThat(runner.getType(), nullValue());

        runner.setType(TEXT);

        assertThat(runner.getType(), is(TEXT));
    }
}