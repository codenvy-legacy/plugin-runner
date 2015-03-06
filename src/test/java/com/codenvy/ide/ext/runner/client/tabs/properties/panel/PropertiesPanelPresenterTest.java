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
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PropertyListener;
import com.codenvy.ide.api.projecttree.TreeStructure;
import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.api.texteditor.HasReadOnlyProperty;
import com.codenvy.ide.api.texteditor.UndoableEditor;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.java.JsonArrayListAdapter;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.TestUtil;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.FailureCallback;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.models.Environment;
import com.codenvy.ide.ext.runner.client.models.EnvironmentImpl;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.impl.environments.GetProjectEnvironmentsAction;
import com.codenvy.ide.ext.runner.client.tabs.container.TabContainer;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.docker.DockerFile;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.docker.DockerFileEditorInput;
import com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.docker.DockerFileFactory;
import com.codenvy.ide.ext.runner.client.util.TimerFactory;
import com.codenvy.ide.imageviewer.ImageViewer;
import com.codenvy.ide.imageviewer.ImageViewerResources;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.CancelCallback;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.ui.dialogs.confirm.ConfirmDialog;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Arrays;

import static com.codenvy.ide.api.editor.EditorPartPresenter.PROP_DIRTY;
import static com.codenvy.ide.api.editor.EditorPartPresenter.PROP_INPUT;
import static com.codenvy.ide.ext.runner.client.constants.TimeInterval.ONE_SEC;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.RAM._512;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.PROJECT;
import static com.codenvy.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Andrienko
 */
@RunWith(GwtMockitoTestRunner.class)
public class PropertiesPanelPresenterTest {

    private static final String TEXT = "some text";
    private Array<ItemReference> result;

    //mocks for constructors
    @Mock
    private PropertiesPanelView                 view;
    @Mock
    private DockerFileFactory                   dockerFileFactory;
    @Mock
    private EditorRegistry                      editorRegistry;
    @Mock
    private FileTypeRegistry                    fileTypeRegistry;
    @Mock
    private ProjectServiceClient                projectService;
    @Mock
    private DialogFactory                       dialogFactory;
    @Mock
    private RunnerLocalizationConstant          locale;
    @Mock
    private GetProjectEnvironmentsAction        projectEnvironmentsAction;
    @Mock
    private NotificationManager                 notificationManager;
    @Mock
    private DtoUnmarshallerFactory              unmarshallerFactory;
    @Mock
    private EventBus                            eventBus;
    @Mock
    private AppContext                          appContext;
    @Mock
    private TabContainer                        tabContainer;
    @Mock
    private AsyncCallbackBuilder<ItemReference> asyncCallbackBuilder;
    @Mock
    private Runner                              runner;
    @Mock
    private TimerFactory                        timerFactory;
    @Mock
    private Environment                         environment;

    @Mock
    private CurrentProject                       currentProject;
    @Mock
    private Timer                                timer;
    @Mock
    private Unmarshallable<Array<ItemReference>> unmarshaller;
    @Mock
    private ProjectDescriptor                    descriptor;
    @Mock
    private TreeStructure                        treeStructure;
    @Mock
    private EditorProvider                       editorProvider;
    @Mock
    private EditorPartPresenter                  editor;
    @Mock
    private Throwable                            exception;
    @Mock
    private DockerFile                           file;
    @Mock
    private AsyncRequestCallback<ItemReference>  asyncRequestCallback;
    @Mock
    private EditorInput                          editorInput;
    @Mock
    private ConfirmDialog                        confirmDialog;

    @Mock
    private PropertiesPanel.RemovePanelListener listener1;
    @Mock
    private PropertiesPanel.RemovePanelListener listener2;

    @Mock
    private ItemReference itemReference1;
    @Mock
    private ItemReference itemReference2;

    @Captor
    private ArgumentCaptor<TimerFactory.TimerCallBack>                 timerCaptor;
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Array<ItemReference>>> asyncRequestCallbackArgCaptor;
    @Captor
    private ArgumentCaptor<PropertyListener>                           propertyListenerArgCaptor;
    @Captor
    private ArgumentCaptor<SuccessCallback<ItemReference>>             successCallbackArgCaptor;
    @Captor
    private ArgumentCaptor<AsyncCallback<String>>                      editorTextCaptor;
    @Captor
    private ArgumentCaptor<FailureCallback>                            failureCallbackArgCaptor;
    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Void>>                 requestCallbackArgCaptor;
    @Captor
    private ArgumentCaptor<AsyncCallback<EditorInput>>                 editorInputCaptor;

    private PropertiesPanelPresenter presenter;

    @Before
    public void setUp() {
        result = new JsonArrayListAdapter<>(Arrays.asList(itemReference1, itemReference2));
        when(timerFactory.newInstance(any(TimerFactory.TimerCallBack.class))).thenReturn(timer);
        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(runner.getRAM()).thenReturn(_512.getValue());
        when(unmarshallerFactory.newArrayUnmarshaller(ItemReference.class)).thenReturn(unmarshaller);

        when(environment.getScope()).thenReturn(SYSTEM);
        when(environment.getPath()).thenReturn(TEXT);
        when(environment.getName()).thenReturn(TEXT);
        when(environment.getType()).thenReturn(TEXT);
        when(environment.getRam()).thenReturn(_512.getValue());

        when(currentProject.getCurrentTree()).thenReturn(treeStructure);
        when(currentProject.getProjectDescription()).thenReturn(descriptor);

        when(editorRegistry.getEditor(isNull(FileType.class))).thenReturn(editorProvider);
        when(editorProvider.getEditor()).thenReturn(editor);
        when(dockerFileFactory.newInstance(TEXT)).thenReturn(file);

        when(asyncCallbackBuilder.unmarshaller(ItemReference.class)).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.success(Matchers.<SuccessCallback<ItemReference>>anyObject())).thenReturn(asyncCallbackBuilder);
        when(asyncCallbackBuilder.failure(any(FailureCallback.class))).thenReturn(asyncCallbackBuilder);

        when(asyncCallbackBuilder.build()).thenReturn(asyncRequestCallback);

        when(editor.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getFile()).thenReturn(file);

        when(locale.runnerTabTemplates()).thenReturn(TEXT);

        presenter = new PropertiesPanelPresenter(view,
                                                 editorRegistry,
                                                 fileTypeRegistry,
                                                 dockerFileFactory,
                                                 projectService,
                                                 dialogFactory,
                                                 locale,
                                                 projectEnvironmentsAction,
                                                 notificationManager,
                                                 unmarshallerFactory,
                                                 eventBus,
                                                 appContext,
                                                 asyncCallbackBuilder,
                                                 tabContainer,
                                                 runner,
                                                 timerFactory);

        when(locale.removeEnvironment()).thenReturn(TEXT);
        when(locale.removeEnvironmentMessage(TEXT)).thenReturn(TEXT);
        when(dialogFactory.createConfirmDialog(eq(TEXT), eq(TEXT), any(ConfirmCallback.class), isNull(CancelCallback.class)))
                .thenReturn(confirmDialog);
        when(runner.getTitle()).thenReturn(TEXT);
    }

    @Test
    public void verifyFirstConstructorWhenCurrentProjectNotNull() {
        verify(appContext).getCurrentProject();

        buttonSaveCancelDeleteShouldBeDisable();

        verifyEnableProperties();
    }

    private void buttonSaveCancelDeleteShouldBeDisable() {
        verify(view).setEnableCancelButton(false);
        verify(view).setEnableSaveButton(false);
        verify(view).setEnableDeleteButton(false);
    }

    private void buttonSaveCancelDeleteShouldNotBeDisable() {
        verify(view, never()).setEnableCancelButton(false);
        verify(view, never()).setEnableSaveButton(false);
        verify(view, never()).setEnableDeleteButton(false);
    }

    private void verifyEnableProperties() {
        verify(view).setEnableNameProperty(false);
        verify(view).setEnableRamProperty(false);
        verify(view).setEnableBootProperty(false);
        verify(view).setEnableShutdownProperty(false);
        verify(view).setEnableScopeProperty(false);

        verify(view).setVisibleButtons(false);
        verify(runner).getRAM();
        verify(view).selectMemory(_512);
    }

    @Test
    public void timerShouldBeLaunched() throws EditorInitException {
        when(runner.getDockerUrl()).thenReturn(TEXT);
        verify(timerFactory).newInstance(timerCaptor.capture());

        timerCaptor.getValue().onRun();

        verify(runner).getDockerUrl();
        verify(timer).schedule(ONE_SEC.getValue());

        verify(timer).cancel();
        verify(dockerFileFactory).newInstance(TEXT);

        verify(fileTypeRegistry).getFileTypeByFile(file);
        verify(editorRegistry).getEditor(isNull(FileType.class));
        verify(editorProvider).getEditor();
        verify(editor).addPropertyListener(any(PropertyListener.class));
        verify(editor).init(any(DockerFileEditorInput.class));

        verify(runner, times(2)).getRAM();
        verify(view, times(2)).selectMemory(_512);
    }

    @Test
    public void timerShouldBeLaunchedAfterCancelChanges() throws EditorInitException {
        PartPresenter source = mock(PartPresenter.class);
        EditorPartPresenter editor2 = mock(EditorMock.class);
        when(editorProvider.getEditor()).thenReturn(editor2);
        when(file.isReadOnly()).thenReturn(true);
        HandlesUndoRedo handlesUndoRedo = mock(HandlesUndoRedo.class);

        when(((UndoableEditor)editor2).getUndoRedo()).thenReturn(handlesUndoRedo);
        when(handlesUndoRedo.undoable()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(editor2.isDirty()).thenReturn(true);

        verify(timerFactory).newInstance(timerCaptor.capture());
        when(runner.getDockerUrl()).thenReturn(TEXT);
        timerCaptor.getValue().onRun();
        initFirstConstructor();

        verify(timerFactory).newInstance(timerCaptor.capture());
        when(runner.getDockerUrl()).thenReturn(TEXT);
        timerCaptor.getValue().onRun();

        presenter.onCancelButtonClicked();

        verify(timerFactory).newInstance(timerCaptor.capture());
        when(runner.getDockerUrl()).thenReturn(TEXT);
        timerCaptor.getValue().onRun();

        verify(runner, times(2)).getDockerUrl();
        verify(timer).schedule(ONE_SEC.getValue());

        verify(timer, times(2)).cancel();
        verify(dockerFileFactory, times(3)).newInstance(TEXT);

        ArgumentCaptor<PropertyListener> propertyListenerArgumentCaptor = ArgumentCaptor.forClass(PropertyListener.class);
        verify(editor2, times(3)).addPropertyListener(propertyListenerArgumentCaptor.capture());
        propertyListenerArgumentCaptor.getValue().propertyChanged(source, PROP_DIRTY);

        verify(view, never()).setEnableSaveButton(true);
        verify(view, never()).setEnableCancelButton(true);
    }

    @Test
    public void timerShouldBeLaunchedWhenDockerUrlIsNull() throws EditorInitException {
        verify(timerFactory).newInstance(timerCaptor.capture());

        timerCaptor.getValue().onRun();

        verify(runner).getDockerUrl();
        verify(timer, times(2)).schedule(ONE_SEC.getValue());
    }

    @Test
    public void verifyFirstConstructorWhenCurrentProjectNull() {
        initFirstConstructor();

        verify(appContext).getCurrentProject();

        buttonSaveCancelDeleteShouldNotBeDisable();

        verifyEnableProperties();
    }

    private void initFirstConstructor() {
        reset(appContext, view, timer, timerFactory, runner);
        when(timerFactory.newInstance(any(TimerFactory.TimerCallBack.class))).thenReturn(timer);
        when(runner.getRAM()).thenReturn(_512.getValue());
        when(appContext.getCurrentProject()).thenReturn(null);

        presenter = new PropertiesPanelPresenter(view,
                                                 editorRegistry,
                                                 fileTypeRegistry,
                                                 dockerFileFactory,
                                                 projectService,
                                                 dialogFactory,
                                                 locale,
                                                 projectEnvironmentsAction,
                                                 notificationManager,
                                                 unmarshallerFactory,
                                                 eventBus,
                                                 appContext,
                                                 asyncCallbackBuilder,
                                                 tabContainer,
                                                 runner,
                                                 timerFactory);
    }

    @Test
    public void verifySecondConstructorWhenScopeIsProject() throws Exception {
        when(appContext.getCurrentProject()).thenReturn(currentProject);
        when(environment.getScope()).thenReturn(PROJECT);

        initSecondConstructor();

        verify(appContext).getCurrentProject();

        buttonSaveCancelDeleteShouldBeDisable();

        verify(environment).getScope();

        verify(view).setEnableNameProperty(true);
        verify(view).setEnableRamProperty(true);
        verify(view).setEnableBootProperty(false);
        verify(view).setEnableShutdownProperty(false);
        verify(view).setEnableScopeProperty(false);

        verify(view).setVisibleButtons(true);

        verify(unmarshallerFactory).newArrayUnmarshaller(ItemReference.class);

        verify(projectService).getChildren(eq(TEXT), asyncRequestCallbackArgCaptor.capture());
        AsyncRequestCallback<Array<ItemReference>> callback = asyncRequestCallbackArgCaptor.getValue();
        TestUtil.invokeMethodByName(callback, "onSuccess", result);

        verify(currentProject, times(2)).getProjectDescription();
        verify(currentProject, times(2)).getCurrentTree();
        verify(environment, times(2)).getName();

        verify(environment).getPath();
        //we can't use mock for fileType that why we have null FileType
        verify(editorRegistry, times(2)).getEditor(isNull(FileType.class));
        verify(editorProvider, times(2)).getEditor();
        verify(editor, times(2)).addPropertyListener(any(PropertyListener.class));

        verify(editor, times(2)).init(any(DockerFileEditorInput.class));

        TestUtil.invokeMethodByName(callback, "onFailure", Throwable.class, exception);
        verify(exception).getMessage();
    }

    @Test
    public void verifySecondConstructorWhenScopeIsSystem() throws Exception {
        initSecondConstructor();

        verify(appContext).getCurrentProject();

        buttonSaveCancelDeleteShouldBeDisable();

        verify(environment).getScope();

        verify(view).setEnableNameProperty(false);
        verify(view).setEnableRamProperty(false);
        verify(view).setEnableBootProperty(false);
        verify(view).setEnableShutdownProperty(false);
        verify(view).setEnableScopeProperty(false);

        verify(view).setVisibleButtons(false);

        verify(environment).getPath();
        verify(dockerFileFactory).newInstance(TEXT);

        //we can't use mock for fileType that why we have null FileType
        verify(editorRegistry).getEditor(isNull(FileType.class));
        verify(editorProvider).getEditor();
        verify(editor).addPropertyListener(any(PropertyListener.class));

        verify(editor).init(any(DockerFileEditorInput.class));
    }

    private void initSecondConstructor() {
        reset(appContext, view, timer, timerFactory, runner);
        when(appContext.getCurrentProject()).thenReturn(currentProject);

        presenter = new PropertiesPanelPresenter(view,
                                                 editorRegistry,
                                                 fileTypeRegistry,
                                                 dockerFileFactory,
                                                 projectService,
                                                 eventBus,
                                                 appContext,
                                                 dialogFactory,
                                                 locale,
                                                 projectEnvironmentsAction,
                                                 notificationManager,
                                                 unmarshallerFactory,
                                                 asyncCallbackBuilder,
                                                 tabContainer,
                                                 environment);
    }

    @Test
    public void propertiesShouldBeChangedWithPropIdPropInputAndEditorIsNotInstanceOfHasReadOnlyProperty() {
        PartPresenter partPresenter = mock(PartPresenter.class);

        initSecondConstructor();

        verify(editor).addPropertyListener(propertyListenerArgCaptor.capture());
        propertyListenerArgCaptor.getValue().propertyChanged(partPresenter, PROP_INPUT);

        verify(view).showEditor(editor);
    }

    @Test
    public void propertiesShouldBeChangedWithPropIdPropInputAndEditorIsInstanceOfHasReadOnlyProperty() {
        PartPresenter partPresenter = mock(PartPresenter.class);
        EditorPartPresenter editor2 = mock(EditorMock.class);
        when(editorProvider.getEditor()).thenReturn(editor2);
        when(file.isReadOnly()).thenReturn(true);

        initSecondConstructor();

        verify(editor2).addPropertyListener(propertyListenerArgCaptor.capture());
        propertyListenerArgCaptor.getValue().propertyChanged(partPresenter, PROP_INPUT);
        verify(file).isReadOnly();
        verify((HasReadOnlyProperty)editor2).setReadOnly(true);
        verify(view).showEditor(editor2);
    }

    @Test
    public void propertiesShouldBeChangedWithPropIdDropDirtyWhenValidateUndoOperationIsTrue() {
        PartPresenter partPresenter = mock(PartPresenter.class);
        EditorPartPresenter editor2 = mock(EditorMock.class);
        when(editorProvider.getEditor()).thenReturn(editor2);
        when(file.isReadOnly()).thenReturn(true);

        initSecondConstructor();

        verify(editor2).addPropertyListener(propertyListenerArgCaptor.capture());
        propertyListenerArgCaptor.getValue().propertyChanged(partPresenter, PROP_DIRTY);

        verify(view).setEnableSaveButton(true);
        verify(view).setEnableCancelButton(true);
    }

    @Test
    public void propertiesShouldBeChangedWithPropIdDropDirtyWhenValidateUndoOperationIsFalse() {
        PartPresenter partPresenter = mock(PartPresenter.class);
        EditorPartPresenter editor2 = mock(EditorMock.class);
        when(editorProvider.getEditor()).thenReturn(editor2);
        when(file.isReadOnly()).thenReturn(true);

        initSecondConstructor();

        verify(editor2).addPropertyListener(propertyListenerArgCaptor.capture());
        propertyListenerArgCaptor.getValue().propertyChanged(partPresenter, PROP_DIRTY);

        verify(view).setEnableSaveButton(true);
        verify(view).setEnableCancelButton(true);
    }

    @Test
    public void configurationShouldBeChangedWhenScopeIsSystem() {
        initFirstConstructor();
        reset(view);
        when(runner.getScope()).thenReturn(SYSTEM);

        presenter.onConfigurationChanged();

        verify(runner).getScope();
        verify(view).setEnableSaveButton(false);
        verify(view).setEnableCancelButton(true);
    }

    @Test
    public void configurationShouldBeChangedAndScopeIsProject() {
        initFirstConstructor();
        when(runner.getScope()).thenReturn(PROJECT);
        reset(view);

        presenter.onConfigurationChanged();

        verify(runner).getScope();
        verify(view).setEnableSaveButton(true);
        verify(view).setEnableCancelButton(true);
    }

    @Test
    public void configurationShouldBeChangedIfRunnerIsNullAndScopeIsSystem() {
        reset(view);
        when(environment.getScope()).thenReturn(SYSTEM);

        presenter.onConfigurationChanged();

        verify(view).setEnableSaveButton(false);
        verify(view).setEnableCancelButton(true);
    }

    @Test
    public void configurationShouldBeChangedIfRunnerIsNullAndScopeIsProject() {
        initSecondConstructor();
        reset(view);
        when(environment.getScope()).thenReturn(PROJECT);

        presenter.onConfigurationChanged();

        verify(view).setEnableSaveButton(true);
        verify(view).setEnableCancelButton(true);
    }

    @Test
    public void copyButtonShouldBeClickedAndContentFromEditorShouldBeReturned() {
        when(editorProvider.getEditor()).thenReturn(editor);
        when(file.isReadOnly()).thenReturn(true);

        initSecondConstructor();

        presenter.onCopyButtonClicked();

        verify(currentProject).getProjectDescription();
        verify(descriptor).getPath();
        verify(asyncCallbackBuilder).unmarshaller(ItemReference.class);
        verify(asyncCallbackBuilder).success(successCallbackArgCaptor.capture());

        successCallbackArgCaptor.getValue().onSuccess(itemReference1);

        verify(editor).getEditorInput();
        verify(editorInput).getFile();

        verify(file).getContent(editorTextCaptor.capture());
        editorTextCaptor.getValue().onSuccess(TEXT);

        verify(currentProject, times(2)).getProjectDescription();
        verify(descriptor, times(2)).getPath();
        verify(asyncCallbackBuilder, times(2)).unmarshaller(ItemReference.class);
        verify(asyncCallbackBuilder, times(2)).success(successCallbackArgCaptor.capture());

        successCallbackArgCaptor.getValue().onSuccess(itemReference2);

        verify(view, times(2)).setEnableCancelButton(false);
        verify(view, times(2)).setEnableSaveButton(false);
        verify(view, times(2)).setEnableDeleteButton(false);

        verify(tabContainer).showTab(TEXT);
        verify(environment).getName();
        verify(environment).getType();
        verify(environment, times(2)).getScope();
        verify(environment).getRam();

        verify(view).setName(TEXT);
        verify(view).setType(TEXT);
        verify(view).selectScope(SYSTEM);
        verify(view).selectMemory(_512);

        verify(asyncCallbackBuilder, times(2)).failure(any(FailureCallback.class));
        verify(asyncCallbackBuilder, times(2)).build();
        verify(projectEnvironmentsAction).perform();

        projectService.createFile(eq(TEXT + EnvironmentImpl.ROOT_FOLDER),
                                  anyString(),
                                  eq(TEXT),
                                  isNull(String.class),
                                  eq(asyncRequestCallback));

        verify(projectService).createFolder(anyString(), eq(asyncRequestCallback));
    }

    @Test
    public void copyButtonShouldBeClickedAndButFileWasCreatedFailed() {
        when(editorProvider.getEditor()).thenReturn(editor);
        when(file.isReadOnly()).thenReturn(true);

        initSecondConstructor();

        presenter.onCopyButtonClicked();

        verify(currentProject).getProjectDescription();
        verify(descriptor).getPath();
        verify(asyncCallbackBuilder).unmarshaller(ItemReference.class);
        verify(asyncCallbackBuilder).success(successCallbackArgCaptor.capture());

        successCallbackArgCaptor.getValue().onSuccess(itemReference1);

        verify(editor).getEditorInput();
        verify(editorInput).getFile();

        verify(file).getContent(editorTextCaptor.capture());
        editorTextCaptor.getValue().onSuccess(TEXT);

        verify(currentProject, times(2)).getProjectDescription();
        verify(descriptor, times(2)).getPath();
        verify(asyncCallbackBuilder, times(2)).unmarshaller(ItemReference.class);
        verify(asyncCallbackBuilder, times(2)).failure(failureCallbackArgCaptor.capture());

        failureCallbackArgCaptor.getValue().onFailure(exception);

        verify(exception).getMessage();

        verify(projectService).createFolder(anyString(), eq(asyncRequestCallback));
    }

    @Test
    public void copyButtonShouldBeClickedAndButFileContentWasReturnedFailed1() {
        when(editorProvider.getEditor()).thenReturn(editor);
        when(file.isReadOnly()).thenReturn(true);

        initSecondConstructor();

        presenter.onCopyButtonClicked();

        verify(currentProject).getProjectDescription();
        verify(descriptor).getPath();
        verify(asyncCallbackBuilder).unmarshaller(ItemReference.class);
        verify(asyncCallbackBuilder).success(successCallbackArgCaptor.capture());

        successCallbackArgCaptor.getValue().onSuccess(itemReference1);

        verify(editor).getEditorInput();
        verify(editorInput).getFile();

        verify(file).getContent(editorTextCaptor.capture());
        editorTextCaptor.getValue().onFailure(exception);

        verify(exception).getMessage();

        verify(projectService).createFolder(anyString(), eq(asyncRequestCallback));
    }

    @Test
    public void copyButtonShouldBeClickedAndButFileContentWasReturnedFailed2() {
        when(editorProvider.getEditor()).thenReturn(editor);
        when(file.isReadOnly()).thenReturn(true);

        initSecondConstructor();

        presenter.onCopyButtonClicked();

        verify(currentProject).getProjectDescription();
        verify(descriptor).getPath();
        verify(asyncCallbackBuilder).unmarshaller(ItemReference.class);
        verify(asyncCallbackBuilder).failure(failureCallbackArgCaptor.capture());

        failureCallbackArgCaptor.getValue().onFailure(exception);

        verify(exception).getMessage();

        verify(projectService).createFolder(anyString(), eq(asyncRequestCallback));
    }

    @Test
    public void saveButtonShouldBeClickedWhenEditorIsNotDirty() throws Exception {
        when(editorProvider.getEditor()).thenReturn(editor);
        initSecondConstructor();
        when(view.getRam()).thenReturn(_512);
        when(view.getName()).thenReturn(TEXT);

        presenter.onSaveButtonClicked();

        verify(view).getRam();
        verify(environment).setRam(512);
        verify(currentProject).getProjectDescription();
        verify(descriptor).getPath();
        verify(environment).getName();

        verify(view).getName();

        verify(projectService).rename(anyString(), eq(TEXT), isNull(String.class), requestCallbackArgCaptor.capture());
        AsyncRequestCallback<Void> callback = requestCallbackArgCaptor.getValue();

        TestUtil.invokeMethodByName(callback, "onSuccess", null);
        verify(projectEnvironmentsAction).perform();

        verify(editor).isDirty();
    }

    @Test
    public void saveButtonShouldBeClickedButRenameFileFailed() throws Exception {
        when(editorProvider.getEditor()).thenReturn(editor);
        initSecondConstructor();
        when(view.getRam()).thenReturn(_512);
        when(view.getName()).thenReturn(TEXT);
        when(exception.getMessage()).thenReturn(TEXT);

        presenter.onSaveButtonClicked();

        verify(view).getRam();
        verify(environment).setRam(512);
        verify(currentProject).getProjectDescription();
        verify(descriptor).getPath();
        verify(environment).getName();

        verify(view).getName();

        verify(projectService).rename(anyString(), eq(TEXT), isNull(String.class), requestCallbackArgCaptor.capture());
        AsyncRequestCallback<Void> callback = requestCallbackArgCaptor.getValue();

        TestUtil.invokeMethodByName(callback, "onFailure", Throwable.class, exception);
        verify(exception).getMessage();
        verify(notificationManager).showError(TEXT);
    }

    @Test
    public void saveButtonShouldBeClickedWhenEditorIsDirty() throws Exception {
        when(editorProvider.getEditor()).thenReturn(editor);
        initSecondConstructor();
        when(view.getRam()).thenReturn(_512);
        when(view.getName()).thenReturn(TEXT);
        when(editor.isDirty()).thenReturn(true);

        presenter.onSaveButtonClicked();

        verify(view).getRam();
        verify(environment).setRam(512);
        verify(currentProject).getProjectDescription();
        verify(descriptor).getPath();
        verify(environment).getName();

        verify(view).getName();

        verify(projectService).rename(anyString(), eq(TEXT), isNull(String.class), requestCallbackArgCaptor.capture());
        AsyncRequestCallback<Void> callback = requestCallbackArgCaptor.getValue();

        TestUtil.invokeMethodByName(callback, "onSuccess", null);
        verify(projectEnvironmentsAction).perform();

        verify(editor).isDirty();
        reset(view);

        verify(editor).doSave(editorInputCaptor.capture());
        editorInputCaptor.getValue().onSuccess(editorInput);
        verify(view).setEnableSaveButton(false);
        verify(view).setEnableCancelButton(false);
    }

    @Test
    public void saveButtonShouldBeClickedButSaveEditorFailed() throws Exception {
        when(editorProvider.getEditor()).thenReturn(editor);
        initSecondConstructor();
        when(view.getRam()).thenReturn(_512);
        when(view.getName()).thenReturn(TEXT);
        when(editor.isDirty()).thenReturn(true);

        presenter.onSaveButtonClicked();

        verify(view).getRam();
        verify(environment).setRam(512);
        verify(currentProject).getProjectDescription();
        verify(descriptor).getPath();
        verify(environment).getName();

        verify(view).getName();

        verify(projectService).rename(anyString(), eq(TEXT), isNull(String.class), requestCallbackArgCaptor.capture());
        AsyncRequestCallback<Void> callback = requestCallbackArgCaptor.getValue();

        TestUtil.invokeMethodByName(callback, "onSuccess", null);
        verify(projectEnvironmentsAction).perform();

        verify(editor).isDirty();
        reset(view);

        verify(editor).doSave(editorInputCaptor.capture());
        editorInputCaptor.getValue().onFailure(exception);
        verify(exception).getMessage();
    }

    @Test
    public void deletedButtonShouldBeClickedAndScopeIsSystem() {
        initSecondConstructor();
        presenter.onDeleteButtonClicked();

        verifyNoMoreInteractions(dialogFactory);
    }

    @Test
    public void deletedButtonShouldBeClickedAndScopeIsProjectAndDialogShouldBeShownSuccess() throws Exception {
        ArgumentCaptor<ConfirmCallback> argumentCaptor = ArgumentCaptor.forClass(ConfirmCallback.class);
        when(environment.getScope()).thenReturn(PROJECT);
        initSecondConstructor();
        presenter.onDeleteButtonClicked();

        verify(dialogFactory).createConfirmDialog(eq(TEXT), eq(TEXT), argumentCaptor.capture(), isNull(CancelCallback.class));
        verify(confirmDialog).show();
        argumentCaptor.getValue().accepted();

        verify(projectService).delete(eq(TEXT), requestCallbackArgCaptor.capture());
        AsyncRequestCallback<Void> callback = requestCallbackArgCaptor.getValue();

        reset(view);
        TestUtil.invokeMethodByName(callback, "onSuccess", null);
        verify(projectEnvironmentsAction).perform();

        buttonSaveCancelDeleteShouldBeDisable();
    }

    @Test
    public void deletedButtonShouldBeClickedAndScopeIsProjectAndDialogShouldBeShownSuccessAndAddedRemoveListenersShouldBeDeleted()
            throws Exception {
        ArgumentCaptor<ConfirmCallback> argumentCaptor = ArgumentCaptor.forClass(ConfirmCallback.class);
        when(environment.getScope()).thenReturn(PROJECT);
        initSecondConstructor();

        presenter.addListener(listener1);
        presenter.addListener(listener2);

        presenter.onDeleteButtonClicked();

        verify(dialogFactory).createConfirmDialog(eq(TEXT), eq(TEXT), argumentCaptor.capture(), isNull(CancelCallback.class));
        verify(confirmDialog).show();
        argumentCaptor.getValue().accepted();

        verify(projectService).delete(eq(TEXT), requestCallbackArgCaptor.capture());
        AsyncRequestCallback<Void> callback = requestCallbackArgCaptor.getValue();

        reset(view);
        TestUtil.invokeMethodByName(callback, "onSuccess", null);
        verify(projectEnvironmentsAction).perform();

        buttonSaveCancelDeleteShouldBeDisable();
    }

    @Test
    public void deletedButtonShouldBeClickedAndScopeIsProjectAndDialogShouldBeShownSuccessButDeletionSelectedEnvironmentFailed()
            throws Exception {
        ArgumentCaptor<ConfirmCallback> argumentCaptor = ArgumentCaptor.forClass(ConfirmCallback.class);
        when(environment.getScope()).thenReturn(PROJECT);
        initSecondConstructor();
        presenter.onDeleteButtonClicked();
        when(exception.getMessage()).thenReturn(TEXT);

        verify(dialogFactory).createConfirmDialog(eq(TEXT), eq(TEXT), argumentCaptor.capture(), isNull(CancelCallback.class));
        verify(confirmDialog).show();
        argumentCaptor.getValue().accepted();

        verify(projectService).delete(eq(TEXT), requestCallbackArgCaptor.capture());
        AsyncRequestCallback<Void> callback = requestCallbackArgCaptor.getValue();

        reset(view);
        TestUtil.invokeMethodByName(callback, "onFailure", Throwable.class, exception);

        verify(exception).getMessage();
        verify(notificationManager).showError(TEXT);
    }

    @Test
    public void changesShouldBeCancelWhenScopeIsSystemAndRunnerNotNull() {
        when(runner.getScope()).thenReturn(SYSTEM);

        reset(view);
        presenter.onCancelButtonClicked();

        verify(runner, times(2)).getScope();
        verify(view).setEnableSaveButton(false);
        verify(view).setEnableCancelButton(false);
        verify(view).setEnableDeleteButton(false);

        verify(runner).getTitle();
        verify(view).setName(TEXT);
        verify(runner, times(2)).getRAM();
        verify(view).selectMemory(_512);
        verify(runner, times(2)).getScope();
        verify(view).selectScope(SYSTEM);
    }

    @Test
    public void changesShouldBeCancelWhenScopeIsProjectAndRunnerNotNull() {
        when(runner.getScope()).thenReturn(PROJECT);

        reset(view);
        presenter.onCancelButtonClicked();

        verify(runner, times(2)).getScope();
        verify(view).setEnableSaveButton(false);
        verify(view).setEnableCancelButton(false);
        verify(view).setEnableDeleteButton(true);

        verify(runner).getTitle();
        verify(view).setName(TEXT);
        verify(runner, times(2)).getRAM();
        verify(view).selectMemory(_512);
        verify(runner, times(2)).getScope();
        verify(view).selectScope(PROJECT);
    }

    @Test
    public void changesShouldBeCancelWhenScopeIsSystemAndEnvironmentNotNull() {
        initSecondConstructor();

        reset(view);
        presenter.onCancelButtonClicked();

        verify(view).setEnableSaveButton(false);
        verify(view).setEnableCancelButton(false);
        verify(view).setEnableDeleteButton(false);

        verify(environment).getName();
        verify(view).setName(TEXT);
        verify(environment).getRam();
        verify(view).selectMemory(_512);
        verify(environment, times(3)).getScope();
        verify(view).selectScope(SYSTEM);
    }

    @Test
    public void changesShouldBeCancelWhenScopeIsProjectAndEnvironmentNotNull() {
        when(environment.getScope()).thenReturn(PROJECT);
        initSecondConstructor();

        reset(view);
        presenter.onCancelButtonClicked();

        verify(view).setEnableSaveButton(false);
        verify(view).setEnableCancelButton(false);
        verify(view).setEnableDeleteButton(true);

        verify(environment).getName();
        verify(view).setName(TEXT);
        verify(environment).getRam();
        verify(view).selectMemory(_512);
        verify(environment, times(3)).getScope();
        verify(view).selectScope(PROJECT);
    }

    @Test
    public void changesShouldBeCancelWhenScopeIsSystemAndEnvironmentNotNullAndEditorInstanceOfUndoableEditorAndEditorIsDirtyAndIsUndoable() {
        EditorPartPresenter editor2 = mock(EditorMock.class);
        when(editorProvider.getEditor()).thenReturn(editor2);
        when(file.isReadOnly()).thenReturn(true);
        HandlesUndoRedo handlesUndoRedo = mock(HandlesUndoRedo.class);


        initSecondConstructor();
        when(((UndoableEditor)editor2).getUndoRedo()).thenReturn(handlesUndoRedo);
        when(handlesUndoRedo.undoable()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(editor2.isDirty()).thenReturn(true);

        presenter.onCancelButtonClicked();

        verify(view, times(2)).setEnableSaveButton(false);
        verify(view, times(2)).setEnableCancelButton(false);
        verify(view, times(2)).setEnableDeleteButton(false);

        verify((UndoableEditor)editor2).getUndoRedo();
        verify(editor2, times(3)).isDirty();
        verify(handlesUndoRedo, times(3)).undoable();

        verify(environment).getName();
        verify(view).setName(TEXT);
        verify(environment).getRam();
        verify(view).selectMemory(_512);
        verify(environment, times(3)).getScope();
        verify(view).selectScope(SYSTEM);
    }

    @Test
    public void presenterShouldGoneContainer() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        verify(container).setWidget(view);
    }

    @Test
    public void presenterShouldGoneContainerWhenEditorNotNull() {
        initSecondConstructor();
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        verify(container).setWidget(view);
        verify(editor).activate();
        verify(editor).onOpen();
    }

    @Test
    public void environmentShouldBeUpdatedWhenScopeIsSystem() {
        reset(view);
        presenter.update(environment);

        verify(view).setEnableCancelButton(false);
        verify(view).setEnableSaveButton(false);
        verify(view).setEnableDeleteButton(false);

        verify(environment).getName();
        verify(view).setName(TEXT);
        verify(environment).getType();
        verify(view).setType(TEXT);
        verify(environment).getRam();
        verify(view).selectMemory(_512);
        verify(environment, times(2)).getScope();
        verify(view).selectScope(SYSTEM);
    }

    @Test
    public void environmentShouldBeUpdatedWhenScopeIsProject() {
        reset(view);
        when(environment.getScope()).thenReturn(PROJECT);
        presenter.update(environment);

        verify(view).setEnableCancelButton(false);
        verify(view).setEnableSaveButton(false);
        verify(view).setEnableDeleteButton(true);

        verify(environment).getName();
        verify(view).setName(TEXT);
        verify(environment).getType();
        verify(view).setType(TEXT);
        verify(environment).getRam();
        verify(view).selectMemory(_512);
        verify(environment, times(2)).getScope();
        verify(view).selectScope(PROJECT);
    }

    @Test
    public void runnerShouldBeUpdated() {
        Runner runner1 = mock(Runner.class);
        when(runner1.getRAM()).thenReturn(_512.getValue());

        reset(view);
        presenter.update(runner1);

        verify(view).setName(runner1.getTitle());
        verify(view).setType(runner1.getType());
        verify(runner1).getRAM();
        verify(view).selectMemory(_512);
        verify(view).selectScope(runner1.getScope());
    }

    @Test
    public void viewShouldBeReset() {
        reset(view);
        presenter.reset();

        verify(view).showEditor(null);
        verify(view).setName("");
        verify(view).setType("");

        verify(view).setEnableDeleteButton(false);
        verify(view).setEnableSaveButton(false);
        verify(view).setEnableCancelButton(false);
    }

    private class EditorMock extends ImageViewer implements HasReadOnlyProperty, UndoableEditor {

        public EditorMock(ImageViewerResources resources, CoreLocalizationConstant constant,
                          DialogFactory dialogFactory) {
            super(resources, constant, dialogFactory);
        }

        @Override
        public void setReadOnly(boolean b) {
            //stubbing method
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public HandlesUndoRedo getUndoRedo() {
            return null;
        }
    }
}
