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
package com.codenvy.ide.ext.runner.client.widgets.runner;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Class provides view representation of runner.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class RunnerWidgetImpl extends Composite implements RunnerWidget, ClickHandler {

    interface RunnerViewImplUiBinder extends UiBinder<Widget, RunnerWidgetImpl> {
    }

    public static final  DateTimeFormat         DATE_TIME_FORMAT = DateTimeFormat.getFormat("dd-MM-yy HH:mm");
    private static final RunnerViewImplUiBinder UI_BINDER        = GWT.create(RunnerViewImplUiBinder.class);

    @UiField
    Label             runnerName;
    @UiField
    Label             ram;
    @UiField
    Label             startTime;
    @UiField
    SimpleLayoutPanel image;
    @UiField(provided = true)
    final RunnerResources resources;

    private final SVGImage inProgress;
    private final SVGImage inQueue;
    private final SVGImage failed;
    private final SVGImage timeout;
    private final SVGImage done;
    private final SVGImage stopped;

    private ActionDelegate delegate;
    private Runner         runner;

    @Inject
    public RunnerWidgetImpl(RunnerResources resources) {
        this.resources = resources;

        initWidget(UI_BINDER.createAndBindUi(this));

        addDomHandler(this, ClickEvent.getType());

        inProgress = new SVGImage(resources.runnerInProgressImage());
        inQueue = new SVGImage(resources.runnerInQueueImage());
        failed = new SVGImage(resources.runnerFailedImage());
        timeout = new SVGImage(resources.runnerTimeoutImage());
        done = new SVGImage(resources.runnerDoneImage());
        stopped = new SVGImage(resources.runnerDoneImage());
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        removeStyleName(resources.runnerCss().runnerShadow());
        addStyleName(resources.runnerCss().runnerWidgetBorders());
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        addStyleName(resources.runnerCss().runnerShadow());
        removeStyleName(resources.runnerCss().runnerWidgetBorders());
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        this.runner = runner;

        changeRunnerStatusIcon();

        runnerName.setText(runner.getTitle());

        ram.setText(runner.getRAM() + "MB");
        startTime.setText(DATE_TIME_FORMAT.format(new Date(runner.getCreationTime())));
    }

    private void changeRunnerStatusIcon() {
        switch (runner.getStatus()) {
            case IN_PROGRESS:
                inProgress.addClassNameBaseVal(resources.runnerCss().blueColor());
                image.getElement().setInnerHTML(inProgress.toString());
                break;

            case IN_QUEUE:
                inQueue.addClassNameBaseVal(resources.runnerCss().yellowColor());
                image.getElement().setInnerHTML(inQueue.toString());
                break;

            case FAILED:
                failed.addClassNameBaseVal(resources.runnerCss().redColor());
                image.getElement().setInnerHTML(failed.toString());
                break;

            case TIMEOUT:
                timeout.addClassNameBaseVal(resources.runnerCss().whiteColor());
                image.getElement().setInnerHTML(timeout.toString());
                break;

            case STOPPED:
                stopped.addClassNameBaseVal(resources.runnerCss().redColor());
                image.getElement().setInnerHTML(stopped.toString());
                break;

            case DONE:
                done.addClassNameBaseVal(resources.runnerCss().greenColor());
                image.getElement().setInnerHTML(done.toString());
                break;

            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void onClick(ClickEvent event) {
        delegate.onRunnerSelected(runner);
    }

}