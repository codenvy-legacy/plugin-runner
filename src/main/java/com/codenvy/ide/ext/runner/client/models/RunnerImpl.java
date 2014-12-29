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
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.Objects;

import static com.codenvy.api.runner.ApplicationStatus.RUNNING;
import static com.codenvy.api.runner.internal.Constants.LINK_REL_SHELL_URL;
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
    private int                          ram;
    private boolean                      isAlive;
    private boolean                      isAnyAppRunning;
    private boolean                      isAnyAppLaunched;

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
        this.title = RUNNER_NAME + RUNNER_NUMBER + (environmentName == null ? "" : " - " + environmentName);
        this.ram = runOptions.getMemorySize();

        RUNNER_NUMBER++;
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
    @Nonnull
    @Override
    public Date getCreationTime() {
        Objects.requireNonNull(descriptor);
        return new Date(descriptor.getCreationTime());
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

        if (!(RUNNING.equals(descriptor.getStatus()) && isAlive)) {
            return null;
        }

        Link appLink = RunnerUtils.getLink(descriptor, LINK_REL_WEB_URL);
        if (appLink == null) {
            return null;
        }

        return appLink.getHref() + getCodeServerParam();
    }

    @Nonnull
    private String getCodeServerParam() {
        Link codeServerLink = RunnerUtils.getLink(descriptor, "code server");

        if (codeServerLink == null) {
            return "";
        }

        String codeServerHref = codeServerLink.getHref();
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
        Link link = RunnerUtils.getLink(descriptor, LINK_REL_SHELL_URL);

        if (link == null) {
            return null;
        }

        return link.getHref();
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getLogUrl() {
        Link link = RunnerUtils.getLink(descriptor, LINK_REL_VIEW_LOG);

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

    /** {@inheritDoc} */
    @Override
    public RunnerMetric getRunnerMetricByName(@Nonnull String name) {
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