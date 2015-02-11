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
package com.codenvy.ide.ext.runner.client.tabs.console.panel;

import com.google.gwt.user.client.ui.IsWidget;

import javax.annotation.Nonnull;

/**
 * The widget that provides an ability to show different messages. It contains methods for showing messages and cleaning message (removing
 * all messages from area).
 *
 * @author Andrey Plotnikov
 * @author Valeriy Svydenko
 */
public interface Console extends IsWidget {

    /**
     * Prints message with s given content.
     *
     * @param message
     *         message that needs to be printed
     */
    void print(@Nonnull String message);

    /**
     * Prints Info message with a given content.
     * Printed line will look like this: [INFO] some string
     *
     * @param line
     *         line that needs to be printed
     */
    void printInfo(@Nonnull String line);

    /**
     * Prints Error message with a given content.
     * Printed line will look like this: [ERROR] some string
     *
     * @param line
     *         line that needs to be printed
     */
    void printError(@Nonnull String line);

    /**
     * Prints Warning message with a given content.
     * Printed line will look like this: [WARNING] some string
     *
     * @param line
     *         line that needs to be printed
     */
    void printWarn(@Nonnull String line);

    /** Scroll to bottom of the view. */
    void scrollBottom();

    /** Removes all messages from widget. */
    void clear();

    /**
     * Changes visibility of the console.
     *
     * @param isVisible
     *         <code>true</code> console is visible,<code>false</code> console isn't visible
     */
    void setVisible(boolean isVisible);

}