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
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.runneractions.RunnerAction;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.ui.dialogs.InputCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * Contains business logic which allows add, edit or remove custom environments (dockers) which need to run project.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class CustomEnvironmentPresenter implements CustomEnvironmentView.ActionDelegate {

    private static final String DOCKER_SCRIPT_NAME          = "/Dockerfile";
    private static final String ENVIRONMENT_NOT_EXIST_ERROR = "Path 'a/.codenvy/runners/environments' doesn't exist.";

    private final String                     envFolderPath;
    private final NotificationManager        notificationManager;
    private final RunnerLocalizationConstant locale;
    private final DialogFactory              dialogFactory;
    private final ProjectServiceClient       projectServiceClient;
    private final DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private final EventBus                   eventBus;
    private final AppContext                 appContext;
    private final EnvironmentActionManager   environmentActionManager;
    private final CustomEnvironmentView      view;
    private final EnvironmentNameValidator   nameValidator;
    private final RunnerResources            resources;
    private final RunnerAction               getEnvironmentsAction;

    private String         selectedEnvironmentName;
    private CurrentProject currentProject;

    @Inject
    protected CustomEnvironmentPresenter(CustomEnvironmentView view,
                                         EventBus eventBus,
                                         AppContext appContext,
                                         EnvironmentActionManager environmentActionManager,
                                         ProjectServiceClient projectServiceClient,
                                         EnvironmentNameValidator nameValidator,
                                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                         NotificationManager notificationManager,
                                         RunnerLocalizationConstant locale,
                                         DialogFactory dialogFactory,
                                         RunnerResources resources,
                                         RunnerActionFactory actionFactory,
                                         @Named("envFolderPath") String envFolderPath) {
        this.view = view;
        this.view.setDelegate(this);

        this.envFolderPath = envFolderPath;
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.environmentActionManager = environmentActionManager;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.dialogFactory = dialogFactory;
        this.nameValidator = nameValidator;
        this.resources = resources;
        this.getEnvironmentsAction = actionFactory.createGetEnvironments();

        updateView();
    }

    private void updateView() {
        boolean isEnvironmentSelected = selectedEnvironmentName != null;

        view.setEditButtonEnabled(isEnvironmentSelected);
        view.setRemoveButtonEnabled(isEnvironmentSelected);
    }


    /** {@inheritDoc} */
    @Override
    public void onEnvironmentSelected(@Nonnull String environmentName) {
        selectedEnvironmentName = environmentName;
        updateView();
    }

    /** {@inheritDoc} */
    @Override
    public void onAddBtnClicked() {
        dialogFactory.createInputDialog(locale.addEnvironmentDialogTitle(),
                                        locale.addEnvironmentDialogLabel(),
                                        new InputCallback() {
                                            @Override
                                            public void accepted(String value) {
                                                createEnvironment(value);

                                                getEnvironmentsAction.perform();
                                            }
                                        }, null).withValidator(nameValidator).show();
    }

    private void createEnvironment(@Nonnull String name) {
        String path = currentProject.getProjectDescription().getPath() + '/' + envFolderPath + '/' + name;

        Unmarshallable<ItemReference> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ItemReference.class);

        projectServiceClient.createFolder(path, new AsyncRequestCallback<ItemReference>(unmarshaller) {
            @Override
            protected void onSuccess(@Nonnull ItemReference result) {
                String environmentName = result.getName();

                createScriptFilesForEnvironment(environmentName);

                environmentActionManager.addActionForEnvironment(environmentName);
                view.closeDialog();
            }

            @Override
            protected void onFailure(@Nonnull Throwable exception) {
                notificationManager.showError(exception.getMessage());
            }
        });
    }

    private void createScriptFilesForEnvironment(@Nonnull final String environmentName) {
        String path = currentProject.getProjectDescription().getPath() + '/' + envFolderPath + '/';

        projectServiceClient.createFile(path,
                                        environmentName + DOCKER_SCRIPT_NAME,
                                        resources.dockerTemplate().getText(),
                                        null,
                                        new AsyncRequestCallback<ItemReference>() {
                                            @Override
                                            protected void onSuccess(@Nonnull ItemReference result) {
                                                editEnvironment(environmentName);
                                            }

                                            @Override
                                            protected void onFailure(@Nonnull Throwable ignore) {
                                                Log.error(CustomEnvironmentPresenter.class, ignore.getMessage());
                                            }
                                        });
    }

    private void editEnvironment(@Nonnull final String environmentName) {
        String path = currentProject.getProjectDescription().getPath() + '/' + envFolderPath + '/' + environmentName;

        Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);

        projectServiceClient.getChildren(path, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(@Nonnull Array<ItemReference> result) {
                for (ItemReference item : result.asIterable()) {
                    createAndOpenFile(item, environmentName);
                }
            }

            @Override
            protected void onFailure(@Nonnull Throwable ignore) {
                Log.error(CustomEnvironmentPresenter.class, ignore.getMessage());
            }
        });
    }

    private void createAndOpenFile(@Nonnull ItemReference item, @Nonnull String environmentName) {
        ProjectNode project = new ProjectNode(null,
                                              currentProject.getProjectDescription(),
                                              null,
                                              eventBus,
                                              projectServiceClient,
                                              dtoUnmarshallerFactory);

        eventBus.fireEvent(new FileEvent(new EnvironmentScript(project,
                                                               item,
                                                               currentProject.getCurrentTree(),
                                                               eventBus,
                                                               projectServiceClient,
                                                               dtoUnmarshallerFactory,
                                                               environmentName), FileEvent.FileOperation.OPEN));
    }

    /** Show dialog window to add,edit or remove custom environments. */
    public void showDialog() {
        currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        selectedEnvironmentName = null;

        updateView();
        refreshEnvironmentsList();

        view.showDialog();
    }

    private void refreshEnvironmentsList() {
        view.setRowData(Collections.<String>createArray());

        environmentActionManager
                .requestCustomEnvironmentsForProject(currentProject.getProjectDescription(), new AsyncCallback<Array<String>>() {
                    @Override
                    public void onSuccess(Array<String> result) {
                        if (result.isEmpty()) {
                            return;
                        }

                        view.setRowData(result);
                        view.selectEnvironment(result.get(0));
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        if (caught.getMessage().contains(ENVIRONMENT_NOT_EXIST_ERROR)) {
                            createEnvironmentsFolder();
                        }

                        notificationManager.showError(locale.retrieveImagesFailed(caught.getMessage()));
                    }
                });
    }

    private void createEnvironmentsFolder() {
        String folderPath = currentProject.getProjectDescription().getPath() + '/' + envFolderPath;

        projectServiceClient.createFolder(folderPath, new AsyncRequestCallback<ItemReference>() {
            @Override
            protected void onSuccess(@Nonnull ItemReference result) {
            }

            @Override
            protected void onFailure(@Nonnull Throwable exception) {
                Log.error(CustomEnvironmentPresenter.class, exception.getMessage());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoveBtnClicked() {
        dialogFactory.createConfirmDialog(locale.removeEnvironment(),
                                          locale.removeEnvironmentMessage(selectedEnvironmentName),
                                          new ConfirmCallback() {
                                              @Override
                                              public void accepted() {
                                                  removeSelectedEnvironment();
                                              }
                                          }, null).show();
    }

    private void removeSelectedEnvironment() {
        String path = currentProject.getProjectDescription().getPath() + '/' + envFolderPath + '/' + selectedEnvironmentName;

        projectServiceClient.delete(path, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(@Nonnull Void result) {
                environmentActionManager.removeActionForEnvironment(selectedEnvironmentName);
                selectedEnvironmentName = null;
                updateView();
                refreshEnvironmentsList();

                getEnvironmentsAction.perform();
            }

            @Override
            protected void onFailure(@Nonnull Throwable exception) {
                notificationManager.showError(exception.getMessage());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onEditBtnClicked() {
        editEnvironment(selectedEnvironmentName);
        view.closeDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseBtnClicked() {
        view.closeDialog();
    }

}