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
package com.codenvy.ide.ext.runner.client.terminal;

import com.codenvy.ide.ext.runner.client.tab.TabPresenter;
import com.google.inject.ImplementedBy;

/**
 * The common representation of terminal container widget. This widget provides an ability to manager many terminal widgets for every
 * runner.
 *
 * @author Valeriy Svydenko
 */
@ImplementedBy(TerminalContainerPresenter.class)
public interface TerminalContainer extends TabPresenter {

}