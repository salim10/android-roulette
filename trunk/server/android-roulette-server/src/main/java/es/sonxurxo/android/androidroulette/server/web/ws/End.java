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

import es.sonxurxo.android.androidroulette.server.model.entities.chat.Chat;
import es.sonxurxo.android.androidroulette.server.model.entities.user.User;
import es.sonxurxo.android.androidroulette.server.web.application.AndroidRouletteApplication;
import es.sonxurxo.android.androidroulette.server.web.session.AndroidRouletteSession;
import es.sonxurxo.android.androidroulette.server.web.ws.json.Ok;

public class End extends WebPage {

    public End() {
        this(null);
    }    
    
    public End(PageParameters pageParameters) {
        super(pageParameters);
        End.endChat();
        
        getRequestCycle().setRequestTarget(new AndroidRouletteJSONRequestTarget(JSONSerializer.toJSON(new Ok())));
        
    }
    
    private static synchronized void endChat() {
    	User user = AndroidRouletteSession.get().getUser();
        Chat chat = user.getChat();
        if (chat != null) {
        	chat.getUser1().setChat(null);
        	chat.getUser2().setChat(null);
        	AndroidRouletteApplication.getChats().remove(chat);
        	chat.setEnded(true);
        }    	
    }
}