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

package es.sonxurxo.android.androidroulette.server.model.entities.user;

import java.io.Serializable;

import es.sonxurxo.android.androidroulette.server.model.entities.chat.Chat;

/**
 * @author "Xurxo Mendez Perez"
 *
 */
public class User implements Serializable {

	private static final long serialVersionUID = -6806303304621226498L;

	/**
	 * Represents the status of the user. By now, only WAITING and CHATTING are implemented
	 * @author "Xurxo Mendez Perez"
	 *
	 */
	public enum USER_STATUS {
		WAITING, READY, TYPING
	}
	
	/**
	 * Acts like identifier
	 */
	private String id;
	
	/**
	 * The chat where the user is chatting
	 */
	private Chat chat;
	
	/**
	 * The status of the user
	 */
	private USER_STATUS status;
	
	/**
	 * Builds a new user with the associated id
	 * @param id
	 */
	public User(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Chat getChat() {
		return this.chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public USER_STATUS getStatus() {
		return this.status;
	}

	public void setStatus(USER_STATUS status) {
		this.status = status;
	}
	
	
}
