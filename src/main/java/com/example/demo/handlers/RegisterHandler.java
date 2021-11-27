package com.example.demo.handlers;

import java.io.IOException;
import java.io.Writer;

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.example.demo.models.BillingInfo;
import com.example.demo.models.PersonalInfo;
import com.example.demo.models.RegisterModel;

@Component
public class RegisterHandler {

	public RegisterModel init() {
		return new RegisterModel();
	}

	public void addPersonalInfo(RegisterModel registerModel, PersonalInfo personalInfo) {
		registerModel.setPersonalInfo(personalInfo);
	}

	public void addBillingInfo(RegisterModel registerModel, BillingInfo billingInfo) {
		registerModel.setBillingInfo(billingInfo);
	}

	public String saveAll(RegisterModel registerModel, MessageContext error) {
		String transitionValue = "success";

		// XXX Save model in database or somewhere else...
		error.addMessage(new MessageBuilder(). //
				error() //
				.source("registration") //
				.defaultText( //
						String.format("Couldn't register user with username: %s!",
								registerModel.getPersonalInfo().getUsername())) //
				.build());
		transitionValue = "failure";

		return transitionValue;
	}

	// https://xpadro.com/2013/04/communication-in-spring-webflow-2.html
	public String validateAjax(RequestContext flowRequestContext, MessageContext context) throws IOException {
		System.out.println("===>validateAjax");
		final MutableAttributeMap<Object> flowScope = flowRequestContext.getFlowScope();

		final String id = flowRequestContext.getCurrentEvent().getId();
		Integer num = (Integer) flowScope.get("xNum");
		if (num == null) {
			num = 0;
		}
		num = num + 1;
		flowScope.put("xNum", num);
		flowRequestContext.getRequestScope().put("testVar", id+":"+ num);
		return null;
	}

	public String ajax3(RequestContext flowRequestContext, MessageContext context) throws IOException {
		System.out.println("===>ajax3");

		final FlowDefinition activeFlow = flowRequestContext.getActiveFlow();

		final StateDefinition state = activeFlow.getState("personal");

		final MutableAttributeMap<Object> flowScope = flowRequestContext.getFlowScope();

		final String key = "ajax3Num";
		Integer num = (Integer) flowScope.get(key);
		if (num == null) {
			num = 0;
		}
		num = num + 1;
		flowScope.put(key, num);
		flowRequestContext.getRequestScope().put("testVar", "ajax3:" + num);
		final ExternalContext externalContext = flowRequestContext.getExternalContext();

		final Writer writer = externalContext.getResponseWriter();
		writer.write("on ajax3:" + num);
		externalContext.recordResponseComplete();

		return null;
	}

	
	public String ajax4(RequestContext flowRequestContext, MessageContext context) throws IOException {
		final String currentState = flowRequestContext.getCurrentState().getId();
		final Event currentEvent = flowRequestContext.getCurrentEvent();
		System.out.println("===>ajax4: " + currentState + "\tcurrentEvent: " + currentEvent);

		final MutableAttributeMap<Object> flowScope = flowRequestContext.getFlowScope();

		final String key = "ajax4Num";
		Integer num = (Integer) flowScope.get(key);
		if (num == null) {
			num = 0;
		}
		num = num + 1;
		flowScope.put(key, num);
		final MutableAttributeMap<Object> requestScope = flowRequestContext.getRequestScope();
		requestScope.put("testVar", "ajax4:" + num);
		requestScope.put("other1", true);
		return null;
	}


	public String validatePersonalInfo(PersonalInfo personalInfo, MessageContext error) {
		String transitionValue = "success";

		// Checking that username is not equal to 'Vakho' :d XXX do whatever you want!
		if (personalInfo.getUsername().equalsIgnoreCase("Vakho")) {
			error.addMessage(new MessageBuilder(). //
					error() //
					.source("username") //
					.defaultText("You are not allowed to use Vakho as the username!") //
					.build());

			transitionValue = "failure";
		}

		// Checking if password matched the confirm password
		if (!personalInfo.getPassword().equals(personalInfo.getConfirmPassword())) {
			error.addMessage(new MessageBuilder(). //
					error() //
					.source("confirmPassword") //
					.defaultText("Password doesn't match up the confirm password!") //
					.build());

			transitionValue = "failure";
		}
		return transitionValue;
	}
}
