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
package org.eclipse.che.ide.ext.runner.client.tabs.properties.panel;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.editor.EditorInitException;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.EditorRegistry;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PropertyListener;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.project.tree.generic.ProjectNode;
import org.eclipse.che.ide.api.texteditor.HandlesUndoRedo;
import org.eclipse.che.ide.api.texteditor.HasReadOnlyProperty;
import org.eclipse.che.ide.api.texteditor.UndoableEditor;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.ext.runner.client.RunnerLocalizationConstant;
import org.eclipse.che.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import org.eclipse.che.ide.ext.runner.client.callbacks.FailureCallback;
import org.eclipse.che.ide.ext.runner.client.callbacks.SuccessCallback;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.models.Runner;
import org.eclipse.che.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import org.eclipse.che.ide.ext.runner.client.tabs.container.TabContainer;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.EnvironmentScript;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.RAM;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.docker.DockerFile;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.docker.DockerFileEditorInput;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.docker.DockerFileFactory;
import org.eclipse.che.ide.ext.runner.client.util.NameGenerator;
import org.eclipse.che.ide.ext.runner.client.util.TimerFactory;
import org.eclipse.che.ide.ext.runner.client.util.annotations.LeftPanel;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static org.eclipse.che.ide.api.editor.EditorPartPresenter.PROP_DIRTY;
import static org.eclipse.che.ide.api.editor.EditorPartPresenter.PROP_INPUT;
import static org.eclipse.che.ide.ext.runner.client.constants.TimeInterval.ONE_SEC;
import static org.eclipse.che.ide.ext.runner.client.models.EnvironmentImpl.ROOT_FOLDER;
import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;

/**
 * The class that manages Properties panel widget.
 *
 * @author Andrey Plotnikov
 * @author Dmitry Shnurenko
 */
public class PropertiesPanelPresenter implements PropertiesPanelView.ActionDelegate, PropertiesPanel {

    private static final String DOCKER_SCRIPT_NAME = "/Dockerfile";

    private final List<RemovePanelListener>           listeners;
    private final PropertiesPanelView                 view;
    private final DockerFileFactory                   dockerFileFactory;
    private final EditorRegistry                      editorRegistry;
    private final FileTypeRegistry                    fileTypeRegistry;
    private final ProjectServiceClient                projectService;
    private final DialogFactory                       dialogFactory;
    private final RunnerLocalizationConstant          locale;
    private final GetProjectEnvironmentsAction        projectEnvironmentsAction;
    private final NotificationManager                 notificationManager;
    private final DtoUnmarshallerFactory              unmarshallerFactory;
    private final CurrentProject                      currentProject;
    private final EventBus                            eventBus;
    private final AsyncCallbackBuilder<ItemReference> asyncCallbackBuilder;
    private final TabContainer                        tabContainer;

    private Timer               timer;
    private EditorPartPresenter editor;
    private int                 undoOperations;
    private Environment         environment;
    private Runner              runner;

    private PropertiesPanelPresenter(PropertiesPanelView view,
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
                                     AppContext appContext,
                                     @LeftPanel TabContainer tabContainer,
                                     AsyncCallbackBuilder<ItemReference> asyncCallbackBuilder) {
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
        this.asyncCallbackBuilder = asyncCallbackBuilder;
        this.tabContainer = tabContainer;

        currentProject = appContext.getCurrentProject();

        if (currentProject == null) {
            return;
        }

        resetButtons();
    }

    private void resetButtons() {
        view.setEnableCancelButton(false);
        view.setEnableSaveButton(false);
        view.setEnableDeleteButton(false);
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
                                    AsyncCallbackBuilder<ItemReference> asyncCallbackBuilder,
                                    @LeftPanel TabContainer tabContainer,
                                    @Assisted @Nonnull final Runner runner,
                                    TimerFactory timerFactory) {
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
             appContext,
             tabContainer,
             asyncCallbackBuilder);

        this.runner = runner;

        // we're waiting for getting application descriptor from server. so we can't show editor without knowing about configuration file.
        timer = timerFactory.newInstance(new TimerFactory.TimerCallBack() {
            @Override
            public void onRun() {
                String dockerUrl = runner.getDockerUrl();
                if (dockerUrl == null) {
                    timer.schedule(ONE_SEC.getValue());
                    return;
                }

                timer.cancel();

                DockerFile file = dockerFileFactory.newInstance(dockerUrl);
                initializeEditor(file, editorRegistry, fileTypeRegistry);

                view.selectMemory(RAM.detect(runner.getRAM()));
            }
        });
        timer.schedule(ONE_SEC.getValue());

        view.setEnableNameProperty(false);
        view.setEnableRamProperty(false);
        view.setEnableBootProperty(false);
        view.setEnableShutdownProperty(false);
        view.setEnableScopeProperty(false);

        view.setVisibleButtons(false);
        view.selectMemory(RAM.detect(runner.getRAM()));
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
                                    AsyncCallbackBuilder<ItemReference> asyncCallbackBuilder,
                                    @LeftPanel TabContainer tabContainer,
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
             appContext,
             tabContainer,
             asyncCallbackBuilder);

        this.environment = environment;

        boolean isProjectScope = PROJECT.equals(environment.getScope());

        view.setEnableNameProperty(isProjectScope);
        view.setEnableRamProperty(isProjectScope);
        view.setEnableBootProperty(false);
        view.setEnableShutdownProperty(false);
        view.setEnableScopeProperty(false);

        view.setVisibleButtons(isProjectScope);

        if (isProjectScope) {
            getProjectEnvironmentDocker();
        } else {
            getSystemEnvironmentDocker();
        }
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

    private void getSystemEnvironmentDocker() {
        DockerFile file = dockerFileFactory.newInstance(environment.getPath());
        initializeEditor(file, editorRegistry, fileTypeRegistry);
    }

    private void initializeEditor(@Nonnull final FileNode file,
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
                        if (editor instanceof HasReadOnlyProperty) {
                            ((HasReadOnlyProperty)editor).setReadOnly(file.isReadOnly());
                        }
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
        Scope scope = runner == null ? environment.getScope() : runner.getScope();

        view.setEnableSaveButton(PROJECT.equals(scope));
        view.setEnableCancelButton(true);
        //Todo why we erase command view.setEnableSaveButton(PROJECT.equals(scope))?
//        view.setEnableSaveButton(true);
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
                                            resetButtons();
                                            tabContainer.showTab(locale.runnerTabTemplates());

                                            boolean isRunnerNull = runner == null;

                                            view.setName(isRunnerNull ? environment.getName() : runner.getTitle());
                                            view.setType(isRunnerNull ? environment.getType() : runner.getType());
                                            view.selectScope(isRunnerNull ? environment.getScope() : runner.getScope());
                                            view.selectMemory(isRunnerNull ? RAM.detect(environment.getRam()) :
                                                              RAM.detect(runner.getRAM()));

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

        projectService.rename(path, view.getName(), null, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void aVoid) {
                projectEnvironmentsAction.perform();
            }

            @Override
            protected void onFailure(Throwable throwable) {
                notificationManager.showError(throwable.getMessage());
            }
        });

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
        Scope scope = environment == null ? runner.getScope() : environment.getScope();

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
            view.selectScope(environment.getScope());
        }

        if (runner != null) {
            view.setName(runner.getTitle());
            view.selectMemory(RAM.detect(runner.getRAM()));
            view.selectScope(runner.getScope());
        }
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
        view.selectMemory(RAM.detect(runner.getRAM()));
        view.selectScope(runner.getScope());
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