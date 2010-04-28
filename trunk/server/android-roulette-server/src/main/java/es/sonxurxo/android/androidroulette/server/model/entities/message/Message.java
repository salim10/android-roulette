/*
 * Android Runner is a multiplayer GPS game fully written by "Xurxo Mendez Perez"
 * 
 * Copyright (C) 2010 "Xurxo Mendez Perez"
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

package es.sonxurxo.android.androidroulette.server.model.entities.message;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author "Xurxo Mendez Perez"
 *
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -1304577497359863323L;

	/**
	 * The text contained in the message
	 */
	private String text;
	
	/**
	 * The date the message is sent
	 */
	private String date;
	
	/**
	 * Builds a new message
	 * @param text
	 * @param date
	 */
	public Message(String text, Calendar date) {
		super();
		this.text = text;
		SimpleDateFormat sdf = new SimpleDateFormat
	     ("dd-MM-yyyy HH:mm:ss", Locale.US);
		this.date = sdf.format(date.getTime());
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
