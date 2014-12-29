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
package com.codenvy.ide.ext.runner.client.console;

import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

/**
 * @author Artem Zatsarynnyy
 * @author Vitaliy Guliy
 * @author Mihail Kuznyetsov
 * @author Andrey Plotnikov
 */
public class ConsoleImpl extends Composite implements Console {

    interface ConsoleImplUiBinder extends UiBinder<Widget, ConsoleImpl> {
    }

    private static final ConsoleImplUiBinder UI_BINDER = GWT.create(ConsoleImplUiBinder.class);

    private static final int MAX_CONSOLE_LINES  = 1_000;
    private static final int CLEAR_CONSOLE_LINE = 100;

    private static final String PRE_STYLE = "style='margin:0px;'";

    private static final String INFO       = "[INFO]";
    private static final String INFO_COLOR = "lightgreen";

    private static final String WARN       = "[WARNING]";
    private static final String WARN_COLOR = "#FFBA00";

    private static final String ERROR       = "[ERROR]";
    private static final String ERROR_COLOR = "#F62217";

    private static final String DOCKER       = "[DOCKER]";
    private static final String DOCKER_COLOR = "#00B7EC";

    private static final String STDOUT       = "[STDOUT]";
    private static final String STDOUT_COLOR = "lightgreen";

    private static final String STDERR       = "[STDERR]";
    private static final String STDERR_COLOR = "#F62217";

    @UiField
    ScrollPanel panel;
    @UiField
    FlowPanel   output;
    @UiField(provided = true)
    final RunnerResources res;

    private final RunnerLocalizationConstant locale;
    private final Runner                     runner;

    @Inject
    public ConsoleImpl(RunnerResources resources,
                       RunnerLocalizationConstant locale,
                       @Nonnull @Assisted Runner runner) {
        this.res = resources;
        this.locale = locale;
        this.runner = runner;

        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void printInfo(@Nonnull String line) {
        HTML message = new HTML();
        message.setHTML(buildSafeHtmlMessage(INFO, INFO_COLOR, INFO + ' ' + line));

        print(message);
    }

    /** {@inheritDoc} */
    @Override
    public void printError(@Nonnull String line) {
        HTML message = new HTML();
        message.setHTML(buildSafeHtmlMessage(ERROR, ERROR_COLOR, ERROR + ' ' + line));

        print(message);
    }

    /** {@inheritDoc} */
    @Override
    public void printWarn(@Nonnull String line) {
        HTML message = new HTML();
        message.setHTML(buildSafeHtmlMessage(WARN, WARN_COLOR, WARN + ' ' + line));

        print(message);
    }

    /** {@inheritDoc} */
    @Override
    public void printDocker(@Nonnull String line) {
        HTML message = new HTML();
        message.setHTML(buildSafeHtmlMessage(DOCKER, DOCKER_COLOR, DOCKER + ' ' + line));

        print(message);
    }

    /** {@inheritDoc} */
    @Override
    public void printDockerError(@Nonnull String line) {
        HTML message = new HTML();
        message.setHTML(buildSafeHtmlMessage(DOCKER, DOCKER_COLOR, ERROR, ERROR_COLOR, DOCKER + ' ' + ERROR + ' ' + line));

        print(message);
    }

    /** {@inheritDoc} */
    @Override
    public void printStdOut(@Nonnull String line) {
        HTML message = new HTML();
        message.setHTML(buildSafeHtmlMessage(STDOUT, STDOUT_COLOR, STDOUT + ' ' + line));

        print(message);
    }

    /** {@inheritDoc} */
    @Override
    public void printStdErr(@Nonnull String line) {
        HTML message = new HTML();
        message.setHTML(buildSafeHtmlMessage(STDERR, STDERR_COLOR, STDERR + ' ' + line));

        print(message);
    }

    /**
     * Return sanitized message (with all restricted HTML-tags escaped) in SafeHtml.
     *
     * @param type
     *         message type (e.g. INFO, ERROR etc.)
     * @param color
     *         color constant
     * @param message
     *         message to print
     * @return message in SafeHtml
     */
    @Nonnull
    private SafeHtml buildSafeHtmlMessage(@Nonnull String type, @Nonnull String color, @Nonnull String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + '>')
                .appendHtmlConstant("[<span style='color:" + color + ";'>")
                .appendHtmlConstant("<b>" + type.replaceAll("[\\[\\]]", "") + "</b></span>]")
                .append(SimpleHtmlSanitizer.sanitizeHtml(message.substring((type).length())))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }

    /**
     * Return sanitized message (with all restricted HTML-tags escaped) in SafeHtml. Use for two-words message types,
     * e.g. [DOCKER] [ERROR].
     *
     * @param type
     *         message type (e.g. DOCKER)
     * @param color
     *         color constant
     * @param subtype
     *         message subtype (e.g. ERROR)
     * @param subcolor
     *         color constant
     * @param message
     *         message to print
     * @return message in SafeHtml
     */
    @Nonnull
    private SafeHtml buildSafeHtmlMessage(@Nonnull String type,
                                          @Nonnull String color,
                                          @Nonnull String subtype,
                                          @Nonnull String subcolor,
                                          @Nonnull String message) {
        return new SafeHtmlBuilder()
                .appendHtmlConstant("<pre " + PRE_STYLE + '>')
                .appendHtmlConstant("[<span style='color:" + color + ";'>")
                .appendHtmlConstant("<b>" + type.replaceAll("[\\[\\]]", "") + "</b></span>]")
                .appendHtmlConstant(" [<span style='color:" + subcolor + ";'>")
                .appendHtmlConstant("<b>" + subtype.replaceAll("[\\[\\]]", "") + "</b></span>]")
                .append(SimpleHtmlSanitizer.sanitizeHtml(message.substring((type + ' ' + subtype).length())))
                .appendHtmlConstant("</pre>")
                .toSafeHtml();
    }

    private void print(@Nonnull Widget message) {
        cleanOverHeadLinesIfAny();

        output.add(message);
    }

    private void cleanOverHeadLinesIfAny() {
        if (output.getWidgetCount() < MAX_CONSOLE_LINES) {
            return;
        }

        // remove first 10% of current lines on screen
        for (int i = 0; i < CLEAR_CONSOLE_LINE; i++) {
            output.remove(0);
        }

        String logUrl = runner.getLogUrl();
        if (logUrl == null) {
            return;
        }

        // print link to full logs in top of console
        HTML html = new HTML();
        html.addStyleName(res.runnerCss().logLink());

        Element text = DOM.createSpan();
        text.setInnerHTML(locale.fullLogTraceConsoleLink());

        Anchor link = new Anchor();
        link.setHref(logUrl);
        link.setText(logUrl);
        link.setTitle(logUrl);
        link.setTarget("_blank");
        link.getElement().getStyle().setColor("#61b7ef");

        html.getElement().appendChild(text);
        html.getElement().appendChild(link.getElement());

        output.insert(html, 0);
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        output.clear();
    }

}