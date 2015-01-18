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
package com.codenvy.ide.ext.runner.client.customenvironment;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.keybinding.KeyBuilder;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.actions.EnvironmentAction;
import com.codenvy.ide.ext.runner.client.inject.factories.EnvironmentActionFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.input.CharCodeWithModifiers;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.RunnerExtension2.GROUP_RUN_WITH_2;

/**
 * Listens for opening/closing a project and adds/removes a corresponding action for executing every custom Docker-script.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public class EnvironmentActionManager implements ProjectActionHandler {

    private final String                                        envFolderPath;
    private final Map<EnvironmentAction, CharCodeWithModifiers> actions2HotKeys;
    private final Map<EnvironmentAction, String>                environments;
    private final EnvironmentActionFactory                      environmentActionFactory;
    private final RunnerLocalizationConstant                    locale;
    private final ActionManager                                 actionManager;
    private final KeyBindingAgent                               keyBindingAgent;
    private final ProjectServiceClient                          projectServiceClient;
    private final DtoUnmarshallerFactory                        dtoUnmarshallerFactory;

    @Inject
    public EnvironmentActionManager(EnvironmentActionFactory environmentActionFactory,
                                    RunnerLocalizationConstant locale,
                                    ActionManager actionManager,
                                    KeyBindingAgent keyBindingAgent,
                                    EventBus eventBus,
                                    ProjectServiceClient projectServiceClient,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                    @Named("envFolderPath") String envFolderPath) {

        this.envFolderPath = envFolderPath;
        this.environmentActionFactory = environmentActionFactory;
        this.locale = locale;
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;

        actions2HotKeys = new HashMap<>();
        environments = new HashMap<>();

        eventBus.addHandler(ProjectActionEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(@Nonnull ProjectActionEvent event) {
        requestCustomEnvironmentsForProject(event.getProject(), new AsyncCallback<Array<String>>() {
            @Override
            public void onSuccess(@Nonnull Array<String> result) {
                for (String environmentName : result.asIterable()) {
                    addActionForEnvironment(environmentName);
                }
            }

            @Override
            public void onFailure(@Nonnull Throwable ignore) {
                Log.error(EnvironmentActionManager.class, ignore.getMessage());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectClosed(@Nonnull ProjectActionEvent event) {
        removeAllActions();
    }

    /**
     * Get list of custom environments for the specified project.
     *
     * @param project
     *         project for which need to get list of environments
     * @param callback
     *         callback to return custom environments
     */
    public void requestCustomEnvironmentsForProject(@Nonnull ProjectDescriptor project,
                                                    @Nonnull final AsyncCallback<Array<String>> callback) {

        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);

        projectServiceClient.getChildren(project.getPath() + '/' + envFolderPath,
                                         new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
                                             @Override
                                             protected void onSuccess(@Nonnull Array<ItemReference> result) {
                                                 Array<String> environmentList = Collections.createArray();

                                                 for (ItemReference item : result.asIterable()) {
                                                     environmentList.add(item.getName());
                                                 }

                                                 callback.onSuccess(environmentList);
                                             }

                                             @Override
                                             protected void onFailure(@Nonnull Throwable caught) {
                                                 callback.onFailure(caught);
                                             }
                                         });
    }

    /**
     * Add action to run the specified custom environment.
     *
     * @param environmentName
     *         name of the custom environment for which need to create action
     */
    public void addActionForEnvironment(@Nonnull String environmentName) {
        EnvironmentAction action = environmentActionFactory.createAction(locale.actionManagerEnvironmentText(environmentName),
                                                                         locale.actionManagerEnvironmentDescription(environmentName),
                                                                         environmentName);

        String actionId = "RunWithEnvironment" + environmentName;

        environments.put(action, environmentName);

        actionManager.registerAction(actionId, action);
        ((DefaultActionGroup)actionManager.getAction(GROUP_RUN_WITH_2)).add(action);

        final int actionNum = actions2HotKeys.size() + 1;
        // Bind hot-key only for the first 10 actions (Ctrl+Alt+0...9)
        if (actionNum > 10) {
            return;
        }

        CharCodeWithModifiers hotKey = new KeyBuilder().action().alt().charCode(actionNum + 47).build();
        keyBindingAgent.getGlobal().addKey(hotKey, actionId);

        actions2HotKeys.put(action, hotKey);
    }

    /**
     * Remove action which corresponds to the specified environment.
     *
     * @param environmentName
     *         name of environment for which need to remove action
     */
    public void removeActionForEnvironment(@Nonnull String environmentName) {
        for (EnvironmentAction action : actions2HotKeys.keySet()) {
            if (environmentName.equals(environments.get(action))) {
                removeAction(action);
                break;
            }
        }
    }

    private void removeAction(@Nonnull EnvironmentAction action) {
        DefaultActionGroup customImagesGroup = (DefaultActionGroup)actionManager.getAction(GROUP_RUN_WITH_2);
        customImagesGroup.remove(action);

        String actionId = actionManager.getId(action);
        actionManager.unregisterAction(actionId);

        // unbind hot-key if action has it
        CharCodeWithModifiers hotKey = actions2HotKeys.get(action);

        if (hotKey != null) {
            keyBindingAgent.getGlobal().removeKey(hotKey, actionId);
        }

        actions2HotKeys.remove(action);
        environments.remove(action);
    }

    private void removeAllActions() {
        for (Map.Entry<EnvironmentAction, CharCodeWithModifiers> entry : actions2HotKeys.entrySet()) {
            removeAction(entry.getKey());
        }
    }

}