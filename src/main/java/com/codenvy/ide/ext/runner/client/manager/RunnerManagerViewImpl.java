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
package com.codenvy.ide.ext.runner.client.manager;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ext.runner.client.RunnerResources;
import com.codenvy.ide.ext.runner.client.inject.factories.WidgetFactory;
import com.codenvy.ide.ext.runner.client.manager.button.ButtonWidget;
import com.codenvy.ide.ext.runner.client.manager.info.MoreInfo;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.tabs.container.TabContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class provides view representation of runner panel.
 *
 * @author Dmitry Shnurenko
 * @author Valeriy Svydenko
 */
public class RunnerManagerViewImpl extends BaseView<RunnerManagerView.ActionDelegate> implements RunnerManagerView {
    interface RunnerManagerViewImplUiBinder extends UiBinder<Widget, RunnerManagerViewImpl> {
    }

    private static final RunnerManagerViewImplUiBinder UI_BINDER = GWT.create(RunnerManagerViewImplUiBinder.class);

    private static final String GWT_POPUP_STANDARD_STYLE = "gwt-PopupPanel";
    private static final String SPLITTER_STYLE_NAME      = "gwt-SplitLayoutPanel-HDragger";

    private static final int SHIFT_LEFT     = 100;
    private static final int SHIFT_TOP      = 130;
    private static final int SPLITTER_WIDTH = 2;

    @UiField(provided = true)
    SplitLayoutPanel mainPanel;

    @UiField
    SimplePanel leftTabsPanel;

    @UiField
    FlowPanel   otherButtonsPanel;
    @UiField
    FlowPanel   runButtonPanel;
    @UiField
    SimplePanel rightPanel;

    //info panel
    @UiField
    Label     appReference;
    @UiField
    FlowPanel moreInfoPanel;
    @UiField
    Label     timeout;
    @UiField
    Image     image;

    @UiField(provided = true)
    final RunnerResources            resources;
    @UiField(provided = true)
    final RunnerLocalizationConstant locale;

    private final WidgetFactory widgetFactory;
    private final PopupPanel    popupPanel;
    private final MoreInfo      moreInfoWidget;

    private ButtonWidget run;
    private ButtonWidget stop;
    private ButtonWidget clean;
    private ButtonWidget docker;

    private String url;

    @Inject
    public RunnerManagerViewImpl(PartStackUIResources partStackUIResources,
                                 RunnerResources resources,
                                 RunnerLocalizationConstant locale,
                                 WidgetFactory widgetFactory,
                                 PopupPanel popupPanel) {
        super(partStackUIResources);

        this.resources = resources;
        this.locale = locale;
        this.widgetFactory = widgetFactory;
        this.moreInfoWidget = widgetFactory.createMoreInfo();
        this.mainPanel = new SplitLayoutPanel(SPLITTER_WIDTH);

        titleLabel.setText(locale.runnersPanelTitle());
        container.add(UI_BINDER.createAndBindUi(this));

        this.mainPanel.setWidgetMinSize(leftTabsPanel, 165);

        this.popupPanel = popupPanel;
        this.popupPanel.removeStyleName(GWT_POPUP_STANDARD_STYLE);
        this.popupPanel.add(moreInfoWidget);

        addMoreInfoPanelHandler();

        changeSplitterStyle();

        initializeButtons();
    }

    private void addMoreInfoPanelHandler() {
        moreInfoPanel.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                image.addStyleName(resources.runnerCss().opacityButton());

                delegate.onMoreInfoBtnMouseOver();
            }
        }, MouseOverEvent.getType());

        moreInfoPanel.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                image.removeStyleName(resources.runnerCss().opacityButton());

                popupPanel.hide();
            }
        }, MouseOutEvent.getType());
    }

    private void changeSplitterStyle() {
        int widgetCount = mainPanel.getWidgetCount();

        for (int i = 0; i < widgetCount; i++) {
            Widget widget = mainPanel.getWidget(i);
            String styleName = widget.getStyleName();

            if (SPLITTER_STYLE_NAME.equals(styleName)) {
                widget.removeStyleName(styleName);
                widget.addStyleName(resources.runnerCss().splitter());
            }
        }
    }

    private void initializeButtons() {
        ButtonWidget.ActionDelegate runDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onRunButtonClicked();
            }
        };
        run = createButton(resources.runButton(), locale.tooltipRunButton(), runDelegate, runButtonPanel);
        run.setEnable();

        ButtonWidget.ActionDelegate stopDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onStopButtonClicked();
            }
        };
        stop = createButton(resources.stopButton(), locale.tooltipStopButton(), stopDelegate, otherButtonsPanel);

        ButtonWidget.ActionDelegate cleanDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onCleanConsoleButtonClicked();
            }
        };
        clean = createButton(resources.cleanButton(), locale.tooltipCleanButton(), cleanDelegate, otherButtonsPanel);

        ButtonWidget.ActionDelegate dockerDelegate = new ButtonWidget.ActionDelegate() {
            @Override
            public void onButtonClicked() {
                delegate.onDockerButtonClicked();
            }
        };
        docker = createButton(resources.dockerButton(), locale.tooltipDockerButton(), dockerDelegate, otherButtonsPanel);
    }

    @Nonnull
    private ButtonWidget createButton(@Nonnull ImageResource icon,
                                      @Nonnull String prompt,
                                      @Nonnull ButtonWidget.ActionDelegate delegate,
                                      @Nonnull FlowPanel buttonPanel) {
        ButtonWidget button = widgetFactory.createButton(prompt, icon);
        button.setDelegate(delegate);
        button.setDisable();

        buttonPanel.add(button);

        return button;
    }

    /** {@inheritDoc} */
    @Override
    public void update(@Nonnull Runner runner) {
        changeButtonsState(runner);

        moreInfoWidget.update(runner);
    }

    private void changeButtonsState(@Nonnull Runner runner) {
        run.setEnable();
        stop.setEnable();
        clean.setEnable();
        docker.setEnable();

        switch (runner.getStatus()) {
            case IN_QUEUE:
                run.setDisable();
                docker.setDisable();
                stop.setDisable();
                break;

            case FAILED:
                stop.setDisable();
                docker.setDisable();
                break;

            case STOPPED:
                stop.setDisable();
                break;

            default:
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationURl(@Nullable String applicationUrl) {
        url = null;
        appReference.removeStyleName(resources.runnerCss().cursor());

        if (applicationUrl != null && applicationUrl.startsWith("http")) {
            url = applicationUrl;
            appReference.addStyleName(resources.runnerCss().cursor());
        }

        appReference.setText(applicationUrl);
    }

    /** {@inheritDoc} */
    @Override
    public void setTimeout(@Nonnull String timeoutValue) {
        timeout.setText(timeoutValue);
    }

    /** {@inheritDoc} */
    @Override
    public void showMoreInfoPopup(@Nonnull Runner runner) {
        moreInfoWidget.update(runner);

        int x = timeout.getAbsoluteLeft() - SHIFT_LEFT;
        int y = timeout.getAbsoluteTop() - SHIFT_TOP;

        popupPanel.setPopupPosition(x, y);
        popupPanel.show();
    }

    /** {@inheritDoc} */
    @Override
    public void updateMoreInfoPopup(@Nonnull Runner runner) {
        moreInfoWidget.update(runner);
    }

    /** {@inheritDoc} */
    @Override
    public void setLeftPanel(@Nonnull TabContainer containerPresenter) {
        containerPresenter.go(leftTabsPanel);
    }

    /** {@inheritDoc} */
    @Override
    public void setRightPanel(@Nonnull TabContainer containerPresenter) {
        containerPresenter.go(rightPanel);
    }

    /** {@inheritDoc} */
    @Override
    public void hideOtherButtons() {
        otherButtonsPanel.setVisible(false);
    }

    /** {@inheritDoc} */
    @Override
    public void showOtherButtons() {
        otherButtonsPanel.setVisible(true);
    }

    @UiHandler("appReference")
    public void onAppReferenceClicked(@SuppressWarnings("UnusedParameters") ClickEvent clickEvent) {
        if (url != null) {
            Window.open(url, "_blank", "");
        }
    }
}