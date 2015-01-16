/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
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

/**
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class RunnerImpl implements Runner {

    private static final String RUNNER_NAME   = "Runner ";
    private static       int    RUNNER_NUMBER = 1;

    private final RunOptions runOptions;
    private final String     title;

    private ApplicationProcessDescriptor descriptor;
    private Status                       status;
    private Date                         creationData;
    private int                          ram;
    private boolean                      isAlive;
    private boolean                      isAnyAppRunning;
    private boolean                      isAnyAppLaunched;
    private boolean                      isConsoleActive;

    /**
     * This runner needs runner options (user configurations). It analyzes all given information and get necessary information.
     *
     * @param runOptions
     *         options which needs to be used
     */
    @AssistedInject
    public RunnerImpl(@Nonnull @Assisted RunOptions runOptions) {
        this(runOptions, null);
    }

    /**
     * This runner needs runner options (user configurations) and environment name (inputted by user).
     * It analyzes all given information and get necessary information.
     *
     * @param runOptions
     *         options which needs to be used
     * @param environmentName
     *         name of custom configuration
     */
    @AssistedInject
    public RunnerImpl(@Nonnull @Assisted RunOptions runOptions, @Nullable @Assisted String environmentName) {
        this.runOptions = runOptions;
        this.title = RUNNER_NAME + RUNNER_NUMBER + (environmentName == null ? "" : " - " + getCorrectName(environmentName));
        this.ram = runOptions.getMemorySize();
        this.isConsoleActive = true;
        this.status = Status.IN_QUEUE;

        creationData = new Date();
        RUNNER_NUMBER++;
    }

    @Nonnull
    private String getCorrectName(@Nonnull String environmentName) {
        int lastIndex = environmentName.lastIndexOf("/") + 1;

        return environmentName.substring(lastIndex, environmentName.length());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConsoleActive() {
        return isConsoleActive;
    }

    /** {@inheritDoc} */
    @Override
    public void activateConsole() {
        isConsoleActive = true;
    }

    /** {@inheritDoc} */
    @Override
    public void activateTerminal() {
        isConsoleActive = false;
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

        runOptions.setMemorySize(ram);
    }

    /** {@inheritDoc} */
    @Override
    public long getCreationTime() {
        return creationData.getTime();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public String getTimeout() {
        RunnerMetric timeoutMetric = getRunnerMetricByName(RunnerMetric.TERMINATION_TIME);

        if (timeoutMetric != null) {
            return getTimeOut(timeoutMetric);
        }

        RunnerMetric lifeTimeMetric = getRunnerMetricByName(RunnerMetric.LIFETIME);

        if (lifeTimeMetric != null && NEW.equals(descriptor.getStatus())) {
            return getLifeTime(lifeTimeMetric);
        }

        return "";
    }

    @Nonnull
    private String getTimeOut(@Nonnull RunnerMetric timeoutMetric) {
        String timeout = timeoutMetric.getValue();

        if (RunnerMetric.ALWAYS_ON.equals(timeout)) {
            return timeout;
        }

        if (timeout == null) {
            return "";
        }

        double terminationTime = NumberFormat.getDecimalFormat().parse(timeout);
        double terminationTimeout = terminationTime - System.currentTimeMillis();

        if (terminationTimeout <= 0) {
            return "";
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
            return "";
        }
        double lifeTime = NumberFormat.getDecimalFormat().parse(lifeTimeValue);

        return StringUtils.timeMlsToHumanReadable((long)lifeTime);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getTotalTime() {
        return StringUtils.timeSecToHumanReadable((System.currentTimeMillis() - creationData.getTime()) / 1000);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getStopTime() {
        RunnerMetric stopTimeMetric = getRunnerMetricByName(RunnerMetric.STOP_TIME);

        if (stopTimeMetric == null) {
            return "";
        }

        String stopTime = stopTimeMetric.getValue();

        if (stopTime == null) {
            return "";
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
    @Override
    public boolean isStarted() {
        Objects.requireNonNull(descriptor);
        return descriptor.getStartTime() != -1;
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
    public boolean isAlive() {
        return isAlive;
    }

    /** {@inheritDoc} */
    @Override
    public void setAliveStatus(boolean isAlive) {
        this.isAlive = isAlive;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAnyAppLaunched() {
        return isAnyAppLaunched;
    }

    /** {@inheritDoc} */
    @Override
    public void setAppLaunchStatus(boolean isAnyAppLaunched) {
        this.isAnyAppLaunched = isAnyAppLaunched;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAnyAppRunning() {
        return isAnyAppRunning;
    }

    /** {@inheritDoc} */
    @Override
    public void setAppRunningStatus(boolean isAnyAppRunning) {
        this.isAnyAppRunning = isAnyAppRunning;
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