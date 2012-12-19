/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.fct.companian.model.uml;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.StateMachine;

import de.fct.companian.model.binding.umlpsc.UMLProtocolDlProxy;
import de.fct.companian.model.binding.umlpsc.UMLStateMachineLoader;
import de.fct.fdmm.hotspot.HookCall;
import de.fct.fdmm.protocoldl.HookProtocolState;
import de.fct.fdmm.protocoldl.MessageType;
import de.fct.fdmm.protocoldl.ProtocolHook;

public class UMLStateMachineTest extends TestCase {

    private static final String MODEL_FILE = "../testdata/statemachines/example1/ProtocolExample.uml";
    
	public void testLoadStateMachine() {
		URI modelUri = URI.createFileURI(MODEL_FILE);
		StateMachine umlStateMachine = UMLStateMachineLoader.loadStateMachine(modelUri);
		Assert.assertNotNull(umlStateMachine);
	}

	public void testInitProxy() throws Exception {
		java.net.URI modelUri = new java.net.URI(MODEL_FILE);
		Assert.assertNotNull(modelUri);
        UMLProtocolDlProxy proxy = new UMLProtocolDlProxy();
        de.fct.fdmm.protocoldl.StateMachine sm = proxy.getStateMachine(MODEL_FILE);
		Assert.assertNotNull(sm);
	}

	public void testGetStartState() throws Exception {
		UMLProtocolDlProxy proxy = new UMLProtocolDlProxy();
		de.fct.fdmm.protocoldl.StateMachine sm = proxy.getStateMachine(MODEL_FILE);

		HookProtocolState startState = sm.getStartState();
		Assert.assertNotNull(startState);
		Assert.assertNotNull(startState.getId());
		Assert.assertEquals("example1", startState.getId());
		Assert.assertNotNull(startState.getSubsequentHooks());
		assert startState.getSubsequentHooks().size() == 1;
	}

	public void testTransition() throws Exception {
        UMLProtocolDlProxy proxy = new UMLProtocolDlProxy();
        de.fct.fdmm.protocoldl.StateMachine sm = proxy.getStateMachine(MODEL_FILE);

        HookProtocolState startState = sm.getStartState();
		ProtocolHook creationMsg = startState.getSubsequentHooks().iterator().next();
		Assert.assertNotNull(creationMsg.getTarget());
		assert creationMsg.getMessageType() == MessageType.CREATION;
		assert creationMsg.toString().equals(MessageType.CREATION.name());
		HookCall hook = creationMsg.getHookCall();
		Assert.assertNull(hook);
		
		HookProtocolState constructed = creationMsg.getTarget();
		Assert.assertNotNull(constructed);
		assert constructed.getId().equals("Constructed");
		
		ProtocolHook configureMsg = constructed.getSubsequentHooks().iterator().next();
		Assert.assertNotNull(configureMsg);
	}

}
