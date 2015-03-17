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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorRegistry;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.project.tree.generic.ProjectNode;
import org.eclipse.che.ide.api.texteditor.HandlesUndoRedo;
import org.eclipse.che.ide.api.texteditor.UndoableEditor;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import org.eclipse.che.ide.ext.runner.client.callbacks.FailureCallback;
import org.eclipse.che.ide.ext.runner.client.callbacks.SuccessCallback;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import org.eclipse.che.ide.ext.runner.client.tabs.container.TabContainer;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.PropertiesPanelPresenter;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.PropertiesPanelView;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.EnvironmentScript;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.docker.DockerFile;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.docker.DockerFileFactory;
import org.eclipse.che.ide.ext.runner.client.util.NameGenerator;
import org.eclipse.che.ide.ext.runner.client.util.annotations.LeftPanel;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.loging.Log;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.che.ide.ext.runner.client.models.EnvironmentImpl.ROOT_FOLDER;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;

/**
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class PropertiesEnvironmentPanel extends PropertiesPanelPresenter {

    private       Environment                                environment;
    private final EditorRegistry                             editorRegistry;
    private final FileTypeRegistry                           fileTypeRegistry;
    private final DockerFileFactory                          dockerFileFactory;
    private final ProjectServiceClient                       projectService;
    private final EventBus                                   eventBus;
    private final RunnerLocalizationConstant                 locale;
    private final GetProjectEnvironmentsAction               projectEnvironmentsAction;
    private final NotificationManager                        notificationManager;
    private final DtoUnmarshallerFactory                     unmarshallerFactory;
    private final AsyncCallbackBuilder<ItemReference>        asyncCallbackBuilder;
    private final AsyncCallbackBuilder<Array<ItemReference>> asyncArrayCallbackBuilder;
    private final AsyncCallbackBuilder<Void>                 voidAsyncCallbackBuilder;
    private final DialogFactory                              dialogFactory;
    private final TabContainer                               tabContainer;

    private final List<RemovePanelListener> listeners;//Todo identifications access checks

    @AssistedInject
    public PropertiesEnvironmentPanel(final PropertiesPanelView view,
                                      @Nonnull final EditorRegistry editorRegistry,
                                      @Nonnull final FileTypeRegistry fileTypeRegistry,
                                      final DockerFileFactory dockerFileFactory,
                                      final ProjectServiceClient projectService,
                                      EventBus eventBus,
                                      AppContext appContext,
                                      DialogFactory dialogFactory,
                                      RunnerLocalizationConstant locale,
                                      GetProjectEnvironmentsAction projectEnvironmentsAction,
                                      NotificationManager notificationManager,
                                      DtoUnmarshallerFactory unmarshallerFactory,
                                      AsyncCallbackBuilder<ItemReference> asyncCallbackBuilder,
                                      AsyncCallbackBuilder<Array<ItemReference>> asyncArrayCallbackBuilder,
                                      AsyncCallbackBuilder<Void> voidAsyncCallbackBuilder,
                                      @LeftPanel TabContainer tabContainer,
                                      @Assisted @Nonnull final Environment environment) {
        super(view, appContext, environment.getScope());

        this.editorRegistry = editorRegistry;
        this.fileTypeRegistry = fileTypeRegistry;
        this.dockerFileFactory = dockerFileFactory;
        this.projectService = projectService;
        this.eventBus = eventBus;
        this.environment = environment;
        this.locale = locale;
        this.projectEnvironmentsAction = projectEnvironmentsAction;
        this.notificationManager = notificationManager;
        this.unmarshallerFactory = unmarshallerFactory;
        this.asyncCallbackBuilder = asyncCallbackBuilder;
        this.asyncArrayCallbackBuilder = asyncArrayCallbackBuilder;
        this.voidAsyncCallbackBuilder = voidAsyncCallbackBuilder;
        this.tabContainer = tabContainer;

        this.dialogFactory = dialogFactory;

        boolean isProjectScope = PROJECT.equals(environment.getScope());

        this.view.setEnableNameProperty(isProjectScope);
        this.view.setEnableRamProperty(isProjectScope);
        this.view.setEnableBootProperty(false);
        this.view.setEnableShutdownProperty(false);
        this.view.setEnableScopeProperty(false);

        this.view.setVisibleSaveButton(isProjectScope);
        this.view.setVisibleDeleteButton(isProjectScope);
        this.view.setVisibleCancelButton(isProjectScope);

        this.listeners = new ArrayList<>();

        if (isProjectScope) {
            getProjectEnvironmentDocker();
        } else {
            getSystemEnvironmentDocker();
        }
    }

    private void getProjectEnvironmentDocker() {
        Unmarshallable<Array<ItemReference>> unmarshaller = unmarshallerFactory.newArrayUnmarshaller(ItemReference.class);

        AsyncRequestCallback<Array<ItemReference>> arrayAsyncCallback =
                asyncArrayCallbackBuilder.unmarshaller(unmarshaller)
                                         .success(new SuccessCallback<Array<ItemReference>>() {
                                             @Override
                                             public void onSuccess(Array<ItemReference> result) {
                                                 for (ItemReference item : result.asIterable()) {
                                                     ProjectNode project = new ProjectNode(null,
                                                                                           currentProject.getProjectDescription(),
                                                                                           null,
                                                                                           eventBus,
                                                                                           projectService,
                                                                                           unmarshallerFactory);

                                                     FileNode file = new EnvironmentScript(project,
                                                                                           item,
                                                                                           currentProject.getCurrentTree(),
                                                                                           eventBus,
                                                                                           projectService,
                                                                                           unmarshallerFactory,
                                                                                           environment.getName());

                                                     initializeEditor(file, editorRegistry, fileTypeRegistry);
                                                 }
                                             }
                                         })
                                         .failure(new FailureCallback() {
                                             @Override
                                             public void onFailure(@Nonnull Throwable exception) {
                                                 Log.error(getClass(), exception.getMessage());
                                             }
                                         })
                                         .build();

        projectService.getChildren(environment.getPath(), arrayAsyncCallback);

    }

    private void getSystemEnvironmentDocker() {
        DockerFile file = dockerFileFactory.newInstance(environment.getPath());
        initializeEditor(file, editorRegistry, fileTypeRegistry);
    }

    /** {@inheritDoc} */
    @Override
    public void onCopyButtonClicked() {
        final String fileName = NameGenerator.generate();
        String path = currentProject.getProjectDescription().getPath() + ROOT_FOLDER + fileName;

        AsyncRequestCallback<ItemReference> callback = asyncCallbackBuilder.unmarshaller(ItemReference.class)
                                                                           .success(new SuccessCallback<ItemReference>() {
                                                                               @Override
                                                                               public void onSuccess(ItemReference result) {
                                                                                   getEditorContent(fileName);
                                                                               }
                                                                           })
                                                                           .failure(new FailureCallback() {
                                                                               @Override
                                                                               public void onFailure(@Nonnull Throwable reason) {
                                                                                   notificationManager.showError(reason.getMessage());
                                                                               }
                                                                           })
                                                                           .build();

        projectService.createFolder(path, callback);
    }

    private void getEditorContent(@Nonnull final String fileName) {
        editor.getEditorInput().getFile().getContent(new AsyncCallback<String>() {
            @Override
            public void onSuccess(String content) {
                createFile(content, fileName);
            }

            @Override
            public void onFailure(Throwable throwable) {
                notificationManager.showError(throwable.getMessage());
            }
        });
    }

    private void createFile(@Nonnull String content, @Nonnull String fileName) {
        String path = currentProject.getProjectDescription().getPath() + ROOT_FOLDER;

        AsyncRequestCallback<ItemReference> callback =
                asyncCallbackBuilder.unmarshaller(ItemReference.class)
                                    .success(new SuccessCallback<ItemReference>() {
                                        @Override
                                        public void onSuccess(ItemReference result) {
                                            setEnableSaveCancelDeleteBtn(false);
                                            tabContainer.showTab(locale.runnerTabTemplates());

                                            view.setName(environment.getName());
                                            view.setType(environment.getType());
                                            view.selectScope(environment.getScope());
                                            view.selectMemory(RAM.detect(environment.getRam()));

                                            projectEnvironmentsAction.perform();
                                        }
                                    })
                                    .failure(new FailureCallback() {
                                        @Override
                                        public void onFailure(@Nonnull Throwable reason) {
                                            Log.error(PropertiesPanelPresenter.class, reason.getMessage());
                                        }
                                    })
                                    .build();

        projectService.createFile(path, fileName + DOCKER_SCRIPT_NAME, content, null, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void onSaveButtonClicked() {
        environment.setRam(view.getRam().getValue());

        String path = currentProject.getProjectDescription().getPath() + ROOT_FOLDER + environment.getName();

        AsyncRequestCallback<Void> asyncRequestCallback = voidAsyncCallbackBuilder
                .success(new SuccessCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        projectEnvironmentsAction.perform();
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        notificationManager.showError(reason.getMessage());
                    }
                })
                .build();

        projectService.rename(path, view.getName(), null, asyncRequestCallback);

        if (editor.isDirty()) {
            editor.doSave(new AsyncCallback<EditorInput>() {
                @Override
                public void onSuccess(EditorInput editorInput) {
                    view.setEnableSaveButton(false);
                    view.setEnableCancelButton(false);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.error(getClass(), throwable.getMessage());
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteButtonClicked() {
        if (PROJECT.equals(environment.getScope())) {
            showDialog();
        }
    }

    private void showDialog() {
        dialogFactory.createConfirmDialog(locale.removeEnvironment(),
                                          locale.removeEnvironmentMessage(environment.getName()),
                                          new ConfirmCallback() {
                                              @Override
                                              public void accepted() {
                                                  removeSelectedEnvironment();
                                              }
                                          }, null).show();
    }

    private void removeSelectedEnvironment() {
        AsyncRequestCallback<Void> asyncRequestCallback = voidAsyncCallbackBuilder
                .success(new SuccessCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        projectEnvironmentsAction.perform();

                        notifyListeners(environment);
                    }
                })
                .failure(new FailureCallback() {
                    @Override
                    public void onFailure(@Nonnull Throwable reason) {
                        notificationManager.showError(reason.getMessage());
                    }
                })
                .build();

        projectService.delete(environment.getPath(), asyncRequestCallback);
    }

    private void notifyListeners(@Nonnull Environment environment) {
        for (RemovePanelListener listener : listeners) {
            listener.onPanelRemoved(environment);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        Scope scope = environment.getScope();

        view.setEnableSaveButton(false);
        view.setEnableCancelButton(false);
        view.setEnableDeleteButton(PROJECT.equals(scope));

        if (editor instanceof UndoableEditor) {
            HandlesUndoRedo undoRedo = ((UndoableEditor)editor).getUndoRedo();
            while (editor.isDirty() && undoRedo.undoable()) {
                undoOperations++;
                undoRedo.undo();
            }
        }
        if (environment != null) {
            view.setName(environment.getName());
            view.selectMemory(RAM.detect(environment.getRam()));
            view.selectScope(scope);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(@Nonnull RemovePanelListener listener) {
        listeners.add(listener);
    }
}
