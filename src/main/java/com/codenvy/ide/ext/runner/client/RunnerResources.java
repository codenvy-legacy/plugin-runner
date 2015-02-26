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
package com.codenvy.ide.ext.runner.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Class contains references to resources which need to correct displaying of runner plugin.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public interface RunnerResources extends ClientBundle {

    @Source("images/runnerwidget/done.svg")
    SVGResource runnerDoneImage();

    @Source("images/runnerwidget/failed.svg")
    SVGResource runnerFailedImage();

    @Source("images/runnerwidget/running.svg")
    SVGResource runnerInProgressImage();

    @Source("images/runnerwidget/in-queue.svg")
    SVGResource runnerInQueueImage();

    @Source("images/runnerwidget/timeout.svg")
    SVGResource runnerTimeoutImage();

    @Source("images/templates/scopeP.svg")
    SVGResource scopeProject();

    @Source("images/templates/scopeS.svg")
    SVGResource scopeSystem();

    @Source("images/mainwidget/run.png")
    ImageResource runButton();

    @Source("images/mainwidget/docker.png")
    ImageResource dockerButton();

    @Source("images/mainwidget/clean.png")
    ImageResource cleanButton();

    @Source("images/mainwidget/stop.png")
    ImageResource stopButton();

    @Source("images/mainwidget/more-icon.png")
    ImageResource moreIcon();

    @Source("images/templates/scope-user.png")
    ImageResource scopeUser();

    @Source("images/templates/scope-workspace.png")
    ImageResource scopeWorkspace();

    @Source("images/run.svg")
    SVGResource runAppImage();

    @Source("images/edit-custom-environments.svg")
    SVGResource editEnvironmentsImage();

    @Source("images/run-with.svg")
    SVGResource runWithImage();

    @Source("images/addEnvironment.svg")
    SVGResource addEnvironmentImage();

    @Source("images/editEnvironment.svg")
    SVGResource editEnvironmentImage();

    @Source("images/removeEnvironment.svg")
    SVGResource removeEnvironmentImage();

    @Source("images/environment.svg")
    SVGResource environmentImage();

    @Source("images/console/arrow-bottom.svg")
    SVGResource arrowBottom();

    @Source("images/console/wrap-text.svg")
    SVGResource wrapText();

    @Source("images/console/erase.svg")
    SVGResource erase();

    @Source("docker-template.txt")
    TextResource dockerTemplate();

    @Source("runner.css")
    RunnerCss runnerCss();

    interface RunnerCss extends CssResource {

        String fullSize();

        String consoleBackground();

        String logLink();

        String blueButton();

        String treeIcon();

        String runButton();

        String cancelButton();

        String fontStyle();

        String runnerWidgetBorders();

        String activeTab();

        String activeTabText();

        String notActiveTabText();

        String greenColor();

        String redColor();

        String yellowColor();

        String whiteColor();

        String fontSizeEight();

        String fontSizeTen();

        String fontSizeEleven();

        String blueColor();

        String opacityButton();

        String splitter();

        String runnerShadow();

        String cursor();

        String typeButton();

        String unAvailableMessage();

        String propertiesFont();

        String environmentSvg();

        String console();

        String consoleButtonShadow();

        String activeConsoleButton();

        String wrappedText();
    }

}
