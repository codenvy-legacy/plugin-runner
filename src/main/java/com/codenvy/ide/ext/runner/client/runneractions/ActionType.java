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
package com.codenvy.ide.ext.runner.client.runneractions;

/**
 * It contains a list of available types of runner actions. It needs for creating an action by type.
 *
 * @author Andrey Plotnikov
 */
public enum ActionType {
    RUN,
    STOP,
    GET_LOGS,
    GET_RUNNING_PROCESS,
    GET_RESOURCES
}