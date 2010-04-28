/*
 * BasePage.java
 * Copyright (c) 2009, Monte Alto Research Center, All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Monte Alto Research Center ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Monte Alto Research Center
 */
package es.sonxurxo.android.androidroulette.server.web.ws;

import java.util.Calendar;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

import es.sonxurxo.android.androidroulette.server.model.entities.chat.Chat;
import es.sonxurxo.android.androidroulette.server.model.entities.message.Message;
import es.sonxurxo.android.androidroulette.server.model.entities.user.User;
import es.sonxurxo.android.androidroulette.server.model.entities.user.User.USER_STATUS;
import es.sonxurxo.android.androidroulette.server.web.naming.PageParametersNaming;
import es.sonxurxo.android.androidroulette.server.web.session.AndroidRouletteSession;
import es.sonxurxo.android.androidroulette.server.web.ws.json.Ended;

public class Send extends WebPage {

	private JSON response;
	
    public Send() {
        this(null);
    }    
    
    public Send(PageParameters pageParameters) {
        super(pageParameters);
        int lastReadMessage = pageParameters.getInt(PageParametersNaming.PARAM_NAME_LAST_READ_MESSAGE);
        String message = pageParameters.getString(PageParametersNaming.PARAM_NAME_MESSAGE);
        User user = AndroidRouletteSession.get().getUser();
        Chat chat = user.getChat();
        
    	if (chat == null) {
	       	this.response = JSONSerializer.toJSON(new Ended());
	    }
	    else {
	    	Calendar date = Calendar.getInstance();
	    	date.add(Calendar.MILLISECOND, -date.getTimeZone().getRawOffset());
	    	
	    	Message myMessage = new Message(message, date);
	    	chat.addMessage(user, myMessage);
	    	List<Message> messages = chat.getMessagesTo(user, lastReadMessage);
	    	JSONObject o = new JSONObject();
	    	o.put("myDate", myMessage.getDate());
	       	if (messages.size() != 0) {
		       	o.put("messages", JSONArray.fromObject(messages));
		    }
	       	User mate = chat.getUser1() == user ? chat.getUser2() : chat.getUser1();
        	o.put("mateTyping", new Boolean(mate.getStatus().equals(USER_STATUS.TYPING)));
        	user.setStatus(USER_STATUS.READY);
        	this.response = o;
	    }
        
        getRequestCycle().setRequestTarget(new AndroidRouletteJSONRequestTarget(this.response));
        
    }
}