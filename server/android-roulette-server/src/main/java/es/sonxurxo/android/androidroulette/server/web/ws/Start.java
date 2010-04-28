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

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

import es.sonxurxo.android.androidroulette.server.model.entities.chat.Chat;
import es.sonxurxo.android.androidroulette.server.model.entities.user.User;
import es.sonxurxo.android.androidroulette.server.model.entities.user.User.USER_STATUS;
import es.sonxurxo.android.androidroulette.server.web.application.AndroidRouletteApplication;
import es.sonxurxo.android.androidroulette.server.web.session.AndroidRouletteSession;

public class Start extends WebPage {

	private JSON response;
	
    public Start() {
        this(null);
    }    
    
    public Start(PageParameters pageParameters) {
        super(pageParameters);
        User user = AndroidRouletteSession.get().getUser();
        int counter = 0;
        int timeout = Integer.parseInt(AndroidRouletteApplication.get().getTimeout());
	    if (Start.mustWaitNegotiation(user)) {
	    	while (user.getStatus().equals(USER_STATUS.WAITING) && counter < timeout) {
	       		try {
					Thread.sleep(1000);
					counter += 1000;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	       	}
	    }
	    
	    if (counter >= timeout) {
	    	AndroidRouletteApplication.setWaitingUser(null);
	    	JSONObject o = new JSONObject();
	       	o.put("message", "noMateFound");
	       	this.response = o;
	    	getRequestCycle().setRequestTarget(new AndroidRouletteJSONRequestTarget(this.response));
	    	return;
	    }
	    
	    user.setStatus(USER_STATUS.READY);
	    
	    JSONObject o = new JSONObject();
       	o.put("message", "ok");
       	
       	this.response = o;
		
	    getRequestCycle().setRequestTarget(new AndroidRouletteJSONRequestTarget(this.response));
        
    }
    
    private static synchronized boolean mustWaitNegotiation(User user) {
    	User waitingUser = AndroidRouletteApplication.getWaitingUser();
	    if (waitingUser != null && waitingUser != user) {
	       	AndroidRouletteApplication.getChats().add(new Chat(1, user, waitingUser));
	       	AndroidRouletteApplication.setWaitingUser(null);
	       	waitingUser.setStatus(USER_STATUS.READY);
	       	return false;
	    }
	    AndroidRouletteApplication.setWaitingUser(user);
	    user.setStatus(USER_STATUS.WAITING);
	    return true;
    }
}