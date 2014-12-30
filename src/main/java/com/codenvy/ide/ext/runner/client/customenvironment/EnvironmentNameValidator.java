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
package com.codenvy.ide.ext.runner.client.customenvironment;

import com.codenvy.ide.ext.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ui.dialogs.input.InputValidator;
import com.codenvy.ide.util.NameUtils;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Special class for validation custom environments names. Names mustn't contain spaces and dots and must correspond special pattern.
 *
 * @author Artem Zatsarynnyy
 * @author Dmitry Shnurenko
 */
//TODO this class must be improved. It is issue which describes problem https://jira.codenvycorp.com/browse/IDEX-1830
public class EnvironmentNameValidator implements InputValidator {

    private final RunnerLocalizationConstant locale;

    @Inject
    public EnvironmentNameValidator(RunnerLocalizationConstant locale) {
        this.locale = locale;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public Violation validate(@Nonnull String value) {

        if (value.contains(" ")) {
            return new Violation() {
                @Nullable
                @Override
                public String getMessage() {
                    return locale.validatorSpaceNotAllowed();
                }
            };
        }

        if (value.contains(".")) {
            return new Violation() {
                @Nullable
                @Override
                public String getMessage() {
                    return locale.validatorDotsNotAllowed();
                }
            };
        }

        if (!NameUtils.checkFolderName(value)) {
            return new Violation() {
                @Nullable
                @Override
                public String getMessage() {
                    return locale.validatorNameInvalid();
                }
            };
        }

        return null;
    }

}