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
package com.codenvy.ide.ext.runner.client.util;

import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Contains implementations of methods which are general for runner plugin classes.
 *
 * @author Dmitry Shnurenko
 */
@Singleton
public class RunnerUtilImpl implements RunnerUtil {

    private static final String MULTIPLE_RAM_SIZE = "128";

    private final DialogFactory              dialogFactory;
    private final RunnerLocalizationConstant locale;

    @Inject
    public RunnerUtilImpl(DialogFactory dialogFactory, RunnerLocalizationConstant locale) {
        this.dialogFactory = dialogFactory;
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRunnerMemoryCorrect(@Nonnegative int totalMemory, @Nonnegative int usedMemory) {
        int availableMemory = totalMemory - usedMemory;

        if (usedMemory < 0 || totalMemory < 0 || availableMemory < 0) {
            showWarning(locale.messagesIncorrectValue());
            return false;
        }

        if (usedMemory < 0 || usedMemory % 128 != 0) {
            showWarning(locale.ramSizeMustBeMultipleOf(MULTIPLE_RAM_SIZE));
            return false;
        }

        if (usedMemory > totalMemory) {
            showWarning(locale.messagesTotalRamLessCustom(usedMemory, totalMemory));
            return false;
        }

        if (usedMemory > availableMemory) {
            showWarning(locale.messagesAvailableRamLessCustom(usedMemory, totalMemory, totalMemory - availableMemory));
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void showWarning(@Nonnull String warningMessage) {
        dialogFactory.createMessageDialog("Warning", warningMessage, null).show();
    }

}