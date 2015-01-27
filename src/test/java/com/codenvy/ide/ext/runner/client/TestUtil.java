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
package com.codenvy.ide.ext.runner.client;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * The class contains methods which are used in several test classes.
 *
 * @author Dmitry Shnurenko
 */
public class TestUtil {

    /**
     * Method returns value of field from superclass by name.
     *
     * @param object
     *         object from super class which need get field value
     * @param fieldName
     *         field name which need get
     * @return value of field by name
     * @throws Exception
     */
    public static <T> Object getFieldValueByName(@Nonnull T object, @Nonnull String fieldName) throws Exception {
        Field field = object.getClass().getSuperclass().getDeclaredField(fieldName);

        field.setAccessible(true);

        return field.get(object);
    }

    /**
     * Methods returns value of field from object using index. Index is number field in class.
     *
     * @param object
     *         object from which need get value of field
     * @param index
     *         index of field for which need get value
     * @return value of field by index
     * @throws Exception
     */
    public static <T> Object getFieldValueByIndex(@Nonnull T object, @Nonnegative int index) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();

        Field field = fields[index];

        field.setAccessible(true);

        return field.get(object);
    }
}
