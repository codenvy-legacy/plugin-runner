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
package com.codenvy.ide.ext.runner.client.runneractions.impl.launch.common;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Inject;

/**
 * Unmarshaller for a log message.
 *
 * @author Artem Zatsarynnyy
 * @author Andrey Plotnikov
 */
public class LogMessageUnmarshaller implements Unmarshallable<LogMessage> {
    private static final String LINE   = "line";
    private static final String NUMBER = "num";

    private LogMessage logMessage;

    @Inject
    public LogMessageUnmarshaller() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Message response) throws UnmarshallerException {
        JSONObject jsonObject = JSONParser.parseStrict(response.getBody()).isObject();

        if (jsonObject == null || !jsonObject.containsKey(LINE)) {
            return;
        }

        int lineNumber = (int)jsonObject.get(NUMBER).isNumber().doubleValue();
        String text = jsonObject.get(LINE).isString().stringValue();

        logMessage = new LogMessage(lineNumber, text);
    }

    /** {@inheritDoc} */
    @Override
    public LogMessage getPayload() {
        return logMessage;
    }

}