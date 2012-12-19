package de.fct.companian.model.binding.umlpsc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHookProxy {

	private static Logger logger = LoggerFactory.getLogger(EventHookProxy.class);

	private Event event;

	private String name;

	public String getName() {
		return this.name;
	}

	public boolean init() {
		if (logger.isDebugEnabled()) {
			logger.debug("init() with event=" + this.event);
		}
		boolean success = true;

		if (event != null) {
			this.name = event.getName();

			if (event instanceof MessageEvent) {
				MessageEvent msgEvent = (MessageEvent) event;
				try {
					String[] sig = this.extractMethodSignature(event.getName());
					this.name = sig[0];
				} catch (Throwable t) {
					logger.warn("init() could not parse signature of msgEvent " + msgEvent.getName() + ". Init did not succeed.");
					success = false;
				}
			} else {
				logger.warn("init() unrecognized event type " + event.getClass().getName());
				success = false;
			}
		}

		return success;
	}

	/**
	 * @param input
	 *            - A org.eclipse.uml2.uml.Event
	 * @see de.upb.fct.fd.ExtDescriptionLanguage#setInput(java.lang.Object)
	 */
	public void setInput(Object input) {
		this.event = (Event) input;
	}

	/**
	 * Parse a method signature or method call signature. <br/>
	 * Given a call signature like "method(Type t)", extract the method name and
	 * param type and parameter name: [method, Type, t] <br/>
	 * Given a signature like "method(X x, Y)", extract the method name and
	 * param name / param type - but leaving empty String if the information is
	 * not available: [method, X, x, Y, ""]
	 * 
	 * @param methodCallSignature
	 * @return each element (2xp+1 sized) (see doc)
	 */
	private String[] extractMethodSignature(String methodCallSignature) throws Exception {
		List<String> extracted = new ArrayList<String>();
		String methodName = methodCallSignature;
		String methodCallDesc = null;
		if (methodCallSignature.indexOf("(") > 0) {
			methodName = methodName.substring(0, methodCallSignature.indexOf("("));
			methodCallDesc = methodCallSignature.substring(methodCallSignature.indexOf("(") + 1, methodCallSignature.lastIndexOf(")"));
		}
		extracted.add(methodName);
		if (methodCallDesc != null) {
			String[] parameters = methodCallDesc.split(",");
			for (String param : parameters) {
				param = param.trim();
				param = param.replaceAll("  ", " ");
				String[] parameterInfo = param.split(" ");
				extracted.add(parameterInfo[0]);
				extracted.add((parameterInfo.length > 1) ? parameterInfo[1] : "");
			}
		}
		return (String[]) extracted.toArray(new String[] {});
	}
}
