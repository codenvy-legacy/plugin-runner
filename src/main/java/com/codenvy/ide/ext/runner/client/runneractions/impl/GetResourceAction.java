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
package com.codenvy.ide.ext.runner.client.runneractions.impl;

import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.ext.runner.client.callbacks.AsyncCallbackBuilder;
import com.codenvy.ide.ext.runner.client.callbacks.SuccessCallback;
import com.codenvy.ide.ext.runner.client.inject.factories.RunnerActionFactory;
import com.codenvy.ide.ext.runner.client.models.Runner;
import com.codenvy.ide.ext.runner.client.runneractions.AbstractRunnerAction;
import com.codenvy.ide.ext.runner.client.util.RunnerUtil;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;

/**
 * Action for getting resources (total and used memory) from current runner.
 *
 * @author Dmitry Shnurenko
 */
public class GetResourceAction extends AbstractRunnerAction {

    private final RunnerServiceClient                                 service;
    private final RunAction                                           runAction;
    private final Provider<AsyncCallbackBuilder<ResourcesDescriptor>> callbackBuilderProvider;
    private final RunnerUtil                                          util;

    @Inject
    public GetResourceAction(RunnerServiceClient service,
                             RunnerActionFactory actionFactory,
                             Provider<AsyncCallbackBuilder<ResourcesDescriptor>> callbackBuilderProvider,
                             RunnerUtil util) {
        this.service = service;
        this.runAction = actionFactory.createRun();
        this.callbackBuilderProvider = callbackBuilderProvider;
        this.util = util;

        addAction(runAction);
    }

    /** {@inheritDoc} */
    @Override
    public void perform(@Nonnull final Runner runner) {
        AsyncRequestCallback<ResourcesDescriptor> callback = callbackBuilderProvider
                .get()
                .unmarshaller(ResourcesDescriptor.class)
                .success(new SuccessCallback<ResourcesDescriptor>() {
                    @Override
                    public void onSuccess(ResourcesDescriptor resourcesDescriptor) {
                        int totalMemory = Integer.valueOf(resourcesDescriptor.getTotalMemory());
                        int usedMemory = Integer.valueOf(resourcesDescriptor.getUsedMemory());
                        int availableMemory = totalMemory - usedMemory;

                        boolean isCorrectMemory = util.isRunnerMemoryCorrect(totalMemory, usedMemory, availableMemory);

                        if (!isCorrectMemory) {
                            return;
                        }

                        runner.setRAM(totalMemory);

                        runAction.perform(runner);
                    }
                })
                .build();

        service.getResources(callback);
    }
}