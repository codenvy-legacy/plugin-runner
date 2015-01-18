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
package com.codenvy.ide.ext.runner.client.customrun;

import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;

import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.MEMORY_1024;
import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.MEMORY_128;
import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.MEMORY_2048;
import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.MEMORY_256;
import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.MEMORY_512;
import static com.codenvy.ide.ext.runner.client.customrun.MemorySize.OTHER_MEMORY;

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
    TextBox     otherValueMemory;

    //radio buttons
    @UiField
    RadioButton memory128;
    @UiField
    RadioButton memory256;
    @UiField
    RadioButton memory512;
    @UiField
    RadioButton memory1024;
    @UiField
    RadioButton memory2048;
    @UiField
    RadioButton otherMemory;

    @UiField(provided = true)
    final RunnerLocalizationConstant locale;
    @UiField(provided = true)
    final RunnerResources            resources;

    private final Map<MemorySize, RadioButton> buttonsMap;
    private final RunnerEnvironmentTree        rootNode;

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

        buttonsMap = new EnumMap<>(MemorySize.class);

        memory128.setText(MEMORY_128.toString());
        memory256.setText(MEMORY_256.toString());
        memory512.setText(MEMORY_512.toString());
        memory1024.setText(MEMORY_1024.toString());
        memory2048.setText(MEMORY_2048.toString());

        buttonsMap.put(MEMORY_128, memory128);
        buttonsMap.put(MEMORY_256, memory256);
        buttonsMap.put(MEMORY_512, memory512);
        buttonsMap.put(MEMORY_1024, memory1024);
        buttonsMap.put(MEMORY_2048, memory2048);
        buttonsMap.put(OTHER_MEMORY, otherMemory);

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
    public void setEnabledRadioButtons(@Nonnegative int workspaceRam) {
        for (RadioButton radioButton : buttonsMap.values()) {
            int runnerMemory = MemorySize.getItemByValue(radioButton.getText()).getValue();

            radioButton.setEnabled(runnerMemory > 0 && runnerMemory <= workspaceRam);
        }

        otherMemory.setEnabled(true);
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
        for (RadioButton radioButton : buttonsMap.values()) {
            if (radioButton.getValue()) {
                return checkAndGetMemory(radioButton.getText());
            }
        }
        return 0;
    }

    private int checkAndGetMemory(@Nonnull String memory) {
        MemorySize memorySize = MemorySize.getItemByValue(memory);

        boolean isOtherRadioButton = OTHER_MEMORY.equals(memorySize);

        return isOtherRadioButton ? getIntegerValue(otherValueMemory.getText()) : memorySize.getValue();
    }

    private int getIntegerValue(@Nonnull String memoryValue) {
        try {
            return Integer.parseInt(memoryValue);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void chooseMemorySizeRadioButton(@Nonnull MemorySize memorySize) {
        for (RadioButton radioButton : buttonsMap.values()) {
            radioButton.setValue(false);
        }

        otherValueMemory.setText("");

        buttonsMap.get(memorySize).setValue(true);
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalMemorySize() {
        return getIntegerValue(memoryTotal.getValue());
    }

    /** {@inheritDoc} */
    @Override
    public void setTotalMemorySize(@Nonnull String memorySize) {
        this.memoryTotal.setText(memorySize);
    }

    /** {@inheritDoc} */
    @Override
    public int getAvailableMemorySize() {
        return getIntegerValue(memoryAvailable.getValue());
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

    @UiHandler("otherMemory")
    void otherValueHandler(@SuppressWarnings("UnusedParameters") ValueChangeEvent<Boolean> event) {
        otherValueMemory.setFocus(true);
    }
}