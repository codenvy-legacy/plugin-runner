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

import com.google.gwt.i18n.client.Messages;
import com.google.inject.Singleton;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Contains all names of graphical elements needed for runner plugin.
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 * @author Dmitry Shnurenko
 */
@Singleton
public interface RunnerLocalizationConstant extends Messages {

    @Key("unknown.error.message")
    String unknownErrorMessage();

    String environmentCooking(@Nonnull String projectName);

    String applicationStarting(@Nonnull String projectName);

    String applicationStopped(@Nonnull String projectName);

    String applicationFailed(@Nonnull String projectName);

    String applicationCanceled(@Nonnull String projectName);

    String applicationMaybeStarted(@Nonnull String projectName);

    String applicationStarted(@Nonnull String projectName);

    String startApplicationFailed(@Nonnull String projectName);

    String applicationLogsFailed();

    @Key("runner.label.application.info")
    String runnerLabelApplicationInfo();

    @Key("runner.label.timeout.info")
    String runnerLabelTimeoutInfo();

    @Key("messages.totalLessRequiredMemory")
    String messagesTotalLessRequiredMemory(@Nonnegative int totalRAM, @Nonnegative int requestedRAM);

    @Key("messages.availableLessRequiredMemory")
    String messagesAvailableLessRequiredMemory(@Nonnegative int totalRAM, @Nonnegative int usedRAM, @Nonnegative int requestedRAM);

    @Key("messages.totalLessOverrideMemory")
    String messagesTotalLessOverrideMemory(@Nonnegative int overrideRAM, @Nonnegative int totalRAM);

    @Key("messages.availableLessOverrideMemory")
    String messagesAvailableLessOverrideMemory();

    @Key("messages.overrideMemory")
    String messagesOverrideMemory();

    @Key("messages.overrideLessRequiredMemory")
    String messagesOverrideLessRequiredMemory(@Nonnegative int overrideRAM, @Nonnegative int requestedRAM);

    @Key("action.project.running.now")
    String projectRunningNow(@Nonnull String project);

    @Key("titles.warning")
    String titlesWarning();

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

    @Key("get.resources.failed")
    String getResourcesFailed();

    String fullLogTraceConsoleLink();

    @Key("custom.environments.title")
    String customEnvironmentsTitle();

    @Key("custom.environments.button.add")
    String customEnvironmentsButtonAdd();

    @Key("custom.environments.button.Remove")
    String customEnvironmentsButtonRemove();

    @Key("custom.environments.button.close")
    String customEnvironmentsButtonClose();

    @Key("custom.environments.button.edit")
    String customEnvironmentsButtonEdit();

    @Key("custom.environments.empty.table")
    String customEnvironmentsEmptyTable();

    @Key("action.manager.environment.text")
    String actionManagerEnvironmentText(@Nonnull String text);

    @Key("action.manager.environment.description")
    String actionManagerEnvironmentDescription(@Nonnull String description);

    @Key("validator.space.not.allowed")
    String validatorSpaceNotAllowed();

    @Key("validator.dots.not.allowed")
    String validatorDotsNotAllowed();

    @Key("validator.name.invalid")
    String validatorNameInvalid();

    @Key("add.environment.dialog.title")
    String addEnvironmentDialogTitle();

    @Key("add.environment.dialog.label")
    String addEnvironmentDialogLabel();

    @Key("retrieve.images.failed")
    String retrieveImagesFailed(@Nonnull String error);

    @Key("remove.environment")
    String removeEnvironment();

    @Key("remove.environment.message")
    String removeEnvironmentMessage(@Nonnull String environmentName);

    @Key("custom.runner.title")
    String customRunnerTitle();

    @Key("custom.runner.get.environment.failed")
    String customRunnerGetEnvironmentFailed();

    @Key("custom.runner.memory.runner")
    String customRunnerMemoryRunner();

    @Key("custom.runner.memory.total")
    String customRunnerMemoryTotal();

    @Key("custom.runner.memory.available")
    String customRunnerMemoryAvailable();

    @Key("custom.runner.skip.build")
    String customRunnerSkipBuild();

    @Key("custom.runner.environment")
    String customRunnerEnvironment();

    @Key("custom.runner.button.cancel")
    String customRunnerButtonCancel();

    @Key("message.fail.remember.options")
    String messageFailRememberOptions();

    @Key("messages.un.multiple.ram.value")
    String ramSizeMustBeMultipleOf(@Nonnegative int multiple);

    @Key("messages.incorrect.value")
    String messagesIncorrectValue();

    @Key("messages.total.ram.less.custom")
    String messagesTotalRamLessCustom(@Nonnegative int totalRam, @Nonnegative int customRam);

    @Key("messages.available.ram.less.custom")
    String messagesAvailableRamLessCustom(@Nonnegative int overrideRam, @Nonnegative int total, @Nonnegative int used);

    String runnerNotReady();

    @Key("runners.panel.title")
    String runnersPanelTitle();

    @Key("tooltip.header")
    String tooltipHeader();

    @Key("tooltip.body.started")
    String tooltipBodyStarted();

    @Key("tooltip.body.finished")
    String tooltipBodyFinished();

    @Key("tooltip.body.timeout")
    String tooltipBodyTimeout();

    @Key("tooltip.body.time.active")
    String tooltipBodyTimeActive();

    @Key("tooltip.body.ram")
    String tooltipBodyRam();

    @Key("runner.tab.history")
    String runnerTabHistory();

    @Key("runner.tab.properties")
    String runnerTabProperties();

    @Key("runner.tab.templates")
    String runnerTabTemplates();

    @Key("url.app.waiting.for.boot")
    String uplAppWaitingForBoot();

    @Key("url.app.runner.stopped")
    String urlAppRunnerStopped();

    @Key("url.app.running")
    String urlAppRunning();

    @Key("tooltip.runner.panel")
    String tooltipRunnerPanel();

    @Key("template.scope")
    String templateScope();

    @Key("template.type")
    String templateType();

    @Key("template.type.all")
    String templateTypeAll();

    @Key("properties.name")
    String propertiesName();

    @Key("properties.ram")
    String propertiesRam();

    @Key("properties.scope")
    String propertiesScope();

    @Key("properties.type")
    String propertiesType();

    @Key("properties.boot")
    String propertiesBoot();

    @Key("properties.shutdown")
    String propertiesShutdown();

    @Key("properties.dockerfile")
    String propertiesDockerfile();

    @Key("properties.button.save")
    String propertiesButtonSave();

    @Key("properties.button.delete")
    String propertiesButtonDelete();

    @Key("properties.button.cancel")
    String propertiesButtonCancel();

    @Key("runner.title")
    String runnerTitle();

    String editorNotReady();

    @Key("tooltip.run.button")
    String tooltipRunButton();

    @Key("tooltip.stop.button")
    String tooltipStopButton();

    @Key("tooltip.clean.button")
    String tooltipCleanButton();

    @Key("tooltip.docker.button")
    String tooltipDockerButton();

    @Key("tooltip.scope.project")
    String tooltipScopeProject();

    @Key("tooltip.scope.system")
    String tooltipScopeSystem();
}