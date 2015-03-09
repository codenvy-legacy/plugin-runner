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
package org.eclipse.che.ide.ext.runner.client.tabs.history.runner;

import org.eclipse.che.ide.ext.runner.client.RunnerResources;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.selection.SelectionManager;
import org.eclipse.che.ide.ext.runner.client.tabs.common.item.ItemWidget;
import org.eclipse.che.ide.ext.runner.client.tabs.common.item.RunnerItems;
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

    private final ItemWidget      itemWidget;
    private final RunnerResources resources;

    private final SVGImage inProgress;
    private final SVGImage inQueue;
    private final SVGImage failed;
    private final SVGImage timeout;
    private final SVGImage done;
    private final SVGImage stopped;

    private Runner runner;

    @Inject
    public RunnerWidget(ItemWidget itemWidget, RunnerResources resources, final SelectionManager selectionManager) {
        this.itemWidget = itemWidget;
        this.resources = resources;

        inProgress = new SVGImage(resources.runnerInProgressImage());
        inQueue = new SVGImage(resources.runnerInQueueImage());
        failed = new SVGImage(resources.runnerFailedImage());
        timeout = new SVGImage(resources.runnerTimeoutImage());
        done = new SVGImage(resources.runnerDoneImage());
        stopped = new SVGImage(resources.runnerDoneImage());

        itemWidget.setDelegate(new ItemWidget.ActionDelegate() {
            @Override
            public void onWidgetClicked() {
                selectionManager.setRunner(runner);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void select() {
        itemWidget.select();
    }

    /** {@inheritDoc} */
    @Override
    public void unSelect() {
        itemWidget.unSelect();
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        this.runner = runner;

        changeRunnerStatusIcon();

        itemWidget.setName(runner.getTitle());
        itemWidget.setDescription(runner.getRAM() + "MB");
        itemWidget.setStartTime(runner.getCreationTime());
    }

    private void changeRunnerStatusIcon() {
        switch (runner.getStatus()) {
            case IN_PROGRESS:
                inProgress.addClassNameBaseVal(resources.runnerCss().blueColor());
                itemWidget.setImage(inProgress);
                break;

            case IN_QUEUE:
                inQueue.addClassNameBaseVal(resources.runnerCss().yellowColor());
                itemWidget.setImage(inQueue);
                break;

            case FAILED:
                failed.addClassNameBaseVal(resources.runnerCss().redColor());
                itemWidget.setImage(failed);
                break;

            case TIMEOUT:
                timeout.addClassNameBaseVal(resources.runnerCss().whiteColor());
                itemWidget.setImage(timeout);
                break;

            case STOPPED:
                stopped.addClassNameBaseVal(resources.runnerCss().redColor());
                itemWidget.setImage(stopped);
                break;

            case DONE:
                done.addClassNameBaseVal(resources.runnerCss().greenColor());
                itemWidget.setImage(done);
                break;

            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return itemWidget.asWidget();
    }
}