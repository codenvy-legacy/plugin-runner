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
package com.codenvy.ide.ext.runner.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Class contains references to resources which need to correct displaying of runner plugin.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public interface RunnerResources extends ClientBundle {

    interface RunnerCss extends CssResource {

        String fullSize();

        String activeTab();
    }

    @Source("runner.css")
    RunnerCss runnerCss();

    @Source("images/run.svg")
    SVGResource runAppImage();

    @Source("images/edit-custom-environments.svg")
    SVGResource editEnvironmentsImage();

    @Source("images/run-with.svg")
    SVGResource runWithImage();
}
