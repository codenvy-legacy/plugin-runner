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
package com.codenvy.ide.ext.runner.client.customenvironment.customenvironmentview;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.google.gwt.event.dom.client.KeyCodes.KEY_ENTER;

/**
 * The Class provides graphical implementation of dialog window to add,edit or remove custom environments.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
public class CustomEnvironmentViewImpl extends Window implements CustomEnvironmentView {

    interface CustomEnvironmentViewImplUiBinder extends UiBinder<Widget, CustomEnvironmentViewImpl> {
    }

    private static final CustomEnvironmentViewImplUiBinder UI_BINDER = GWT.create(CustomEnvironmentViewImplUiBinder.class);

    @UiField
    ScrollPanel listPanel;

    private Button            btnRemove;
    private Button            btnEdit;
    private CellTable<String> environmentsTable;
    private ActionDelegate    delegate;

    @Inject
    public CustomEnvironmentViewImpl(RunnerLocalizationConstant locale,
                                     RunnerResources runnerResources,
                                     com.codenvy.ide.Resources resources) {

        setTitle(locale.customEnvironmentsTitle());
        setWidget(UI_BINDER.createAndBindUi(this));

        createButtons(locale, runnerResources);

        createTable(locale, resources);
    }

    private void createTable(@Nonnull RunnerLocalizationConstant locale, @Nonnull com.codenvy.ide.Resources resources) {
        environmentsTable = new CellTable<>(15, resources);

        Column<String, String> nameColumn = new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String customEnvironment) {
                return customEnvironment;
            }
        };
        nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        environmentsTable.addColumn(nameColumn);

        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<>();

        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                delegate.onEnvironmentSelected(selectionModel.getSelectedObject());
            }
        });
        environmentsTable.setSelectionModel(selectionModel);

        environmentsTable.addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if (selectionModel.getSelectedObject() != null) {
                    delegate.onEditBtnClicked();
                }
            }
        }, DoubleClickEvent.getType());

        environmentsTable.addDomHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (KEY_ENTER == event.getNativeKeyCode() && selectionModel.getSelectedObject() != null) {
                    delegate.onEditBtnClicked();
                }
            }
        }, KeyUpEvent.getType());

        environmentsTable.setEmptyTableWidget(new Label(locale.customEnvironmentsEmptyTable()));
        environmentsTable.setWidth("100%");
        listPanel.add(environmentsTable);
    }

    private void createButtons(@Nonnull RunnerLocalizationConstant locale, @Nonnull RunnerResources resources) {
        Button btnAdd = createButton(locale.customEnvironmentsButtonAdd(),
                                     new SVGImage(resources.addEnvironmentImage()),
                                     "customEnvironments-add",
                                     new ClickHandler() {
                                         @Override
                                         public void onClick(ClickEvent event) {
                                             delegate.onAddBtnClicked();
                                         }
                                     });
        btnAdd.getElement().getStyle().setFloat(Style.Float.LEFT);
        btnAdd.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);

        btnRemove = createButton(locale.customEnvironmentsButtonRemove(),
                                 new SVGImage(resources.removeEnvironmentImage()),
                                 "customEnvironments-remove",
                                 new ClickHandler() {
                                     @Override
                                     public void onClick(ClickEvent event) {
                                         delegate.onRemoveBtnClicked();
                                     }
                                 });
        btnRemove.getElement().getStyle().setFloat(Style.Float.LEFT);

        btnEdit = createButton(locale.customEnvironmentsButtonEdit(),
                               new SVGImage(resources.editEnvironmentImage()),
                               "customEnvironments-edit",
                               new ClickHandler() {
                                   @Override
                                   public void onClick(ClickEvent event) {
                                       delegate.onEditBtnClicked();
                                   }
                               });
        btnEdit.getElement().getStyle().setFloat(Style.Float.LEFT);

        final Button btnClose = createButton(locale.customEnvironmentsButtonClose(), "customEnvironments-close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseBtnClicked();
            }
        });
        btnClose.addStyleName(resources.runnerCss().blueButton());
        btnClose.getElement().getStyle().setMarginRight(10, Style.Unit.PX);


        getFooter().add(btnAdd);
        getFooter().add(btnEdit);
        getFooter().add(btnRemove);
        getFooter().add(btnClose);
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
        delegate.onCloseBtnClicked();
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoveButtonEnabled(boolean isEnabled) {
        btnRemove.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEditButtonEnabled(boolean isEnabled) {
        btnEdit.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setRowData(@Nonnull Array<String> environments) {
        List<String> environmentList = new ArrayList<>();

        for (String environmentName : environments.asIterable()) {
            environmentList.add(environmentName);
        }
        environmentsTable.setRowData(environmentList);
    }

    /** {@inheritDoc} */
    @Override
    public void selectEnvironment(@Nonnull String environment) {
        environmentsTable.getSelectionModel().setSelected(environment, true);
        delegate.onEnvironmentSelected(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void closeDialog() {
        hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        show();
    }

}