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
package org.eclipse.che.ide.ext.runner.client.util;

import org.eclipse.che.api.project.shared.dto.ProjectTypeDefinition;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentLeaf;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.ide.api.project.type.ProjectTypeRegistry;
import org.eclipse.che.ide.ext.runner.client.inject.factories.ModelsFactory;
import org.eclipse.che.ide.ext.runner.client.models.Environment;
import org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.eclipse.che.ide.ext.runner.client.tabs.properties.panel.common.Scope.SYSTEM;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrienko Alexander
 * @author Dmitry Shnurenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class GetEnvironmentsUtilImplTest {

    private static final String PROJECT_TYPE = "some project type";
    private static final String CATEGORY1    = "Category1";

    //mocks for constructor
    @Mock
    private RunnerEnvironmentTree tree;
    @Mock
    private ModelsFactory         modelsFactory;
    @Mock
    private ProjectTypeRegistry   projectTypeRegistry;

    //runnerEnvironmentTrees
    @Mock
    private RunnerEnvironmentTree runnerEnvTree1;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree2;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree3;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree4;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree5;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree6;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree7;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree8;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree9;
    @Mock
    private RunnerEnvironmentTree runnerEnvTree10;

    //runnerEnvironmentLeafs
    @Mock
    private RunnerEnvironmentLeaf runnerEnvLeaf1;
    @Mock
    private RunnerEnvironmentLeaf runnerEnvLeaf2;
    @Mock
    private RunnerEnvironmentLeaf runnerEnvLeaf3;
    @Mock
    private RunnerEnvironmentLeaf runnerEnvLeaf4;
    @Mock
    private RunnerEnvironmentLeaf runnerEnvLeaf5;
    @Mock
    private RunnerEnvironmentLeaf runnerEnvLeaf6;

    @Mock
    private RunnerEnvironment runEnvironment1;
    @Mock
    private RunnerEnvironment runEnvironment2;

    @Mock
    private Environment environment1;
    @Mock
    private Environment environment2;

    @Mock
    private ProjectTypeDefinition definition;

    @InjectMocks
    private GetEnvironmentsUtilImpl getEnvironmentsUtil;

    @Before
    public void setUp() {
        generateDifficultTree();

        List<String> runnerCategoryList = Arrays.asList(CATEGORY1, "Category2", "Category3");
        when(projectTypeRegistry.getProjectType(PROJECT_TYPE)).thenReturn(definition);
        when(definition.getRunnerCategories()).thenReturn(runnerCategoryList);
        when(tree.getNode(CATEGORY1.toLowerCase())).thenReturn(runnerEnvTree6);
        when(tree.getDisplayName()).thenReturn(CATEGORY1);
    }

    @Test
    public void allEnvironmentsShouldBeReturned() {
        List<RunnerEnvironmentLeaf> result = getEnvironmentsUtil.getAllEnvironments(tree);

        assertThat(result.size(), is(6));
        assertThat(result, hasItems(runnerEnvLeaf1, runnerEnvLeaf2, runnerEnvLeaf3, runnerEnvLeaf4, runnerEnvLeaf5, runnerEnvLeaf6));
    }

    @Test
    public void environmentsFromNodeShouldBeReturned() {
        Environment environment1 = mock(Environment.class);
        Environment environment2 = mock(Environment.class);
        RunnerEnvironment runnerEnv1 = mock(RunnerEnvironment.class);
        RunnerEnvironment runnerEnv2 = mock(RunnerEnvironment.class);

        when(runnerEnvLeaf1.getEnvironment()).thenReturn(runnerEnv1);
        when(runnerEnvLeaf2.getEnvironment()).thenReturn(runnerEnv2);

        when(modelsFactory.createEnvironment(runnerEnv1, Scope.SYSTEM)).thenReturn(environment1);
        when(modelsFactory.createEnvironment(runnerEnv2, Scope.SYSTEM)).thenReturn(environment2);

        List<Environment> result = getEnvironmentsUtil.getEnvironmentsFromNodes(Arrays.asList(runnerEnvLeaf1, runnerEnvLeaf2),
                                                                                Scope.SYSTEM);

        assertThat(result.size(), is(2));
        assertThat(result, hasItems(environment1, environment2));

        verify(modelsFactory).createEnvironment(runnerEnv1, Scope.SYSTEM);
        verify(modelsFactory).createEnvironment(runnerEnv2, Scope.SYSTEM);
    }

    @Test
    public void environmentShouldBeReturnedByProjectType() {
        when(runnerEnvLeaf4.getEnvironment()).thenReturn(runEnvironment1);
        when(runnerEnvLeaf5.getEnvironment()).thenReturn(runEnvironment2);

        when(modelsFactory.createEnvironment(runEnvironment1, SYSTEM)).thenReturn(environment1);
        when(modelsFactory.createEnvironment(runEnvironment2, SYSTEM)).thenReturn(environment2);

        List<Environment> result = getEnvironmentsUtil.getEnvironmentsByProjectType(tree, PROJECT_TYPE, SYSTEM);

        verify(projectTypeRegistry).getProjectType(PROJECT_TYPE);
        verify(definition).getRunnerCategories();
        verify(tree).setDisplayName(CATEGORY1);
        verify(tree).getNode(CATEGORY1.toLowerCase());

        verify(runnerEnvTree6).getLeaves();
        verify(runnerEnvTree6).getNodes();
        verify(runnerEnvTree8).getLeaves();
        verify(runnerEnvTree8).getNodes();
        verify(runnerEnvTree9).getLeaves();
        verify(runnerEnvTree9).getNodes();

        verify(runnerEnvLeaf4).getEnvironment();
        verify(runnerEnvLeaf5).getEnvironment();
        verify(modelsFactory).createEnvironment(runEnvironment1, SYSTEM);
        verify(modelsFactory).createEnvironment(runEnvironment2, SYSTEM);

        verify(environment1).setType(CATEGORY1);
        verify(environment2).setType(CATEGORY1);

        assertThat(result, hasItems(environment1, environment2));
    }

    @Test
    public void allTreeShouldBeReturnedByProjectTypeIfThisProjectTypeIsNotExist() {
        when(tree.getNode(CATEGORY1.toLowerCase())).thenReturn(null);

        RunnerEnvironmentTree result = getEnvironmentsUtil.getRunnerCategoryByProjectType(tree, PROJECT_TYPE);

        verify(projectTypeRegistry).getProjectType(PROJECT_TYPE);
        verify(definition).getRunnerCategories();
        verify(tree).setDisplayName(CATEGORY1);
        verify(tree).getNode(CATEGORY1.toLowerCase());

        assertThat(result, is(tree));
    }

    @Test
    public void nodeForProjectTypeShouldBeReturned() {
        RunnerEnvironmentTree result = getEnvironmentsUtil.getRunnerCategoryByProjectType(tree, PROJECT_TYPE);

        verify(projectTypeRegistry).getProjectType(PROJECT_TYPE);
        verify(definition).getRunnerCategories();
        verify(tree).setDisplayName(CATEGORY1);
        verify(tree).getNode(CATEGORY1.toLowerCase());

        assertThat(result, is(runnerEnvTree6));
    }

    /*
    * This method generate difficult tree for testing
    *
    *  + env1                                leaf1
    *  + env2                                leaf2
    *  + env3                                  -
    *      |- env4                           leaf3
    *      |- env5                             -
    *          |- env6                         -
    *              |- env8                   leaf4
    *              |- env9                   leaf5
    *          |- env7                         -
    *              |- env10                  leaf6
    *
    *   deep = 0 amount environments = 1
    *   deep = 1 amount environments = 3  (env1, env2, env3)
    *   deep = 2 amount environments = 5  (env1, env2, env3, env4, env5)
    *   deep = 3 amount environments = 7  (env1, env2, env3, env4, env5, env6, env7)
    *   deep = 4 amount environments = 10 (env1, env2, env3, env4, env5, env6, env7, env8, env9, env10)
    */
    private void generateDifficultTree() {
        //generate runnerEnvironmentTrees
        List<RunnerEnvironmentTree> subListEnv1 = Arrays.asList(runnerEnvTree1, runnerEnvTree2, runnerEnvTree3);
        when(tree.getNodes()).thenReturn(subListEnv1);

        List<RunnerEnvironmentTree> subListEnv2 = Arrays.asList(runnerEnvTree4, runnerEnvTree5);
        when(runnerEnvTree3.getNodes()).thenReturn(subListEnv2);

        List<RunnerEnvironmentTree> subListEnv3 = Arrays.asList(runnerEnvTree6, runnerEnvTree7);
        when(runnerEnvTree5.getNodes()).thenReturn(subListEnv3);

        List<RunnerEnvironmentTree> subListEnv4 = Arrays.asList(runnerEnvTree8, runnerEnvTree9);
        when(runnerEnvTree6.getNodes()).thenReturn(subListEnv4);

        List<RunnerEnvironmentTree> subListEnv5 = Arrays.asList(runnerEnvTree10);
        when(runnerEnvTree7.getNodes()).thenReturn(subListEnv5);

        //generate runnerEnvironmentLeafs
        List<RunnerEnvironmentLeaf> listLeaf1 = Arrays.asList(runnerEnvLeaf1, runnerEnvLeaf2);
        when(tree.getLeaves()).thenReturn(listLeaf1);

        List<RunnerEnvironmentLeaf> listLeaf2 = Arrays.asList(runnerEnvLeaf3);
        when(runnerEnvTree3.getLeaves()).thenReturn(listLeaf2);

        List<RunnerEnvironmentLeaf> listLeaf3 = Arrays.asList(runnerEnvLeaf4, runnerEnvLeaf5);
        when(runnerEnvTree6.getLeaves()).thenReturn(listLeaf3);

        List<RunnerEnvironmentLeaf> listLeaf4 = Arrays.asList(runnerEnvLeaf6);
        when(runnerEnvTree7.getLeaves()).thenReturn(listLeaf4);
    }
}
