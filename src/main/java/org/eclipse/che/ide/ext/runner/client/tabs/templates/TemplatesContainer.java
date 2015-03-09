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
package org.eclipse.che.ide.ext.runner.client.tabs.templates;

import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.tabs.common.TabPresenter;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;

import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Provides methods which allow work with templates panel.
 *
 * @author Dmitry Shnurenko
 */
@ImplementedBy(TemplatesPresenter.class)
public interface TemplatesContainer extends TabPresenter {
    /**
     * Calls special method on view which set current environment selected.
     *
     * @param environment
     *         environment which was selected
     */
    void select(@Nonnull Environment environment);

    /**
     * Calls method on view which adds environment widget on templates panel.
     *
     * @param environments
     *         list of environments which need add
     * @param scope
     *         scope of environments which are saved in list
     */
    void addEnvironments(@Nonnull List<Environment> environments, @Nonnull Scope scope);

    /**
     * Adds language type buttons on templates panel.
     *
     * @param tree
     *         tree which need analyze to defined quantity of language type buttons which need add on panel
     */
    void addButton(@Nonnull RunnerEnvironmentTree tree);

    /** Shows system environments when user click on templates tab the first time. */
    void showSystemEnvironments();

}