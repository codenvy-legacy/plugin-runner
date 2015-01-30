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

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtml;
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
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.DOCKER;
import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.ERROR;
import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.INFO;
import static com.codenvy.ide.ext.runner.client.widgets.console.MessageType.WARNING;

/**
 * @author Artem Zatsarynnyy
 * @author Vitaliy Guliy
 * @author Mihail Kuznyetsov
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public class ConsoleImpl extends Composite implements Console {

    interface ConsoleImplUiBinder extends UiBinder<Widget, ConsoleImpl> {
    }

    private static final ConsoleImplUiBinder UI_BINDER = GWT.create(ConsoleImplUiBinder.class);

    private static final int MAX_CONSOLE_LINES  = 1_000;
    private static final int CLEAR_CONSOLE_LINE = 100;

    @UiField
    ScrollPanel panel;
    @UiField
    FlowPanel   output;
    @UiField
    FlowPanel   mainPanel;
    @UiField(provided = true)
    final RunnerResources res;

    private final RunnerLocalizationConstant locale;
    private final Provider<MessageBuilder>   messageBuilderProvider;
    private final Runner                     runner;

    @Inject
    public ConsoleImpl(RunnerResources resources,
                       RunnerLocalizationConstant locale,
                       Provider<MessageBuilder> messageBuilderProvider,
                       @Nonnull @Assisted Runner runner) {
        this.res = resources;
        this.locale = locale;
        this.messageBuilderProvider = messageBuilderProvider;
        this.runner = runner;

        initWidget(UI_BINDER.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void print(@Nonnull String text) {
        //The message from server can be include a few lines of console
        for (String message : text.split("\n")) {
            if (message.isEmpty()) {
                // don't print empty message
                continue;
            }

            MessageType messageType = MessageType.detect(message);
            MessageBuilder messageBuilder = messageBuilderProvider.get()
                                                                  .message(message)
                                                                  .type(messageType);

            if (DOCKER.equals(messageType) && message.startsWith(DOCKER.getPrefix() + ' ' + ERROR.getPrefix())) {
                messageBuilder.type(ERROR);
            }

            print(messageBuilder.build());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void printInfo(@Nonnull String line) {
        MessageBuilder messageBuilder = messageBuilderProvider.get()
                                                              .type(INFO)
                                                              .message(INFO.getPrefix() + ' ' + line);
        print(messageBuilder.build());
    }

    /** {@inheritDoc} */
    @Override
    public void printError(@Nonnull String line) {
        MessageBuilder messageBuilder = messageBuilderProvider.get()
                                                              .type(ERROR)
                                                              .message(ERROR.getPrefix() + ' ' + line);
        print(messageBuilder.build());
    }

    /** {@inheritDoc} */
    @Override
    public void printWarn(@Nonnull String line) {
        MessageBuilder messageBuilder = messageBuilderProvider.get()
                                                              .type(WARNING)
                                                              .message(WARNING.getPrefix() + ' ' + line);
        print(messageBuilder.build());
    }

    private void print(@Nonnull SafeHtml message) {
        cleanOverHeadLinesIfAny();

        HTML html = new HTML(message);
        html.getElement().getStyle().setPaddingLeft(2, Style.Unit.PX);

        output.add(html);

        scrollBottom();
    }

    private void cleanOverHeadLinesIfAny() {
        if (output.getWidgetCount() < MAX_CONSOLE_LINES) {
            return;
        }

        // remove first 10% of current lines on screen
        for (int i = 0; i < CLEAR_CONSOLE_LINE; i++) {
            output.remove(0);
        }

        Link logLink = runner.getLogUrl();
        if (logLink == null) {
            return;
        }

        String logUrl = logLink.getHref();
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
    public void scrollBottom() {
        panel.getElement().setScrollTop(panel.getElement().getScrollHeight());
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        output.clear();
    }

}