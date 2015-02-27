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
package com.codenvy.ide.ext.runner.client.tabs.properties.panel;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PropertyListener;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.api.texteditor.UndoableEditor;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.customenvironment.EnvironmentScript;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.impl.docker.DockerFileFactory;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import com.codenvy.ide.ext.runner.client.util.TimerFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.editor.EditorPartPresenter.PROP_DIRTY;
import static com.codenvy.ide.api.editor.EditorPartPresenter.PROP_INPUT;
import static com.codenvy.ide.ext.runner.client.constants.TimeInterval.ONE_SEC;
import static com.codenvy.ide.ext.runner.client.models.EnvironmentImpl.ROOT_FOLDER;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;

/**
 * The class that manages Properties panel widget.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class PropertiesPanelPresenter implements PropertiesPanelView.ActionDelegate, PropertiesPanel {

    private static final String DOCKER_SCRIPT_NAME = "/Dockerfile";

    private final List<RemovePanelListener>    listeners;
    private final PropertiesPanelView          view;
    private final DockerFileFactory            dockerFileFactory;
    private final EditorRegistry               editorRegistry;
    private final FileTypeRegistry             fileTypeRegistry;
    private final ProjectServiceClient         projectService;
    private final DialogFactory                dialogFactory;
    private final RunnerLocalizationConstant   locale;
    private final GetProjectEnvironmentsAction projectEnvironmentsAction;
    private final NotificationManager          notificationManager;
    private final DtoUnmarshallerFactory       unmarshallerFactory;
    private final CurrentProject               currentProject;
    private final EventBus                     eventBus;

    private Timer               timer;
    private EditorPartPresenter editor;
    private int                 undoOperations;
    private Environment         environment;
    private String              environmentName;

    public PropertiesPanelPresenter(PropertiesPanelView view,
                                    DockerFileFactory dockerFileFactory,
                                    EditorRegistry editorRegistry,
                                    FileTypeRegistry fileTypeRegistry,
                                    ProjectServiceClient projectService,
                                    DialogFactory dialogFactory,
                                    RunnerLocalizationConstant locale,
                                    GetProjectEnvironmentsAction projectEnvironmentsAction,
                                    NotificationManager notificationManager,
                                    DtoUnmarshallerFactory unmarshallerFactory,
                                    EventBus eventBus,
                                    AppContext appContext) {
        this.view = view;
        this.view.setDelegate(this);

        this.dockerFileFactory = dockerFileFactory;
        this.editorRegistry = editorRegistry;
        this.fileTypeRegistry = fileTypeRegistry;
        this.projectService = projectService;
        this.dialogFactory = dialogFactory;
        this.locale = locale;
        this.projectEnvironmentsAction = projectEnvironmentsAction;
        this.notificationManager = notificationManager;
        this.unmarshallerFactory = unmarshallerFactory;
        this.eventBus = eventBus;
        this.listeners = new ArrayList<>();

        currentProject = appContext.getCurrentProject();
    }

    @AssistedInject
    public PropertiesPanelPresenter(final PropertiesPanelView view,
                                    final EditorRegistry editorRegistry,
                                    final FileTypeRegistry fileTypeRegistry,
                                    final DockerFileFactory dockerFileFactory,
                                    ProjectServiceClient projectService,
                                    DialogFactory dialogFactory,
                                    RunnerLocalizationConstant locale,
                                    GetProjectEnvironmentsAction projectEnvironmentsAction,
                                    NotificationManager notificationManager,
                                    DtoUnmarshallerFactory unmarshallerFactory,
                                    EventBus eventBus,
                                    AppContext appContext,
                                    @Assisted @Nonnull final Runner runner) {
        this(view,
             dockerFileFactory,
             editorRegistry,
             fileTypeRegistry,
             projectService,
             dialogFactory,
             locale,
             projectEnvironmentsAction,
             notificationManager,
             unmarshallerFactory,
             eventBus,
             appContext);

        // we're waiting for getting application descriptor from server. so we can't show editor without knowing about configuration file.
        timer = new Timer() {
            @Override
            public void run() {
                String dockerUrl = runner.getDockerUrl();
                if (dockerUrl == null) {
                    timer.schedule(ONE_SEC.getValue());
                    return;
                }

                timer.cancel();

                FileNode file = dockerFileFactory.newInstance(dockerUrl);
                initializeEditor(file, editorRegistry, fileTypeRegistry);

                view.selectMemory(RAM.detect(runner.getRAM()));
            }
        };
        timer.schedule(ONE_SEC.getValue());
    }

    @AssistedInject
    public PropertiesPanelPresenter(final PropertiesPanelView view,
                                    final EditorRegistry editorRegistry,
                                    final FileTypeRegistry fileTypeRegistry,
                                    final DockerFileFactory dockerFileFactory,
                                    final ProjectServiceClient projectService,
                                    EventBus eventBus,
                                    AppContext appContext,
                                    DialogFactory dialogFactory,
                                    RunnerLocalizationConstant locale,
                                    GetProjectEnvironmentsAction projectEnvironmentsAction,
                                    NotificationManager notificationManager,
                                    DtoUnmarshallerFactory unmarshallerFactory,
                                    TimerFactory timerFactory,
                                    @Assisted @Nonnull final Environment environment) {
        this(view,
             dockerFileFactory,
             editorRegistry,
             fileTypeRegistry,
             projectService,
             dialogFactory,
             locale,
             projectEnvironmentsAction,
             notificationManager,
             unmarshallerFactory,
             eventBus,
             appContext);

        this.environment = environment;
        this.environmentName = environment.getName();


        // we're waiting for getting application descriptor from server. so we can't show editor without knowing about configuration file.
        timer = timerFactory.newInstance(new TimerFactory.TimerCallBack() {
            @Override
            public void onRun() {
                timer.cancel();

                if (PROJECT.equals(environment.getScope())) {
                    getProjectEnvironmentDocker();
                } else {
                    getSystemEnvironmentDocker(environment);
                }
            }
        });
        timer.schedule(ONE_SEC.getValue());
    }

    private void getProjectEnvironmentDocker() {
        Unmarshallable<Array<ItemReference>> unmarshaller = unmarshallerFactory.newArrayUnmarshaller(ItemReference.class);

        projectService.getChildren(environment.getPath(), new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> result) {
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

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(getClass(), exception.getMessage());
            }
        });
    }

    private void getSystemEnvironmentDocker(@Nonnull Environment environment) {
        FileNode file = dockerFileFactory.newInstance(environment.getPath());
        initializeEditor(file, editorRegistry, fileTypeRegistry);
    }

    private void initializeEditor(@Nonnull FileNode file,
                                  @Nonnull EditorRegistry editorRegistry,
                                  @Nonnull FileTypeRegistry fileTypeRegistry) {
        FileType fileType = fileTypeRegistry.getFileTypeByFile(file);
        editor = editorRegistry.getEditor(fileType).getEditor();

        // wait when editor is initialized
        editor.addPropertyListener(new PropertyListener() {
            @Override
            public void propertyChanged(PartPresenter source, int propId) {
                switch (propId) {
                    case PROP_INPUT:
                        view.showEditor(editor);
                        break;

                    case PROP_DIRTY:
                        if (validateUndoOperation()) {
                            view.setEnableSaveButton(true);
                            view.setEnableCancelButton(true);
                        }
                        break;

                    default:
                }
            }
        });

        try {
            editor.init(new DockerFileEditorInput(fileType, file));
        } catch (EditorInitException e) {
            Log.error(getClass(), e);
        }
    }

    private boolean validateUndoOperation() {
        // this code needs for right behaviour when someone is clicking on 'Cancel' button. We need to make disable some buttons.
        if (undoOperations == 0) {
            return true;
        }

        undoOperations--;
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void onConfigurationChanged() {
        view.setEnableSaveButton(true);
        view.setEnableCancelButton(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onSaveButtonClicked() {
        view.setEnableSaveButton(false);
        view.setEnableCancelButton(false);

        if (!environmentName.equals(view.getName())) {
            createEnvironment();
        } else {
            environment.setRam(view.getRam().getValue());
        }

        if (editor.isDirty()) {
            editor.doSave();
        }
    }

    private void createEnvironment() {
        String path = currentProject.getProjectDescription().getPath() + ROOT_FOLDER + view.getName();

        Unmarshallable<ItemReference> unmarshaller = unmarshallerFactory.newUnmarshaller(ItemReference.class);

        projectService.createFolder(path, new AsyncRequestCallback<ItemReference>(unmarshaller) {
            @Override
            protected void onSuccess(@Nonnull ItemReference result) {
                getEditorContent();
            }

            @Override
            protected void onFailure(@Nonnull Throwable exception) {
                notificationManager.showError(exception.getMessage());
            }
        });
    }

    private void getEditorContent() {
        editor.getEditorInput().getFile().getContent(new AsyncCallback<String>() {
            @Override
            public void onSuccess(String content) {
                createFile(content);
            }

            @Override
            public void onFailure(Throwable throwable) {
                notificationManager.showError(throwable.getMessage());
            }
        });
    }

    private void createFile(@Nonnull String content) {
        String path = currentProject.getProjectDescription().getPath() + ROOT_FOLDER;

        projectService.createFile(path,
                                  view.getName() + DOCKER_SCRIPT_NAME,
                                  content,
                                  null,
                                  new AsyncRequestCallback<ItemReference>() {
                                      @Override
                                      protected void onSuccess(@Nonnull ItemReference result) {
                                          getProjectEnvironmentDocker();

                                          projectEnvironmentsAction.perform();
                                      }

                                      @Override
                                      protected void onFailure(@Nonnull Throwable ignore) {
                                          Log.error(PropertiesPanelPresenter.class, ignore.getMessage());
                                      }
                                  });
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
        projectService.delete(environment.getPath(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(@Nonnull Void result) {
                projectEnvironmentsAction.perform();

                reset();

                notifyListeners(environment);
            }

            @Override
            protected void onFailure(@Nonnull Throwable exception) {
                notificationManager.showError(exception.getMessage());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        view.setEnableSaveButton(false);
        view.setEnableCancelButton(false);

        if (editor instanceof UndoableEditor) {
            HandlesUndoRedo undoRedo = ((UndoableEditor)editor).getUndoRedo();
            while (editor.isDirty() && undoRedo.undoable()) {
                undoOperations++;
                undoRedo.undo();
            }
        }

        view.setName(environment.getName());
        view.selectMemory(RAM.detect(environment.getRam()));
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        if (editor == null) {
            return;
        }

        editor.activate();
        editor.onOpen();
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        view.setName(runner.getTitle());
        view.setType(runner.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Environment environment) {
        view.setEnableCancelButton(false);
        view.setEnableSaveButton(false);
        view.setEnableDeleteButton(PROJECT.equals(environment.getScope()));

        view.setName(environment.getName());
        view.setType(environment.getType());
        view.selectMemory(RAM.detect(environment.getRam()));
        view.selectScope(environment.getScope());
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        view.showEditor(null);

        view.setName("");
        view.setType("");

        view.setEnableDeleteButton(false);
        view.setEnableSaveButton(false);
        view.setEnableCancelButton(false);
    }


    public void addListener(@Nonnull RemovePanelListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(@Nonnull Environment environment) {
        for (RemovePanelListener listener : listeners) {
            listener.onPanelRemoved(environment);
        }
    }

}