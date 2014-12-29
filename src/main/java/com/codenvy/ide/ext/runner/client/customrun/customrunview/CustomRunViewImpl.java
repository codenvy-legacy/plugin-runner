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
package com.codenvy.ide.ext.runner.client.customrun.customrunview;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.customrun.RunnerDataAdapter;
import com.codenvy.ide.ext.runner.client.customrun.RunnerRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class provides graphical implementation of dialog window to change settings of custom environments.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public class CustomRunViewImpl extends Window implements CustomRunView {

    interface CustomRunViewImplUiBinder extends UiBinder<Widget, CustomRunViewImpl> {
    }

    private static final CustomRunViewImplUiBinder UI_BINDER = GWT.create(CustomRunViewImplUiBinder.class);

    private static final String MEMORY_128   = "128MB";
    private static final String MEMORY_256   = "256MB";
    private static final String MEMORY_512   = "512MB";
    private static final String MEMORY_1024  = "1GB";
    private static final String MEMORY_2048  = "2GB";
    private static final String OTHER_MEMORY = "Other (MB):";

    @UiField
    Label       noEnvLabel;
    @UiField
    SimplePanel treeContainer;
    @UiField
    TextBox     memoryTotal;
    @UiField
    TextBox     memoryAvailable;
    @UiField
    CheckBox    skipBuild;
    @UiField
    CheckBox    rememberRunMemory;
    @UiField
    TextArea    descriptionField;
    @UiField
    RadioButton radioButOther;
    @UiField
    TextBox     otherValueMemory;
    @UiField
    FlowPanel   memoryPanel1;
    @UiField
    FlowPanel   memoryPanel2;

    @UiField(provided = true)
    final RunnerLocalizationConstant locale;
    @UiField(provided = true)
    final RunnerResources            resources;

    private final List<RadioButton>     radioButtons;
    private final RunnerEnvironmentTree rootNode;

    private ActionDelegate actionDelegate;
    private Tree<Object>   tree;
    private Button         runButton;

    @Inject
    public CustomRunViewImpl(RunnerLocalizationConstant locale,
                             RunnerResources runnerResources,
                             com.codenvy.ide.Resources resources,
                             DtoFactory dtoFactory,
                             RunnerDataAdapter runnerDataAdapter,
                             RunnerRenderer runnerRenderer) {
        this.locale = locale;
        this.resources = runnerResources;

        setWidget(UI_BINDER.createAndBindUi(this));
        setTitle(locale.customRunnerTitle());

        rootNode = dtoFactory.createDto(RunnerEnvironmentTree.class);
        radioButtons = new ArrayList<>();

        tree = Tree.create(resources, runnerDataAdapter, runnerRenderer);
        treeContainer.setWidget(noEnvLabel);
        tree.getModel().setRoot(rootNode);
        tree.setTreeEventHandler(new Tree.Listener<Object>() {
            @Override
            public void onNodeAction(TreeNodeElement<Object> treeNodeElement) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<Object> treeNodeElement) {
            }

            @Override
            public void onNodeContextMenu(int i, int i2, TreeNodeElement<Object> treeNodeElement) {
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Object> treeNodeElement, MouseEvent mouseEvent) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Object> treeNodeElement, MouseEvent mouseEvent) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<Object> treeNodeElement) {
            }

            @Override
            public void onNodeSelected(TreeNodeElement<Object> treeNodeElement, SignalEvent signalEvent) {
                Object data = treeNodeElement.getData();

                boolean isLeaf = data instanceof RunnerEnvironmentLeaf;

                actionDelegate.onEnvironmentSelected(isLeaf ? ((RunnerEnvironmentLeaf)data).getEnvironment() : null);
            }

            @Override
            public void onRootContextMenu(int i, int i2) {
            }

            @Override
            public void onRootDragDrop(MouseEvent mouseEvent) {
            }

            @Override
            public void onKeyboard(KeyboardEvent keyboardEvent) {
            }
        });

        for (int i = 0; i < memoryPanel1.getWidgetCount(); i++) {
            radioButtons.add((RadioButton)memoryPanel1.getWidget(i));
            radioButtons.add((RadioButton)memoryPanel2.getWidget(i));
        }

        createButtons();
    }

    private void createButtons() {
        runButton = createButton(locale.actionRun(), "project-customRun-run", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                actionDelegate.onRunClicked();
            }
        });
        runButton.addStyleName(resources.runnerCss().runButton());

        Button cancelButton = createButton(locale.customRunnerButtonCancel(), "project-customRun-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                actionDelegate.onCancelClicked();
            }
        });
        cancelButton.addStyleName(resources.runnerCss().cancelButton());

        getFooter().add(runButton);
        getFooter().add(cancelButton);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledRadioButtons(int workspaceRam) {
        for (RadioButton radioButton : radioButtons) {
            int runnerMemory = getMemoryValue(radioButton.getText());

            radioButton.setEnabled(runnerMemory > 0 && runnerMemory <= workspaceRam);
        }

        radioButOther.setEnabled(true);
    }

    private int getMemoryValue(@Nonnull String memorySize) {
        switch (memorySize) {
            case MEMORY_128:
                return 128;
            case MEMORY_256:
                return 256;
            case MEMORY_512:
                return 512;
            case MEMORY_1024:
                return 1024;
            case MEMORY_2048:
                return 2048;
            case OTHER_MEMORY:
                return getIntegerValue(otherValueMemory.getText());
            default:
                return 256;
        }
    }

    private int getIntegerValue(@Nonnull String ramValue) {
        try {
            return Integer.parseInt(ramValue);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnvironmentDescription(@Nullable String description) {
        descriptionField.setText(description);
    }

    /** {@inheritDoc} */
    @Override
    public void setRunButtonState(boolean enabled) {
        runButton.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void addRunner(@Nonnull RunnerEnvironmentTree environmentTree) {
        rootNode.getNodes().add(environmentTree);
        tree.renderTree(1);
        checkTreeVisibility(environmentTree);
    }

    private void checkTreeVisibility(@Nonnull RunnerEnvironmentTree environmentTree) {
        boolean isTreeEmpty = environmentTree.getNodes().isEmpty() && environmentTree.getLeaves().isEmpty();

        treeContainer.setWidget(isTreeEmpty ? noEnvLabel : tree);
    }

    /** {@inheritDoc} */
    @Override
    public int getRunnerMemorySize() {
        for (RadioButton radioButton : radioButtons) {
            if (radioButton.getValue()) {
                return getMemoryValue(radioButton.getText());
            }
        }
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setRunnerMemorySize(@Nonnull String memorySize) {
        resetRadioButtons();
        otherValueMemory.setText("");

        int index;
        switch (memorySize) {
            case MEMORY_128:
                index = 0;
                break;
            case MEMORY_1024:
                index = 1;
                break;
            case MEMORY_256:
                index = 2;
                break;
            case MEMORY_2048:
                index = 3;
                break;
            case MEMORY_512:
                index = 4;
                break;
            default:
                index = 5; //index = 5 corresponds to 'Other'- radioButton
                otherValueMemory.setText(memorySize);
        }

        radioButtons.get(index).setValue(true);
    }

    private void resetRadioButtons() {
        for (RadioButton radioButton : radioButtons) {
            radioButton.setValue(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalMemorySize() {
        return getIntegerValue(memoryTotal.getText());
    }

    /** {@inheritDoc} */
    @Override
    public void setTotalMemorySize(@Nonnull String memorySize) {
        this.memoryTotal.setText(memorySize);
    }

    /** {@inheritDoc} */
    @Override
    public int getAvailableMemorySize() {
        return getIntegerValue(memoryAvailable.getText());
    }

    /** {@inheritDoc} */
    @Override
    public void setAvailableMemorySize(@Nonnull String memorySize) {
        this.memoryAvailable.setText(memorySize);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        onClose();
        hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        runButton.setEnabled(false);
        rootNode.getNodes().clear();
        tree.renderTree();

        show();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSkipBuildSelected() {
        return skipBuild.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRememberOptionsSelected() {
        return rememberRunMemory.getValue();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
        descriptionField.setText("");
        skipBuild.setValue(false);
        rememberRunMemory.setValue(false);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate actionDelegate) {
        this.actionDelegate = actionDelegate;
    }

    @UiHandler("radioButOther")
    void otherValueHandler(@SuppressWarnings("UnusedParameters") ValueChangeEvent<Boolean> event) {
        otherValueMemory.setFocus(true);
    }
}