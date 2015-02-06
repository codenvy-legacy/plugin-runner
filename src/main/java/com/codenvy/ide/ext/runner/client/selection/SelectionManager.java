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
package com.codenvy.ide.ext.runner.client.selection;

import com.codenvy.ide.ext.runner.client.models.Runner;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey Plotnikov
 */
@Singleton
public class SelectionManager {

    private final List<SelectionChangeListener> listeners;
    private       Runner                        selectedRunner;
    private       String                        selectedEnvironment;

    @Inject
    public SelectionManager() {
        listeners = new ArrayList<>();
    }

    @Nullable
    public Runner getRunner() {
        return selectedRunner;
    }

    public void setRunner(@Nullable Runner selectedRunner) {
        this.selectedRunner = selectedRunner;
        notifyListeners(Selection.RUNNER);
    }

    @Nullable
    public String getEnvironment() {
        return selectedEnvironment;
    }

    public void setEnvironment(@Nullable String selectedEnvironment) {
        this.selectedEnvironment = selectedEnvironment;
        notifyListeners(Selection.ENVIRONMENT);
    }

    public void addListener(@Nonnull SelectionChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(@Nonnull Selection selection) {
        for (SelectionChangeListener listener : listeners) {
            listener.onSelectionChanged(selection);
        }
    }

    public interface SelectionChangeListener {
        void onSelectionChanged(@Nonnull Selection selection);
    }

}