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

import net.sf.json.JSONSerializer;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

import es.sonxurxo.android.androidroulette.server.model.entities.user.User;
import es.sonxurxo.android.androidroulette.server.web.application.AndroidRouletteApplication;
import es.sonxurxo.android.androidroulette.server.web.session.AndroidRouletteSession;
import es.sonxurxo.android.androidroulette.server.web.ws.json.Ok;

public class Cancel extends WebPage {

    public Cancel() {
        this(null);
    }    
    
    public Cancel(PageParameters pageParameters) {
        super(pageParameters);
        Cancel.cancel();
        
        getRequestCycle().setRequestTarget(new AndroidRouletteJSONRequestTarget(JSONSerializer.toJSON(new Ok())));
        
    }
    
    private static synchronized void cancel() {
    	User user = AndroidRouletteSession.get().getUser();
    	User waitingUser = AndroidRouletteApplication.getWaitingUser();
    	if (user == waitingUser) {
    		AndroidRouletteApplication.setWaitingUser(null);
    	}    	
    }
}