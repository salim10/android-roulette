/*
 * Android Runner is a multiplayer GPS game fully written by Xurxo Mendez Perez
 * 
 * Copyright (C) 2009 Xurxo Mendez Perez
 *   
 * This file is part of Android Runner.
 * 
 * Android Runner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Android Runner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Android Runner.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.sonxurxo.android.androidroulette.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author "Xurxo Mendez Perez"
 *
 */
public class HttpHelper {

//	private final String SERVER_HOST_IP = "79.148.102.37";
	private final String SERVER_HOST_IP = "178.33.42.37";
	private final String GAME_URL = "android-roulette";
	private final String FULL_ADDRESS = "http://" + this.SERVER_HOST_IP + 
//	":" + SERVER_PORT + 
	"/" + this.GAME_URL + "/";

//	private final String SERVER_HOST_IP = "193.144.50.66";
//	private final String GAME_URL = "android-roulette";
//	private final String SERVER_PORT = "8080";	
//	private final String FULL_ADDRESS = "http://" + this.SERVER_HOST_IP + 
//	":" + this.SERVER_PORT + 
//	"/" + this.GAME_URL + "/";
	
//	private final String SERVER_HOST_IP = "10.0.2.2";
//	private final String GAME_URL = "android-roulette-server";
//	private final String SERVER_PORT = "9090";	
//	private final String FULL_ADDRESS = "http://" + this.SERVER_HOST_IP + 
//	":" + this.SERVER_PORT + 
//	"/" + this.GAME_URL + "/";
	private final String PING_URL = "ping";
	private final String START_URL = "start";
	private final String POOL_URL = "pool";
	private final String SEND_URL = "send";
	private final String END_URL = "end";
	private final String CANCEL_URL = "cancel";
	
	private static final int TIMEOUT = 30000;
	private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
	
	private final String PARAMETER_NAME_LAST_READ_MESSAGE = "lastReadMessage";
	private final String PARAMETER_NAME_MESSAGE = "message";
	private final String PARAMETER_NAME_TYPING = "typing";
	
	private final String JSON_MESSAGES = "messages";
	private final String JSON_MESSAGE = "message";
	private final String JSON_SERVER_MESSAGE = "serverMessage";
	private final String JSON_TEXT = "text";
	private final String JSON_DATE = "date";
	private final String JSON_OK = "ok";
	private final String JSON_ENDED = "ended";
	private final String JSON_NO_MESSAGES = "noMessages";
	private final String JSON_MY_DATE = "myDate";
	private final String JSON_MATE_TYPING = "mateTyping";
	private final String JSON_NO_MATE_FOUND = "noMateFound";
		
	private static HttpClient client = new DefaultHttpClient();
	private HttpUriRequest request;
	private HttpResponse response;
	private static HttpHelper instance;

	static {
		instance = new HttpHelper();
	}

	public static HttpHelper getInstance() {
		return instance;
	}	
	
	public String convertStreamToString(InputStream is) throws IOException {
		
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
 
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        }
        return "";
    }

	public String ping() throws Exception {

		this.request = new HttpPost(this.FULL_ADDRESS + this.PING_URL);

        try {
        	HttpConnectionParams.setConnectionTimeout(client.getParams(), HttpHelper.TIMEOUT);
        	HttpConnectionParams.setSoTimeout(client.getParams(), HttpHelper.TIMEOUT);
        	this.response = client.execute(this.request);
        	String s = this.convertStreamToString(this.response.getEntity().getContent());
        	String serverMessage = new JSONObject(s).has(this.JSON_SERVER_MESSAGE) ? 
        			new JSONObject(s).getString(this.JSON_SERVER_MESSAGE) : null;
        	return serverMessage;
        	
        } catch (Exception e) {
        	throw e;
        }
	}
	
	public void start() throws NoMateFoundException, Exception {

		this.request = new HttpPost(this.FULL_ADDRESS + this.START_URL);

        try {
        	HttpConnectionParams.setConnectionTimeout(client.getParams(), HttpHelper.TIMEOUT);
        	HttpConnectionParams.setSoTimeout(client.getParams(), HttpHelper.TIMEOUT);
        	this.response = client.execute(this.request);
        	String s = this.convertStreamToString(this.response.getEntity().getContent());
        	if (new JSONObject(s).getString(this.JSON_MESSAGE).equals(this.JSON_NO_MATE_FOUND)) {
        		throw new NoMateFoundException();
        	}
        	
        } catch (NoMateFoundException e) {
        	throw e;
        } catch (ConnectTimeoutException e) {
        	throw e;
        } catch (SocketTimeoutException e) {
        	throw new ConnectTimeoutException();
        } catch (Exception e) {
        	throw e;
        }
	}
	
	public boolean end() throws Exception {

		this.request = new HttpPost(this.FULL_ADDRESS + this.END_URL);

        try {
        	HttpConnectionParams.setConnectionTimeout(client.getParams(), HttpHelper.TIMEOUT);
        	HttpConnectionParams.setSoTimeout(client.getParams(), HttpHelper.TIMEOUT);
        	this.response = client.execute(this.request);
        	String s = this.convertStreamToString(this.response.getEntity().getContent());
        	return new JSONObject(s).getString(this.JSON_MESSAGE).equals(this.JSON_OK);
        } catch (ConnectTimeoutException e) {
        	throw e;
        } catch (Exception e) {
        	throw e;
        }
	}
	
	public boolean cancel() throws Exception {

		this.request = new HttpPost(this.FULL_ADDRESS + this.CANCEL_URL);

        try {
        	HttpConnectionParams.setConnectionTimeout(client.getParams(), HttpHelper.TIMEOUT);
        	HttpConnectionParams.setSoTimeout(client.getParams(), HttpHelper.TIMEOUT);
        	this.response = client.execute(this.request);
        	String s = this.convertStreamToString(this.response.getEntity().getContent());
        	return new JSONObject(s).getString(this.JSON_MESSAGE).equals(this.JSON_OK);
        } catch (ConnectTimeoutException e) {
        	throw e;
        } catch (Exception e) {
        	throw e;
        }
	}
	
	public PoolResponse pool(long lastReadMessage, boolean typing) throws EndedException, Exception {

		HttpConnectionParams.setConnectionTimeout(client.getParams(), HttpHelper.TIMEOUT);
    	HttpConnectionParams.setSoTimeout(client.getParams(), HttpHelper.TIMEOUT);
    	
		this.request = new HttpPost(this.FULL_ADDRESS + this.POOL_URL + 
				"?" + this.PARAMETER_NAME_LAST_READ_MESSAGE + "=" + 
				String.valueOf(lastReadMessage) + 
				"&" + this.PARAMETER_NAME_TYPING + "=" + 
				String.valueOf(typing));

        try {
        	this.response = client.execute(this.request);
        	PoolResponse result = new PoolResponse();
        	String s = this.convertStreamToString(this.response.getEntity().getContent());
        	JSONObject object = new JSONObject(s);
        	if (object.has(this.JSON_MESSAGE) && object.getString(this.JSON_MESSAGE).equals(this.JSON_ENDED)) {
        		throw new EndedException();
        	}
        	if (object.has(this.JSON_MATE_TYPING)) {
        		result.setMateTyping(object.getBoolean(this.JSON_MATE_TYPING));
        	}
        	if (object.has(this.JSON_MESSAGE) && object.getString(this.JSON_MESSAGE).equals(this.JSON_NO_MESSAGES)) {
        		return result;
        	}
        	
        	JSONArray array = new JSONObject(s).getJSONArray(this.JSON_MESSAGES);
        	List<Message> messagesList = new ArrayList<Message>();
        	for (int i = 0; i < array.length(); i++) {
        		JSONObject messageObject = array.getJSONObject(i);
        		SimpleDateFormat sdf = new SimpleDateFormat
       	     		(HttpHelper.DATE_FORMAT, Locale.US);
        		Calendar date = Calendar.getInstance();
        		
        		date.setTime(sdf.parse(messageObject.getString(this.JSON_DATE)));
        		date.add(Calendar.MILLISECOND, date.getTimeZone().getRawOffset());
        		
        		messagesList.add(new Message(messageObject.getString(this.JSON_TEXT), 
        				date));
        	}
        	result.setMessages(messagesList);
        	return result;
        } catch (ConnectTimeoutException e) {
        	throw e;
        } catch (EndedException e) {
        	throw e;
        } catch (Exception e) {
        	throw e;
        }
	}
	
	public PoolResponse send(String message, long lastReadMessage) throws EndedException, Exception {

		HttpConnectionParams.setConnectionTimeout(client.getParams(), HttpHelper.TIMEOUT);
    	HttpConnectionParams.setSoTimeout(client.getParams(), HttpHelper.TIMEOUT);
    	
    	this.request = new HttpPost(this.FULL_ADDRESS + this.SEND_URL + 
				"?" + this.PARAMETER_NAME_LAST_READ_MESSAGE + "=" + 
				String.valueOf(lastReadMessage) + 
				"&" + this.PARAMETER_NAME_MESSAGE + "=" + URLEncoder.encode(String.valueOf(message), "UTF-8"));

        try {
        	this.response = client.execute(this.request);
        	PoolResponse result = new PoolResponse();
        	String s = this.convertStreamToString(this.response.getEntity().getContent());
        	JSONObject object = new JSONObject(s);
        	if (object.has(this.JSON_MESSAGE) && object.getString(this.JSON_MESSAGE).equals(this.JSON_ENDED)) {
        		throw new EndedException();
        	}
        	if (object.has(this.JSON_MATE_TYPING)) {
        		result.setMateTyping(object.getBoolean(this.JSON_MATE_TYPING));
        	}
        	SimpleDateFormat sdf = new SimpleDateFormat
	     		(HttpHelper.DATE_FORMAT, Locale.US);
		
        	String myDate = object.getString(this.JSON_MY_DATE);
        	Calendar myDateCalendar = Calendar.getInstance();
        	myDateCalendar.setTime(sdf.parse(myDate));
        	myDateCalendar.add(Calendar.MILLISECOND, myDateCalendar.getTimeZone().getRawOffset());
        	
        	List<Message> messsagesList = new ArrayList<Message>();
        	
        	if (new JSONObject(s).has(this.JSON_MESSAGES)) {
	    		JSONArray array = new JSONObject(s).getJSONArray(this.JSON_MESSAGES);
	        	
	        	for (int i = 0; i < array.length(); i++) {
	        		JSONObject messageObject = array.getJSONObject(i);
	        		Calendar date = Calendar.getInstance();
		    		date.setTime(sdf.parse(messageObject.getString(this.JSON_DATE)));
		    		date.add(Calendar.MILLISECOND, date.getTimeZone().getRawOffset());
	        		messsagesList.add(new Message(messageObject.getString(this.JSON_TEXT), 
	        				date));
	        	}
        	}
        	messsagesList.add(new Message(null, myDateCalendar));
        	result.setMessages(messsagesList);
        	return result;
        } catch (ConnectTimeoutException e) {
        	throw e;
        } catch (EndedException e) {
        	throw e;
        } catch (Exception e) {
        	throw e;
        }
	}
}