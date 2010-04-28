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

import es.sonxurxo.android.androidroulette.server.web.application.AndroidRouletteApplication;

public class Ping extends WebPage {

	private JSON response;
	
    public Ping() {
        this(null);
    }    
    
    public Ping(PageParameters pageParameters) {
        super(pageParameters);
        JSONObject o = new JSONObject();
       	o.put("message", "ok");
       	this.response = o;
       	if (AndroidRouletteApplication.get().hasServerMessage()) {
       		o.put("serverMessage", AndroidRouletteApplication.get().getServerMessage());
	    }
        getRequestCycle().setRequestTarget(new AndroidRouletteJSONRequestTarget(this.response));
        
    }
}