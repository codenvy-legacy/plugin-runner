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

import com.google.gwt.i18n.client.Messages;
import com.google.inject.Singleton;

/**
 * Contains all names of graphical elements needed for runner plugin.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public interface RunnerLocalizationConstant extends Messages {

    @Key("runner.label.application.info")
    String runnerLabelApplicationInfo();

    @Key("runner.label.timeout.info")
    String runnerLabelTimeoutInfo();

    @Key("runner.tab.console")
    String runnerTabConsole();

    @Key("runner.tab.terminal")
    String runnerTabTerminal();

    @Key("action.run")
    String actionRun();

    @Key("action.custom.run")
    String actionCustomRun();

    @Key("action.run.description")
    String actionRunDescription();

    @Key("action.edit.run")
    String actionEditRun();

    @Key("action.edit.run.description")
    String actionEditRunDescription();

    @Key("action.group.run.with")
    String actionGroupRunWith();

    @Key("action.group.run.with.description")
    String actionGroupRunWithDescription();

    @Key("action.custom.run.description")
    String actionCustomRunDescription();
}
