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

    @Source("images/templates/scopeProject.svg")
    SVGResource scopeProject();

    @Source("images/templates/scopeSystem.svg")
    SVGResource scopeSystem();

    @Source("images/mainwidget/run.svg")
    SVGResource runAppImage();

    @Source("images/mainwidget/re-run.svg")
    SVGResource reRunAppImage();

    @Source("images/mainwidget/stop.svg")
    SVGResource stopButton();

    @Source("images/mainwidget/more-icon.png")
    ImageResource moreIcon();

    @Source("images/console/arrow-bottom.svg")
    SVGResource arrowBottom();

    @Source("images/console/wrap-text.svg")
    SVGResource wrapText();

    @Source("images/console/erase.svg")
    SVGResource erase();

    // TODO unused template. May be need to remove it
    @Source("docker-template.txt")
    TextResource dockerTemplate();

    @Source("runner.css")
    RunnerCss runnerCss();

    interface RunnerCss extends CssResource {

        String fullSize();

        String consoleBackground();

        String logLink();

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

        String runnersAction();

        String runnerFontStyle();

        String mainButtonIcon();
    }

}
