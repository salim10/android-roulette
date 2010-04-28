/*
 * Copyright (c) 2009, Monte Alto Research Center, All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Monte Alto Research Center ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Monte Alto Research Center
 */
package es.sonxurxo.android.androidroulette.server.web.application;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import es.sonxurxo.android.androidroulette.server.model.entities.chat.Chat;
import es.sonxurxo.android.androidroulette.server.model.entities.user.User;
import es.sonxurxo.android.androidroulette.server.web.session.AndroidRouletteSession;
import es.sonxurxo.android.androidroulette.server.web.ws.Cancel;
import es.sonxurxo.android.androidroulette.server.web.ws.Ping;
import es.sonxurxo.android.androidroulette.server.web.ws.End;
import es.sonxurxo.android.androidroulette.server.web.ws.Pool;
import es.sonxurxo.android.androidroulette.server.web.ws.Send;
import es.sonxurxo.android.androidroulette.server.web.ws.Start;

public class AndroidRouletteApplication extends WebApplication {

	private String serverMessage;
	private String timeout;
	
	private static User waitingUser;
	private static List<Chat> chats = new ArrayList<Chat>();
	
    public AndroidRouletteApplication() {
        super();
    }

    public static User getWaitingUser() {
		return AndroidRouletteApplication.waitingUser;
	}

	public static void setWaitingUser(User waitingUser) {
		AndroidRouletteApplication.waitingUser = waitingUser;
	}

	public static List<Chat> getChats() {
		return AndroidRouletteApplication.chats;
	}

	public static void setConversations(List<Chat> conversations) {
		AndroidRouletteApplication.chats = conversations;
	}

	public String getServerMessage() {
		return this.serverMessage;
	}

	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
	}
	
	public boolean hasServerMessage() {
		return StringUtils.isNotEmpty(this.serverMessage);
	}

	public String getTimeout() {
		return this.timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	@Override
    protected void init() {
        super.init();
        mountBookmarkablePage("/start", Start.class);
        mountBookmarkablePage("/pool", Pool.class);
        mountBookmarkablePage("/send", Send.class);
        mountBookmarkablePage("/end", End.class);
        mountBookmarkablePage("/cancel", Cancel.class);
        mountBookmarkablePage("/ping", Ping.class);
    }  
	
    @Override
    public Session newSession(Request request, Response response) {
        return new AndroidRouletteSession(request);
    }
    
    public static AndroidRouletteApplication get() {
        return (AndroidRouletteApplication) Application.get();
    }

	@Override
	public Class<? extends WebPage> getHomePage() {
		// TODO Auto-generated method stub
		return null;
	}
}