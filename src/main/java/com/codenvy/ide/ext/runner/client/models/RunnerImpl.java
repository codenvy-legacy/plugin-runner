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
import com.codenvy.api.project.server.ProjectDescription;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
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
import static com.codenvy.api.runner.internal.Constants.LINK_REL_WEB_URL;

/**
 * @author Andrey Plotnikov
 */
public class RunnerImpl implements Runner {

    private static final String RUNNER_NAME   = "Runner ";
    private static       int    RUNNER_NUMBER = 1;

    private final int    ram;
    private final String title;

    private ApplicationProcessDescriptor description;
    private Status                       status;
    private boolean                      isAlive;

    /**
     * The constructor for default configuration runner. This runner just needs project description where all configurations are located.
     * It analyzes project description and get all needed information.
     *
     * @param projectDescription
     *         project description that needs to be analyzed
     */
    @AssistedInject
    public RunnerImpl(@Assisted ProjectDescription projectDescription) {
        this(projectDescription.getRunners().getConfig(projectDescription.getRunners().getDefault()).getRam(),
             RUNNER_NAME + RUNNER_NAME);
    }

    /**
     * The constructor for custom runner. This runner needs runner options (user configurations) and environment name (inputted by user).
     * It analyzes all given information and get necessary information.
     *
     * @param runOptions
     *         custom configuration of runner
     * @param environmentName
     *         name of custom configuration
     */
    @AssistedInject
    public RunnerImpl(@Assisted RunOptions runOptions, @Assisted String environmentName) {
        this(runOptions.getMemorySize(), RUNNER_NAME + RUNNER_NUMBER + " - " + environmentName);
    }

    private RunnerImpl(@Nonnegative int ram, @Nonnull String title) {
        this.ram = ram;
        this.title = title;

        RUNNER_NUMBER++;
    }

    /** {@inheritDoc} */
    @Override
    public int getRAM() {
        return ram;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Date getCreationTime() {
        Objects.requireNonNull(description);
        return new Date(description.getCreationTime());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isStarted() {
        Objects.requireNonNull(description);
        return description.getStartTime() != -1;
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
        if (description == null) {
            return null;
        }

        if (!(RUNNING.equals(description.getStatus()) && isAlive)) {
            return null;
        }

        Link appLink = RunnerUtils.getLink(description, LINK_REL_WEB_URL);
        if (appLink == null) {
            return null;
        }

        return appLink.getHref() + getCodeServerParam();
    }

    @Nonnull
    private String getCodeServerParam() {
        Link codeServerLink = RunnerUtils.getLink(description, "code server");

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
        Link link = RunnerUtils.getLink(description, LINK_REL_SHELL_URL);

        if (link == null) {
            return null;
        }

        return link.getHref();
    }

    /** {@inheritDoc} */
    @Override
    public void setProcessDescription(@Nonnull ApplicationProcessDescriptor description) {
        this.description = description;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RunnerImpl runner = (RunnerImpl)o;

        boolean isEqualed = Objects.equals(description, runner.description);
        isEqualed &= Objects.equals(status, runner.status);
        isEqualed &= Objects.equals(title, runner.title);
        isEqualed &= ram == runner.ram;
        isEqualed &= isAlive == runner.isAlive;

        return isEqualed;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = Objects.hash(description, title, status);
        // for primitive types isn't good idea to use Objects. in Java 8 add an ability to use hash method for primitive types
        result = 31 * result + ram;
        result = 31 * result + (isAlive ? 1 : 0);

        return result;
    }

}