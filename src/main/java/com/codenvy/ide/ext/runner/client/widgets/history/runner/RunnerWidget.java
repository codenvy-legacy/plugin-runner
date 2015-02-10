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
package com.codenvy.ide.ext.runner.client.widgets.history.runner;

import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.selection.SelectionManager;
import com.codenvy.ide.ext.runner.client.widgets.general.GeneralWidget;
import com.codenvy.ide.ext.runner.client.widgets.general.RunnerItems;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;

/**
 * The class contains methods which allow change view representation of runner.
 *
 * @author Dmitry Shnurenko
 */
public class RunnerWidget implements RunnerItems<Runner> {

    private final GeneralWidget   generalWidget;
    private final RunnerResources resources;

    private final SVGImage inProgress;
    private final SVGImage inQueue;
    private final SVGImage failed;
    private final SVGImage timeout;
    private final SVGImage done;
    private final SVGImage stopped;

    private Runner runner;

    @Inject
    public RunnerWidget(GeneralWidget generalWidget, RunnerResources resources, final SelectionManager selectionManager) {
        this.generalWidget = generalWidget;
        this.resources = resources;

        inProgress = new SVGImage(resources.runnerInProgressImage());
        inQueue = new SVGImage(resources.runnerInQueueImage());
        failed = new SVGImage(resources.runnerFailedImage());
        timeout = new SVGImage(resources.runnerTimeoutImage());
        done = new SVGImage(resources.runnerDoneImage());
        stopped = new SVGImage(resources.runnerDoneImage());

        generalWidget.setDelegate(new GeneralWidget.ActionDelegate() {
            @Override
            public void onWidgetClicked() {
                selectionManager.setRunner(runner);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        generalWidget.select();
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        generalWidget.unSelect();
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        this.runner = runner;

        changeRunnerStatusIcon();

        generalWidget.setName(runner.getTitle());
        generalWidget.setDescription(runner.getRAM() + "MB");
        generalWidget.setStartTime(runner.getCreationTime());
    }

    private void changeRunnerStatusIcon() {
        switch (runner.getStatus()) {
            case IN_PROGRESS:
                inProgress.addClassNameBaseVal(resources.runnerCss().blueColor());
                generalWidget.setImage(inProgress);
                break;

            case IN_QUEUE:
                inQueue.addClassNameBaseVal(resources.runnerCss().yellowColor());
                generalWidget.setImage(inQueue);
                break;

            case FAILED:
                failed.addClassNameBaseVal(resources.runnerCss().redColor());
                generalWidget.setImage(failed);
                break;

            case TIMEOUT:
                timeout.addClassNameBaseVal(resources.runnerCss().whiteColor());
                generalWidget.setImage(timeout);
                break;

            case STOPPED:
                stopped.addClassNameBaseVal(resources.runnerCss().redColor());
                generalWidget.setImage(stopped);
                break;

            case DONE:
                done.addClassNameBaseVal(resources.runnerCss().greenColor());
                generalWidget.setImage(done);
                break;

            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return generalWidget.asWidget();
    }
}