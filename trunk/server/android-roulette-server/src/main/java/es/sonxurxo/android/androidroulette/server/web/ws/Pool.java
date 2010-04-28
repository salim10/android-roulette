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

public class Pool extends WebPage {

	private JSON response;
	
    public Pool() {
        this(null);
    }    
    
    public Pool(PageParameters pageParameters) {
        super(pageParameters);
        int lastReadMessage = pageParameters.getInt(PageParametersNaming.PARAM_NAME_LAST_READ_MESSAGE);
        User user = AndroidRouletteSession.get().getUser();
	    Chat chat = user.getChat();
	    if (chat == null) {
	       	this.response = JSONSerializer.toJSON(new Ended());
	    }
	    else {
	    	List<Message> messages = chat.getMessagesTo(user, lastReadMessage);
		    if (messages.size() == 0) {
		    	JSONObject o = new JSONObject();
		    	o.put("message", "noMessages");
		    	this.response = o;
		    }
		    else {
		       	JSONObject o = new JSONObject();
		       	o.put("messages", JSONArray.fromObject(messages));
		       	this.response = o;
		    }
		    
		    // For newer client versions
		    if (pageParameters.get(PageParametersNaming.PARAM_NAME_TYPING) != null) {
		    	boolean typing = pageParameters.getBoolean(PageParametersNaming.PARAM_NAME_TYPING);
		    	user.setStatus(typing ? USER_STATUS.TYPING : USER_STATUS.READY);
	        }
		    User mate = chat.getUser1() == user ? chat.getUser2() : chat.getUser1();
		    ((JSONObject)this.response).put("mateTyping", 
        			new Boolean(mate.getStatus().equals(USER_STATUS.TYPING)));
	    }
	    getRequestCycle().setRequestTarget(new AndroidRouletteJSONRequestTarget(this.response));
        
    }
}