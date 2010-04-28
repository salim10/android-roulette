/*
 * Copyright (c) 2009, Monte Alto Research Center, All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Monte Alto Research Center ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Monte Alto Research Center
 */
package es.sonxurxo.android.androidroulette.server.web.session;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

import es.sonxurxo.android.androidroulette.server.model.entities.user.User;


public class AndroidRouletteSession extends WebSession {    

	private static final long serialVersionUID = -8689994071227082283L;

	private User user;
	
	public AndroidRouletteSession(Request request) {
        super(request);
        this.user = new User(this.getId());
    }

    public static AndroidRouletteSession get() {
        return (AndroidRouletteSession) Session.get();
    }

	public User getUser() {
		return this.user;
	}
}
