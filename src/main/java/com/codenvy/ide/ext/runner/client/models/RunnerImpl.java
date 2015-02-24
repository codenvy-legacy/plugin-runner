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

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.api.runner.dto.RunnerMetric;
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.util.StringUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.Objects;

import static com.codenvy.api.runner.ApplicationStatus.NEW;
import static com.codenvy.api.runner.internal.Constants.LINK_REL_RUNNER_RECIPE;
import static com.codenvy.api.runner.internal.Constants.LINK_REL_SHELL_URL;
import static com.codenvy.api.runner.internal.Constants.LINK_REL_STOP;
import static com.codenvy.api.runner.internal.Constants.LINK_REL_VIEW_LOG;
import static com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL;
import static com.codenvy.ide.ext.runner.client.constants.TimeInterval.ONE_SEC;
import static com.codenvy.ide.ext.runner.client.manager.RunnerManagerPresenter.TIMER_STUB;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.DONE;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.FAILED;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.IN_QUEUE;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.RUNNING;
import static com.codenvy.ide.ext.runner.client.models.Runner.Status.STOPPED;

/**
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 * @author Dmitry Shnurenko
 */
public class RunnerImpl implements Runner {

    private static final String RUNNER_NAME = "Runner ";

    private final RunOptions runOptions;
    private final String     title;

    private ApplicationProcessDescriptor descriptor;
    private Status                       status;
    private String                       activeTab;
    private long                         creationTime;
    private int                          ram;

    /**
     * This runner needs runner options (user configurations). It analyzes all given information and get necessary information.
     *
     * @param locale
     *         localization constants
     * @param runnerCounter
     *         utility that support the counter of runners
     * @param runOptions
     *         options which needs to be used
     */
    @AssistedInject
    public RunnerImpl(@Nonnull RunnerLocalizationConstant locale,
                      @Nonnull RunnerCounter runnerCounter,
                      @Nonnull @Assisted RunOptions runOptions) {
        this(locale, runnerCounter, runOptions, null);
    }

    /**
     * This runner needs runner options (user configurations) and environment name (inputted by user).
     * It analyzes all given information and get necessary information.
     *
     * @param locale
     *         localization constants
     * @param runnerCounter
     *         utility that support the counter of runners
     * @param runOptions
     *         options which needs to be used
     * @param environmentName
     *         name of custom configuration
     */
    @AssistedInject
    public RunnerImpl(@Nonnull RunnerLocalizationConstant locale,
                      @Nonnull RunnerCounter runnerCounter,
                      @Nonnull @Assisted RunOptions runOptions,
                      @Nullable @Assisted String environmentName) {
        this.runOptions = runOptions;
        this.ram = runOptions.getMemorySize();
        this.title = RUNNER_NAME +
                     runnerCounter.getRunnerNumber() +
                     (environmentName == null ? "" : " - " + getCorrectName(environmentName));
        this.activeTab = locale.runnerTabConsole();
        this.status = Status.IN_QUEUE;

        creationTime = System.currentTimeMillis();
    }

    @Nonnull
    private String getCorrectName(@Nonnull String environmentName) {
        int lastIndex = environmentName.lastIndexOf("/") + 1;

        return environmentName.substring(lastIndex, environmentName.length());
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public ApplicationProcessDescriptor getDescriptor() {
        return descriptor;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getActiveTab() {
        return activeTab;
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveTab(@Nonnull String tab) {
        activeTab = tab;
    }

    /** {@inheritDoc} */
    @Override
    public int getRAM() {
        return ram;
    }

    /** {@inheritDoc} */
    @Override
    public void setRAM(@Nonnegative int ram) {
        this.ram = ram;
    }

    /** {@inheritDoc} */
    @Override
    public long getCreationTime() {
        return creationTime;
    }

    /** {@inheritDoc} */
    @Override
    public void resetCreationTime() {
        if (FAILED.equals(status) || STOPPED.equals(status) || IN_QUEUE.equals(status)) {
            creationTime = System.currentTimeMillis();
            return;
        }

        creationTime = descriptor == null ? System.currentTimeMillis() : descriptor.getCreationTime();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public String getTimeout() {
        if (!(DONE.equals(status) || RUNNING.equals(status))) {
            return TIMER_STUB;
        }

        RunnerMetric timeoutMetric = getRunnerMetricByName(RunnerMetric.TERMINATION_TIME);

        if (timeoutMetric != null) {
            return getTimeOut(timeoutMetric);
        }

        RunnerMetric lifeTimeMetric = getRunnerMetricByName(RunnerMetric.LIFETIME);

        if (lifeTimeMetric != null && NEW.equals(descriptor.getStatus())) {
            return getLifeTime(lifeTimeMetric);
        }

        return TIMER_STUB;
    }

    @Nonnull
    private String getTimeOut(@Nonnull RunnerMetric timeoutMetric) {
        String timeout = timeoutMetric.getValue();

        if (RunnerMetric.ALWAYS_ON.equals(timeout)) {
            return timeout;
        }

        if (timeout == null) {
            return TIMER_STUB;
        }

        double terminationTime = NumberFormat.getDecimalFormat().parse(timeout);
        double terminationTimeout = terminationTime - System.currentTimeMillis();

        if (terminationTimeout <= 0) {
            return TIMER_STUB;
        }

        return StringUtils.timeMlsToHumanReadable((long)terminationTimeout);
    }

    @Nonnull
    private String getLifeTime(@Nonnull RunnerMetric lifeTimeMetric) {
        String lifeTimeValue = lifeTimeMetric.getValue();

        if (RunnerMetric.ALWAYS_ON.equals(lifeTimeValue)) {
            return lifeTimeValue;
        }

        if (lifeTimeValue == null) {
            return TIMER_STUB;
        }
        double lifeTime = NumberFormat.getDecimalFormat().parse(lifeTimeValue);

        return StringUtils.timeMlsToHumanReadable((long)lifeTime);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getActiveTime() {
        return isAlive() ? StringUtils.timeSecToHumanReadable((System.currentTimeMillis() - creationTime) / ONE_SEC.getValue())
                         : TIMER_STUB;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getStopTime() {
        if (isAlive()) {
            return TIMER_STUB;
        }

        RunnerMetric stopTimeMetric = getRunnerMetricByName(RunnerMetric.STOP_TIME);

        if (stopTimeMetric == null) {
            return TIMER_STUB;
        }

        String stopTime = stopTimeMetric.getValue();

        if (stopTime == null) {
            return TIMER_STUB;
        }
        double stopTimeMls = NumberFormat.getDecimalFormat().parse(stopTime);

        return DateTimeFormat.getFormat("dd-MM-yy HH:mm:ss").format(new Date((long)stopTimeMls));
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getEnvironmentId() {
        return runOptions.getEnvironmentId();
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Status getStatus() {
        return status;
    }

    /** {@inheritDoc} */
    @Override
    public void setStatus(@Nonnull Status status) {
        this.status = status;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getApplicationURL() {
        if (descriptor == null) {
            return null;
        }

        String appUrl = getUrlByName(LINK_REL_WEB_URL);
        if (appUrl == null) {
            return null;
        }

        return appUrl + getCodeServerParam();
    }

    @Nonnull
    private String getCodeServerParam() {
        String codeServerHref = getUrlByName("code server");
        if (codeServerHref == null) {
            return "";
        }

        int colon = codeServerHref.lastIndexOf(':');

        String hostParam = "?h=" + codeServerHref.substring(0, colon);
        String portParam = "";

        if (colon > 0) {
            portParam = "&p=" + codeServerHref.substring(colon + 1);
        }

        return hostParam + portParam;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getTerminalURL() {
        return getUrlByName(LINK_REL_SHELL_URL);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Link getLogUrl() {
        return RunnerUtils.getLink(descriptor, LINK_REL_VIEW_LOG);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getDockerUrl() {
        return getUrlByName(LINK_REL_RUNNER_RECIPE);
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Link getStopUrl() {
        return RunnerUtils.getLink(descriptor, LINK_REL_STOP);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAlive() {
        switch (status) {
            case IN_QUEUE:
                return true;
            case IN_PROGRESS:
                return true;
            case RUNNING:
                return true;
            case TIMEOUT:
                return true;
            case DONE:
                return true;
            default:
                return false;
        }
    }

    @Nullable
    private String getUrlByName(@Nonnull String name) {
        Link link = RunnerUtils.getLink(descriptor, name);

        if (link == null) {
            return null;
        }

        return link.getHref();
    }

    /** {@inheritDoc} */
    @Override
    public void setProcessDescriptor(@Nullable ApplicationProcessDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /** {@inheritDoc} */
    @Override
    public long getProcessId() {
        Objects.requireNonNull(descriptor);
        return descriptor.getProcessId();
    }

    @Nullable
    private RunnerMetric getRunnerMetricByName(@Nonnull String name) {
        if (descriptor == null) {
            return null;
        }

        for (RunnerMetric stat : descriptor.getRunStats()) {
            if (name.equals(stat.getName())) {
                return stat;
            }
        }

        return null;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public RunOptions getOptions() {
        return runOptions;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RunnerImpl runner = (RunnerImpl)o;

        return Objects.equals(title, runner.title);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

}